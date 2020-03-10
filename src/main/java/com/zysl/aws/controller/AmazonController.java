package com.zysl.aws.controller;

import com.zysl.aws.common.result.CodeMsg;
import com.zysl.aws.common.result.Result;
import com.zysl.aws.config.BizConfig;
import com.zysl.aws.enums.DownTypeEnum;
import com.zysl.aws.model.*;
import com.zysl.aws.model.db.S3File;
import com.zysl.aws.service.AmasonService;
import com.zysl.aws.service.FileService;
import com.zysl.aws.service.IPDFService;
import com.zysl.aws.service.IWordService;
import com.zysl.aws.utils.BizUtil;
import com.zysl.aws.utils.MD5Utils;
import com.zysl.aws.utils.S3ClientFactory;
import com.zysl.cloud.utils.StringUtils;
import com.zysl.cloud.utils.common.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import software.amazon.awssdk.services.s3.model.Bucket;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;
import java.util.regex.Pattern;

@CrossOrigin
@RestController
@RequestMapping("/aws/s3")
@Slf4j
public class AmazonController {

    @Autowired
    private AmasonService amasonService;
    @Autowired
    private IWordService wordService;
    @Autowired
    private IPDFService pdfService;
    @Autowired
    private S3ClientFactory s3ClientFactory;
    @Autowired
    private FileService fileService;
    @Autowired
    private BizConfig bizConfig;

