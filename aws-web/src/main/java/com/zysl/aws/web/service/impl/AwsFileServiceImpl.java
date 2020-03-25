package com.zysl.aws.web.service.impl;

import com.zysl.aws.web.config.BizConfig;
import com.zysl.aws.web.config.BizConstants;
import com.zysl.aws.web.enums.DeleteStoreEnum;
import com.zysl.aws.web.model.*;
import com.zysl.aws.web.model.db.S3File;
import com.zysl.aws.web.service.AwsFileService;
import com.zysl.aws.web.service.FileService;
import com.zysl.aws.web.utils.DateUtil;
import com.zysl.aws.web.utils.MD5Utils;
import com.zysl.cloud.utils.common.AppLogicException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import sun.misc.BASE64Decoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.*;

/**
 * 文件处理类
 */
@Service
@Slf4j
public class AwsFileServiceImpl extends BaseService implements AwsFileService {

    @Autowired
    private FileService fileService;
    @Autowired
    private BizConfig bizConfig;

    @Override
    public UploadFieResponse uploadFile(UploadFileRequest request) {
        log.info("--uploadFile下载文件开始时间--：{}", System.currentTimeMillis());
        UploadFieResponse response = new UploadFieResponse();

        String bucketName = request.getBucketName();
        String fileId = request.getFileId();
//        byte[] data = request.getData().getBytes();
        //进行解密
        BASE64Decoder decoder = new BASE64Decoder();
        byte[] data = null;
        try {
            data = decoder.decodeBuffer(request.getData());
        } catch (IOException e) {
            e.printStackTrace();
        }

        S3Client s3 = getS3Client(getServiceNo(bucketName));
        if(null != doesBucketExist(bucketName)){
            log.info("--bucket存在--");
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

        //上传文件
        PutObjectResponse putObjectResponse = upload(bucketName, fileId, bytes);
        log.info("---上传文件返回-putObjectResponse:{}", putObjectResponse);
        UploadFieResponse response = new UploadFieResponse();
        if(null != putObjectResponse){
            //修改文件信息
            response.setFolderName(bucketName);
            response.setFileName(fileId);
            response.setVersionId(putObjectResponse.versionId());
            return response;
        }else {
            log.info("--文件上传失败--");
            return null;
        }
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
    public byte[] downloadFile(DownloadFileRequest request) {
        log.info("--downloadFile下载文件开始时间--:{}", System.currentTimeMillis());
        S3File s3File = fileService.getFileInfo(request.getBucketName(), request.getFileId());
        log.info("--downloadFile下载文件查询数据库结束时间--:{}", System.currentTimeMillis());
        if(null == s3File){
            log.info("--数据库记录不存在--");
            byte[] bytes = getS3FileInfo(request.getBucketName(), request.getFileId(), request.getVersionId(), "");
            return bytes;
        }
        Long fileKey = s3File.getId();
        //最大可下载次数
        Integer maxAmount = s3File.getMaxAmount();
        //下载有效截至时间
        Date validityTime = s3File.getValidityTime();
        //判断文件是否还在有效期,当前时间小于截至时间则还可以下载
        if(!StringUtils.isEmpty(validityTime) && DateUtil.doCompareDate(new Date(), validityTime) < 0) {
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

            byte[] bytes = getS3FileInfo(bucketName, key, request.getVersionId(), "");

            //下载成功后修改最大下载次数
            if(!StringUtils.isEmpty(maxAmount)){
                log.info("--修改文件下载次数--可用次数maxAmount:{}", maxAmount);
                fileService.updateFileAmount(--maxAmount, fileKey);
                log.info("--updateFileAmount修改文件下载次数结束时间--:{}", System.currentTimeMillis());
            }
            return bytes;
        }else {
            log.info("--文件夹不存在--");
            return null;
        }
    }

  /*  @Override
    public String shareDownloadFile(HttpServletResponse response, DownloadFileRequest request) {
        log.info("--shareDownloadFile下载文件开始时间--:{}", System.currentTimeMillis());
        S3File s3File = fileService.getFileInfo(request.getBucketName(), request.getFileId());
        log.info("--shareDownloadFile下载文件查询数据库结束时间--:{}", System.currentTimeMillis());
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
    }*/

    @Override
    public byte[] shareDownloadFile(HttpServletResponse response, DownloadFileRequest request) {
        //查询文件信息
        S3File s3File = fileService.getFileInfo(request.getBucketName(), request.getFileId());
        if(null != s3File && !StringUtils.isEmpty(s3File.getSourceFileId())){
            return this.downloadFile(request);
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
    public byte[] getS3FileInfo(String bucketName, String key, String versionId, String userId){
        log.info("--调用s3接口下载文件内容入参-bucketName:{},-key:{},versionId:{}", bucketName, key, versionId);
        log.info("--getS3FileInfo下载文件开始时间--:{}", System.currentTimeMillis());

        S3Client s3 = getS3Client(getServiceNo(bucketName));
        log.info("--getS3Client获取初始化对象结束时间--:{}", System.currentTimeMillis());
        try {
            //如果userid不为空，则判断标签是否有权限
            if(!StringUtils.isEmpty(userId)){
                if(!isTageExist(userId, bucketName, key, versionId)){
                    throw new AppLogicException("没有查询权限");
                }
            }

            GetObjectRequest.Builder request = null;
            if(StringUtils.isEmpty(versionId)){
                request = GetObjectRequest.builder().bucket(bucketName).key(key);
            }else {
                request = GetObjectRequest.builder().bucket(bucketName).key(key).versionId(versionId);
            }
            ResponseBytes<GetObjectResponse> objectAsBytes = s3.getObject(request.build(),
                    ResponseTransformer.toBytes());
            log.info("--getObject结束时间--:{}", System.currentTimeMillis());
            GetObjectResponse objectResponse = objectAsBytes.response();

            Date date1 = Date.from(objectResponse.lastModified());
            Date date2 = DateUtil.createDate(bizConfig.DOWNLOAD_TIME);

            byte[] bytes = objectAsBytes.asByteArray();
            log.info("--asByteArray结束时间--:{}", System.currentTimeMillis());
            if(DateUtil.doCompareDate(date1, date2) < 0){
                //进行解码
                BASE64Decoder decoder = new BASE64Decoder();
                byte[] fileContent = decoder.decodeBuffer(new String(bytes));
                return fileContent;
            }else {
                return bytes;
            }

//            byte[] bytes = objectAsBytes.asByteArray();
//            log.info("--asByteArray结束时间--:{}", System.currentTimeMillis());
//            String a = new String(bytes);
//            byte[] aa = a.getBytes();
//            String str = objectAsBytes.asUtf8String();
//            return new String(bytes);
        } catch (Exception e) {
            log.error("--s3接口下载文件信息异常：--{}", e);
            return null;
        }
    }

    /*@Override
    public Long getFileSize(String bucketName, String key) {
        //查询是否存在db，不存在则查询服务器
        S3File s3File = fileService.getFileInfo(bucketName, key);
        if(null == s3File){
            Long fileSize = getS3FileSize(bucketName, key, "");
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
        Long fileSize = getS3FileSize(folderName, FileName, "");
        return fileSize;
    }*/

    /**
     * 调用s3接口查询服务器文件大小
     * @param bucketName
     * @param key
     * @return
     */
    @Override
    public Long getS3FileSize(String bucketName, String key, String versionId){

        FileInfoRequest fileInfoRequest = this.getS3ToFileInfo(bucketName, key, versionId);
        if(null != fileInfoRequest){
            return fileInfoRequest.getContentLength();
        }else{
            log.error("--调用s3接口查询服务器文件大小异常：--{}");
            return -1L;
        }
       /* try {
            HeadObjectRequest headObjectRequest = null;
            if(StringUtils.isEmpty(versionId)){
                headObjectRequest = HeadObjectRequest.builder().bucket(bucketName).key(key).build();
            }else{
                headObjectRequest = HeadObjectRequest.builder().bucket(bucketName).key(key).versionId(versionId).build();
            }
            log.info("--headObject-s3下载接口入参--headObjectRequest:{}", headObjectRequest);
            HeadObjectResponse headObjectResponse = s3.headObject(headObjectRequest);
            Long fileSize = headObjectResponse.contentLength();
            return fileSize;
        }catch (Exception e) {
            log.error("--调用s3接口查询服务器文件大小异常：--{}", e.getMessage());
            return -1L;
        }*/
    }

    @Override
    public FileInfoRequest getS3ToFileInfo(String bucketName, String key, String versionId) {
        S3Client s3 = getS3Client(getServiceNo(bucketName));

        try {
            HeadObjectRequest headObjectRequest = null;
            if(StringUtils.isEmpty(versionId)){
                headObjectRequest = HeadObjectRequest.builder().bucket(bucketName).key(key).build();
            }else{
                headObjectRequest = HeadObjectRequest.builder().bucket(bucketName).key(key).versionId(versionId).build();
            }
            log.info("--headObject-s3下载接口入参--headObjectRequest:{}", headObjectRequest);
            HeadObjectResponse headObjectResponse = s3.headObject(headObjectRequest);

            Long a = Date.from(headObjectResponse.lastModified()).getTime();

            FileInfoRequest fileInfoRequest = new FileInfoRequest();
            fileInfoRequest = transformation(fileInfoRequest, headObjectResponse);

            List<TageDTO> tageList = new ArrayList<>();
            //查询文件标签信息
            List<Tag> tages = this.getObjectTagging(bucketName, key, versionId);
            tages.forEach(obj ->{
                TageDTO tageDTO = new TageDTO();
                tageDTO.setValue(obj.value());
                tageDTO.setKey(obj.key());
                tageList.add(tageDTO);
            });
            fileInfoRequest.setTageList(tageList);
            return fileInfoRequest;
        }catch (Exception e) {
            log.error("--getS3ToFileInfo调用s3接口查询服务器文件信息异常：--{}", e);
            return null;
        }
    }

    public FileInfoRequest transformation(FileInfoRequest fileInfoRequest, HeadObjectResponse headObjectResponse){
        fileInfoRequest.setDeleteMarker(headObjectResponse.deleteMarker());
        fileInfoRequest.setAcceptRanges(headObjectResponse.acceptRanges());
        fileInfoRequest.setExpiration(headObjectResponse.expiration());
        fileInfoRequest.setRestore(headObjectResponse.restore());
        fileInfoRequest.setLastModified(null == headObjectResponse.lastModified() ? null : Date.from(headObjectResponse.lastModified()));
        fileInfoRequest.setContentLength(headObjectResponse.contentLength());
        fileInfoRequest.setETag(headObjectResponse.eTag());
        fileInfoRequest.setMissingMeta(headObjectResponse.missingMeta());
        fileInfoRequest.setVersionId(headObjectResponse.versionId());
        fileInfoRequest.setCacheControl(headObjectResponse.cacheControl());
        fileInfoRequest.setContentDisposition(headObjectResponse.contentDisposition());
        fileInfoRequest.setContentEncoding(headObjectResponse.contentEncoding());
        fileInfoRequest.setContentLanguage(headObjectResponse.contentLanguage());
        fileInfoRequest.setContentType(headObjectResponse.contentType());
        fileInfoRequest.setExpires(null == headObjectResponse.expires() ? null : Date.from(headObjectResponse.expires()));
        fileInfoRequest.setWebsiteRedirectLocation(headObjectResponse.websiteRedirectLocation());
        fileInfoRequest.setMetadata(headObjectResponse.metadata());
        fileInfoRequest.setSseCustomerAlgorithm(headObjectResponse.sseCustomerAlgorithm());
        fileInfoRequest.setSseCustomerKeyMD5(headObjectResponse.sseCustomerKeyMD5());
        fileInfoRequest.setSsekmsKeyId(headObjectResponse.ssekmsKeyId());
        fileInfoRequest.setPartsCount(headObjectResponse.partsCount());
        fileInfoRequest.setObjectLockRetainUntilDate(null == headObjectResponse.objectLockRetainUntilDate() ? null : Date.from(headObjectResponse.objectLockRetainUntilDate()));
        return fileInfoRequest;
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
//        Long fileSize = getFileSize(bucketName,fileName);
        S3File s3FileDB = new S3File();
        s3FileDB.setServiceNo(getServiceNo(bucketName));
        s3FileDB.setFolderName(bucketName);
        s3FileDB.setFileName(fileName);
//        s3FileDB.setFileSize();
        s3FileDB.setCreateTime(new Date());

        //文件内容md5码
        S3Client s3Client = getS3Client(s3FileDB.getServiceNo());
        ResponseBytes<GetObjectResponse> objectAsBytes = s3Client.getObject(b -> b.bucket(bucketName).key(fileName),
                ResponseTransformer.toBytes());
        byte[] bytes = objectAsBytes.asByteArray();
        String md5 = MD5Utils.encode(new String(bytes));
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

    @Override
    public boolean deleteFile(DelObjectRequest request) {
        //获取s3连接对象
        S3Client s3 = getS3Client(getServiceNo(request.getBucketName()));

        try {
            DeleteObjectsRequest deleteObjectsRequest = null;
            if(DeleteStoreEnum.COVER.getCode().equals(request.getDeleteStore())){
                if(StringUtils.isEmpty(request.getVersionId())){
                    //删除整个文件信息
                    List<ObjectIdentifier> objects = new ArrayList<>();
                    //查询文件的版本信息
                    List<FileVersionResponse> versions = getS3FileVersion(request.getBucketName(), request.getKey());
                    versions.forEach(obj -> {
                        ObjectIdentifier objectIdentifier = ObjectIdentifier.builder()
                                .key(obj.getKey())
                                .versionId(obj.getVersionId()).build();
                        objects.add(objectIdentifier);
                    });
                    //删除列表
                    Delete delete = Delete.builder().objects(objects).build();
                    //逻辑删除
                    deleteObjectsRequest = DeleteObjectsRequest.builder()
                            .bucket(request.getBucketName())
                            .delete(delete)
                            .build();
                }else{
                    //删除文件指定版本信息
                    ObjectIdentifier objectIdentifier = ObjectIdentifier.builder()
                            .key(request.getKey())
                            .versionId(request.getVersionId()).build();
                    List<ObjectIdentifier> objects = new ArrayList<>();
                    objects.add(objectIdentifier);
                    Delete delete = Delete.builder().objects(objects).build();

                    //逻辑删除
                    deleteObjectsRequest = DeleteObjectsRequest.builder()
                            .bucket(request.getBucketName())
                            .delete(delete)
                            .build();
                }
            }else{
                ObjectIdentifier objectIdentifier = ObjectIdentifier.builder().key(request.getKey()).build();
                List<ObjectIdentifier> objects = new ArrayList<>();
                objects.add(objectIdentifier);
                Delete delete = Delete.builder().objects(objects).build();

                //逻辑删除
                deleteObjectsRequest = DeleteObjectsRequest.builder()
                        .bucket(request.getBucketName())
                        .delete(delete)
                        .build();
            }
            log.info("---deleteObjects删除对象入参-deleteObjectsRequest：{}", deleteObjectsRequest);
            DeleteObjectsResponse deleteObjectsResponse = s3.deleteObjects(deleteObjectsRequest);
            log.info("deleteObjectResponse:"+deleteObjectsResponse.toString());
            log.info("deleteObjectResponse.deleteMarker():"+deleteObjectsResponse.deleted());
            log.info("deleteObjectsResponse.errors():"+deleteObjectsResponse.errors());
            return CollectionUtils.isEmpty(deleteObjectsResponse.errors());
        }catch (Exception e){
            log.error("--deleteFile文件删除异常：{}--", e);
            throw new AppLogicException("文件删除失败");
        }
    }

    @Override
    public boolean updateFileTage(UpdateFileTageRequest request) {

        //获取s3连接对象
        S3Client s3 = getS3Client(getServiceNo(request.getBucket()));

        List<TageDTO> tageList = request.getTageList();
        //文件tage设置参数
        List<Tag> tagSet = new ArrayList<>();
        tageList.forEach(obj -> {
            tagSet.add(Tag.builder().key(obj.getKey()).value(obj.getValue()).build());
        });
        Tagging tagging = Tagging.builder().tagSet(tagSet).build();

        //设置标签入参
        PutObjectTaggingRequest putObjectTaggingRequest = null;
        //循环处理key
        List<KeyVersionDTO> keyList = request.getKeyList();
        for (KeyVersionDTO obj : keyList) {
            if(!StringUtils.isEmpty(obj.getVersionId())){
                putObjectTaggingRequest = PutObjectTaggingRequest.builder()
                        .bucket(request.getBucket())
                        .key(obj.getKey())
                        .versionId(obj.getVersionId())
                        .tagging(tagging)
                        .build();
            }else{
                putObjectTaggingRequest = PutObjectTaggingRequest.builder()
                        .bucket(request.getBucket())
                        .key(obj.getKey())
                        .tagging(tagging)
                        .build();
            }

            try {
                log.info("--调用s3接口putObjectTagging入参--putObjectTaggingRequest:{}", putObjectTaggingRequest);
                PutObjectTaggingResponse putObjectTaggingResponse = s3.putObjectTagging(putObjectTaggingRequest);
                log.info("--调用s3接口putObjectTagging入参--putObjectTaggingResponse:{}", putObjectTaggingResponse);
            }catch (Exception e){
                log.error("--调用putObjectTagging接口设置标签异常--：{}",e);
                throw new AppLogicException("调用putObjectTagging接口设置标签异常：{}", e);
            }
        }

        return true;
    }

    @Override
    public List<Tag> getObjectTagging(String bucket, String key, String versionId) {
        //获取s3连接对象
        S3Client s3 = getS3Client(getServiceNo(bucket));

        GetObjectTaggingRequest tagging = null;
        if(StringUtils.isEmpty(versionId)){
            tagging = GetObjectTaggingRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build();
        }else{
            tagging = GetObjectTaggingRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .versionId(versionId)
                    .build();
        }

        log.info("--调用s3接口getObjectTagging查询标签入参--tagging:{}", tagging);
        GetObjectTaggingResponse tagResponse = s3.getObjectTagging(tagging);
        List<Tag> tagList = tagResponse.tagSet();
        return tagList;
    }

    @Override
    public boolean isTageExist(TageExistDTO tageDto) {
        log.info("--isTageExist判断标签权限--tageDto:{}", tageDto);
        //获取s3连接对象
        S3Client s3 = getS3Client(getServiceNo(tageDto.getBucket()));

        GetObjectTaggingRequest tagging = null;
        if(!StringUtils.isEmpty(tageDto.getVersionId())){
            tagging = GetObjectTaggingRequest.builder()
                    .bucket(tageDto.getBucket())
                    .key(tageDto.getKey())
                    .versionId(tageDto.getVersionId())
                    .build();
        }else{
            tagging = GetObjectTaggingRequest.builder()
                    .bucket(tageDto.getBucket())
                    .key(tageDto.getKey())
                    .build();
        }

        try {
            log.info("--调用s3接口getObjectTagging查询标签入参--tagging:{}", tagging);
            GetObjectTaggingResponse tagResponse = s3.getObjectTagging(tagging);
            List<Tag> tagList = tagResponse.tagSet();
            log.info("--调用s3接口getObjectTagging查询标签返回--tagList:{}", tagList);
            for (Tag tag : tagList) {
                //判断标签可以是否是owner
                if(BizConstants.TAG_OWNER.equals(tag.key()) &&
                        tageDto.getUserId().equals(tag.value())){
                    //在判断标签value
                    return true;
                }
            }
        }catch (Exception e){
            log.error("--查询标签异常：{}--", e);
        }
        return false;
    }

    @Override
    public boolean isTageExist(String userId, String bucket, String key, String versionId) {
        log.info("--isTageExist判断标签权限--userId:{}，bucket：{},key:{},versionId:{}",
                userId, bucket, key, versionId);
        //获取s3连接对象
        S3Client s3 = getS3Client(getServiceNo(bucket));

        GetObjectTaggingRequest tagging = null;
        if(!StringUtils.isEmpty(versionId)){
            tagging = GetObjectTaggingRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .versionId(versionId)
                    .build();
        }else{
            tagging = GetObjectTaggingRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build();
        }

        try {
            log.info("--调用s3接口getObjectTagging查询标签入参--tagging:{}", tagging);
            GetObjectTaggingResponse tagResponse = s3.getObjectTagging(tagging);
            List<Tag> tagList = tagResponse.tagSet();
            log.info("--调用s3接口getObjectTagging查询标签返回--tagList:{}", tagList);
            for (Tag tag : tagList) {
                //判断标签可以是否是owner
                if(BizConstants.TAG_OWNER.equals(tag.key()) &&
                        userId.equals(tag.value())){
                    //在判断标签value
                    return true;
                }
            }
        }catch (Exception e){
            log.error("--查询标签异常：{}--", e);
        }

        return false;
    }

    @Override
    public CopyObjectResponse copyFile(CopyFileRequest request) {
        //获取s3连接对象
        S3Client s3 = getS3Client(getServiceNo(request.getSourceBucket()));

        List<Tag> tagSet = new ArrayList<>();
        tagSet.add(Tag.builder().key("foo").value("1").build());
        tagSet.add(Tag.builder().key("bar").value("2").build());
        tagSet.add(Tag.builder().key("baz").value("3").build());
        Tagging tagsCopy = Tagging.builder().tagSet(tagSet).build();


        //copySource 目标对象，文件夹+文件地址
        //bucket复制后的文件夹， key 复制后的文件名称
        CopyObjectRequest copyObjectRequest = CopyObjectRequest.builder().
                bucket(request.getDestBucket()).key(request.getDestKey())
                .copySource(request.getSourceBucket()+"/"+request.getSourceKey()).build();

        try {
            log.info("--调用s3 接口复制文件copyObject--copyObjectRequest:{}", copyObjectRequest);
            CopyObjectResponse copyObjectResponse = s3.copyObject(copyObjectRequest);
            log.info("----copyObjectResponse:{}", copyObjectResponse);
            return copyObjectResponse;
        }catch (Exception e){
            log.error("----文件复制异常：{}", e);
            throw new AppLogicException("copyFile文件复制异常");
        }
    }

    @Override
    public void uploadPartCopy(CopyFileRequest request) {
        //获取s3连接对象
        S3Client s3 = getS3Client(getServiceNo(request.getSourceBucket()));
        UploadPartCopyRequest uploadPartCopyRequest = UploadPartCopyRequest.builder()
                .bucket(request.getDestBucket())
                .key(request.getDestKey())
                .copySource(request.getSourceBucket()+"/"+request.getSourceKey())
                .build();
        UploadPartCopyResponse uploadPartCopyResponse = s3.uploadPartCopy(uploadPartCopyRequest);

        System.out.println("uploadPartCopyResponse:"+uploadPartCopyResponse);
    }

    @Override
    public boolean moveFile(CopyFileRequest request) {
        //先复制文件
        CopyObjectResponse copyObjectResponse = this.copyFile(request);
        if(null != copyObjectResponse){
            //复制成功后，物理删除源文件
            DelObjectRequest delRequest = new DelObjectRequest();
            delRequest.setBucketName(request.getSourceBucket());
            delRequest.setKey(request.getSourceKey());
            delRequest.setDeleteStore(DeleteStoreEnum.COVER.getCode());
            boolean delFlag = this.deleteFile(delRequest);
            return delFlag;
        }
        return false;
    }


    @Override
    public void restoreObject(ResObjectRequest request) {
        //获取s3连接对象
        S3Client s3 = getS3Client(getServiceNo(request.getBucketName()));
        RestoreObjectRequest restoreObjectRequest = null;
        if(StringUtils.isEmpty(request.getVersionId())){
            restoreObjectRequest = RestoreObjectRequest.builder()
                    .bucket(request.getBucketName())
                    .key(request.getKey()).build();
        }else {
            restoreObjectRequest = RestoreObjectRequest.builder()
                    .bucket(request.getBucketName())
                    .key(request.getKey()).versionId(request.getVersionId()).build();
        }

        log.info("---restoreObject还原删除对象入参-restoreObjectRequest：{}", restoreObjectRequest);



//        s3.re
        RestoreObjectResponse restoreObjectResponse = s3.restoreObject(restoreObjectRequest);
        log.info("---restoreObject还原删除对象入参-restoreObjectRequest：{}", restoreObjectResponse.toString());

    }

    @Override
    public void abortMultipartUpload(byte[] bytes){
        S3Client s3 = getS3Client(getServiceNo("test-yy10"));


//
//        CompleteMultipartUploadRequest
//        s3.completeMultipartUpload();


        //上传文件
//        PutObjectRequest putObjectRequest = PutObjectRequest.builder().build();

//        AbortMultipartUploadRequest request = AbortMultipartUploadRequest.builder().build();

        CreateMultipartUploadRequest createRequest = CreateMultipartUploadRequest.builder()
                .bucket("test-yy10")
                .key("multi.txt")
                .build();
        CreateMultipartUploadResponse createResponse = s3.createMultipartUpload(createRequest);
        String uploadId = createResponse.uploadId();

        UploadPartRequest uploadPartRequest = UploadPartRequest.builder()
                .bucket("test-yy10")
                .key("multi.txt")
                .uploadId(uploadId)
                .partNumber(1)
                .build();
        RequestBody requestBody = RequestBody.fromBytes(bytes);
        UploadPartResponse uploadPartResponse = s3.uploadPart(uploadPartRequest,requestBody);
        String eTag = uploadPartResponse.eTag();

        CompletedPart part1 = CompletedPart.builder()
                .partNumber(1)
                .eTag(eTag).build();


        List<CompletedPart> completedParts = new ArrayList<>();
        completedParts.add(part1);

        CompletedMultipartUpload completedMultipartUpload = CompletedMultipartUpload.builder()
                .parts(completedParts).build();
        CompleteMultipartUploadRequest completeMultipartUploadRequest =
                CompleteMultipartUploadRequest.builder()
                        .bucket("test-yy10")
                        .key("multi.txt")
                        .uploadId(uploadId)
                        .multipartUpload(completedMultipartUpload)
                        .build();
        CompleteMultipartUploadResponse completeMultipartUploadResponse = s3.completeMultipartUpload(completeMultipartUploadRequest);

        log.info("完成多部分上载completeMultipartUploadResponse:{}",completeMultipartUploadResponse);
        //中止分段上传， 提供上传 ID、存储桶名称和键名
                //s3.abortMultipartUpload()

    }

    private ByteBuffer getRandomByteBuffer(int size) {
        byte[] b = new byte[size];
        new Random().nextBytes(b);
        return ByteBuffer.wrap(b);
    }
}
