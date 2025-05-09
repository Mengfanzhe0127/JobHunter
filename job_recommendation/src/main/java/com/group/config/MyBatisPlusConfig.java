package com.group.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.group.utils.UserPositionsInjector;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 拦截器分页插件
 */
//自定义构造类
@Configuration
@MapperScan("com.group.mapper")
public class MyBatisPlusConfig {

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
     MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
     //添加分页插件
     interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
     //添加乐观锁插件
     interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
     return interceptor;
    }

//    public UserPositionsInjector userPositionsInjector() {
//        return new UserPositionsInjector();
//    }
}