    /**
     * 文件分享
     * @param request
     * @return
     */
    @PostMapping("/shareFile")
    public Result shareFile(@RequestBody ShareFileRequest request){
        log.info("--开始调用shareFile分享文件的信息接口:{}--",request);

        return amasonService.shareFile(request);
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
        log.info("--开始调用createBucket创建文件夹接口--bucketName:{},serviceNo:{}",
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
        return amasonService.deleteBucket(bucketName);
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
     * @returnuploadFile
     */
    @PostMapping("/uploadFile")
    public Result uploadFile(@RequestBody UploadFileRequest request){
        log.info("--开始调用uploadFile上传文件接口request：{}--", request);
        return  amasonService.uploadFile(request);
    }

    /**
     * 下载文件
     * @param bucketName
     * @param fileId
     * @return
     */
    @GetMapping("/downloadFile")
    public Result downloadFile(HttpServletResponse response, String bucketName, String fileId, String type, String versionId){
        DownloadFileRequest request = new DownloadFileRequest();
        request.setBucketName(bucketName);
        request.setFileId(fileId);
        request.setVersionId(versionId);
        request.setType(type);
        log.info("--开始调用downloadFile下载文件接口--request:{} ", request);
        Long startTime = System.currentTimeMillis();
        String str = amasonService.downloadFile(response, request);
        if(!StringUtils.isEmpty(str)){
            log.info("--下载接口返回的文件数据大小--", str.length());
            if(DownTypeEnum.COVER.getCode().equals(request.getType())){
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
                    response.setHeader("Content-Disposition", "attachment;fileName="+request.getFileId());
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
    public Result getFileSize(String bucketName, String fileName){
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
        log.info("--开始getVideo获取视频文件信息--bucketName:{},fileId:{}", bucketName, fileId);
        DownloadFileRequest request = new DownloadFileRequest();
        request.setBucketName(bucketName);
        request.setFileId(fileId);
        String str = amasonService.downloadFile(response, request);
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
                log.error("--文件流转换异常：--", e);
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

    /**
     * word转pdf
     * @description 可设置水印、密码
     * @author miaomingming
     * @date 16:35 2020/2/20
     * @param [request]
     * @return com.zysl.cloud.utils.common.BaseResponse<com.zysl.aws.model.WordToPDFDTO>
     **/
    @PostMapping("/word2pdf")
    public BaseResponse<WordToPDFDTO> changeWordToPdf(@RequestBody WordToPDFRequest request){
        log.info("===changeWordToPdf.param:{}===",request);
        BaseResponse<WordToPDFDTO> baseResponse = new BaseResponse<>();
        baseResponse.setSuccess(false);
        //step 1.文件后缀校验
        if(StringUtils.isBlank(request.getBucketName()) || StringUtils.isBlank(request.getFileName())){
            log.info("===文件夹或文件名为空:{}===",request);
            baseResponse.setMsg("文件夹或文件名为空.");
            return baseResponse;
        }
        if(!request.getFileName().toLowerCase().endsWith("doc")
            && !request.getFileName().toLowerCase().endsWith("docx")){
            log.info("===不是word文件:{}===",request.getFileName());
            baseResponse.setMsg("不是word文件.");
            return baseResponse;
        }
        //step 2.读取源文件--
        //调用s3接口下载文件内容
        String fileStr = amasonService.getS3FileInfo(request.getBucketName(),request.getFileName(), "");
        BASE64Decoder decoder = new BASE64Decoder();
        byte[] inBuff = null;
        try {
            inBuff = decoder.decodeBuffer(fileStr);
        } catch (IOException e) {
            log.info("--changeWordToPdf文件下载异常：--{}", e);
        }

        //step 3.word转pdf、加水印 300,300
        String fileName = BizUtil.getTmpFileNameWithoutSuffix(request.getFileName());
        byte[] outBuff = wordService.changeWordToPDF(fileName, inBuff,false, request.getTextMark());
        log.info("===changeToPDF===outBuff,length:{}", outBuff != null ? outBuff.length : 0);
        if(outBuff == null || outBuff.length == 0){
          log.info("===changeToPDF===pdfFileData is null .fileName:{}",request.getFileName());
          baseResponse.setMsg("word转换的pdf大小为0..");
          return baseResponse;
        }

        //step 4.实现加密
        if(!StringUtils.isBlank(request.getUserPwd()) && !StringUtils.isBlank(request.getOwnerPwd())){
            byte[] addPwdOutBuff = pdfService.addPwd(outBuff,request.getUserPwd(),request.getOwnerPwd());
            if(addPwdOutBuff == null || addPwdOutBuff.length == 0){
                log.info("===addPwd===file add pwd err.fileName:{}",request.getFileName());
                baseResponse.setMsg("word转换的pdf加密后大小为0..");
                return baseResponse;
            }
        }

        //step 5.上传到temp-001
        BASE64Encoder encoder = new BASE64Encoder();
        String str = encoder.encode(outBuff);
        amasonService.upload(request.getBucketName(), fileName + "text.pdf", str.getBytes());
        //step 6.新文件入库
        String serverNo = s3ClientFactory.getServerNo(request.getBucketName());
        S3File addS3File = new S3File();
        //服务器编号
        addS3File.setServiceNo(serverNo);
        //文件名称
        addS3File.setFileName(fileName + "text.pdf");
        //文件夹名称
        addS3File.setFolderName(request.getBucketName());
        //文件大小
        addS3File.setFileSize(Long.valueOf(outBuff.length));
        //上传时间
        addS3File.setUploadTime(new Date());
        //创建时间
        addS3File.setCreateTime(new Date());
        //文件内容md5
        String md5Content = MD5Utils.encode(new String(outBuff));
        //文件内容md5
        addS3File.setContentMd5(md5Content);
        //向数据库保存文件信息
        long num = fileService.addFileInfo(addS3File);
        log.info("--插入数据返回num:{}", num);

        //step 7.设置返回参数
        WordToPDFDTO dto = new WordToPDFDTO();
        dto.setBucketName(bizConfig.WORD_TO_PDF_BUCKET_NAME);
        dto.setFileName(fileName + "text.pdf");
        baseResponse.setModel(dto);
        baseResponse.setSuccess(true);
        return baseResponse;
    }


    /**
     * 设置文件夹的版本控制权限
     * @param bucketName
     * @param status
     * @return
     */
    @PostMapping("/setVersion")
    public Result updatFileVersion(@RequestBody SetFileVersionRequest request){
        log.info("--updatFileVersion设置文件夹的版本控制权限--request:{}", request);
        return amasonService.setFileVersion(request);
    }

    /**
     * 获取文件版本信息
     * @param bucketName
     * @param fileName
     * @return
     */
    @GetMapping("/getFileVersion")
    public Result getFileVersion(String bucketName, String fileName){
        log.info("--getFileVersion获取文件版本信息--bucketName:{},fileName:{}", bucketName, fileName);
        List<FileVersionResponse> list = amasonService.getS3FileVersion(bucketName, fileName);
        return Result.success(list);
    }

}
