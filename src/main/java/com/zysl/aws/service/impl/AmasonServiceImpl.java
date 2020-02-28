package com.zysl.aws.service.impl;

import com.zysl.aws.common.result.Result;
import com.zysl.aws.config.BizConfig;
import com.zysl.aws.model.FileInfo;
import com.zysl.aws.model.ShareFileRequest;
import com.zysl.aws.model.UploadFileRequest;
import com.zysl.aws.model.db.S3File;
import com.zysl.aws.model.db.S3Folder;
import com.zysl.aws.service.AmasonService;
import com.zysl.aws.service.FileService;
import com.zysl.aws.utils.BatchListUtil;
import com.zysl.aws.utils.DateUtil;
import com.zysl.aws.utils.MD5Utils;
import com.zysl.aws.utils.S3ClientFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;
import java.util.*;


@Service
@Slf4j
public class AmasonServiceImpl implements AmasonService {

    @Autowired
    private S3ClientFactory s3ClientFactory;

    @Autowired
    private FileService fileService;

    @Autowired
    private BizConfig bizConfig;


    public S3Client getS3Client(String bucketName){
//        bucketName = "test-yy01";
        return s3ClientFactory.getS3Client(s3ClientFactory.getServerNo(bucketName));
    }

    @Override
    public List<Bucket> getBuckets() {
        log.info("--getBuckets获取存储桶信息--");
        S3Client s3 = getS3Client("");
        ListBucketsRequest listBucketsRequest = ListBucketsRequest.builder().build();
        ListBucketsResponse response = s3.listBuckets(listBucketsRequest);
        List<Bucket> bucketList = response.buckets();
        log.info("---bucketList:{}", bucketList);

        return bucketList;
    }

    @Override
    public Result createBucket(String bucketName, String serviceNo) {
        log.info("---创建文件夹createBucket:---bucketName:{},serviceName:{}",bucketName, serviceNo);
        S3Client s3 = s3ClientFactory.getS3Client(serviceNo);
        S3Folder s3Folder = doesBucketExist(bucketName);
        log.info("--存储桶是否存在--s3Folder：{}", s3Folder);
        if(null != s3Folder){
            log.info("--文件夹已经存在--");
            return Result.success("文件夹已经存在");
        }else{
            CreateBucketResponse response = s3.createBucket(CreateBucketRequest.builder().bucket(bucketName).build());
            String fileName = response.location();
            //保存文件夹信息
            int num = fileService.insertFolderInfo(bucketName, serviceNo);
            log.info("--文件信息保存返回--num:{}", num);
            return Result.success(fileName);
        }
    }

    @Override
    public Result deleteBucket(String bucketName) {
        S3Client s3 = getS3Client(bucketName);
        if(null == doesBucketExist(bucketName)){
            log.info("--文件夹不存在--");
            return Result.error("NoSuchBucket");
        }else{
            ListObjectsResponse objectList = s3.listObjects(ListObjectsRequest.builder().bucket(bucketName).build());
            if(!CollectionUtils.isEmpty(objectList.contents())){
                log.info("--文件下有文件--");
                return Result.error("BucketNotEmpty");
            }else{
                DeleteBucketResponse deleteBucketResponse = s3.deleteBucket(DeleteBucketRequest.builder().bucket(bucketName).build());
                deleteBucketResponse.responseMetadata();

                //删除文件夹信息
                fileService.deleteFolderByName(bucketName);

                return Result.success();
            }
        }
    }

    @Override
    public Result getFilesByBucket(String bucketName) {
        List<FileInfo> fileList = new ArrayList<>();
        S3Client s3 = getS3Client(bucketName);

        ListObjectsV2Response result = s3.listObjectsV2(ListObjectsV2Request.builder().
                bucket(bucketName).fetchOwner(true).build());

        do{
            List<S3Object> list = result.contents();
            List<FileInfo> resultList = addFileInfo(list);
            //合并list结果集
            fileList.addAll(resultList);
            String s = list.get(list.size()-1).key();
            result = s3.listObjectsV2(ListObjectsV2Request.builder().bucket(bucketName).startAfter(s).build());
        }while (result.isTruncated());

        List<S3Object> list = result.contents();
        List<FileInfo> resultList = addFileInfo(list);
        //合并list结果集
        fileList.addAll(resultList);
        log.info("-----objectList.contents().fileList：{}", fileList.size());

        return Result.success(fileList);
    }

