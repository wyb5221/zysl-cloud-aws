package com.zysl.aws.service;

import com.zysl.aws.model.BucketFileRequest;
import com.zysl.aws.model.FileInfo;
import com.zysl.aws.model.SetFileVersionRequest;
import software.amazon.awssdk.services.s3.model.Bucket;

import java.util.List;

public interface AwsBucketService {

    /**
     * 创建存储桶bucket
     */
    String createBucket(String bucketName, String serviceNo);

    /**
     * 查询bucket下所有对象
     * @param request
     * @return
     */
    List<FileInfo> getFilesByBucket(BucketFileRequest request);

    /**
     * 设置文件版本权限
     * @return
     */
    Integer setFileVersion(SetFileVersionRequest request);

    /**
     * 获取所有存储桶（bucket）的信息
     */
    List<Bucket> getBuckets();



    void copyObject();
}
