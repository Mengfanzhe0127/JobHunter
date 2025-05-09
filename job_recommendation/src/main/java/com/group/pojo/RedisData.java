package com.group.pojo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @Author: mfz
 * @Date: 2024/03/27/18:22
 * @Description: 热点数据缓存实现类
 * ! 装饰器
 */
@Data
public class RedisData {
    private LocalDateTime expireTime;
    private Object data;
}