    public List<FileInfo> addFileInfo(List<S3Object> list){
        List<FileInfo> fileList = new ArrayList<>();
        list.stream().forEach(obj -> {
            FileInfo fileInfo = new FileInfo();
            fileInfo.setKey(obj.key());
            fileInfo.setETag(obj.eTag());
            fileInfo.setLastModified(obj.lastModified());
            fileInfo.setSize(obj.size());
            fileList.add(fileInfo);
        });
        log.info("-----fileList：{}", fileList.size());
        return fileList;
    }

    @Override
    public Result uploadFile(UploadFileRequest request) {
        Map<String, Object> map = new HashMap<>();

       /* try {
            String filePath = "D:\\tmp\\testFile\\tt01.doc";

            File file = new File(filePath);
            FileInputStream inputStream = new FileInputStream(file);

            byte[] bytes = new byte[(int) file.length()];
            inputStream.read(bytes);
            inputStream.close();

            BASE64Encoder encoder = new BASE64Encoder();
            String str = encoder.encode(bytes);
            request.setData(str);
        }catch (Exception e){

        }*/


        if(StringUtils.isEmpty(request.getBucketName())){
            return Result.error("入参bucketName不能为空");
        }
        if(StringUtils.isEmpty(request.getData())){
            return Result.error("入参data不能为空");
        }

        String bucketName = request.getBucketName();
        String fileId = request.getFileId();
        byte[] data = request.getData().getBytes();
        //是否覆盖 0不覆盖 1覆盖
        String inplace = request.getInplace();

        S3Client s3 = getS3Client(bucketName);
        boolean upFlag = false;
        if(null != doesBucketExist(bucketName)){
            log.info("--文件夹存在--");
            if(StringUtils.isEmpty(fileId)){
                fileId = UUID.randomUUID().toString().replaceAll("-","");
            }
            /**
             * 根据文件内容md5值判断文件是否存在，存在则直接返回
             */
            //文件内容md5
            String md5Content = MD5Utils.encode(request.getData());
            S3File s3File = fileService.queryFileInfoByMd5(md5Content);
            //文件信息存在
            if(null != s3File){
                //文件存在则直接返回
                map.put("bucketName", s3File.getFolderName());
                map.put("fileId", s3File.getFileName());
                return Result.success(map);
            }else{
                //文件不存在，则上传
                //上传文件
                upFlag = upload(bucketName, fileId, data);

                if(upFlag){
                    //上传时间
                    Date uploadTime = new Date();
                    //根据文件夹名称获取服务器编号
                    String serverNo = s3ClientFactory.getServerNo(request.getBucketName());
                    //文件大小
                    long fileSize = request.getData().length();

                    S3File addS3File = new S3File();
                    //服务器编号
                    addS3File.setServiceNo(serverNo);
                    //文件名称
                    addS3File.setFileName(request.getFileId());
                    //文件夹名称
                    addS3File.setFolderName(request.getBucketName());
                    //文件大小
                    addS3File.setFileSize(fileSize);
                    //最大可下载次数
                    addS3File.setMaxAmount(request.getMaxAmount());
                    //上传时间
                    addS3File.setUploadTime(uploadTime);
                    //创建时间
                    addS3File.setCreateTime(uploadTime);
                    //获取下载有效截至时间
                    if(!StringUtils.isEmpty(request.getValidity())){
                        Date validityTime = DateUtil.addDateHour(uploadTime, request.getValidity());
                        addS3File.setValidityTime(validityTime);
                    }
                    //文件内容md5
                    addS3File.setContentMd5(md5Content);
                    //向数据库保存文件信息
                    fileService.addFileInfo(addS3File);
                    map.put("bucketName", request.getBucketName());
                    map.put("fileId", fileId);
                    return Result.success(map);
                }else {
                    return Result.error("文件上传失败");
                }
            }
        }else {
            log.info("--文件夹不存在:{}--",request.getBucketName());
            return Result.error(bucketName + "文件夹不存在,请先创建");
        }
    }

