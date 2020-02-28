package com.zysl.aws;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//@EnableEurekaClient
@SpringBootApplication
@MapperScan("com.zysl.aws.mapper")
public class ZyslAwsApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZyslAwsApplication.class, args);
    }

}
