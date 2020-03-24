package com.zysl.aws.web;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@EnableEurekaClient
@SpringBootApplication
@MapperScan("com.zysl.aws.web.mapper")
public class ZyslAwsApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZyslAwsApplication.class, args);
    }

}
