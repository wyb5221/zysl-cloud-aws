package com.zysl.aws.utils;

import com.zysl.aws.exception.HostNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.internal.http.loader.DefaultSdkHttpClientBuilder;
import software.amazon.awssdk.http.SdkHttpConfigurationOption;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.utils.AttributeMap;

import javax.annotation.PostConstruct;
import java.net.URI;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import software.amazon.awssdk.utils.StringUtils;

@Component
@Slf4j
public class S3ClientFactory {

    @Autowired
    private MyConfig myConfig;

    //s3服务器连接信息
    private static Map<String, S3Client> awsMap = new HashMap<>();
    //s3服务器文件夹信息
    private static Map<String, String> folderMap = new HashMap<>();

    /**
     * 初始化s3连接
     * @param
     * @return
     * @throws Exception
     */
    @PostConstruct
    public void amazonS3ClientInit() {
        DefaultSdkHttpClientBuilder defaultSdkHttpClientBuilder = new DefaultSdkHttpClientBuilder();
        AttributeMap attributeMap = AttributeMap.builder()
                .put(SdkHttpConfigurationOption.TRUST_ALL_CERTIFICATES, true)
                .put(SdkHttpConfigurationOption.WRITE_TIMEOUT, Duration.ofSeconds(300))
                .put(SdkHttpConfigurationOption.READ_TIMEOUT, Duration.ofSeconds(300))
                .put(SdkHttpConfigurationOption.CONNECTION_TIMEOUT, Duration.ofSeconds(10))
                .put(SdkHttpConfigurationOption.CONNECTION_MAX_IDLE_TIMEOUT, Duration.ofSeconds(300))
                .build();

        //获取配置信息
        List<Map<String, Object>> listProps = myConfig.getListProps();
        for (int i = 0; i < listProps.size(); i++) {
            Map<String, Object> map = listProps.get(i);
            //服务器别名
            String h_name = map.get("hostName").toString();
            //s3服务器连接登陆用户、密码、地址
            String accessKey = map.get("accessKey").toString();
            String secretKey = map.get("secretKey").toString();
            String endpoint = map.get("endpoint").toString();
            //s3服务器文件夹名称
            String[] p_value = map.get("folderName").toString().split(",");

            for (int j = 0; j < p_value.length; j++) {
                folderMap.put(p_value[j], h_name);
            }

            //初始化s3连接
            AwsBasicCredentials awsCreds = AwsBasicCredentials.create(accessKey, secretKey);
            S3Client s3Client = S3Client.builder().
                    httpClient(defaultSdkHttpClientBuilder.buildWithDefaults(attributeMap)).
                    credentialsProvider(StaticCredentialsProvider.create(awsCreds)).
                    endpointOverride(URI.create(endpoint)).
                    region(Region.US_EAST_1).
                    build();
            awsMap.put(h_name, s3Client);
        }
    }

    public S3Client getS3Client(String folderName) {
        String hostName = folderMap.get(folderName);
        if(StringUtils.isEmpty(hostName) || !awsMap.containsKey(hostName)){
            log.warn("===getS3Client===找不到对应的s3_server:{}",folderName);
            throw new HostNotFoundException("找不到对应的s3_server:" + folderName);
        }

        return  awsMap.get(folderName);

    }

}
