package com.group.Component;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.net.NetUtil;
import cn.hutool.core.util.IdUtil;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class IDGeneratorSnowFlake {
    private long workerId =0;

    private long dataCenterId =1;

    private Snowflake snowflake = IdUtil.createSnowflake(workerId,dataCenterId);


    /**
     * 构造之后初始化加载
     */
    @PostConstruct
    public void init(){

        try {

            workerId = NetUtil.ipv4ToLong(NetUtil.getLocalhostStr());
            log.info("当前机器的workerId：{}",workerId);
        }catch (Exception e){
            log.warn("当前机器的workerId获取失败：{}",e);
            workerId = NetUtil.getLocalhostStr().hashCode();
        }
    }
    public synchronized long snowFlakeId(){
        long snowFlakeId = snowflake.nextId();
        log.info("snowFlakeId:{}",snowFlakeId);
        return snowFlakeId;
    }

    public synchronized long snowFlakeId(long workerId, long datacenterId){
        Snowflake snowflake = IdUtil.createSnowflake(workerId,datacenterId);
        long snowFlakeId = snowflake.nextId();
        log.info("snowFlakeId:{}",snowFlakeId);
        return snowFlakeId;
    }
}