    /**
     * 判断文件夹是否存在
     * @param bucketName
     * @return
     */
    public S3Folder doesBucketExist(String bucketName ){
        List<S3Folder> folderList = fileService.queryS3FolderInfo();
        for (S3Folder obj : folderList) {
            if(bucketName.equals(obj.getFolderName())){
                return obj;
            }
        }
        return null;
    }

    /**
     * 判断服务器上文件是否存在
     * @param bucketName
     * @param fileId
     * @return
     */
    public boolean doesObjectExist(String bucketName, String fileId){
        S3Client s3 = getS3Client(bucketName);
        log.info("==doesObjectExist==bucketName:{},fileId:{}",bucketName,fileId);
        try {
            ResponseBytes<GetObjectResponse> objectAsBytes = s3.getObject(b -> b.bucket(bucketName).key(fileId),
                    ResponseTransformer.toBytes());
            byte[] bytes = objectAsBytes.asByteArray();
            if(null != bytes && bytes.length > 0){
                return true;
            }
        }catch (NoSuchKeyException e){
            log.warn("--doesObjectExist异常--NoSuchKeyException:{}", fileId);
        }catch (Exception e){
            log.error("--doesObjectExist异常--:{}", e);
        }
        return false;
    }

    /**
     * 文件上传
     * @param bucketName
     * @param fileId
     * @param data
     */
    @Override
    public boolean upload(String bucketName, String fileId, byte[] data){
        log.info("--upload开始上传文件，入参bucketName：{}-,fileId:{}-,data:{}", bucketName, fileId, data.length);
        S3Client s3 = getS3Client(bucketName);
        try {
            PutObjectResponse putObjectResponse = s3.putObject(PutObjectRequest.builder().bucket(bucketName).key(fileId)
                            .build(),
                    RequestBody.fromBytes(data));
            log.info("--上传文件--putObjectResponse:{}", putObjectResponse);
            if(null != putObjectResponse){
                return true;
            }
        }catch (Exception e){
            log.info("--上传文件异常--：", e);
        }
        return false;
    }

