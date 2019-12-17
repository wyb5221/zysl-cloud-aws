package com.zysl.aws.controller;

import com.zysl.aws.common.result.CodeMsg;
import com.zysl.aws.common.result.Result;
import com.zysl.aws.model.BucketResponse;
import com.zysl.aws.model.UploadFileRequest;
import com.zysl.aws.service.AmasonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import software.amazon.awssdk.services.s3.model.Bucket;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/aws/s3")
@Slf4j
public class AmazonController {

    @Autowired
    private AmasonService amasonService;


    /**
     * 获取所有文件夹（bucket）的信息
     * @return
     */
    @GetMapping("/buckets")
    public Result getBuckets(){
        log.info("--开始调用queryBuckets查询获取所有文件夹（bucket）的信息接口--");
        List<Bucket> bucketList = amasonService.getBuckets();
        List<BucketResponse> result = new ArrayList<>();
        for (Bucket bucket : bucketList) {
            BucketResponse bucketResponse = new BucketResponse();
            bucketResponse.setName(bucket.name());
            bucketResponse.setCreationDate(bucket.creationDate());
            result.add(bucketResponse);
        }

        return Result.success(result);
    }

    /**
     * 创建文件夹
     * @param bucketName
     * @return
     */
    @GetMapping("/createBucket")
    public Result createBucket(String bucketName){
        log.info("--开始调用createBucket创建文件夹接口--bucketName:{}", bucketName);
        String pattern = "^[a-zA-Z0-9.\\-_]{3,60}$";
        //判断存储桶是否满足命名规则
        if(Pattern.compile(pattern).matcher(bucketName).matches()){
            Bucket bucket = amasonService.createBucket(bucketName);
            return Result.success(bucket);
        }else{
            return Result.error(CodeMsg.BIND_ERROR);
        }
    }

    /**
     * 删除文件夹
     * @param bucketName
     * @return
     */
    @GetMapping("/deleteBucket")
    public Result deleteBucket(String bucketName){
        log.info("--开始调用deleteBucket删除文件夹接口--bucketName:{}", bucketName);
        amasonService.deleteBucket(bucketName);
        return Result.success();
    }

    /**
     * 获取文件夹下所有对象
     * @return
     */
    @GetMapping("/getFilesByBucket")
    public Result getFilesByBucket(String bucketName){
        log.info("--开始调用getFilesByBucket获取文件夹下所有对象接口--bucketName:{}", bucketName);
        return amasonService.getFilesByBucket(bucketName);
    }

    /**
     * 上传文件
     * @param request
     * @return
     */
    @PostMapping("/uploadFile")
    public Result uploadFile(@RequestBody UploadFileRequest request){
        log.info("--开始调用uploadFile上传文件接口--");
        return  amasonService.uploadFile(request);
    }

    /**
     * 下载文件
     * @param bucketName
     * @param fileId
     * @return
     */
    @GetMapping("/downloadFile")
    public Result downloadFile(String bucketName, String fileId){
        log.info("--开始调用uploadFile上传文件接口--bucketName:{},fileId:{}", bucketName, fileId);
        return amasonService.downloadFile(bucketName, fileId);
    }

    /**
     * 删除文件
     * @param bucketName
     * @param fileName
     * @return
     */
    @GetMapping("/deleteFile")
    public Result deleteFile(String bucketName, String fileName){
        log.info("--开始调用uploadFile删除文件接口--bucketName:{},fileName:{}", bucketName, fileName);
        amasonService.deleteFile(bucketName, fileName);
        return Result.success();
    }

}
