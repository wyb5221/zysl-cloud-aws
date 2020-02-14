package com.zysl.aws.controller;

import com.zysl.aws.common.result.CodeMsg;
import com.zysl.aws.common.result.Result;
import com.zysl.aws.enums.DownTypeEnum;
import com.zysl.aws.model.BucketResponse;
import com.zysl.aws.model.ShareFileRequest;
import com.zysl.aws.model.UploadFileRequest;
import com.zysl.aws.service.AmasonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import software.amazon.awssdk.services.s3.model.Bucket;
import sun.misc.BASE64Decoder;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/aws/s3")
@Slf4j
public class AmazonController {

    @Autowired
    private AmasonService amasonService;


  @PostMapping("/shareFile")
  public Result shareFile(@RequestBody ShareFileRequest request){
    log.info("--开始调用shareFile分享文件的信息接口:{}--",request);

    return Result.success(amasonService.shareFile(request));
  }


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
    public Result createBucket(String bucketName, String serviceNo){
        log.info("--开始调用createBucket创建文件夹接口--bucketName:{},serviceName:{}",
                bucketName, serviceNo);
        String pattern = "^[a-zA-Z0-9.\\-_]{3,60}$";
        //判断存储桶是否满足命名规则
        if(Pattern.compile(pattern).matcher(bucketName).matches()){
            return amasonService.createBucket(bucketName, serviceNo);
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
    public Result downloadFile(HttpServletResponse response, String bucketName, String fileId, String type){
        log.info("--开始调用downloadFile下载文件接口--bucketName:{},fileId:{}，type：{}", bucketName, fileId, type);
        Long startTime = System.currentTimeMillis();
        String str = amasonService.downloadFile(response, bucketName, fileId);
        log.info("--下载接口返回的文件数据大小--", str.length());
        if(!StringUtils.isEmpty(str)){
            if(DownTypeEnum.COVER.getCode().equals(type)){
                Long usedTime = System.currentTimeMillis() - startTime;
                Map<String, Object> result = new HashMap<>();
                result.put("data", str);
                result.put("reason", "");
                result.put("usedTime", usedTime);
                return Result.success(result);
            }else {
                try {
                    //1下载文件流
                    OutputStream outputStream = response.getOutputStream();
                    response.setContentType("application/octet-stream");//告诉浏览器输出内容为流
                    response.setHeader("Content-Disposition", "attachment;fileName="+fileId);
                    response.setCharacterEncoding("UTF-8");

                    BASE64Decoder decoder = new BASE64Decoder();
                    byte[] bytes = decoder.decodeBuffer(str);
                    outputStream.write(bytes);
                    outputStream.flush();
                    outputStream.close();
                } catch (IOException e) {
                    log.info("--文件下载异常：--", e);
                }
                return null;
            }
        }else {
            return Result.error("文件下载无数据返回");
        }
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

    /**
     * 获取文件大小
     * @param bucketName
     * @param fileName
     */
    @GetMapping("/getFileSize")
    public Long getFileSize(String bucketName, String fileName){
        log.info("--开始getFileSize调用获取文件大小--bucketName:{},fileName:{}", bucketName, fileName);
        return amasonService.getFileSize(bucketName, fileName);

    }

    /**
     * 获取视频文件信息
     * @param response
     * @param bucketName
     * @param fileId
     */
    @GetMapping("/getVideo")
    public void getVideo(HttpServletResponse response, String bucketName, String fileId){
        String str = amasonService.downloadFile(response, bucketName, fileId);
        try {
            BASE64Decoder decoder = new BASE64Decoder();
            byte[] bytes = decoder.decodeBuffer(str);

            response.reset();
            //设置头部类型
            response.setContentType("video/mp4;charset=UTF-8");

            ServletOutputStream out = null;
            try {
                out = response.getOutputStream();
                out.write(bytes);
                out.flush();
            }catch (Exception e){
                log.info("--文件流转换异常：--", e);
            }finally {
                try {
                    out.close();
                } catch (IOException e) {

                }
                out = null;
            }
        } catch (IOException e) {
            log.error("--文件下载异常：--", e);
        } catch (Exception ex) {
            log.error("--视频文件获取异常：--", ex);
        }

    }

}
