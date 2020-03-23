package com.zysl.aws.service.impl;

import com.zysl.aws.enums.DeleteStoreEnum;
import com.zysl.aws.enums.KeyTypeEnum;
import com.zysl.aws.model.*;
import com.zysl.aws.service.AwsFileService;
import com.zysl.aws.service.AwsFolderService;
import com.zysl.cloud.utils.common.AppLogicException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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

    //TODO  目录下内容不为空的异常情况需要单独处理
    @Override
    public boolean deleteFolder(DelObjectRequest request) {
        log.info("----deleteFolder删除目录入参--deleteFolder:{}", request);
        S3Client s3 = getS3Client(getServiceNo(request.getBucketName()));

        DelObjectRequest delRequest = request;
        //查询目录下的对象信息
        QueryObjectsRequest fileRequest = new QueryObjectsRequest();
        fileRequest.setBucketName(request.getBucketName());
        fileRequest.setKey(request.getKey());
        List<FileInfo> objectList = getS3FileList(fileRequest);
        boolean flag = operFolder(objectList, request);
        log.info("--子目录删除返回--flag:{}",flag);
        if(flag){
            deleteS3Folder(delRequest);
        }

        return true;

    }

    public boolean operFolder(List<FileInfo> objectList, DelObjectRequest request){
        //目录下对象信息为空，则直接删除文件夹
        if(!CollectionUtils.isEmpty(objectList)){
            for (FileInfo obj : objectList) {
                String key = obj.getKey();//yy01
                //是文件夹继续处理
                if(key.endsWith("/")){
                    key = key.substring(0, key.length() - 1);
                    //查询目录下的对象信息
                    QueryObjectsRequest fileRequest = new QueryObjectsRequest();
                    fileRequest.setBucketName(request.getBucketName());
                    fileRequest.setKey(key);
                    List<FileInfo> list = getS3FileList(fileRequest);

                    //继续删除文件夹
                    DelObjectRequest del = new DelObjectRequest();
                    del.setKey(key);
                    del.setBucketName(request.getBucketName());
                    operFolder(list, del);
                }else{
                    DelObjectRequest delObjectRequest = new DelObjectRequest();
                    delObjectRequest.setBucketName(request.getBucketName());
                    delObjectRequest.setKey(key);
                    delObjectRequest.setDeleteStore(DeleteStoreEnum.COVER.getCode());
                    //删除文件
                    awsFileService.deleteFile(delObjectRequest);
                }
            }
            //删除文件夹
            deleteS3Folder(request);
            return true;
        }else{
            //删除文件夹
            deleteS3Folder( request);
            return true;
        }
    }

    public DeleteObjectsResponse deleteS3Folder(DelObjectRequest request){
        log.info("----deleteS3Folder删除对象--request:{}", request);
        S3Client s3 = getS3Client(getServiceNo(request.getBucketName()));

        List<ObjectIdentifier> objects = new ArrayList<>();
        ObjectIdentifier objectIdentifier = ObjectIdentifier.builder()
                .key(request.getKey()+"/").build();
        objects.add(objectIdentifier);
        Delete delete = Delete.builder().objects(objects).build();
        DeleteObjectsResponse deleteObjectsResponse = s3.deleteObjects(DeleteObjectsRequest.builder()
                .bucket(request.getBucketName())
                .delete(delete)
                .build());
        log.info("deleteObjectsResponse:{}", deleteObjectsResponse);
        return deleteObjectsResponse;
    }

    //TODO  需要支持分页查询
    @Override
    public List<FileInfo> getS3FileList(QueryObjectsRequest request) {
        S3Client s3 = getS3Client(getServiceNo(request.getBucketName()));
        List<FileInfo> fileList = new ArrayList<>();

        ListObjectsResponse response = null;
        ListObjectsRequest listObjectsRequest = null;
        String key = "";
        if(KeyTypeEnum.FOLDER.getCode().equals(request.getKeyType())){
            //查询目录
//            do{
                if(StringUtils.isEmpty(request.getKey())){
                    listObjectsRequest =
                            ListObjectsRequest.builder()
                                    .bucket(request.getBucketName())
//                                    .prefix(request.getKey()+"/")
                                    .delimiter("/")
//                                    .maxKeys(2)
                                    .build();
                }else{
                    listObjectsRequest =
                            ListObjectsRequest.builder()
                                    .bucket(request.getBucketName())
                                    .prefix(request.getKey()+"/")
                                    .delimiter("/")
//                                    .maxKeys(2)
//                                    .marker(key)
                                    .build();
                }
                log.info("--查询文件列表入参listObjectsRequest:{}", listObjectsRequest);

                response = s3.listObjects(listObjectsRequest);
                List<CommonPrefix> folderList = response.commonPrefixes();

//                List<S3Object> list = response.contents();
//                key = folderList.get(folderList.size() - 1).prefix();
                List<FileInfo> resultList = new ArrayList<>();

                folderList.forEach(obj ->{
                    FileInfo fileInfo = new FileInfo();
                    fileInfo.setKey(obj.prefix());
                    resultList.add(fileInfo);
                });

                //合并list结果集
                fileList.addAll(resultList);
//            }while (response.isTruncated());
        }else if(KeyTypeEnum.FILE.getCode().equals(request.getKeyType())){
            //查询文件
//            do{
                if(StringUtils.isEmpty(request.getKey())){
                    listObjectsRequest =
                            ListObjectsRequest.builder()
                                    .bucket(request.getBucketName())
//                                    .prefix(request.getKey()+"/")
                                    .delimiter("/")
//                                    .maxKeys(2)
                                    .build();
                }else{
                    listObjectsRequest =
                            ListObjectsRequest.builder()
                                    .bucket(request.getBucketName())
                                    .prefix(request.getKey()+"/")
                                    .delimiter("/")
//                                    .maxKeys(2)
//                                    .marker(key)
                                    .build();
                }
                log.info("--查询文件列表入参listObjectsRequest:{}", listObjectsRequest);

                response = s3.listObjects(listObjectsRequest);

//                List<CommonPrefix> folderList = response.commonPrefixes();

                List<S3Object> list = response.contents();
//                key = list.get(list.size() - 1).key();
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
//            }while (response.isTruncated());
        }else{
            //查询全部
            if(StringUtils.isEmpty(request.getKey())){
                listObjectsRequest =
                        ListObjectsRequest.builder()
                                .bucket(request.getBucketName())
//                                    .prefix(request.getKey()+"/")
                                .delimiter("/")
//                                    .maxKeys(2)
                                .build();
            }else{
                listObjectsRequest =
                        ListObjectsRequest.builder()
                                .bucket(request.getBucketName())
                                .prefix(request.getKey()+"/")
                                .delimiter("/")
//                                    .maxKeys(2)
//                                    .marker(key)
                                .build();
            }
            log.info("--查询文件列表入参listObjectsRequest:{}", listObjectsRequest);

            response = s3.listObjects(listObjectsRequest);

            List<CommonPrefix> folderList = response.commonPrefixes();
            List<FileInfo> resultList1 = new ArrayList<>();
            folderList.forEach(obj ->{
                FileInfo fileInfo = new FileInfo();
                fileInfo.setKey(obj.prefix());
                resultList1.add(fileInfo);
            });
            //合并list结果集
            fileList.addAll(resultList1);

            List<S3Object> list = response.contents();
//                key = list.get(list.size() - 1).key();
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
        }

        List<FileInfo> resultList = fileList.stream().filter(obj -> !obj.getKey().equals(request.getKey()+"/")).collect(Collectors.toList());
        return resultList;
    }

    @Override
    public CopyObjectResponse copyFolder(CopyFileRequest request) {
        //获取s3连接对象
        S3Client s3 = getS3Client(getServiceNo(request.getSourceBucket()));

        //copySource 目标对象，文件夹+文件地址
        //bucket复制后的文件夹， key 复制后的文件名称
        CopyObjectRequest copyObjectRequest = CopyObjectRequest.builder().
                bucket(request.getDestBucket()).key(request.getDestKey()+"/")
                .copySource(request.getSourceBucket()+"/"+request.getSourceKey()+"/").build();

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

    @Override
    public boolean moveFolder(CopyFileRequest request) {
        log.info("--moveFolder移动目录入参request：{}--", request);
        //获取s3连接对象
        S3Client s3 = getS3Client(getServiceNo(request.getSourceBucket()));
        String delKey = request.getSourceKey();
        //先复制根目录
        //copySource 目标对象，文件夹+文件地址
        //bucket复制后的文件夹， key 复制后的文件名称
        CopyObjectRequest copyObjectRequest = CopyObjectRequest.builder().
                bucket(request.getDestBucket()).key(request.getDestKey()+"/")
                .copySource(request.getSourceBucket()+"/"+request.getSourceKey()+"/").build();
        log.info("--调用s3 接口复制文件copyObject--copyObjectRequest:{}", copyObjectRequest);
        CopyObjectResponse copyObjectResponse = s3.copyObject(copyObjectRequest);
        log.info("----copyObjectResponse:{}", copyObjectResponse);

        //在查询目录下是否有内容
        //查询目录下的对象信息
        QueryObjectsRequest fileRequest = new QueryObjectsRequest();
        fileRequest.setBucketName(request.getSourceBucket());
        fileRequest.setKey(request.getSourceKey());
        List<FileInfo> objectList = getS3FileList(fileRequest);
        //目录的移动
        boolean flag = moveSubObject(objectList, request);
        if(flag){
            //目录移动完成，进行源目录的删除
            DelObjectRequest delRequest = new DelObjectRequest();
            delRequest.setBucketName(request.getSourceBucket());
            delRequest.setKey(delKey);
            boolean delFlag = deleteFolder(delRequest);
            return delFlag;
        }else{
            return flag;
        }

    }

    public boolean moveSubObject(List<FileInfo> objectList, CopyFileRequest request){
        if(CollectionUtils.isEmpty(objectList)){
            return true;
        }
        //循环移动object对象
        for (FileInfo obj : objectList) {
            String key = obj.getKey();
            String destKey = key.replace(request.getSourceKey()+"/", request.getDestKey()+"/");
            CopyFileRequest subRequest = new CopyFileRequest();
            //直接移动当前这个对象
            subRequest.setSourceBucket(request.getSourceBucket());
            subRequest.setSourceKey(key);
            subRequest.setDestBucket(request.getDestBucket());
            subRequest.setDestKey(destKey);
            //直接移动当前对象
            getS3copyObject(subRequest);
            //判断当前对象是否是目录，是目录则查询目录下的内容进行复制
            if(key.endsWith("/")){
                //如果当前对象是目录。 则在查询当前目录的内容
                //查询目录下的对象信息
                QueryObjectsRequest fileRequest = new QueryObjectsRequest();
                fileRequest.setBucketName(request.getSourceBucket());
                fileRequest.setKey(key.substring(0, key.length() - 1));
                List<FileInfo> list = getS3FileList(fileRequest);
                //在次进行对象的移动
                moveSubObject(list, request);
            }
        }
        return true;
    }

    public CopyObjectResponse getS3copyObject(CopyFileRequest request) {
        //获取s3连接对象
        S3Client s3 = getS3Client(getServiceNo(request.getSourceBucket()));


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

}
