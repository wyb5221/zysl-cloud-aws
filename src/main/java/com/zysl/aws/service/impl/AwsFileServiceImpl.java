package com.zysl.aws.service.impl;

import com.zysl.aws.model.*;
import com.zysl.aws.model.db.S3File;
import com.zysl.aws.service.AwsFileService;
import com.zysl.aws.service.FileService;
import com.zysl.aws.utils.DateUtil;
import com.zysl.aws.utils.MD5Utils;
import com.zysl.cloud.utils.common.AppLogicException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import sun.misc.BASE64Encoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * 文件处理类
 */
@Service
@Slf4j
public class AwsFileServiceImpl extends BaseService implements AwsFileService {

    @Autowired
    private FileService fileService;

    @Override
    public UploadFieResponse uploadFile(UploadFileRequest request) {
        log.info("--uploadFile下载文件开始时间--：{}", System.currentTimeMillis());
        UploadFieResponse response = new UploadFieResponse();

        String bucketName = request.getBucketName();
        String fileId = request.getFileId();
        byte[] data = request.getData().getBytes();

        S3Client s3 = getS3Client(getServiceNo(bucketName));
        if(null != doesBucketExist(bucketName)){
            log.info("--文件夹存在--");
            if(StringUtils.isEmpty(fileId)){
                fileId = UUID.randomUUID().toString().replaceAll("-","");
                request.setFileId(fileId);
            }

            //上传文件
            PutObjectResponse putObjectResponse = upload(bucketName, fileId, data);

            if(null != putObjectResponse){
                //修改文件信息
//                updateFileInfo(request);

                response.setFolderName(request.getBucketName());
                response.setFileName(fileId);
                response.setVersionId(putObjectResponse.versionId());
                return response;
            }else {
                log.info("--文件上传失败--");
                return null;
            }
        }else {
            log.info("--文件夹不存在:{}--",request.getBucketName());
            return null;
        }
    }

    @Override
    public UploadFieResponse uploadFile(HttpServletRequest request) {
        //获取文件流对象数据
        MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest)request;
        byte[] bytes = null;
        try {
            bytes = multipartHttpServletRequest.getFile("file").getBytes();
        } catch (IOException e) {
            log.error("--uploadFile获取文件流异常--：{}", e);
            throw new AppLogicException("获取文件流异常");
        }

        String bucketName = request.getParameter("bucketName");
        String fileId = request.getParameter("fileId");
        Integer maxAmount = null == request.getParameter("maxAmount") ? null : Integer.valueOf(request.getParameter("maxAmount"));
        Integer validity = null == request.getParameter("validity") ? null : Integer.valueOf(request.getParameter("validity"));

        BASE64Encoder encoder = new BASE64Encoder();
        String str = encoder.encode(bytes);
        UploadFileRequest fileRequest = new UploadFileRequest();
        fileRequest.setBucketName(bucketName);
        fileRequest.setFileId(fileId);
        fileRequest.setData(str);
        fileRequest.setMaxAmount(maxAmount);
        fileRequest.setValidity(validity);
        log.info("--开始调用uploadFile上传文件接口fileRequest：{}--", fileRequest);

