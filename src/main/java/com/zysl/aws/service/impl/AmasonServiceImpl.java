package com.zysl.aws.service.impl;

import com.zysl.aws.common.result.Result;
import com.zysl.aws.model.FileInfo;
import com.zysl.aws.model.UploadFileRequest;
import com.zysl.aws.model.db.S3File;
import com.zysl.aws.model.db.S3Folder;
import com.zysl.aws.service.AmasonService;
import com.zysl.aws.service.FileService;
import com.zysl.aws.utils.Md5Util;
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

import javax.servlet.http.HttpServletResponse;
import java.util.*;


@Service
@Slf4j
public class AmasonServiceImpl implements AmasonService {

    @Autowired
    private S3ClientFactory s3ClientFactory;

    @Autowired
    private FileService fileService;


    public S3Client getS3Client(String bucketName){
        bucketName = "test-yy01";
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
//        if(!doesBucketExist(bucketName)){
//            log.info("--文件夹不存在--");
//            return Result.error("NoSuchBucket");
//        }else{
//            S3Client s3 = getS3Client("temp-001");
            S3Client s3 = getS3Client(bucketName);
//                ListObjectsResponse objectList = s3.listObjects
//                        (new ListObjectsRequest().bucket(bucketName));

            ListObjectsResponse objectList = s3.listObjects
                    (ListObjectsRequest.builder().bucket(bucketName).build());
            List<S3Object> list = objectList.contents();
            log.info("-----objectList.contents().list：{}", list);

            List<FileInfo> fileList = new ArrayList<>();
            list.stream().forEach(obj -> {
                FileInfo fileInfo = new FileInfo();
                fileInfo.setKey(obj.key());
                fileInfo.setKey(obj.key());
                fileInfo.setETag(obj.eTag());
                fileInfo.setLastModified(obj.lastModified());
                fileInfo.setSize(obj.size());
                fileList.add(fileInfo);
            });
        log.info("-----fileList：{}", fileList);

        return Result.success(fileList);
//        }
    }

    @Override
    public Result uploadFile(UploadFileRequest request) {
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
            String md5Content = Md5Util.getMd5Content(request.getData());
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
                    //向数据库保存文件信息
                    fileService.addFileInfo(request);
                    map.put("bucketName", request.getBucketName());
                    map.put("fileId", fileId);
                    return Result.success(map);
                }else {
                    return Result.error("文件上传失败");
                }
            }

            //文件id是否为空
 /*           if(!StringUtils.isEmpty(fileId)){
                //20200118,贾总要求出去文件判断-->业务层处理，这里直接替换
//                //判断文件是否存在服务器
//                Boolean falg = doesObjectExist(bucketName, fileId);
//                log.info("--判断文件是否存在服务器--falg:{},inplace:{}", falg, inplace);
//                //如果文件存在且 文件需要覆盖
//                if(!falg || (falg && InplaceEnum.COVER.getCode().equals(inplace))){
////                    上传文件
//                    log.info("--文件夹存在，执行文件上传--");
//                    上传文件
                    upFlag = upload(bucketName, fileId, data);
//                }else{
//                    log.info("--文件已存在--");
//                    return Result.error("文件已存在");
//                }
            }else{
                fileId = UUID.randomUUID().toString().replaceAll("-","");
                //上传文件
                upFlag = upload(bucketName, fileId, data);
            }*/
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
        S3Client s3 = getS3Client(bucketName);

        if(null != doesBucketExist(bucketName)){
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
    public Optional<Bucket> getBucket(String bucketName) {
//        return s3.listBuckets().stream().filter(b -> b.getName().equals(bucketName)).findFirst();
        return null;
    }

    @Override
    public Long getFileSize(String bucketName, String key) {
        S3Client s3 = getS3Client(bucketName);
        HeadObjectResponse headObjectResponse = s3.headObject(b -> b.bucket(bucketName).key(key));
        Long fileSize = headObjectResponse.contentLength();

        return fileSize;
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
