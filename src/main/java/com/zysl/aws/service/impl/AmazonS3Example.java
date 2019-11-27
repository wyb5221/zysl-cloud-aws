package com.zysl.aws.service.impl;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.List;

public class AmazonS3Example {

    private static final String SUFFIX = "/";

    public static void main(String[] args) {

        //用于标识用户身份验证的凭据对象
        //用户必须具有AWSConnector和AmazonS3FullAccess
        AWSCredentials credentials = new BasicAWSCredentials(
                "your accesskey","your secretkey");

        //基于凭据创建客户端连接
        AmazonS3 s3client = new AmazonS3Client(credentials);

        // create bucket-对于所有S3用户，名称必须是唯一的
        String bucketName = "javatutorial-net-example-bucket";
        s3client.createBucket(bucketName);

        // 列出存储桶
        for (Bucket bucket : s3client.listBuckets()) {
            System.out.println(" - " + bucket.getName());
        }

        // 在bucket中创建文件夹
        String folderName = "testfolder";
//        createFolder(bucketName, folderName, s3client);

        // 将文件上载到文件夹并将其设置为公用
        String fileName = folderName + SUFFIX + "testvideo.mp4";
        s3client.putObject(new PutObjectRequest(bucketName, fileName,
                new File("D:\\tmp\\S3\\test.txt"))
                .withCannedAcl(CannedAccessControlList.PublicRead));

//        deleteFolder(bucketName, folderName, s3client);

        // 删除存储桶
//        s3client.deleteBucket(bucketName);
    }

    public static void createFolder(String bucketName, String folderName, AmazonS3 client) {
        // 为文件夹创建元数据并将内容长度设置为0
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(0);
        // 创建空内容
        InputStream emptyContent = new ByteArrayInputStream(new byte[0]);
        // 创建PutObjectRequest
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName,
                folderName + SUFFIX, emptyContent, metadata);
        // 向S3发送创建文件夹的请求
        client.putObject(putObjectRequest);
    }

    /**
     * 此方法首先删除给定文件夹中的所有文件，然后文件夹本身
     *
     */
    public static void deleteFolder(String bucketName, String folderName, AmazonS3 client) {
        List<S3ObjectSummary> fileList =
                client.listObjects(bucketName, folderName).getObjectSummaries();
        for (S3ObjectSummary file : fileList) {
            client.deleteObject(bucketName, file.getKey());
        }
        client.deleteObject(bucketName, folderName);
    }

}
