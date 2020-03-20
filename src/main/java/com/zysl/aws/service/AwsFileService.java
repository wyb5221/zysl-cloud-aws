package com.zysl.aws.service;

import com.zysl.aws.model.*;
import software.amazon.awssdk.services.s3.model.CopyObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface AwsFileService {

    /**
     * 上传文件到对应路径
     * @param request
     */
    UploadFieResponse uploadFile(UploadFileRequest request);

    /**
     * 文件流上传
     * @param request
     * @return
     */
    UploadFieResponse uploadFile(HttpServletRequest request);

    /**
     * 下载文件
     */
    String downloadFile(DownloadFileRequest request);

    /**
     * 分享文件下载
     */
    String shareDownloadFile(HttpServletResponse response, DownloadFileRequest request);

    /**
     * 查询文件大小
     * @param bucketName
     * @param key
     * @return
     */
//    Long getFileSize(String bucketName, String key);

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
    Long getS3FileSize(String bucketName, String key, String versionId);

    /**
     * 调用s3接口上传文件
     * @param bucketName
     * @param fileId
     * @param data
     * @return
     */
    PutObjectResponse upload(String bucketName, String fileId, byte[] data);

    /**
     * 调用s3接口查询文件版本列表
     * @param bucketName
     * @param key
     */
    List<FileVersionResponse> getS3FileVersion(String bucketName, String key);

    /**
     * 删除文件
     */
    boolean deleteFile(DelObjectRequest request);

    /**
     * 文件复制
     * @param request
     */
    CopyObjectResponse copyFile(CopyFileRequest request);

    /**
     * 文件复制
     * @param request
     */
    boolean moveFile(CopyFileRequest request);


    /**
     * 还原已删除对象
     * @param request
     */
    void restoreObject(ResObjectRequest request);

}
