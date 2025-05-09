package com.group.service.impl;


import cn.hutool.core.lang.generator.SnowflakeGenerator;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.group.Component.IDGeneratorSnowFlake;
import com.group.mapper.PositionMapper;
import com.group.mapper.UserCapacityMapper;
import com.group.mapper.UserMapper;
import com.group.mapper.UserNoResumeMapper;
import com.group.pojo.*;
import com.group.service.PositionService;
import com.group.service.UserCapacityService;
import com.group.service.UserPositionService;
import com.group.service.UserService;
import com.group.utils.MLServiceClient;
import com.group.utils.RSAUtils;
import com.group.utils.ResumeSDKParseUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.group.utils.RedisConstants.*;
import static com.group.utils.ResumeSDKConstants.APP_CODE;
import static com.group.utils.ResumeSDKConstants.ResumeSDK_URL;

/**
* @author mfz
* @description 针对表【position(职位表)】的数据库操作Service实现
* @createDate 2024-01-23 00:33:46
*/
@Service
@Slf4j
public class PositionServiceImpl extends ServiceImpl<PositionMapper, Position>
    implements PositionService {
    @Autowired
    private PositionMapper positionMapper;

    @Autowired
    private IDGeneratorSnowFlake idGenerator;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private UserPositionService userPositionService;

    @Autowired
    private UserService userService;

    @Autowired
    private MLServiceClient mlServiceClient;

    @Autowired
    private UserCapacityService userCapacityService;

    @Autowired
    private UserCapacityMapper userCapacityMapper;

    @Autowired
    private RSAUtils rsaUtils;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserNoResumeMapper userNoResumeMapper;

    @Override
    public List<Position> selectFavoritePositionList(Long userId) {
        return positionMapper.selectFavoritePositionList(userId);
    }

    @Override
    public PageBean getPositionsByIds(List<PositionWithScore> positionsWithScores, int pageNum, int pageSize) {
        List<Long> idList = positionsWithScores.stream()
                .map(PositionWithScore::getId)
                .collect(Collectors.toList());

        List<Position> allPositions = positionMapper.selectBatchIds(idList);
        int total = allPositions.size();

        int startIndex = (pageNum - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, total);

        List<Position> positionsPage = allPositions.subList(startIndex, endIndex);
        total = positionsPage.size();

        // 将匹配度添加到职位详细信息中
        List<PositionWithScore> positionsPageWithScores = new ArrayList<>();
        for (Position position : positionsPage) {
            for (PositionWithScore positionWithScore : positionsWithScores) {
                if (position.getId().equals(positionWithScore.getId())) {
                    PositionWithScore posWithScore = new PositionWithScore();
                    posWithScore.setId(position.getId());
                    posWithScore.setCompany(position.getCompany());
                    posWithScore.setTitle(position.getTitle());
                    posWithScore.setSalary(position.getSalary());
                    posWithScore.setEducation(position.getEducation());
                    posWithScore.setDescription(position.getDescription());
                    posWithScore.setHiringManager(position.getHiringManager());
                    posWithScore.setLastActive(position.getLastActive());
                    posWithScore.setAddress(position.getAddress());
                    posWithScore.setLink(position.getLink());
                    posWithScore.setEid(position.getEid());
                    posWithScore.setScore(positionWithScore.getScore());
                    positionsPageWithScores.add(posWithScore);
                    break;
                }
            }
        }
        positionsPageWithScores.sort(Comparator.comparing(PositionWithScore::getScore).reversed());
        PageBean pageBean = new PageBean();
        pageBean.setTotal(total);
        pageBean.setRows(positionsPageWithScores);

        return pageBean;
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

    @Override
    public void addPosition(Position position) {
        positionMapper.insert(position);
    }

    @Override
    public Long getUniqueIdBySnowFlake() {
        return idGenerator.snowFlakeId();
    }

    @Override
    public void saveEid(Long employerId,Long positionId) {
        UpdateWrapper<Position> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id",positionId);
        updateWrapper.set("eid",employerId);
        positionMapper.update(null,updateWrapper);
    }

    @Override
    public PageBean getPositionsByML2(Long id, int pageNum, int pageSize) {
        //先查询缓存
        String key = CACHE_POSITIONS_INFO_KEY+id+"_page"+pageNum;//用户对应的职位推荐键
        String positionsJson = stringRedisTemplate.opsForValue().get(key);
        if(StrUtil.isNotBlank(positionsJson)) {
            return JSONUtil.toBean(positionsJson,PageBean.class);
        }
        if(positionsJson != null) {
            log.info("ID为"+id+"的求职者不存在推荐的职位");
        }
//        log.info("未在缓存中查询到推荐信息，转为查询MySQL");
        return queryWithMutex(id,pageNum,pageSize);
    }

    /**
     * 缓存穿透
     * @param id
     * @param pageNum
     * @param pageSize
     * @return
     */
    private PageBean queryWithMutex (Long id,int pageNum,int pageSize) {
        String key = CACHE_POSITIONS_INFO_KEY + id;
        String lockKey = LOCK_POSITIONS_KEY + id;
        PageBean pageBean = null;
        try {
            boolean isLock = tryLock(lockKey);
            if(!isLock) {
                //未获得同步锁，休眠重试
                Thread.sleep(50);
                //递归查询
                return queryWithMutex(id,pageNum,pageSize);
            }
            //简历解析参数配置
            String url = ResumeSDK_URL;
            String fname = userService.getResumeById(id);
//            String fname = "https://web-job-recommendation.oss-cn-chengdu.aliyuncs.com/5724b5b9-ccaf-44c4-8c5d-c1988a671d3c.png";
//            log.info("简历访问："+fname);
            String appcode = APP_CODE;

            //进行简历解析
//            try {
//                String parseInfo = ResumeSDKParseUtils.parser(url, fname, appcode);
//            }catch(Exception e) {
//                log.info("简历解析失败");
//                throw new RuntimeException();
//            }
            String parseInfo = ResumeSDKParseUtils.parser(url, fname, appcode);
//            String parseInfo = userCapacityMapper.selectCapacityById(id);
            //创建 ObjectMapper
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode parseInfoNode = objectMapper.readTree(parseInfo);
            try {
//                JsonNode parseInfoNode = objectMapper.readTree(parseInfo);
                if(parseInfoNode.has("name") && parseInfoNode.get("name").asText() != null) {
                    String name = parseInfoNode.get("name").asText();
                    String encryptedName = rsaUtils.encrypt(name);
                    ((ObjectNode)parseInfoNode).put("name",encryptedName);
                }
                if(parseInfoNode.has("phone") && parseInfoNode.get("phone").asText() != null) {
                    String encryptedPhone = rsaUtils.encrypt(parseInfoNode.get("phone").asText());
                    ((ObjectNode)parseInfoNode).put("phone",encryptedPhone);
                }
                if(parseInfoNode.has("email") && parseInfoNode.get("email").asText() != null) {
                    String encryptedEmail = rsaUtils.encrypt(parseInfoNode.get("email").asText());
                    ((ObjectNode)parseInfoNode).put("email",encryptedEmail);
                }
                if(parseInfoNode.has("city") && parseInfoNode.get("city").asText() != null) {
                    String encryptedCity = rsaUtils.encrypt(parseInfoNode.get("city").asText());
                    ((ObjectNode)parseInfoNode).put("city",encryptedCity);
                }
                if(parseInfoNode.has("city_norm") && parseInfoNode.get("city_norm").asText() != null) {
                    String encrypedCityNorm = rsaUtils.encrypt(parseInfoNode.get("city_norm").asText());
                    ((ObjectNode)parseInfoNode).put("city_norm",encrypedCityNorm);
                }

            }catch(Exception e) {
                log.error("加密过程出错，报错信息:"+e.getMessage());
                throw new RuntimeException();
            }

            String idealCity = userMapper.selectIdealCityById(id);
            if(idealCity != null) {
                String[] cityArr = idealCity.split(",");
                List<String> idealCityList = new ArrayList<>();
                for (String i : cityArr) {
                    idealCityList.add(i);
                }
                ((ObjectNode)parseInfoNode).put("ideal_city",idealCityList.toString());
            }
            List<Integer> salary = new ArrayList<>();
            Integer min = userMapper.selectMinSalaryById(id);
            if(min != null) {
                salary.add(min);
            }
            Integer max = userMapper.selectMaxSalaryById(id);
            if(max != null) {
                salary.add(max);
            }
            ((ObjectNode)parseInfoNode).put("salary",salary.toString());

            String encryptedParseInfo = parseInfoNode.toString();
//            FileWriter ft = new FileWriter("new_resume_result.txt");
//            BufferedWriter bf = new BufferedWriter(ft);
//            bf.write(parseInfoNode.toString());
//            bf.close();
//            ft.close();

//            log.info("简历解析完成，测试ML POST接口");


//            调用脚本
            List<PositionWithScore> positionWithScores = mlServiceClient.sendParseAndGetPositions(encryptedParseInfo, id);

//            log.info("分页查询参数：{},{}", pageNum, pageSize);
            pageBean = getPositionsByIds(positionWithScores, pageNum, pageSize);
            if (pageBean == null) {
                stringRedisTemplate.opsForValue().set(key, "", CACHE_NULL_TTL, TimeUnit.MINUTES);
                log.info("ID为" + id + "的求职者未获得职位推荐");
                return null;
            }
            // 将职位推荐结果写入缓存
            stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(pageBean),
                    CACHE_POSITIONS_INFO_TTL, TimeUnit.MINUTES);

            // 异步执行将参数写入user_position
            CompletableFuture.runAsync(() -> {
                userPositionService.saveUserPositions(id, positionWithScores.stream()
                        .map(PositionWithScore::getId)
                        .collect(Collectors.toList()));
                if (userCapacityService.getUserCapacity(id) == null) {
                    userCapacityService.saveUserCapacity(id, parseInfo);
                }
            });

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            unlock(lockKey);
        }
        return pageBean;
    }

    @Override
    public PageBean getPositionByML3(Long id, int pageNum, int pageSize) throws Exception{
        //先查询缓存
        String key = CACHE_POSITIONS_INFO_KEY+id+"_page"+pageNum;//用户对应的职位推荐键
        String positionsJson = stringRedisTemplate.opsForValue().get(key);
        if(StrUtil.isNotBlank(positionsJson)) {
            return JSONUtil.toBean(positionsJson,PageBean.class);
        }
        if(positionsJson != null) {
            log.info("ID为"+id+"的求职者不存在推荐的职位");
        }
//        log.info("未在缓存中查询到推荐信息，转为查询MySQL");
        return queryWithMutex3(id,pageNum,pageSize);
    }

    private PageBean queryWithMutex3 (Long id,int pageNum,int pageSize) {
//        log.info("ID为:{}的用户启用无简历职位推荐",id);
        String key = CACHE_POSITIONS_INFO_KEY + id;
        String lockKey = LOCK_POSITIONS_KEY + id;
        PageBean pageBean = null;
        try {
            boolean isLock = tryLock(lockKey);
            if(!isLock) {
                //未获得同步锁，休眠重试
                Thread.sleep(50);
                //递归查询
                return queryWithMutex3(id,pageNum,pageSize);
            }

            UserNoResume noResumeInfo = userNoResumeMapper.selectById(id);
//            log.info("无简历调用ML脚本，获得推荐职位");


//            调用脚本
            List<PositionWithScore> positionWithScores = mlServiceClient.sendNoResumeInfoAndGetPositions(noResumeInfo,id);
//            log.info("分页查询参数：{},{}",pageNum,pageSize);
            pageBean = getPositionsByIds(positionWithScores,pageNum,pageSize);
            if(pageBean == null) {
                stringRedisTemplate.opsForValue().set(key,"",CACHE_NULL_TTL,TimeUnit.MINUTES);
                log.info("ID为"+id+"的求职者未获得职位推荐");
                return null;
            }
            //将职位推荐结果写入缓存
            stringRedisTemplate.opsForValue().set(key,JSONUtil.toJsonStr(pageBean),
                    CACHE_POSITIONS_INFO_TTL,TimeUnit.MINUTES);

            //异步执行将参数写入user_position
            CompletableFuture.runAsync(() -> {
                userPositionService.saveUserPositions(id, positionWithScores.stream()
                        .map(PositionWithScore::getId)
                        .collect(Collectors.toList()));
//                userCapacityService.saveUserCapacity(id,parseInfo);
//                if(userCapacityService.getUserCapacity(id) == null) {
//                    userCapacityService.saveUserCapacity(id, parseInfo);
//                }
            });

        }catch(Exception e) {
            throw new RuntimeException(e);
        }finally {
            unlock(lockKey);
        }
        return pageBean;
    }
}




