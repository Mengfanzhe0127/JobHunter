package com.group.service.impl;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.Mode;
import cn.hutool.crypto.Padding;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.group.Component.IDGeneratorSnowFlake;
import com.group.mapper.UserMapper;
import com.group.pojo.PageBean;
import com.group.pojo.RedisData;
import com.group.pojo.Result;
import com.group.pojo.User;
import com.group.service.UserService;
import com.group.utils.MLServiceClient;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.AES;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Date;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.group.utils.RedisConstants.*;

/**
* @author mfz
* @description 针对表【user(用户表)】的数据库操作Service实现
* @createDate 2024-01-23 00:20:25
*/
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private MLServiceClient mlServiceClient;

    @Override
    public void userRegister(byte[] name, byte[] email, byte[] phone, byte[] password) {
        //利用雪花算法为用户生成唯一Id
        Long id = getUniqueIdBySnowFlake();
        userMapper.insertByNameEmailPhonePassword(id,name,email,phone,password);
    }

    @Autowired
    private IDGeneratorSnowFlake idGenerator;

    @Override
    public Long getUniqueIdBySnowFlake() {

        return idGenerator.snowFlakeId();
    }

    @Override
    public User userLogin(byte[] phoneOrEmail, byte[] password) {
        //将byte数组转换为字符串
        String phoneOrEmailStr = new String(phoneOrEmail);
        //判断邮箱or手机号
        boolean isPhone = phoneOrEmailStr.matches("^1[3-9]\\d{9}$"); // 使用正则表达式匹配手机号
        //根据手机号或邮箱查询用户
        User user = isPhone ? userMapper.getByPhoneAndPassword(phoneOrEmail,password) : userMapper.getByEmailAndPassword(phoneOrEmail,password);
        //如果用户存在，比较密码是否正确
        if (user != null) {
            return user; // 密码正确，返回用户
        } else {
            return null; // 用户不存在或密码错误，返回null
        }
    }

    @Override
    public User encryptGetById(Long id) {
        /**
         * 通过设置null解决缓存穿透
         */
        //定义key-value
        String key = CACHE_USER_INFO_KEY+id;
        String userJson = stringRedisTemplate.opsForValue().get(key);
        if(StrUtil.isNotBlank(userJson)) {
            User user = JSONUtil.toBean(userJson,User.class);
            return user;
        }
        //解决缓存穿透
        //判断命中的是否是空值(不等于null及为空字符串，说明在数据库中不存在该数据)
        if(userJson != null) {
            log.info("ID为"+id+"的用户不存在");
            return null;
        }

        return queryWithMutex(id);
//        return queryWithLogicalExpire(id);
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

    //线程池
    private static final ExecutorService CACHE_REBUILD_EXECUTOR = Executors.newFixedThreadPool(10);

    /**
     * 基于逻辑过期的缓存击穿策略
     * @param id
     * @return
     */
    private User queryWithLogicalExpire(Long id) {
        String key = CACHE_USER_INFO_KEY+id;
        String userJson = stringRedisTemplate.opsForValue().get(key);
        //逻辑缓存，若缓存中不存在,直接返回null?
        if(StrUtil.isBlank(userJson)) {
            log.info("ID为"+id+"的用户不存在");
            return null;
        }
        // 命中，先将缓存序列化为user对象
        RedisData redisData = JSONUtil.toBean(userJson,RedisData.class);
        User user = JSONUtil.toBean((JSONObject) redisData.getData(),User.class);
        LocalDateTime expireTime = redisData.getExpireTime();
        //缓存未过期
        if(expireTime.isAfter(LocalDateTime.now())) {
            return user;
        }
        // 已过期，缓存重建
        String lockKey = LOCK_USER_KEY+id;
        boolean isLock = tryLock(lockKey);
        //若取锁成功
        if(isLock) {
            //建立独立线程，重新写入缓存
            CACHE_REBUILD_EXECUTOR.submit(() -> {
                try{
                    //重建缓存
                    this.saveUserRedis(id,10L);
                }catch(Exception e) {
                    throw new RuntimeException(e);
                }finally{
                    unlock(lockKey);
                }

            });

        }
        //返回用户信息
        return user;
    }

    /**
     * 基于同步锁的解决缓存击穿策略
     * @param id
     * @return
     */
    private User queryWithMutex(Long id) {
        //用户信息键
        String key = CACHE_USER_INFO_KEY+id;
        //同步锁键
        String lockKey = LOCK_USER_KEY+id;
        User user = null;
        try{
            boolean isLock = tryLock(lockKey);
            if(!isLock) {
                //未获得同步锁，休眠重试
                Thread.sleep(50);
                //递归查询
                return queryWithMutex(id);
            }

            //成功，从数据库中查询
            user = userMapper.encryptSelectById(id);
            log.info("user为"+user);
            if(user == null) {
                stringRedisTemplate.opsForValue().set(key,"",CACHE_NULL_TTL,TimeUnit.MINUTES);
                log.info("ID为"+id+"的用户不存在");
                return null;
            }
            //存在，写入Redis
            stringRedisTemplate.opsForValue().set(key,JSONUtil.toJsonStr(user),CACHE_USER_INFO_TTL, TimeUnit.MINUTES);
        }catch (Exception e) {
            throw new RuntimeException(e);
        }finally{
            //释放锁
            unlock(lockKey);
        }
        return user;
    }

    public void saveUserRedis(Long id,Long expireSeconds) {
        User user = userMapper.encryptSelectById(id);
        //封装逻辑过期时间
        RedisData redisData = new RedisData();
        redisData.setData(user);
        redisData.setExpireTime(LocalDateTime.now().plusSeconds(expireSeconds));
        stringRedisTemplate.opsForValue().set(CACHE_USER_INFO_KEY+id,JSONUtil.toJsonStr(redisData));
    }

    @Override
    @Transactional
    public void encryptUpdate(User u) {
        //更新数据库
        Long id  = u.getId();
        if(id == null) {
            log.info("用户ID不能为空");
        }

        userMapper.encryptUpdate(u);
        //更新数据库成功，删除缓存
        stringRedisTemplate.delete(CACHE_USER_INFO_KEY+id);

        //删除职位推荐缓存
        stringRedisTemplate.delete(CACHE_POSITIONS_INFO_KEY + id);

    }

    @Override
    public Date getBirthById(Long id) {
        return userMapper.selectBirthById(id);
    }

    @Override
    @Transactional
    public void updateResume(String url,Long id) {
//        UpdateWrapper<User> userUpdateWrapper =  new UpdateWrapper<>();
//        userUpdateWrapper.like("id",id);
//        userUpdateWrapper.set("resume",url);
//        userMapper.update(null,userUpdateWrapper);
        userMapper.updateResumeById(url,id);
    }

    @Override
    public String getResumeById(Long id) {
        return userMapper.selectResumeById(id);
    }

    @Override
    public void refreshUserInfoCache(Long id) {
        String key = CACHE_USER_INFO_KEY+id;
        User u = userMapper.encryptSelectById(id);
        String resume = u.getResume();
        if(resume != null && !resume.isEmpty()) {
            //更新缓存信息
            stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(u), 30L, TimeUnit.MINUTES);
        }
    }

    @Override
    public PageBean getUsersByEmployerId(Long employerId,int pageNum,int pageSize,String description) {
        List<User> userList = userMapper.selectListByEmployerId(employerId);
        if(userList == null) {
            return null;
        }
        int total = userList.size();
        int startIndex = (pageNum - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, total);
        List<User> userPage = userList.subList(startIndex,endIndex);
        PageBean pageBean = new PageBean();
        pageBean.setTotal(total);
        pageBean.setRows(userPage);

        return pageBean;
    }

    @Override
    public SseEmitter userGetCapacity(Long userId) {
        String id = userId.toString();
        SseEmitter emitter = new SseEmitter();
        new Thread(() -> {
            try {
                for (int i = 0; i < 10; i++) {
                    String data = mlServiceClient.getCapacity(id);
                    emitter.send(data);
                    Thread.sleep(1000); // Simulate delay between batches
                }
                emitter.complete(); // Complete the SSE connection
            } catch (Exception e) {
                emitter.completeWithError(e); // Handle errors
            }
        }).start();
        return emitter;
    }



    //创建连接

    private final ExecutorService executorService = Executors.newCachedThreadPool();//异步发送消息
    public void userGetCapacity2(Long userId) {
        String id = userId.toString();
        executorService.execute(() -> {
            SseEmitter emitter = new SseEmitter();
            OkHttpClient client = new OkHttpClient();
            RequestBody body = null;
            Request request = new Request.Builder()
                    .url("http://113.54.246.85:5000/ml_capacity_endpoint"+"?userId="+userId)
                    .method("GET",body)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("X-ML-Request","true")
                    .build();

            //将请求添加进消息
            HashMap<String,String> assistant = new HashMap<>();
            assistant.put("role","assistant");
            assistant.put("content","");

            //异步请求
            try {
                Response response = client.newCall(request).execute();
                //判断响应成功
                if(response.isSuccessful()) {
                    //获取响应流
                    try(ResponseBody responseBody = response.body()) {
                        if(responseBody != null) {
                            InputStream inputStream = responseBody.byteStream();
                            byte[] buffer = new byte[2048];
                            int bytesRead;
                            StringBuilder temp = new StringBuilder();
                            while((bytesRead = inputStream.read(buffer))!=-1) {
                                temp.append(new String(buffer,0,bytesRead));
                                String result = "";
                                if(temp.lastIndexOf("\n\n") != -1) {
                                    com.alibaba.fastjson.JSONObject jsonObject = JSON.parseObject(temp.substring(6, temp.lastIndexOf("\n\n")));
                                    temp = new StringBuilder(temp.substring(temp.lastIndexOf("\n\n") + 2));
                                    if(jsonObject != null && jsonObject.getString("result") != null){
                                        result = jsonObject.getString("result");
                                    }
                                }
                                if(!result.equals("")){
                                    //SSE协议默认是以两个\n换行符为结束标志 需要在进行一次转义才能成功发送给前端
                                    emitter.send(SseEmitter.event().data(result.replace("\n","\\n")));
                                    //将结果汇总起来
                                    assistant.put("content",assistant.get("content") + result);
                                }
                            }

                        }
                    }
                }else {
                    System.out.println("UnExpected Code "+ response);
                }
            }catch(IOException e) {
                log.error("流式调用出错，断开与"+id+"的连接");
            }
        });
    }

    @Override
    public void updateImageById(Long id, String imageUrl) {
        userMapper.updateImageById(id,imageUrl);
    }

    @Override
    public String getImageById(Long uid) {
        return userMapper.selectImageById(uid);
    }

}




