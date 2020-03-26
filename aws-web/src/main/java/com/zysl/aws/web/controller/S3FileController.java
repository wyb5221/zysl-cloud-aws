package com.zysl.aws.web.controller;

import com.zysl.aws.web.enums.DownTypeEnum;
import com.zysl.aws.web.model.*;
import com.zysl.aws.web.model.db.S3File;
import com.zysl.aws.web.service.AwsFileService;
import com.zysl.cloud.aws.utils.MD5Utils;
import com.zysl.cloud.utils.StringUtils;
import com.zysl.cloud.utils.common.AppLogicException;
import com.zysl.cloud.utils.common.BasePaginationResponse;
import com.zysl.cloud.utils.common.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import software.amazon.awssdk.services.s3.model.CopyObjectResponse;
import sun.misc.BASE64Encoder;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 文件处理controller
 */
@CrossOrigin
@RestController
@RequestMapping("/aws/file")
@Slf4j
public class S3FileController {

    @Autowired
    private AwsFileService awsFileService;

    /**
     * 上传文件
     * @param request
     * @returnuploadFile
     */
    @PostMapping("/uploadFile")
    public BaseResponse<UploadFieResponse> uploadFile(@RequestBody UploadFileRequest request){
        log.info("--开始调用uploadFile上传文件接口request：{}--", request);
        BaseResponse<UploadFieResponse> baseResponse = new BaseResponse<>();

        List<String> validations = new ArrayList<>();
        if(StringUtils.isBlank(request.getBucketName())){
            validations.add("bucketName不能为空！");
        }
        if(StringUtils.isBlank(request.getFileId())){
            validations.add("fileId不能为空！");
        }
        //入参校验不通过
        if(!CollectionUtils.isEmpty(validations)){
            baseResponse.setSuccess(false);
            baseResponse.setValidations(validations);
            return baseResponse;
        }

        UploadFieResponse response = awsFileService.uploadFile(request);
        if(null != response){
            baseResponse.setSuccess(true);
            baseResponse.setModel(response);
        }else{
            baseResponse.setSuccess(false);
            baseResponse.setMsg("文件上传失败");
        }
        return baseResponse;
    }

    /**
     * 文件流上传
     * @param request
     * @returnuploadFile
     */
    @PostMapping("/uploadFileInfo")
    public BaseResponse<UploadFieResponse> uploadFileInfo(HttpServletRequest request) throws IOException {
        log.info("--开始调用uploadFile上传文件接口request：{}--", request.toString());
        BaseResponse<UploadFieResponse> baseResponse = new BaseResponse<>();
        List<String> validations = new ArrayList<>();
        if(StringUtils.isBlank(request.getParameter("bucketName"))){
            validations.add("bucketName不能为空！");
        }
        if(StringUtils.isBlank(request.getParameter("fileId"))){
            validations.add("fileId不能为空！");
        }
        //入参校验不通过
        if(!CollectionUtils.isEmpty(validations)){
            baseResponse.setSuccess(false);
            baseResponse.setValidations(validations);
            return baseResponse;
        }

        UploadFieResponse response = awsFileService.uploadFile(request);
        if(null != response){
            baseResponse.setSuccess(true);
            baseResponse.setModel(response);
        }else{
            baseResponse.setSuccess(false);
            baseResponse.setMsg("文件上传失败");
        }
        return baseResponse;
    }

