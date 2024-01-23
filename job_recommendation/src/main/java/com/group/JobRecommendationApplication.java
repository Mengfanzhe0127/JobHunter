package com.group;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.group.mapper")
public class JobRecommendationApplication {

    public static void main(String[] args) {
        SpringApplication.run(JobRecommendationApplication.class, args);
    }

}
