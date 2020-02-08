package com.zysl.aws.service.impl;

import com.zysl.aws.common.result.Result;
import com.zysl.aws.model.UploadFileRequest;
import com.zysl.aws.service.AmasonService;
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

import javax.servlet.http.HttpServletResponse;
import java.util.*;


@Service
@Slf4j
public class AmasonServiceImpl implements AmasonService {

    @Autowired
    private S3Client s3;

    @Override
    public List<Bucket> getBuckets() {
        log.info("--getBuckets获取存储桶信息--");
        ListBucketsRequest listBucketsRequest = ListBucketsRequest.builder().build();
        ListBucketsResponse response = s3.listBuckets(listBucketsRequest);
        List<Bucket> bucketList = response.buckets();
        log.info("---bucketList:{}", bucketList);

        return bucketList;
    }

    @Override
    public Bucket createBucket(String bucketName) {
        log.info("---创建文件夹createBucket:---bucketName:{}",bucketName);
        boolean flag = doesBucketExist(bucketName);
        log.info("--存储桶是否存在--flag：{}", flag);
        if(flag){
            log.info("--文件夹已经存在--");
            return getBucket(bucketName).get();
        }else{
            CreateBucketResponse response = s3.createBucket(CreateBucketRequest.builder().bucket(bucketName).build());
            response.responseMetadata();
            log.info("---创建文件夹response:{}", response);
            return null;
        }
    }

    @Override
    public Result deleteBucket(String bucketName) {
        if(!doesBucketExist(bucketName)){
            log.info("--文件夹不存在--");
            return Result.error("NoSuchBucket");
        }else{
            ListObjectsResponse objectList = s3.listObjects(ListObjectsRequest.builder().bucket(bucketName).build());
            if(!CollectionUtils.isEmpty(objectList.contents())){
                log.info("--文件下有文件--");
                return Result.error("BucketNotEmpty");
            }else{
                s3.deleteBucket(DeleteBucketRequest.builder().bucket(bucketName).build());
                return Result.success();
            }
        }
    }

    @Override
    public Result getFilesByBucket(String bucketName) {
            if(!doesBucketExist(bucketName)){
                log.info("--文件夹不存在--");
                return Result.error("NoSuchBucket");
            }else{
                ListObjectsResponse objectList = s3.listObjects(ListObjectsRequest.builder().bucket(bucketName).build());
                objectList.contents();
                return Result.success(objectList.contents());
            }
    }

    @Override
    public Result uploadFile(UploadFileRequest request) {
        log.info("===uploadFile.param:{}=====",request);
        Map<String, Object> map = new HashMap<>();

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

        if(doesBucketExist(bucketName)){
            log.info("--文件夹存在--");
            //文件id是否为空
            if(!StringUtils.isEmpty(fileId)){
                //20200118,贾总要求出去文件判断-->业务层处理，这里直接替换
//                //判断文件是否存在服务器
//                Boolean falg = doesObjectExist(bucketName, fileId);
//                log.info("--判断文件是否存在服务器--falg:{},inplace:{}", falg, inplace);
//                //如果文件存在且 文件需要覆盖
//                if(!falg || (falg && InplaceEnum.COVER.getCode().equals(inplace))){
                //上传文件
//                    log.info("--文件夹存在，执行文件上传--");
                //上传文件
                upload(bucketName, fileId, data);
                map.put("fileId", fileId);
                return Result.success(map);
//                }else{
//                    log.info("--文件已存在--");
//                    return Result.error("文件已存在");
//                }
            }else{
                fileId = UUID.randomUUID().toString().replaceAll("-","");
                //上传文件
                upload(bucketName, fileId, data);
                map.put("fileId", fileId);
                return Result.success(map);
            }
        }else {
            log.info("--文件夹不存在:{}--",request.getBucketName());
            return Result.error(bucketName + "文件夹不存在,请先创建");
        }
    }

    public boolean doesBucketExist(String bucketName){
        ListBucketsRequest listBucketsRequest = ListBucketsRequest.builder().build();
        ListBucketsResponse response = s3.listBuckets(listBucketsRequest);
        List<Bucket> bucketList = response.buckets();
        log.info("---bucketList:{}", bucketList);
        for (Bucket bucket: bucketList ) {
            if(bucketName.equals(bucket.name()))
               return true;
        }
        return false;
    }

    /**
     * 判断服务器上文件是否存在
     * @param bucketName
     * @param fileId
     * @return
     */
    public boolean doesObjectExist(String bucketName, String fileId){
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
    public void upload(String bucketName, String fileId, byte[] data){
        log.info("--upload开始上传文件，入参bucketName：{}-,fileId:{}-,data:{}", bucketName, fileId, data.length);

        try {
            PutObjectResponse putObjectResponse = s3.putObject(PutObjectRequest.builder().bucket(bucketName).key(fileId)
                            .build(),
                    RequestBody.fromBytes(data));
            log.info("--上传文件--putObjectResponse:{}", putObjectResponse);
        }catch (Exception e){
            log.info("--上传文件异常--：", e);
        }
    }

    @Override
    public String downloadFile(HttpServletResponse response, String bucketName, String key) {

        if(doesBucketExist(bucketName)){
            log.info("--文件夹存在--");
            ResponseBytes<GetObjectResponse> objectAsBytes = s3.getObject(b -> b.bucket(bucketName).key(key),
                    ResponseTransformer.toBytes());
            String str = objectAsBytes.asUtf8String();
            return str;
        }else {
            log.info("--文件夹不存在--");
            return null;
        }
    }

    @Override
    public Result deleteFile(String bucketName, String key) {
       /* if(s3.doesBucketExist(bucketName)){
            s3.deleteObject(bucketName, key);
            return Result.success();
        }else{
            return Result.error(bucketName+"不存在");
        }*/
       return null;
    }

    @Override
    public Optional<Bucket> getBucket(String bucketName) {
//        return s3.listBuckets().stream().filter(b -> b.getName().equals(bucketName)).findFirst();
        return null;
    }

   /* @Override
    public void upload(MultipartFile file, String uid) {
        String tempFileName = "wyb";//Md5Util.md5(uid+file.getOriginalFilename()+System.currentTimeMillis())+"."+ StringUtil.getSuffix(file.getOriginalFilename());
        String originalFileName = file.getOriginalFilename();
        String contentType = file.getContentType();
        long fileSize = file.getSize();
        String dateDir = new SimpleDateFormat("/yyyy/MM/dd").format(new Date());
        String tempBucketName = "bucketName"+dateDir;
        String filePath = dateDir+"/" + tempFileName;
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(contentType);
        objectMetadata.setContentLength(fileSize);
        try {
            PutObjectResult putObjectResult = s3.putObject(tempBucketName, tempFileName, file.getInputStream(), objectMetadata);
        } catch (AmazonServiceException e) {
//            throw new BizException(CodeMsg.AMAZON_ERROR.fillArgs(e.getErrorMessage()));
        } catch (IOException e) {
//            throw new BizException(CodeMsg.AMAZON_ERROR.fillArgs(e.getMessage()));
        }
//        AmazonFileModel amazonFileModel = new AmazonFileModel ();
//        amazonFileModel .setFileName(originalFileName);
//        amazonFileModel .setFileSize(fileSize);
//        amazonFileModel .setFileType(contentType);
//        amazonFileModel .setFilePath(filePath);
//        amazonFileModel .setUrl("http://zhanglf-bucket.s3.cn-north-1tainiu.com"+filePath);

    }*/


}
