package com.zysl.aws.web;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.google.common.collect.Lists;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;

import java.util.List;

//@EnableEurekaClient
//@SpringBootApplication
//@MapperScan("com.zysl.aws.web.mapper")
public class ZyslAwsApplication {

//    public static void main(String[] args) {
//        SpringApplication.run(ZyslAwsApplication.class, args);
//    }


    @Bean
    public HttpMessageConverters fastJsonHttpMessageConverters(){
        //1.创建信息转换对象
        FastJsonHttpMessageConverter fastConverter;
        fastConverter = new FastJsonHttpMessageConverter();
        //2.创建config对象并设置序列化规则
        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        fastJsonConfig.setSerializerFeatures(SerializerFeature.SkipTransientField,
                SerializerFeature.WriteNullStringAsEmpty,
                SerializerFeature.WriteMapNullValue,SerializerFeature.WriteNullListAsEmpty);
        //中文乱码解决
        List<MediaType> fastMediaTypes = Lists.newArrayList();
        fastMediaTypes.add(MediaType.APPLICATION_JSON_UTF8);
        fastConverter.setSupportedMediaTypes(fastMediaTypes);
        //4.将转换规则应用于转换对象
        fastConverter.setFastJsonConfig(fastJsonConfig);

        return  new HttpMessageConverters(fastConverter);
    }

}
