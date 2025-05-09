package com.group.config;

import com.group.filter.LoginAndRegisterCheckFilter;
import com.group.interceptor.CorsInterceptor;
import com.group.interceptor.LoginAndRegisterCheckInterceptor;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Author: mfz
 * @Date: 2024/04/10/18:45
 * @Description:
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Resource
    private LoginAndRegisterCheckInterceptor loginAndRegisterCheckInterceptor;

    @Resource
    private CorsInterceptor corsInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 跨域拦截器需放在最上面
        registry.addInterceptor(corsInterceptor).addPathPatterns("/**");
        // 校验token的过滤器
        registry.addInterceptor(loginAndRegisterCheckInterceptor).addPathPatterns("/**");
    }
}
