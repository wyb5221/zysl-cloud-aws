package com.zysl.aws.web.controller;

import com.zysl.aws.web.model.BucketFileRequest;
import com.zysl.aws.web.model.FileInfo;
import com.zysl.aws.web.model.SetFileVersionRequest;
import com.zysl.aws.web.service.AwsBucketService;
import com.zysl.cloud.utils.StringUtils;
import com.zysl.cloud.utils.common.BasePaginationResponse;
import com.zysl.cloud.utils.common.BaseResponse;
import com.zysl.cloud.utils.enums.RespCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.regex.Pattern;

/**
 * bucket处理controller
 */
@CrossOrigin
@RestController
@RequestMapping("/aws/bucket")
@Slf4j
public class S3BucketController {

    @Autowired
    private AwsBucketService bucketService;


    /**
     * 创建存储桶bucket
     * @param bucketName
     * @return
     */
    @GetMapping("/createBucket")
    public BaseResponse<String> createBucket(String bucketName, String serviceNo){
        log.info("--开始调用createBucket创建文件夹接口--bucketName:{},serviceNo:{}",
                bucketName, serviceNo);
        BaseResponse<String> baseResponse = new BaseResponse<>();
        baseResponse.setSuccess(false);

        String pattern = "^[a-zA-Z0-9.\\-_]{3,60}$";
        //判断存储桶是否满足命名规则
        if(Pattern.compile(pattern).matcher(bucketName).matches()){
            String result = bucketService.createBucket(bucketName, serviceNo);
            if(StringUtils.isBlank(result)){
                //接口返回为空，文件夹创建失败
                baseResponse.setMsg("文件夹创建失败");
            }else{
                baseResponse.setSuccess(true);
                baseResponse.setModel(result);
            }
        }else{
            baseResponse.setCode(RespCodeEnum.ILLEGAL_PARAMETER.getCode());
            baseResponse.setMsg("文件夹名称不满足命名规则");
        }
        return baseResponse;
    }

    /**
     * 获取buckrt下所有对象
     * @return
     */
    @PostMapping("/getFilesByBucket")
    public BasePaginationResponse<FileInfo> getFilesByBucket(@RequestBody BucketFileRequest request){
        log.info("--开始调用getFilesByBucket获取文件夹下所有对象接口--request:{}", request);
        BasePaginationResponse baseResponse = new BasePaginationResponse<>();
        List<FileInfo> fileList = bucketService.getFilesByBucket(request);
        baseResponse.setSuccess(true);
        baseResponse.setModelList(fileList);
        return baseResponse;
    }

    /**
     * 设置文件夹的版本控制权限
     * @param bucketName
     * @param status
     * @return
     */
    @PostMapping("/setVersion")
    public BaseResponse<String> updatFileVersion(@RequestBody SetFileVersionRequest request){
        log.info("--updatFileVersion设置文件夹的版本控制权限--request:{}", request);
        BaseResponse<String> baseResponse = new BaseResponse<>();

        Integer code = bucketService.setFileVersion(request);
        if(RespCodeEnum.SUCCESS.getCode().equals(code)){
            baseResponse.setSuccess(true);
        }else{
            baseResponse.setMsg("文件夹版本权限设置失败");
            baseResponse.setCode(code);
        }
        return baseResponse;
    }

    /**
     * 查询所有bucket列表
     * @param bucketName
     * @param serviceNo
     * @return
     */
    @GetMapping("/getBuckets")
    public BasePaginationResponse<String> getBuckets(String serviceNo){
        log.info("--getBuckets查询所有bucket列表 serviceNo:{}", serviceNo);
        BasePaginationResponse<String> response = new BasePaginationResponse<>();
        List<String> bucketList = bucketService.getBuckets(serviceNo);
        response.setSuccess(true);
        response.setModelList(bucketList);

        return response;
    }

}
