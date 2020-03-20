package com.zysl.aws.service.impl;

import com.zysl.aws.enums.KeyTypeEnum;
import com.zysl.aws.model.BucketFileRequest;
import com.zysl.aws.model.CreateFolderRequest;
import com.zysl.aws.model.FileInfo;
import com.zysl.aws.model.QueryObjectsRequest;
import com.zysl.aws.service.AwsFileService;
import com.zysl.aws.service.AwsFolderService;
import com.zysl.cloud.utils.common.AppLogicException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 目录操作service
 */
@Service
@Slf4j
public class AwsFolderServiceImpl extends BaseService implements AwsFolderService {

    @Autowired
    private AwsFileService awsFileService;

    @Override
    public boolean createFolder(CreateFolderRequest request) {
        //获取s3连接
        S3Client s3 = getS3Client(getServiceNo(request.getBucketName()));

        PutObjectRequest putObjectRequest = PutObjectRequest.builder().bucket(request.getBucketName()).
                key(request.getFolderName() + "/").build();
        RequestBody body = RequestBody.empty();
        try {
            log.info("--createFolder文件夹创建putObjectRequest：{}--" , putObjectRequest);
            PutObjectResponse putObjectResponse = s3.putObject(putObjectRequest, body);
            return null != putObjectResponse;
        }catch (Exception e) {
            log.info("--createFolder文件夹创建异常：{}--" , e);
            throw new AppLogicException("文件夹创建异常");
        }
    }

    @Override
    public boolean deleteFolder(String bucketName, String key, Integer deleteStore) {
        S3Client s3 = getS3Client(getServiceNo(bucketName));

        List<ObjectIdentifier> objects = new ArrayList<>();

        Delete delete = Delete.builder().objects(objects).build();
        DeleteObjectsResponse deleteObjectsResponse = s3.deleteObjects(DeleteObjectsRequest.builder()
                .bucket(bucketName)
                .delete(delete).build());
        deleteObjectsResponse.responseMetadata();

        return true;

//        if(null == doesBucketExist(bucketName)){
//            log.info("--文件夹不存在,NoSuchBucket--");
//            return false;
//        }else{
        /*ListObjectsResponse objectList = s3.listObjects(ListObjectsRequest.builder().bucket(bucketName).build());
        if(!CollectionUtils.isEmpty(objectList.contents())){
            log.info("--文件下有文件,BucketNotEmpty--");
            return false;
        }else{
            DeleteBucketResponse deleteBucketResponse = s3.deleteBucket(DeleteBucketRequest.builder().bucket(bucketName).build());
            deleteBucketResponse.responseMetadata();

            //删除文件夹信息
            fileService.deleteFolderByName(bucketName);

            return true;
        }*/
//        }
    }

    @Override
    public List<FileInfo> getS3FileList(QueryObjectsRequest request) {
        S3Client s3 = getS3Client(getServiceNo(request.getBucketName()));
        List<FileInfo> fileList = new ArrayList<>();

        ListObjectsResponse response = null;
        ListObjectsRequest listObjectsRequest = null;
        String key = "";
        if(KeyTypeEnum.FOLDER.getCode().equals(request.getKeyType())){
            //查询目录
            do{
                if(StringUtils.isEmpty(key)){
                    listObjectsRequest =
                            ListObjectsRequest.builder()
                                    .bucket(request.getBucketName())
                                    .prefix(request.getKey()+"/")
                                    .delimiter("/")
                                    .maxKeys(2)
                                    .build();
                }else{
                    listObjectsRequest =
                            ListObjectsRequest.builder()
                                    .bucket(request.getBucketName())
                                    .prefix(request.getKey()+"/")
                                    .delimiter("/")
                                    .maxKeys(2)
                                    .marker(key).build();
                }
                log.info("--查询文件列表入参listObjectsRequest:{}", listObjectsRequest);

                response = s3.listObjects(listObjectsRequest);
                List<CommonPrefix> folderList = response.commonPrefixes();

                List<S3Object> list = response.contents();
                key = folderList.get(folderList.size() - 1).prefix();
                List<FileInfo> resultList = new ArrayList<>();

                folderList.forEach(obj ->{
                    FileInfo fileInfo = new FileInfo();
                    fileInfo.setKey(obj.prefix());
                    resultList.add(fileInfo);
                });

                //合并list结果集
                fileList.addAll(resultList);
            }while (response.isTruncated());
        }else if(KeyTypeEnum.FILE.getCode().equals(request.getKeyType())){
            //查询文件
            do{
                if(StringUtils.isEmpty(key)){
                    listObjectsRequest =
                            ListObjectsRequest.builder()
                                    .bucket(request.getBucketName())
                                    .prefix(request.getKey()+"/")
//                                    .delimiter("/")
                                    .maxKeys(2)
                                    .build();
                }else{
                    listObjectsRequest =
                            ListObjectsRequest.builder()
                                    .bucket(request.getBucketName())
                                    .prefix(request.getKey()+"/")
                                    .maxKeys(2)
                                    .marker(key).build();
                }
                log.info("--查询文件列表入参listObjectsRequest:{}", listObjectsRequest);

                response = s3.listObjects(listObjectsRequest);

//                List<CommonPrefix> folderList = response.commonPrefixes();

                List<S3Object> list = response.contents();
                key = list.get(list.size() - 1).key();
                List<FileInfo> resultList = new ArrayList<>();

                list.forEach(obj ->{
                    FileInfo fileInfo = new FileInfo();
                    fileInfo.setKey(obj.key());
                    fileInfo.setFileSize(obj.size());
                    fileInfo.setUploadTime(Date.from(obj.lastModified()));
                    resultList.add(fileInfo);
                });

                //合并list结果集
                fileList.addAll(resultList);
            }while (response.isTruncated());
        }else{
            //查询全部

        }

        return fileList;
    }


    /**
     * 拼接返回集合
     * @param list
     * @return
     */
    public List<FileInfo> addFileInfo(List<S3Object> list){
        List<FileInfo> fileList = new ArrayList<>();
        list.stream().forEach(obj -> {
            FileInfo fileInfo = new FileInfo();
            fileInfo.setKey(obj.key());
            fileInfo.setUploadTime(Date.from(obj.lastModified()));
            fileInfo.setFileSize(obj.size());
            fileList.add(fileInfo);
        });
        log.info("-----fileList：{}", fileList.size());
        return fileList;
    }
}