    /**
     * 下载文件
     * @param bucketName
     * @param fileId
     * @return
     */
    @GetMapping("/downloadFile")
    public BaseResponse<DownloadFileResponse> downloadFile(HttpServletRequest request, HttpServletResponse response, String bucketName, String fileId, String type, String versionId, String userId){
        log.info("--开始调用downloadFile下载文件接口--bucketName:{},fileId：{}，versionId:{},userId:{} ", bucketName, fileId, versionId, userId);
        BaseResponse<DownloadFileResponse> baseResponse = new BaseResponse<>();
        List<String> validations = new ArrayList<>();
        if(StringUtils.isBlank(bucketName)){
            validations.add("bucketName不能为空！");
        }
        if(StringUtils.isBlank(fileId)){
            validations.add("fileId不能为空！");
        }
        //入参校验不通过
        if(!CollectionUtils.isEmpty(validations)){
            baseResponse.setSuccess(false);
            baseResponse.setValidations(validations);
            return baseResponse;
        }

        Long startTime = System.currentTimeMillis();
//        String str = awsFileService.downloadFile(response, request);
        byte[] str = awsFileService.getS3FileInfo(bucketName, fileId, versionId, userId);
        if(null != str){
            log.info("--下载接口返回的文件数据大小--", str.length);
            if(DownTypeEnum.COVER.getCode().equals(type)){
                Long usedTime = System.currentTimeMillis() - startTime;
                DownloadFileResponse downloadFileResponse = new DownloadFileResponse();

                BASE64Encoder encoder = new BASE64Encoder();
                //返回加密
                downloadFileResponse.setData(encoder.encode(str));
                downloadFileResponse.setUsedTime(usedTime);

                //获取返回对象
                baseResponse.setSuccess(true);
                baseResponse.setModel(downloadFileResponse);
                return baseResponse;
            }else {
                try {
                    //1下载文件流
                    OutputStream outputStream = response.getOutputStream();
                    response.setContentType("application/octet-stream");//告诉浏览器输出内容为流
                    response.setCharacterEncoding("UTF-8");

                    String userAgent = request.getHeader("User-Agent").toUpperCase();//获取浏览器名（IE/Chome/firefox）
                   /* if (userAgent.contains("firefox")) {
                        fileId = new String(fileId.getBytes("UTF-8"), "ISO8859-1"); // firefox浏览器
                    } else if (userAgent.contains("MSIE") ||
                            (userAgent.indexOf("GECKO")>0 && userAgent.indexOf("RV:11")>0)) {
                        fileId = URLEncoder.encode(fileId, "UTF-8");// IE浏览器
                    }else if (userAgent.contains("CHROME")) {
                        fileId = new String(fileId.getBytes("UTF-8"), "ISO8859-1");// 谷歌
                    }*/
                    if (userAgent.contains("MSIE") ||
                            (userAgent.indexOf("GECKO")>0 && userAgent.indexOf("RV:11")>0)) {
                        fileId = URLEncoder.encode(fileId, "UTF-8");// IE浏览器
                    }else{
                        fileId = new String(fileId.getBytes("UTF-8"), "ISO8859-1");// 谷歌
                    }
                    response.setHeader("Content-Disposition", "attachment;fileName="+fileId);

                    outputStream.write(str);
                    outputStream.flush();
                    outputStream.close();
                } catch (IOException e) {
                    log.info("--文件下载异常：--", e);
                    throw new AppLogicException("文件流处理异常");
                }
                return null;
            }
        }else {
            //获取返回对象
            baseResponse.setSuccess(false);
            baseResponse.setMsg("文件下载无数据返回");
            return baseResponse;
        }
    }

    /**
     * 获取文件大小
     * @param bucketName
     * @param fileName
     */
    @GetMapping("/getFileSize")
    public BaseResponse<Long> getFileSize(String bucketName, String fileName, String versionId){
        log.info("--开始getFileSize调用获取文件大小--bucketName:{},fileName:{},versionId:{}", bucketName, fileName, versionId);
        BaseResponse<Long> baseResponse = new BaseResponse<>();
        List<String> validations = new ArrayList<>();
        if(StringUtils.isBlank(bucketName)){
            validations.add("bucketName不能为空！");
        }
        if(StringUtils.isBlank(fileName)){
            validations.add("fileName不能为空！");
        }
        //入参校验不通过
        if(!CollectionUtils.isEmpty(validations)){
            baseResponse.setSuccess(false);
            baseResponse.setValidations(validations);
            return baseResponse;
        }

        Long fileSize = awsFileService.getS3FileSize(bucketName, fileName, versionId);
        if(fileSize >= 0){
            baseResponse.setSuccess(true);
            baseResponse.setModel(fileSize);
        }else{
            baseResponse.setSuccess(false);
            baseResponse.setMsg("文件大小查询失败");
        }
        return baseResponse;
    }

    /**
     * 获取文件信息
     * @param bucketName
     * @param fileName
     */
    @GetMapping("/getFileInfo")
    public BaseResponse<FileInfoRequest> getFileInfo(String bucketName, String fileName, String versionId){
        log.info("--开始getFileInfo调用获取文件信息--bucketName:{},fileName:{},versionId:{}", bucketName, fileName, versionId);
        BaseResponse<FileInfoRequest> baseResponse = new BaseResponse<>();
        List<String> validations = new ArrayList<>();
        if(StringUtils.isBlank(bucketName)){
            validations.add("bucketName不能为空！");
        }
        if(StringUtils.isBlank(fileName)){
            validations.add("fileName不能为空！");
        }
        //入参校验不通过
        if(!CollectionUtils.isEmpty(validations)){
            baseResponse.setSuccess(false);
            baseResponse.setValidations(validations);
            return baseResponse;
        }

        FileInfoRequest fileInfoRequest = awsFileService.getS3ToFileInfo(bucketName, fileName, versionId);
        if(null != fileInfoRequest){
            baseResponse.setSuccess(true);
            baseResponse.setMsg("文件信息查询成功");
            baseResponse.setModel(fileInfoRequest);
        }else{
            baseResponse.setSuccess(false);
            baseResponse.setMsg("文件信息查询失败");
        }
        return baseResponse;
    }