        return this.uploadFile(fileRequest);
    }

    /**
     * 调用s3接口上传文件
     * @param bucketName
     * @param fileId
     * @param data
     */
    @Override
    public PutObjectResponse upload(String bucketName, String fileId, byte[] data){
        log.info("--upload开始上传文件，入参bucketName：{}-,fileId:{}-,data:{}", bucketName, fileId, data.length);
        S3Client s3 = getS3Client(getServiceNo(bucketName));
        try {
            PutObjectResponse putObjectResponse = s3.putObject(PutObjectRequest.builder().bucket(bucketName).key(fileId)
                            .build(),
                    RequestBody.fromBytes(data));
            log.info("--上传文件--putObjectResponse:{}", putObjectResponse);
            return putObjectResponse;
        }catch (Exception e){
            log.error("--上传文件异常--：", e);
            throw new AppLogicException("上传文件失败");
        }
    }

    @Override
    public String downloadFile(HttpServletResponse response, DownloadFileRequest request) {
        log.info("--downloadFile下载文件开始时间--:{}", System.currentTimeMillis());
        S3File s3File = fileService.getFileInfo(request.getBucketName(), request.getFileId());
        log.info("--downloadFile下载文件查询数据库结束时间--:{}", System.currentTimeMillis());
        if(null == s3File){
            log.info("--数据库记录不存在--");
            String str = getS3FileInfo(request.getBucketName(), request.getFileId(), request.getVersionId());
            return str;
        }
        Long fileKey = s3File.getId();
        //最大可下载次数
        Integer maxAmount = s3File.getMaxAmount();
        //下载有效截至时间
        Date validityTime = s3File.getValidityTime();
        //判断文件是否还在有效期,当前时间小于截至时间则还可以下载
        if(!StringUtils.isEmpty(validityTime) && !DateUtil.doCompareDate(new Date(), validityTime)) {
            log.info("--文件已超过有效期,有效期截至时间validityTime:--{}", validityTime);
            return null;
        }
        if(!StringUtils.isEmpty(maxAmount) && maxAmount <= 0){
            log.info("--文件已无可下载次数--可用次数maxAmount:{}", maxAmount);
            return null;
        }

        String bucketName = request.getBucketName();
        String key = request.getFileId();
        // 判断是否源文件
        if (s3File != null && s3File.getSourceFileId() != null && s3File.getSourceFileId() > 0) {
            log.info("--downloadFile下载文件查询源文件开始时间--:{}", System.currentTimeMillis());
            s3File = fileService.getFileInfo(s3File.getSourceFileId());
            log.info("--downloadFile下载文件查询源文件结束时间--:{}", System.currentTimeMillis());
            bucketName = s3File.getFolderName();
            key = s3File.getFileName();
        }

        if(null != doesBucketExist(bucketName)){
            log.info("--文件夹存在--");

            String str = getS3FileInfo(bucketName, key, request.getVersionId());

            //下载成功后修改最大下载次数
            if(!StringUtils.isEmpty(maxAmount)){
                log.info("--修改文件下载次数--可用次数maxAmount:{}", maxAmount);
                fileService.updateFileAmount(--maxAmount, fileKey);
                log.info("--updateFileAmount修改文件下载次数结束时间--:{}", System.currentTimeMillis());
            }
            return str;
        }else {
            log.info("--文件夹不存在--");
            return null;
        }
    }

    @Override
    public String shareDownloadFile(HttpServletResponse response, DownloadFileRequest request) {
        //查询文件信息
        S3File s3File = fileService.getFileInfo(request.getBucketName(), request.getFileId());
        if(null != s3File && !StringUtils.isEmpty(s3File.getSourceFileId())){
            return this.downloadFile(response, request);
        }else{
            log.info("--不是分享文件，不能下载--");
            return null;
        }
    }

    /**
     * 调用s3接口下载文件内容
     * @param bucketName
     * @param key
     * @return
     */
    @Override
    public String getS3FileInfo(String bucketName, String key, String versionId){
        log.info("--调用s3接口下载文件内容入参-bucketName:{},-key:{},versionId:{}", bucketName, key, versionId);
        log.info("--getS3FileInfo下载文件开始时间--:{}", System.currentTimeMillis());

        S3Client s3 = getS3Client(getServiceNo(bucketName));
        log.info("--getS3Client获取初始化对象结束时间--:{}", System.currentTimeMillis());
        try {
            GetObjectRequest.Builder request = null;
            if(StringUtils.isEmpty(versionId)){
                request = GetObjectRequest.builder().bucket(bucketName).key(key);
            }else {
                request = GetObjectRequest.builder().bucket(bucketName).key(key).versionId(versionId);
            }
            ResponseBytes<GetObjectResponse> objectAsBytes = s3.getObject(request.build(),
                    ResponseTransformer.toBytes());
            log.info("--getObject结束时间--:{}", System.currentTimeMillis());

            /*ResponseBytes<GetObjectResponse> objectAsBytes = s3.getObject(b ->
                            b.bucket(bucketName).key(key).versionId(versionId),
                    ResponseTransformer.toBytes());*/
            byte[] bytes = objectAsBytes.asByteArray();
            log.info("--asByteArray结束时间--:{}", System.currentTimeMillis());
//            String a = new String(bytes);
//            byte[] aa = a.getBytes();
//            String str = objectAsBytes.asUtf8String();
            return new String(bytes);
        } catch (Exception e) {
            log.error("--s3接口下载文件信息异常：--{}", e);
            return null;
        }
    }

    @Override
    public Long getFileSize(String bucketName, String key) {
        //查询是否存在db，不存在则查询服务器
        S3File s3File = fileService.getFileInfo(bucketName, key);
        if(null == s3File){
            Long fileSize = getS3FileSize(bucketName, key);
            return fileSize;
        }
        //判断是否源文件
        if(s3File.getSourceFileId() != null && s3File.getSourceFileId() > 0){
            //根据id查询文件信息
            s3File = fileService.getFileInfo(s3File.getSourceFileId());
            if(s3File == null){//找不到源文件
                log.warn("--getFileSize--找不到源文件key:{}",key);
                return -1L;
            }
        }

        String folderName = s3File.getFolderName();
        String FileName = s3File.getFileName();
        //查询文件大小
        Long fileSize = getS3FileSize(folderName, FileName);
        return fileSize;
    }

    /**
     * 调用s3接口查询服务器文件大小
     * @param bucketName
     * @param key
     * @return
     */
    @Override
    public Long getS3FileSize(String bucketName, String key){
        S3Client s3 = getS3Client(getServiceNo(bucketName));
        try {
            HeadObjectResponse headObjectResponse = s3.headObject(b ->
                    b.bucket(bucketName).key(key));
            Long fileSize = headObjectResponse.contentLength();
            return fileSize;
        }catch (Exception e) {
            log.error("--调用s3接口查询服务器文件大小异常：--{}", e.getMessage());
            return -1L;
        }
    }

    @Override
    public UploadFieResponse shareFile(ShareFileRequest request){
        //查询是否存在db，不存在则先记录
        S3File s3File = fileService.getFileInfo(request.getBucketName(),request.getFileName());
        if(s3File == null){
            //查询服务器是否存在该文件
            if(!doesObjectExist(request.getBucketName(),request.getFileName())){
                log.info("文件信息不存在！");
                return null;
            }
            //新增记录
            Long fileKey = addNewFile(request.getBucketName(),request.getFileName());
            s3File = fileService.getFileInfo(fileKey);
        }else {
            //判断是否源文件
            if(s3File.getSourceFileId() != null && s3File.getSourceFileId() > 0){
                //根据id查询文件信息
                s3File = fileService.getFileInfo(s3File.getSourceFileId());
                if(s3File == null){//找不到源文件
                    log.warn("--shareFile--找不到源文件:{}",request);
                    return null;
                }
            }
        }

        String shareFileName = getFileNameAddTimeStamp(request.getFileName());

        //设置分享-插入记录
        s3File.setSourceFileId(s3File.getId());
        s3File.setId(null);
        s3File.setContentMd5("");
        s3File.setFileName(shareFileName);
        s3File.setMaxAmount(request.getMaxDownloadAmout());
        Date createDate = new Date();
        s3File.setCreateTime(createDate);
        //获取下载有效截至时间
        if(!StringUtils.isEmpty(request.getMaxHours())){
            Date validityTime = DateUtil.addDateHour(createDate, request.getMaxHours());
            s3File.setValidityTime(validityTime);
        }
        Long fileKey = fileService.addFileInfo(s3File);
        log.info("--分享插入记录返回--fileKey：{}", fileKey);

        UploadFieResponse response = new UploadFieResponse();
        response.setFileName(shareFileName);
        response.setFolderName(request.getBucketName());
        return response;
    }

    @Override
    public boolean deleteFile(String bucketName, String key) {
        S3Client s3 = getS3Client(getServiceNo(bucketName));

        try {
//            DeleteObjectsRequest
//            s3.deleteObjects()
            DeleteObjectResponse deleteObjectResponse = s3.deleteObject(DeleteObjectRequest.
                    builder().
                    bucket(bucketName).
                    key(key).
                    build());
            log.info("deleteObjectResponse:"+deleteObjectResponse.toString());
            log.info("deleteObjectResponse.deleteMarker():"+deleteObjectResponse.deleteMarker());
            return deleteObjectResponse.deleteMarker();
        }catch (Exception e){
            log.info("--deleteFile文件删除异常：{}--", e);
            throw new AppLogicException("文件删除失败");
        }
    }

    @Override
    public List<FileVersionResponse> getS3FileVersion(String bucketName, String key) {
        S3Client s3 = getS3Client(getServiceNo(bucketName));
        ListObjectVersionsResponse response = s3.
                listObjectVersions(ListObjectVersionsRequest.builder().
                        bucket(bucketName).
                        prefix(key).
                        build());

        List<FileVersionResponse> listVersion = new ArrayList<>();
        List<ObjectVersion> list = response.versions();
        list.forEach(obj -> {
            FileVersionResponse version = new FileVersionResponse();
            version.setKey(obj.key());
            version.setETag(obj.eTag());
            version.setIsLatest(obj.isLatest());
            version.setLastModified(obj.lastModified());
            version.setSize(obj.size());
            version.setVersionId(obj.versionId());
            version.setStorageClass(obj.storageClassAsString());
            listVersion.add(version);
        });

        return listVersion;
    }

    /**文件名称加时间戳
     *
     * @description
     * @author miaomingming
     * @date 9:54 2020/2/14
     * @param fileName
     * @return java.lang.String
     **/
    private String getFileNameAddTimeStamp(String fileName){
        if(fileName == null){
            return System.currentTimeMillis() + "";
        }else if(fileName.indexOf(".") == -1){
            return fileName + "_" + System.currentTimeMillis();
        }else{
            return fileName.substring(0,fileName.lastIndexOf("."))
                    + "_" + System.currentTimeMillis()
                    + fileName.substring(fileName.lastIndexOf("."));
        }
    }

    /**
     * 添加文件信息
     * @param bucketName
     * @param fileName
     * @return
     */
    private Long addNewFile(String bucketName,String fileName){
        //获取文件大小
        Long fileSize = getFileSize(bucketName,fileName);
        S3File s3FileDB = new S3File();
        s3FileDB.setServiceNo(getServiceNo(bucketName));
        s3FileDB.setFolderName(bucketName);
        s3FileDB.setFileName(fileName);
        s3FileDB.setFileSize(fileSize);
        s3FileDB.setCreateTime(new Date());

        //文件内容md5码
        S3Client s3Client = getS3Client(s3FileDB.getServiceNo());
        ResponseBytes<GetObjectResponse> objectAsBytes = s3Client.getObject(b -> b.bucket(bucketName).key(fileName),
                ResponseTransformer.toBytes());
        String md5 = MD5Utils.encode(objectAsBytes.asUtf8String());
        s3FileDB.setContentMd5(md5);

        return fileService.addFileInfo(s3FileDB);
    }

    /**
     * 判断服务器上文件是否存在
     * @param bucketName
     * @param fileId
     * @return
     */
    public boolean doesObjectExist(String bucketName, String fileId){
        S3Client s3 = getS3Client(getServiceNo(bucketName));
        log.info("==doesObjectExist==bucketName:{},fileId:{}",bucketName,fileId);
        try {
            ResponseBytes<GetObjectResponse> objectAsBytes = s3.getObject(b -> b.bucket(bucketName).key(fileId),
                    ResponseTransformer.toBytes());
            byte[] bytes = objectAsBytes.asByteArray();
            if(null != bytes && bytes.length > 0){
                return true;
            }
        }catch (NoSuchKeyException e){
            log.error("--doesObjectExist异常--NoSuchKeyException:{}", fileId);
        }catch (Exception e){
            log.error("--doesObjectExist异常--:{}", e);
        }
        return false;
    }

}