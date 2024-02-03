package com.group;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@MapperScan("com.group.mapper")
@ServletComponentScan //打开对jwt令牌的支持
public class JobRecommendationApplication {

    public static void main(String[] args) {
        SpringApplication.run(JobRecommendationApplication.class, args);
    }

}