    /**
     * 获取视频文件信息
     * @param response
     * @param bucketName
     * @param fileId
     */
    @GetMapping("/getVideo")
    public void getVideo(HttpServletResponse response, String bucketName, String fileId, String versionId){
        log.info("--开始getVideo获取视频文件信息--bucketName:{},fileId:{},versionId:{}", bucketName, fileId, versionId);
        DownloadFileRequest request = new DownloadFileRequest();
        request.setBucketName(bucketName);
        request.setFileId(fileId);

        try {
            byte[] bytes = awsFileService.getS3FileInfo(bucketName, fileId, versionId, "");
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
     * @return com.zysl.cloud.utils.common.BaseResponse<com.zysl.aws.web.model.WordToPDFDTO>
     **/
    /*@PostMapping("/word2pdf")
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
        byte[] inBuff = awsFileService.getS3FileInfo(request.getBucketName(),request.getFileName(), request.getVersionId(), "");
        if(null == inBuff){
            log.info("===文件不存在:{}===",request.getFileName());
            baseResponse.setMsg("文件不存在.");
            return baseResponse;
        }
//        BASE64Decoder decoder = new BASE64Decoder();
//        byte[] inBuff = null;
//        try {
//            inBuff = decoder.decodeBuffer(fileStr);
//        } catch (IOException e) {
//            log.error("--changeWordToPdf文件下载异常：--{}", e);
//        }

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
//        BASE64Encoder encoder = new BASE64Encoder();
//        String str = encoder.encode(outBuff);
        PutObjectResponse putObjectResponse = awsFileService.upload(request.getBucketName(), fileName + "text.pdf", outBuff);

        WordToPDFDTO dto = new WordToPDFDTO();
        if(null != putObjectResponse){
            //文件上传成功
            dto.setVersionId(putObjectResponse.versionId());
            //step 6.新文件入库
            //向数据库保存文件信息
            S3File addS3File = initS3File(request.getBucketName(), fileName, outBuff);
            long num = fileService.addFileInfo(addS3File);
            log.info("--插入数据返回num:{}", num);
        }
        //step 7.设置返回参数
        dto.setBucketName(request.getBucketName());
        dto.setFileName(fileName + "text.pdf");
        baseResponse.setModel(dto);
        baseResponse.setSuccess(true);
        return baseResponse;
    }*/

    /**
     * 获取文件对象
     * @param bucketName
     * @param fileName
     * @param outBuff
     * @return
     */
    public S3File initS3File(String bucketName, String fileName, byte[] outBuff){
        S3File addS3File = new S3File();
//        String serverNo = getServiceNo(bucketName);
        //服务器编号
        addS3File.setServiceNo("");
        //文件名称
        addS3File.setFileName(fileName + "text.pdf");
        //文件夹名称
        addS3File.setFolderName(bucketName);
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
        return addS3File;
    }
    /**
     * 文件分享
     * @param request
     * @return
     */
//    @PostMapping("/shareFile")
    public BaseResponse<UploadFieResponse> shareFile(@RequestBody ShareFileRequest request){
        log.info("--开始调用shareFile分享文件的信息接口:{}--",request);
        BaseResponse<UploadFieResponse> baseResponse = new BaseResponse<>();

        UploadFieResponse uploadFieResponse = awsFileService.shareFile(request);
        if(null != uploadFieResponse){
            baseResponse.setSuccess(true);
            baseResponse.setModel(uploadFieResponse);
        }else{
            baseResponse.setSuccess(false);
            baseResponse.setMsg("文件分享失败");
        }
        return baseResponse;
    }

    /**
     * 获取文件版本信息
     * @param bucketName
     * @param fileName
     * @return
     */
    @GetMapping("/getFileVersion")
    public BasePaginationResponse<FileVersionResponse> getFileVersion(String bucketName, String fileName){
        log.info("--getFileVersion获取文件版本信息--bucketName:{},fileName:{}", bucketName, fileName);
        BasePaginationResponse<FileVersionResponse> baseResponse = new BasePaginationResponse<>();
        List<String> validations = new ArrayList<>();
        if(StringUtils.isBlank(bucketName)){
            validations.add("bucketName不能为空！");
        }
        if(StringUtils.isBlank(fileName)){
            validations.add("fileName不能为空！");
        }
        //入参校验不通过
        if(!CollectionUtils.isEmpty(validations)){
            baseResponse.setSuccess(false);
            baseResponse.setMsg("入参校验失败");
            baseResponse.setValidations(validations);
            return baseResponse;
        }

        List<FileVersionResponse> list = awsFileService.getS3FileVersion(bucketName, fileName);

        baseResponse.setSuccess(true);
        baseResponse.setModelList(list);
        return baseResponse;
    }


    /**
     * 分享下载文件
     * @param bucketName
     * @param fileId
     * @return
     */
    @GetMapping("/shareDownloadFile")
    public BaseResponse<DownloadFileResponse> shareDownloadFile(HttpServletResponse response, String bucketName, String fileId, String versionId){
        DownloadFileRequest request = new DownloadFileRequest();
        request.setBucketName(bucketName);
        request.setFileId(fileId);
        request.setVersionId(versionId);
        log.info("--开始调用downloadFile下载文件接口--request:{} ", request);
        BaseResponse<DownloadFileResponse> baseResponse = new BaseResponse<>();

        //入参校验
        List<String> validations = new ArrayList<>();
        if(StringUtils.isBlank(bucketName)){
            validations.add("bucketName不能为空！");
        }
        if(StringUtils.isBlank(fileId)){
            validations.add("fileId不能为空！");
        }
        //入参校验不通过
        if(!CollectionUtils.isEmpty(validations)){
            baseResponse.setSuccess(false);
            baseResponse.setValidations(validations);
            return baseResponse;
        }

        byte[] bytes = awsFileService.shareDownloadFile(response, request);
        if(null != bytes){
            log.info("--下载接口返回的文件数据大小--", bytes.length);
            try {
                //1下载文件流
                OutputStream outputStream = response.getOutputStream();
                response.setContentType("application/octet-stream");//告诉浏览器输出内容为流
                response.setHeader("Content-Disposition", "attachment;fileName="+request.getFileId());
                response.setCharacterEncoding("UTF-8");

                outputStream.write(bytes);
                outputStream.flush();
                outputStream.close();
            } catch (IOException e) {
                log.info("--文件下载异常：--", e);
                throw new AppLogicException("文件流处理异常");
            }
            return null;
        }else {
            //获取返回对象
            baseResponse.setSuccess(false);
            baseResponse.setMsg("文件下载无数据返回");
            return baseResponse;
        }
    }

    /**
     * 设置文件标签信息
     * @return
     */
    @PostMapping("/setTag")
    public BaseResponse<String> setObjectTag(@RequestBody UpdateFileTageRequest request){
        log.info("--setObjectTag设置文件标签信息request--:{}", request);
        BaseResponse<String> baseResponse = new BaseResponse<>();
        //入参校验
        List<String> validations = new ArrayList<>();
        if(StringUtils.isBlank(request.getBucket())){
            validations.add("bucket不能为空！");
        }
        //入参校验不通过
        if(!CollectionUtils.isEmpty(validations)){
            baseResponse.setSuccess(false);
            baseResponse.setValidations(validations);
            return baseResponse;
        }

        boolean flag = awsFileService.updateFileTage(request);
        baseResponse.setSuccess(flag);
        return baseResponse;
    }

    /**
     * 删除文件
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<String> deleteFile(@RequestBody DelObjectRequest request){
        log.info("--开始调用deleteFile删除文件接口--request:{} ", request);
        BaseResponse<String> baseResponse = new BaseResponse<>();

        //入参校验
        List<String> validations = new ArrayList<>();
        if(StringUtils.isBlank(request.getBucketName())){
            validations.add("bucketName不能为空！");
        }
        if(StringUtils.isBlank(request.getKey())){
            validations.add("fileId不能为空！");
        }
        //入参校验不通过
        if(!CollectionUtils.isEmpty(validations)){
            baseResponse.setSuccess(false);
            baseResponse.setValidations(validations);
            return baseResponse;
        }

        boolean delFlag = awsFileService.deleteFile(request);
        baseResponse.setSuccess(delFlag);
        return baseResponse;
    }

    /**
     * 文件复制
     * @param request
     * @return
     */
//    @PostMapping("/copy")
    public BaseResponse<String> copyFile(@RequestBody CopyFileRequest request){
        log.info("--copyFile 文件复制--request:{}", request);
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
        CopyObjectResponse response = awsFileService.copyFile(request);
        if(null != response){
            baseResponse.setSuccess(true);
            baseResponse.setMsg("文件复制成功！");
            return baseResponse;
        }else{
            baseResponse.setSuccess(false);
            baseResponse.setMsg("文件复制失败！");
            return baseResponse;
        }
    }

    /**
     * 文件移动
     * @param request
     * @return
     */
    @PostMapping("/move")
    public BaseResponse<String> moveFile(@RequestBody CopyFileRequest request){
        log.info("--moveFile 文件移动--request:{}", request);
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
        boolean moveFlag = awsFileService.moveFile(request);
        if(moveFlag){
            baseResponse.setSuccess(true);
            baseResponse.setMsg("文件移动成功！");
            return baseResponse;
        }else{
            baseResponse.setSuccess(false);
            baseResponse.setMsg("文件移动失败！");
            return baseResponse;
        }
    }



}
