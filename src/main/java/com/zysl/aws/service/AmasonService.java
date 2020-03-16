package com.zysl.aws.service;

import com.zysl.aws.common.result.Result;
import com.zysl.aws.model.*;
import software.amazon.awssdk.services.s3.model.Bucket;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface AmasonService {

    /**
     * 获取存储桶所有文件夹（bucket）的信息
     */
    List<Bucket> getBuckets();

    /**
     * 创建文件夹
     */
    String createBucket(String bucketName, String serviceNo);

    /**
     * 删除文件夹
     */
    Result deleteBucket(String bucketName);

    /**
     * 获取文件夹下所有对象
     */
    List<FileInfo> getFilesByBucket(BucketFileRequest request);

    /**
     * 上传文件到对应路径
     * @param request
     */
    UploadFieResponse uploadFile(UploadFileRequest request);

    UploadFieResponse uploadFile(HttpServletRequest request);

    /**
     * 下载文件
     */
    String downloadFile(HttpServletResponse response, DownloadFileRequest request);

    /**
     * 删除文件
     */
    Result deleteFile(String bucketName, String key);

    /**
     * 查询文件大小
     * @param bucketName
     * @param key
     * @return
     */
    Long getFileSize(String bucketName, String key);

    /**
     * 分享文件
     * @param request
     * @return
     */
    UploadFieResponse shareFile(ShareFileRequest request);

    /**
     * 调用s3接口下载文件内容
     * @param bucketName
     * @param key
     * @return
     */
    String getS3FileInfo(String bucketName, String key, String versionId);

    /**
     * 调用s3接口查询服务器文件大小
     * @param bucketName
     * @param key
     * @return
     */
    Long getS3FileSize(String bucketName, String key);

    /**
     * 调用s3接口上传文件
     * @param bucketName
     * @param fileId
     * @param data
     * @return
     */
    PutObjectResponse upload(String bucketName, String fileId, byte[] data);

    /**
     *
     * @param bucketName
     * @param key
     */
    List<FileVersionResponse> getS3FileVersion(String bucketName, String key);

    /**
     * 设置文件版本权限
     * @return
     */
    Integer setFileVersion(SetFileVersionRequest request);

    void createFolder();
}
