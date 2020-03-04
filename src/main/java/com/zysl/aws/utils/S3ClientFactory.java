package com.zysl.aws.utils;

import com.zysl.aws.common.exception.HostNotFoundException;
import com.zysl.aws.model.db.S3Folder;
import com.zysl.aws.model.db.S3Service;
import com.zysl.aws.service.FileService;
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

@Component
@Slf4j
public class S3ClientFactory {

    @Autowired
    private FileService fileService;

    //s3服务器连接信息
    private static Map<String, S3Client> awsMap = new HashMap<>();

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
                .put(SdkHttpConfigurationOption.WRITE_TIMEOUT, Duration.ofSeconds(300))//写入超时
                .put(SdkHttpConfigurationOption.READ_TIMEOUT, Duration.ofSeconds(300))//读取超时
                .put(SdkHttpConfigurationOption.CONNECTION_TIMEOUT, Duration.ofSeconds(10))//连接超时
                .put(SdkHttpConfigurationOption.CONNECTION_MAX_IDLE_TIMEOUT, Duration.ofSeconds(300))//连接最大空闲超时
                .build();

        List<S3Service> serviceList = fileService.queryS3Service();
        for (int i = 0; i < serviceList.size(); i++) {
            S3Service s3Service = serviceList.get(i);
            //s3服务器连接登陆用户、密码
            String accessKey = s3Service.getAccesskey();
            String secretKey = s3Service.getSecretkey();
            //s3服务器连接登陆地址
            String endpoint = s3Service.getEndpoint();

            //初始化s3连接
            AwsBasicCredentials awsCreds = AwsBasicCredentials.create(accessKey, secretKey);
            S3Client s3Client = S3Client.builder().
                    httpClient(defaultSdkHttpClientBuilder.buildWithDefaults(attributeMap)).
                    credentialsProvider(StaticCredentialsProvider.create(awsCreds)).
                    endpointOverride(URI.create(endpoint)).
                    region(Region.US_EAST_1).
                    build();
            awsMap.put(s3Service.getServiceNo(), s3Client);
        }
    }

    /**
     * 根据文件夹名称获取服务器编号
     * @param folderName
     * @return
     */
    public String getServerNo(String folderName) {
        log.info("---getServerNo---folderName：{}", folderName);

        S3Folder s3Folder = fileService.queryS3Folder(folderName);
        if(null == s3Folder){
            log.warn("===getS3Client===找不到对应的s3_server:{}",folderName);
            throw new HostNotFoundException("找不到对应的s3_server:" + folderName);
        }
        return s3Folder.getServiceNo();
    }

    /**
     * 根据服务器编号获取服务器初始化对象
     * @param serverNo
     * @return
     */
    public S3Client getS3Client(String serverNo) {
        log.info("---getS3Client入参---serverNo：{}", serverNo);
        return  awsMap.get(serverNo);
    }

}
