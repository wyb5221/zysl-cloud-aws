package com.zysl.aws.web.utils;

import com.zysl.cloud.aws.biz.service.IS3FactoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.S3Client;

@Component
@Slf4j
public class S3ClientFactory {

    @Autowired
    private IS3FactoryService factoryService;
    /**
     * 根据文件夹名称获取服务器编号
     * @param folderName
     * @return
     */
    public String getServerNo(String folderName) {
        log.info("---getServerNo---folderName：{}", folderName);

        return factoryService.getServerNo(folderName);
    }

    /**
     * 根据服务器编号获取服务器初始化对象
     * @param serverNo
     * @return
     */
    public S3Client getS3Client(String serverNo) {
        return factoryService.getS3ClientByServerNo(serverNo);
    }

}
