package com.group.service.impl;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.group.Component.IDGeneratorSnowFlake;
import com.group.mapper.EmployerMapper;
import com.group.pojo.Employer;
import com.group.pojo.Position;
import com.group.pojo.User;
import com.group.service.EmployerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.group.utils.RedisConstants.*;


/**
* @author mfz
* @description 针对表【employer(招聘者表)】的数据库操作Service实现
* @createDate 2024-01-23 00:23:15
*/
@Service
@Slf4j
public class EmployerServiceImpl extends ServiceImpl<EmployerMapper, Employer>
    implements EmployerService{
    @Autowired
    private EmployerMapper employerMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public void employerRegister(byte[] name, byte[] email, String company, byte[] password) {
        //利用雪花算法为用户生成唯一Id
        Long id = getUniqueIdBySnowFlake();
        employerMapper.insertByNameEmailCompanyPassword(id,name,email,company,password);
    }

    @Autowired
    private IDGeneratorSnowFlake idGenerator;

    @Override
    public Long getUniqueIdBySnowFlake() {

        return idGenerator.snowFlakeId();
    }

    @Override
    public Employer employerLogin(byte[] phoneOrEmail, byte[] password) {
//        String phoneOrEmailStr = new String(phoneOrEmail);
//        boolean isPhone = phoneOrEmailStr.matches("^1[3-9]\\d{9}$");

        Employer employer = employerMapper.getByEmailAndPassword(phoneOrEmail,password);
        if(employer != null) {
            return employer;
        }else {
            return null;
        }
    }

    @Override
    public Employer encryptGetById(Long id) {
        String key = CACHE_EMPLOYER_INFO_KEY+id;
        String employerJson = stringRedisTemplate.opsForValue().get(key);
        //解决缓存穿透
        if(StrUtil.isNotBlank(employerJson)) {
            return JSONUtil.toBean(employerJson,Employer.class);
        }
        if(employerJson != null) {
            log.info("ID为"+id+"的招聘者不存在");
        }
        return queryWithMutex(id);
    }

    @Override
    public void encryptUpdate(Employer employer) {
        employerMapper.encryptUpdate(employer);
    }

    @Override
    public List<Position> employerGetPositions(Long employerId) {
        return employerMapper.selectPositionsById(employerId);
    }

    @Override
    public void updateImageById(Long id, String imageUrl) {
        employerMapper.updateImageById(id,imageUrl);
    }

    @Override
    public String getImageById(long l) {
        return employerMapper.selectImageById(l);
    }

    /**
     * 创建同步锁
     * @param key
     * @return
     */
    private boolean tryLock(String key) {
        Boolean flag = stringRedisTemplate.opsForValue().setIfAbsent(key, "1", 10, TimeUnit.SECONDS);
        return BooleanUtil.isTrue(flag);
    }

    /**
     * 删除同步锁
     * @param key
     */
    private void unlock(String key) {
        stringRedisTemplate.delete(key);
    }

    /**
     * 基于同步锁的解决缓存击穿策略
     * @param id
     * @return
     */
    private Employer queryWithMutex(Long id) {
        //用户信息键
        String key = CACHE_EMPLOYER_INFO_KEY+id;
        //同步锁键
        String lockKey = LOCK_EMPLOYER_KEY+id;
        Employer employer = null;
        try{
            boolean isLock = tryLock(lockKey);
            if(!isLock) {
                //未获得同步锁，休眠重试
                Thread.sleep(50);
                //递归查询
                return queryWithMutex(id);
            }

            //成功，从数据库中查询
            employer = employerMapper.encryptSelectById(id);
            if(employer == null) {
                stringRedisTemplate.opsForValue().set(key,"",CACHE_NULL_TTL,TimeUnit.MINUTES);
                log.info("ID为"+id+"的用户不存在");
                return null;
            }
            //存在，写入Redis
            stringRedisTemplate.opsForValue().set(key,JSONUtil.toJsonStr(employer),CACHE_EMPLOYER_INFO_TTL, TimeUnit.MINUTES);
        }catch (Exception e) {
            throw new RuntimeException(e);
        }finally{
            //释放锁
            unlock(lockKey);
        }
        return employer;
    }
}




