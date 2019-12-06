package com.zysl.aws.service.impl;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import com.zysl.aws.common.result.CodeMsg;
import com.zysl.aws.common.result.Result;
import com.zysl.aws.enums.InplaceEnum;
import com.zysl.aws.model.UploadFileRequest;
import com.zysl.aws.service.AmasonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Slf4j
public class AmasonServiceImpl implements AmasonService {

    @Autowired
    private AmazonS3Client s3;

//    @Value("${cloud.aws.bucket}")
//    private String bucket;

    @Override
    public List<Bucket> getBuckets() {
        List<Bucket> bucketList = s3.listBuckets();

        return bucketList;
    }

    @Override
    public Bucket createBucket(String bucketName) {
        log.info("---创建文件夹createBucket:---bucketName:{}",bucketName);
        boolean flag = s3.doesBucketExistV2(bucketName);
        log.info("--存储桶是否存在--flag：{}", flag);
        if(flag){
            log.info("--文件夹已经存在--");
            s3.createBucket(bucketName);
            return getBucket(bucketName);
        }else{
            Bucket bucket = s3.createBucket(bucketName);
            return bucket;
        }
    }

    @Override
    public Result deleteBucket(String bucketName  ) {
        if(!checkBucketExists(s3, bucketName)){
            log.info("--文件夹不存在--");
            return Result.error("NoSuchBucket");
        }else{
            ObjectListing objectListing = s3.listObjects(bucketName);
            List<S3ObjectSummary> s3ObjectSummaries = objectListing.getObjectSummaries();
            if(!CollectionUtils.isEmpty(s3ObjectSummaries)){
                log.info("--文件下有文件--");
                return Result.error("BucketNotEmpty");
            }else{
                try {
                    s3.deleteBucket(bucketName);
                    return Result.success();
                }catch (Exception e){
                    log.info("--文件夹删除异常：{}--", e);
                    return Result.error(CodeMsg.MYSQL_DELETE_EXCEPTION);
                }
            }
        }
    }

    @Override
    public Result getFilesByBucket(String bucketName) {
        if(!checkBucketExists(s3, bucketName)){
            log.info("--文件夹不存在--");
            return Result.error("NoSuchBucket");
        }else{
            ObjectListing objectListing = s3.listObjects(bucketName);
            List<S3ObjectSummary> s3ObjectSummaries = objectListing.getObjectSummaries();
            return Result.success(s3ObjectSummaries);
        }
    }

    @Override
    public Result uploadFile(UploadFileRequest request) {
        Map<String, Object> map = new HashMap<>();

        String bucketName = request.getBucketName();
        String fileId = request.getFileId();
        byte[] data = request.getData();
        //是否覆盖 0不覆盖 1覆盖
        String inplace = request.getInplace();

        if(checkBucketExists(s3, bucketName)){
            log.info("--文件夹存在--");
            //文件id是否为空
            if(!StringUtils.isEmpty(fileId)){
                //判断文件是否存在服务器
                Boolean falg = s3.doesObjectExist(bucketName, fileId);
                //如果文件存在且 文件需要覆盖
                if(!falg || (falg && InplaceEnum.COVER.getCode().equals(inplace))){
                    //上传文件
                    log.info("--文件夹存在且文件需要覆盖--");
                    //上传文件
                    PutObjectResult result = upload(bucketName, fileId, data);
                    map.put("fileId", fileId);
                    map.put("result", result);
                    return Result.success(map);
                }else{
                    log.info("--文件已存在--");
                    return Result.error("文件已存在");
                }
            }else{
                fileId = UUID.randomUUID().toString().replaceAll("-","");
                //上传文件
                PutObjectResult result = upload(bucketName, fileId, data);
                map.put("fileId", fileId);
                map.put("result", result);
                return Result.success(map);
            }
        }else {
            log.info("--文件夹不存在--");
            return Result.error(bucketName + "文件夹不存在,请先创建");
        }
    }

    public PutObjectResult upload(String bucketName, String fileId, byte[] data){
        log.info("--upload开始上传文件，入参bucketName：{}-,fileId:{}-,data:{}", bucketName, fileId, data.length);
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(fileId);
        metadata.setContentLength(data.length);

        InputStream input = new ByteArrayInputStream(data);;

        PutObjectResult result = s3.putObject(new PutObjectRequest(bucketName, fileId, input, metadata)
                .withCannedAcl(CannedAccessControlList.PublicRead));

        return result;
    }

    @Override
    public Result downloadFile(String bucketName, String key) {
        if(checkBucketExists(s3, bucketName)){
            log.info("--文件夹存在--");
            Long startTime = System.currentTimeMillis();
            S3Object object = s3.getObject(new GetObjectRequest(bucketName,key));
            S3ObjectInputStream objectInputStream = object.getObjectContent();

            try {
                byte[] bytes = IOUtils.toByteArray(objectInputStream);
                Long usedTime = System.currentTimeMillis() - startTime;
                Map<String, Object> result = new HashMap<>();
                result.put("data", bytes);
                result.put("reason", "");
                result.put("usedTime", usedTime);
                return Result.success(result);
            } catch (IOException e) {
                log.info("--文件下载异常：{}--", e);
                return Result.error(CodeMsg.MYSQL_QUERY_EXCEPTION);
            }
        }else{
            return Result.error(bucketName+"不存在");
        }
    }

    @Override
    public Result deleteFile(String bucketName, String key) {
        if(checkBucketExists(s3, bucketName)){
            try {
                s3.deleteObject(bucketName, key);
                return Result.success();
            }catch (Exception e){
                log.info("--文件删除异常：{}--", e);
                return Result.error("文件删除异常");
            }
        }else{
            return Result.error(bucketName+"不存在");
        }
    }

    @Override
    public Bucket getBucket(String bucket_name) {
        Bucket named_bucket = null;
        List<Bucket> buckets = s3.listBuckets();
        for (Bucket b : buckets) {
            if (b.getName().equals(bucket_name)) {
                named_bucket = b;
            }
        }
        return named_bucket;
    }

    /**
     * 验证s3上是否存在名称为bucketName的Bucket
     * @param bucketName
     * @return
     */
    public static boolean checkBucketExists (AmazonS3 s3, String bucketName) {
        List<Bucket> buckets = s3.listBuckets();
        for (Bucket bucket : buckets) {
            if (Objects.equals(bucket.getName(), bucketName)) {
                return true;
            }
        }
        return false;
    }

    @Override
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

    }

}
