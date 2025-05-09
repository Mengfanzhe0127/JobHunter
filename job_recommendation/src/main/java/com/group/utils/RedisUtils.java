package com.group.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @Author: mfz
 * @Date: 2024/02/24/22:29
 * @Description: Redis工具类
 * 令牌以用户ID作为主键
 */

@Component
public class RedisUtils {
    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    /**
     * 令牌过期时间
     */
    private static final long TOKEN_EXPIRE = 1800000L;

    /**
     * 存储令牌
     * @param id
     * @param token
     */
    public void setToken(String id, String token) {
        //使用用户ID作为键，令牌作为值
        redisTemplate.opsForValue().set(id, token);
        //设置过期时间
        redisTemplate.expire(id, TOKEN_EXPIRE, TimeUnit.SECONDS);
    }

    /**
     * 获取令牌
     * @param id
     * @return
     */
    public String getToken(String id) {
        //根据用户ID获取令牌
        Object token = redisTemplate.opsForValue().get(id);
        //如果令牌存在，返回字符串类型，否则返回null
        return token == null ? null : token.toString();
    }

    /**
     * 删除令牌
     * @param id
     */
    public void deleteToken(String id) {
        //根据用户ID删除令牌
        redisTemplate.delete(id);
    }

    /**
     * 更新令牌
     * @param id
     * @param token
     */
    public void updateToken(String id, String token) {
        //先删除旧的令牌
        deleteToken(id);
        //再存储新的令牌
        setToken(id, token);
    }
}
