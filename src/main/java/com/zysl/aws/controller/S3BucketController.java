package com.zysl.aws.controller;

import com.zysl.aws.model.FileInfo;
import com.zysl.aws.model.SetFileVersionRequest;
import com.zysl.aws.service.AmasonService;
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
 * 文件夹处理controller
 */
@CrossOrigin
@RestController
@RequestMapping("/aws/bucket")
@Slf4j
public class S3BucketController {

    @Autowired
    private AmasonService amasonService;


    /**
     * 创建文件夹
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
            String result = amasonService.createBucket(bucketName, serviceNo);
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
     * 设置文件夹的版本控制权限
     * @param bucketName
     * @param status
     * @return
     */
    @PostMapping("/setVersion")
    public BaseResponse<String> updatFileVersion(@RequestBody SetFileVersionRequest request){
        log.info("--updatFileVersion设置文件夹的版本控制权限--request:{}", request);
        BaseResponse<String> baseResponse = new BaseResponse<>();

        Integer code = amasonService.setFileVersion(request);
        if(RespCodeEnum.SUCCESS.getCode().equals(code)){
            baseResponse.setSuccess(true);
        }else{
            baseResponse.setMsg("文件夹版本权限设置失败");
            baseResponse.setCode(code);
        }
        return baseResponse;
    }

    /**
     * 获取文件夹下所有对象
     * @return
     */
    @PostMapping("/getFilesByBucket")
    public BasePaginationResponse<FileInfo> getFilesByBucket(String bucketName){
        log.info("--开始调用getFilesByBucket获取文件夹下所有对象接口--bucketName:{}", bucketName);
        BasePaginationResponse baseResponse = new BasePaginationResponse<>();
        List<FileInfo> fileList = amasonService.getFilesByBucket(bucketName);
        baseResponse.setSuccess(true);
        baseResponse.setModelList(fileList);
        return baseResponse;
    }

}
