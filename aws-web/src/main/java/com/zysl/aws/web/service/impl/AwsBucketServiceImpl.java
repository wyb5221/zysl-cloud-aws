package com.zysl.aws.web.service.impl;

import com.zysl.aws.web.model.BucketFileRequest;
import com.zysl.aws.web.model.FileInfo;
import com.zysl.aws.web.model.SetFileVersionRequest;
import com.zysl.aws.web.model.UploadFileRequest;
import com.zysl.aws.web.model.db.S3File;
import com.zysl.aws.web.model.db.S3Folder;
import com.zysl.aws.web.model.db.S3Service;
import com.zysl.aws.web.service.AwsBucketService;
import com.zysl.cloud.aws.config.BizConfig;
import com.zysl.cloud.aws.utils.DateUtil;
import com.zysl.cloud.utils.BeanCopyUtil;
import com.zysl.cloud.utils.enums.RespCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Service
@Slf4j
public class AwsBucketServiceImpl extends BaseService implements AwsBucketService {


    @Autowired
    private BizConfig bizConfig;

    @Override
    public List<String> getBuckets(String serviceNo) {
        log.info("--getBuckets获取存储桶信息--serviceNo：{}", serviceNo);
        List<String> bucketList = new ArrayList<>();
        if(StringUtils.isEmpty(serviceNo)){
            //查询数据库的服务信息
            List<S3Service> serviceList = new ArrayList<>();
// fileService.queryS3Service();
            for (S3Service obj: serviceList) {
                List<String> buckets = getS3Buckets(obj.getServiceNo());
                bucketList.addAll(buckets);
            }
        }else{
            List<String> buckets = getS3Buckets(serviceNo);
            bucketList.addAll(buckets);
        }
        return bucketList;
    }

    /**
     * 调用s3接口查询bucket
     * @param serviceNo
     * @return
     */
    public List<String> getS3Buckets(String serviceNo){
        S3Client s3 = getS3Client(serviceNo);
        ListBucketsRequest listBucketsRequest = ListBucketsRequest.builder().build();
        ListBucketsResponse response = s3.listBuckets(listBucketsRequest);
        List<Bucket> bucketList = response.buckets();

        List<String> buskets = new ArrayList<>();
        bucketList.forEach(obj -> buskets.add(obj.name()));
        log.info("---buskets:{}", buskets.size());
        return buskets;
    }
    @Override
    public String createBucket(String bucketName, String serviceNo) {
        log.info("---创建文件夹createBucket:---bucketName:{},serviceName:{}",bucketName, serviceNo);
        S3Client s3 = getS3Client(serviceNo);
        S3Folder s3Folder = doesBucketExist(bucketName);
        log.info("--存储桶是否存在--s3Folder：{}", s3Folder);
        if(null != s3Folder){
            log.info("--文件夹已经存在--");
            return "文件夹已经存在";
        }else{
            CreateBucketResponse response = s3.createBucket(CreateBucketRequest.builder().bucket(bucketName).build());
            if(null != response){
                String fileName = response.location();
                log.info("--创建接口返回--fileName:{}， response：{}", fileName, response);
                //保存文件夹信息
//                int num = fileService.insertFolderInfo(bucketName, serviceNo);
//                log.info("--文件信息保存返回--num:{}", num);

                //启动文件夹的版本控制
                PutBucketVersioningResponse result = s3.putBucketVersioning(PutBucketVersioningRequest.builder()
                        .bucket(bucketName)
                        .versioningConfiguration(
                                VersioningConfiguration.builder()
                                        .status(BucketVersioningStatus.ENABLED)
                                        .build())
                        .build());
                log.info("--启用版本控制返回--result:{}", result);
                return bucketName;
            }else{
                log.info("--bucket创建失败--");
                return null;
            }
        }
    }

    @Override
    public List<FileInfo> getFilesByBucket(BucketFileRequest request) {

//        PageHelper.startPage(request.getPageIndex(), request.getPageSize());
        //数据库返回信息
//        List<S3File> fileList = fileService.queryFileBybucket(request);
        List<FileInfo> fileInfoList = new ArrayList<>();
//        fileList.forEach(obj -> {
//            FileInfo fileInfo = BeanCopyUtil.copy(obj, FileInfo.class);
//            fileInfoList.add(fileInfo);
//        });

//        PageInfo<FileInfo> pageInfo = new PageInfo<>(fileInfoList);
        log.info("-----objectList.contents().fileInfoList：{}", fileInfoList.size());

        return fileInfoList;

    }




    @Override
    public Integer setFileVersion(SetFileVersionRequest request) {
        if(null != request){
            S3Client s3 = getS3Client(getServiceNo(request.getBucketName()));
            //启动文件夹的版本控制
            PutBucketVersioningResponse result = s3.putBucketVersioning(PutBucketVersioningRequest.builder()
                    .bucket(request.getBucketName())
                    .versioningConfiguration(
                            VersioningConfiguration.builder()
                                    .status(request.getStatus())//BucketVersioningStatus.ENABLED
                                    .build())
                    .build());
            log.info("--setFileVersion启用版本控制返回--result:{}", result.sdkHttpResponse().statusCode());

            if("200".equals(result.sdkHttpResponse().statusCode()+"")){
                return RespCodeEnum.SUCCESS.getCode();
            }else {
                log.info("--setFileVersion启用版本控制返回--result.sdkHttpResponse:{}", result.sdkHttpResponse().statusText());
                return RespCodeEnum.FAILED.getCode();
            }
        }else {
            log.info("--setFileVersion参数为空--");
            return RespCodeEnum.ILLEGAL_PARAMETER.getCode();
        }
    }

    @Override
    public void copyObject() {
        S3Client s3 = getS3Client(getServiceNo("test-yy05"));
        //copySource 目标对象，文件夹+文件地址
        //bucket复制后的文件夹， key 复制后的文件名称
        /*CopyObjectRequest copyObjectRequest = CopyObjectRequest.builder().
                bucket("test-yy03").key("doc/tt01.doc").copySource("test-yy05/txt/tt01.doc").build();
        */
//        CopyObjectRequest copyObjectRequest = CopyObjectRequest.builder().
//                bucket("test-yy03").key("txt/").copySource("test-yy05/txt/").build();
        CopyObjectRequest copyObjectRequest = CopyObjectRequest.builder().
                bucket("test-yy03").key("txt/").copySource("test-yy05/txt/").
                copySourceSSECustomerAlgorithm("copySourceSSECustomerAlgorithm").build();

        CopyObjectResponse copyObjectResponse = s3.copyObject(copyObjectRequest);
        PutBucketReplicationRequest putBucketReplicationRequest = PutBucketReplicationRequest.builder()
                .bucket("")
                .build();
        System.out.println(copyObjectResponse);

    }


}
