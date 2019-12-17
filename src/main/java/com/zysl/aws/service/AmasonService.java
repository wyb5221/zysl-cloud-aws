package com.zysl.aws.service;

import com.zysl.aws.common.result.Result;
import com.zysl.aws.model.UploadFileRequest;
import software.amazon.awssdk.services.s3.model.Bucket;

import java.util.List;
import java.util.Optional;

public interface AmasonService {

    /**
     * 获取存储桶所有文件夹（bucket）的信息
     */
    List<Bucket> getBuckets();

    /**
     * 创建文件夹
     */
    Bucket createBucket(String bucketName);

    /**
     * 删除文件夹
     */
    Result deleteBucket(String bucketName);

    /**
     * 获取文件夹下所有对象
     */
    Result getFilesByBucket(String bucketName);

    /**
     * 上传文件到对应路径
     * @param request
     */
    Result uploadFile(UploadFileRequest request);

    /**
     * 下载文件
     */
    Result downloadFile(String bucketName, String key);

    /**
     * 删除文件
     */
    Result deleteFile(String bucketName, String key);

    /**
     * 获取存储桶
     * @param bucket_name
     * @return
     */
    Optional<Bucket> getBucket(String bucket_name);


}
