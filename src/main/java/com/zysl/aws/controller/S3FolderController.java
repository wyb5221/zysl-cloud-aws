package com.zysl.aws.controller;

import com.zysl.aws.model.CreateFolderRequest;
import com.zysl.aws.service.AwsFolderService;
import com.zysl.cloud.utils.StringUtils;
import com.zysl.cloud.utils.common.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

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
     * 删除bucket下的对象
     * @param bucketName
     * @param key
     * @return
     */
    @GetMapping("/deleteFile")
    public BaseResponse<String> deleteFile(String bucketName, String key){
        log.info("--getFileVersion获取文件版本信息--bucketName:{},key:{}", bucketName, key);
        BaseResponse<String> baseResponse = new BaseResponse<String>();
        List<String> validations = new ArrayList<>();
        if(StringUtils.isBlank(bucketName)){
            validations.add("bucketName不能为空！");
        }
        if(StringUtils.isBlank(key)){
            validations.add("key不能为空！");
        }
        //入参校验不通过
        if(!CollectionUtils.isEmpty(validations)){
            baseResponse.setSuccess(false);
            baseResponse.setValidations(validations);
            return baseResponse;
        }

        boolean flag = folderService.deleteFolder(bucketName, key);
        baseResponse.setSuccess(flag);
        return baseResponse;

    }

}
