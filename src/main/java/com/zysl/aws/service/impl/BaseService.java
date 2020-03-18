package com.zysl.aws.service.impl;

import com.zysl.aws.model.db.S3Folder;
import com.zysl.aws.service.FileService;
import com.zysl.aws.utils.S3ClientFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.S3Client;

import java.util.List;

@Component
public class BaseService {

    @Autowired
    private S3ClientFactory s3ClientFactory;
    @Autowired
    private FileService fileService;

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
     * 判断文件夹是否存在
     * @param bucketName
     * @return
     */
    public S3Folder doesBucketExist(String bucketName ){
        List<S3Folder> folderList = fileService.queryS3FolderInfo();
        for (S3Folder obj : folderList) {
            if(bucketName.equals(obj.getFolderName())){
                return obj;
            }
        }
        return null;
    }
}
