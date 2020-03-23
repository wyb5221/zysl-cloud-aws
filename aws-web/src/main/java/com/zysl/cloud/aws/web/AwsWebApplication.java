package com.zysl.cloud.aws.web;

import com.zysl.cloud.utils.SpringContextUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@EnableEurekaClient
@Import(SpringContextUtil.class)
@SpringBootApplication
@ComponentScan("com.zysl.cloud")
public class AwsWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(AwsWebApplication.class, args);
    }

}
