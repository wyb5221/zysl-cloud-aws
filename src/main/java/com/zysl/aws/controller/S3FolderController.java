package com.zysl.aws.controller;

import com.zysl.aws.model.*;
import com.zysl.aws.service.AwsFolderService;
import com.zysl.cloud.utils.StringUtils;
import com.zysl.cloud.utils.common.BasePaginationResponse;
import com.zysl.cloud.utils.common.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import software.amazon.awssdk.services.s3.model.CopyObjectResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * 目录处理类
 */
@CrossOrigin
@RestController
@RequestMapping("/aws/folder")
@Slf4j
public class S3FolderController {

    @Autowired
    private AwsFolderService folderService;

    /**
     * 创建目标
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<String> createFolder(@RequestBody CreateFolderRequest request){
        log.info("--开始调用createFolder创建文件夹接口--request:{}", request);
        BaseResponse<String> baseResponse = new BaseResponse<String>();
        List<String> validations = new ArrayList<>();
        if(StringUtils.isBlank(request.getBucketName())){
            validations.add("bucketName不能为空！");
        }
        if(StringUtils.isBlank(request.getFolderName())){
            validations.add("folderName不能为空！");
        }
        //入参校验不通过
        if(!CollectionUtils.isEmpty(validations)){
            baseResponse.setSuccess(false);
            baseResponse.setMsg("入参校验失败");
            baseResponse.setValidations(validations);
            return baseResponse;
        }

        boolean flag = folderService.createFolder(request);
        if(flag){
            baseResponse.setSuccess(true);
        }else{
            baseResponse.setSuccess(false);
            baseResponse.setMsg("文件夹创建失败");
        }
        return baseResponse;
    }

    /**
     * 删除目录
     * @param bucketName
     * @param key
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<String> deleteFile(@RequestBody DelObjectRequest request){
        log.info("--deleteFile删除目录--request:{}", request);
        BaseResponse<String> baseResponse = new BaseResponse<>();
        List<String> validations = new ArrayList<>();
        if(StringUtils.isBlank(request.getBucketName())){
            validations.add("bucketName不能为空！");
        }
        if(StringUtils.isBlank(request.getKey())){
            validations.add("key不能为空！");
        }
        //入参校验不通过
        if(!CollectionUtils.isEmpty(validations)){
            baseResponse.setSuccess(false);
            baseResponse.setMsg("入参校验失败");
            baseResponse.setValidations(validations);
            return baseResponse;
        }

        boolean flag = folderService.deleteFolder(request);
        baseResponse.setSuccess(flag);
        return baseResponse;
    }

    /**
     * 查询目录对象列表
     * @return
     */
    @PostMapping("/objects")
    public BasePaginationResponse<FileInfo> getS3Objects(@RequestBody QueryObjectsRequest request){
        log.info("--getS3Objects查询目录对象列表:request:{}", request);
        BasePaginationResponse<FileInfo> baseResponse = new BasePaginationResponse<>();
        List<String> validations = new ArrayList<>();
        if(StringUtils.isBlank(request.getBucketName())){
            validations.add("bucketName不能为空！");
        }
        //入参校验不通过
        if(!CollectionUtils.isEmpty(validations)){
            baseResponse.setSuccess(false);
            baseResponse.setMsg("入参校验失败");
            baseResponse.setValidations(validations);
            return baseResponse;
        }


        List<FileInfo> objects = folderService.getS3FileList(request);
        baseResponse.setSuccess(true);
        baseResponse.setModelList(objects);
        return baseResponse;
    }

    /**
     * 目录复制
     * @param request
     * @return
     */
    @PostMapping("/copy")
    public BaseResponse<String> copyFolder(@RequestBody CopyFileRequest request){
        log.info("--copyFolder 文件复制--request:{}", request);
        BaseResponse<String> baseResponse = new BaseResponse<>();
        //入参校验
        List<String> validations = new ArrayList<>();
        if(StringUtils.isBlank(request.getSourceBucket())){
            validations.add("sourceBucket不能为空！");
        }
        if(StringUtils.isBlank(request.getSourceKey())){
            validations.add("sourceKey不能为空！");
        }
        if(StringUtils.isBlank(request.getDestBucket())){
            validations.add("destBucket不能为空！");
        }
        if(StringUtils.isBlank(request.getDestKey())){
            validations.add("destKey不能为空！");
        }
        //入参校验不通过
        if(!CollectionUtils.isEmpty(validations)){
            baseResponse.setSuccess(false);
            baseResponse.setValidations(validations);
            return baseResponse;
        }

        CopyObjectResponse response = folderService.copyFolder(request);
        if(null != response){
            baseResponse.setSuccess(true);
            baseResponse.setMsg("目录复制成功！");
            return baseResponse;
        }else{
            baseResponse.setSuccess(false);
            baseResponse.setMsg("目录复制失败！");
            return baseResponse;
        }
    }
}
