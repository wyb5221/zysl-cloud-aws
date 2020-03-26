package com.zysl.aws.web.service.impl;

import com.zysl.aws.web.model.db.S3Folder;
import com.zysl.aws.web.utils.S3ClientFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.S3Client;

import java.util.List;

@Component
public class BaseService {

    @Autowired
    private S3ClientFactory s3ClientFactory;

    /**
     * 根据bucketName获取serviceNo
     * @param bucketName
     * @return
     */
    public String getServiceNo(String bucketName){
        return s3ClientFactory.getServerNo(bucketName);
    }

    /**
     * 根据serviceNo获取s3连接
     * @param serviceNo
     * @return
     */
    public S3Client getS3Client(String serviceNo){
        return s3ClientFactory.getS3Client(serviceNo);
    }

    /**
     * 判断bucket是否存在
     * @param bucketName
     * @return
     */
    public S3Folder doesBucketExist(String bucketName ){

        return null;
    }
}