    @Override
    public String downloadFile(HttpServletResponse response, String bucketName, String key) {
        S3File s3File = fileService.getFileInfo(bucketName,key);
        if(null == s3File){
            log.info("--数据库记录不存在--");
            String str = getS3FileInfo(bucketName, key);
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

        // 判断是否源文件
        if (s3File != null && s3File.getSourceFileId() != null && s3File.getSourceFileId() > 0) {
          s3File = fileService.getFileInfo(s3File.getSourceFileId());
            bucketName = s3File.getFolderName();
            key = s3File.getFileName();
        }

        String folderName = bucketName;
        String fileName = key;
        if(null != doesBucketExist(folderName)){
            log.info("--文件夹存在--");

            String str = getS3FileInfo(bucketName, key);

            //下载成功后修改最大下载次数
            if(!StringUtils.isEmpty(maxAmount)){
                log.info("--修改文件下载次数--可用次数maxAmount:{}", maxAmount);
                fileService.updateFileAmount(--maxAmount, fileKey);
            }

            return str;
        }else {
            log.info("--文件夹不存在--");
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
    public String getS3FileInfo(String bucketName, String key){
        log.info("--调用s3接口下载文件内容入参-bucketName:{},-key:{}", bucketName, key);
        S3Client s3 = getS3Client(bucketName);
        try {
            ResponseBytes<GetObjectResponse> objectAsBytes = s3.getObject(b ->
                            b.bucket(bucketName).key(key),
                    ResponseTransformer.toBytes());
            byte[] bytes = objectAsBytes.asByteArray();
//            String a = new String(bytes);
//            byte[] aa = a.getBytes();
//            String str = objectAsBytes.asUtf8String();
            return new String(bytes);
        } catch (Exception e) {
            log.info("--s3接口下载文件信息异常：--{}", e);
            return null;
        }
    }

    @Override
    public Result deleteFile(String bucketName, String key) {
        S3Client s3 = getS3Client(bucketName);

        DeleteObjectResponse deleteObjectResponse = s3.deleteObject(DeleteObjectRequest.
                builder().
                bucket(bucketName).
                key(key).
                build());
        log.info("deleteObjectResponse:"+deleteObjectResponse.toString());
        log.info("deleteObjectResponse.deleteMarker():"+deleteObjectResponse.deleteMarker());

       return null;
    }

    @Override
    public Result getFileSize(String bucketName, String key) {
        //查询是否存在db，不存在则查询服务器
        S3File s3File = fileService.getFileInfo(bucketName, key);
        if(null == s3File){
            Long fileSize = getS3FileSize(bucketName, key);
            if(fileSize >= 0){
                return Result.success(fileSize);
            }else {
                return Result.error("文件不存在");
            }
        }
        //判断是否源文件
        if(s3File.getSourceFileId() != null && s3File.getSourceFileId() > 0){
            //根据id查询文件信息
            s3File = fileService.getFileInfo(s3File.getSourceFileId());
            if(s3File == null){//找不到源文件
                log.warn("--getFileSize--找不到源文件key:{}",key);
                return Result.error("找不到源文件");
            }
        }

        String folderName = s3File.getFolderName();
        String FileName = s3File.getFileName();
        //查询文件大小
        Long fileSize = getS3FileSize(folderName, FileName);
        if(fileSize >= 0){
            return Result.success(fileSize);
        }else {
            return Result.error("文件不存在");
        }
    }

    /**
     * 调用s3接口查询服务器文件大小
     * @param bucketName
     * @param key
     * @return
     */
    @Override
    public Long getS3FileSize(String bucketName, String key){
        S3Client s3 = getS3Client(bucketName);
        try {
            HeadObjectResponse headObjectResponse = s3.headObject(b ->
                    b.bucket(bucketName).key(key));
            Long fileSize = headObjectResponse.contentLength();
            return fileSize;
        }catch (Exception e) {
            log.info("--调用s3接口查询服务器文件大小异常：--{}", e.getMessage());
            return -1L;
        }
    }

    @Override
    public Result shareFile(ShareFileRequest request){
        //查询是否存在db，不存在则先记录
        S3File s3File = fileService.getFileInfo(request.getBucketName(),request.getFileName());
        if(s3File == null){
            //查询服务器是否存在该文件
            if(!doesObjectExist(request.getBucketName(),request.getFileName())){
                return Result.error("文件不文件");
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
                    return Result.error("找不到源文件");
                }
            }
        }

        String shareFileName = getFileNameAddTimeStamp(request.getFileName());
        //设置分享-插入记录
        s3File.setSourceFileId(s3File.getId());
        s3File.setId(null);
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
        Map<String, String> resultMap = new HashMap<>();
        //文件夹名称
        resultMap.put("folderName", request.getBucketName());
        //文件名称
        resultMap.put("fileName", shareFileName);
        return Result.success(resultMap);
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
        Result result = getFileSize(bucketName,fileName);
        Long fileSize = (Long) result.getData();
        S3File s3FileDB = new S3File();
        s3FileDB.setServiceNo(s3ClientFactory.getServerNo(bucketName));
        s3FileDB.setFolderName(bucketName);
        s3FileDB.setFileName(fileName);
        s3FileDB.setFileSize(fileSize);
        s3FileDB.setCreateTime(new Date());

      //文件内容md5码
      S3Client s3Client = s3ClientFactory.getS3Client(s3FileDB.getServiceNo());
      ResponseBytes<GetObjectResponse> objectAsBytes = s3Client.getObject(b -> b.bucket(bucketName).key(fileName),
          ResponseTransformer.toBytes());
      String md5 = MD5Utils.encode(objectAsBytes.asUtf8String());
      s3FileDB.setContentMd5(md5);

      return fileService.addFileInfo(s3FileDB);
    }


}
