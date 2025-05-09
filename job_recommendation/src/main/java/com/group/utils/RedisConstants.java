package com.group.utils;

/**
 * @Author: mfz
 * @Date: 2024/02/26/21:35
 * @Description: Redis static常量数据保存
 */
public class RedisConstants {
    public static final String CACHE_USER_INFO_KEY = "cache:user:";
    public static final String CACHE_EMPLOYER_INFO_KEY = "cache:employer:";
    public static final String CACHE_POSITIONS_INFO_KEY = "cache:positions:";
    public static final Long CACHE_NULL_TTL = 2L;
    public static final Long CACHE_USER_INFO_TTL = 120L;
    public static final Long CACHE_EMPLOYER_INFO_TTL = 120L;
    public static final Long CACHE_POSITIONS_INFO_TTL = 30L;
    public static final String LOCK_USER_KEY = "lock:user:";
    public static final String LOCK_EMPLOYER_KEY = "lock:employer:";
    public static final String LOCK_POSITIONS_KEY = "lock:employer:";
    public static final Long LOCK_USER_TTL = 10L;

}
