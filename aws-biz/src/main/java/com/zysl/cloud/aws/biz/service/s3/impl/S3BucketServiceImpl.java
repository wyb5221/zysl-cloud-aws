package com.zysl.cloud.aws.biz.service.s3.impl;


import com.alibaba.fastjson.JSON;
import com.zysl.cloud.aws.api.enums.BucketVerStatusEnum;
import com.zysl.cloud.aws.api.req.BucketFileRequest;
import com.zysl.cloud.aws.api.req.SetFileVersionRequest;
import com.zysl.cloud.aws.biz.constant.S3Method;
import com.zysl.cloud.aws.biz.enums.ErrCodeEnum;
import com.zysl.cloud.aws.biz.service.s3.IS3BucketService;
import com.zysl.cloud.aws.biz.service.s3.IS3FactoryService;
import com.zysl.cloud.aws.domain.bo.S3ObjectBO;
import com.zysl.cloud.utils.StringUtils;
import com.zysl.cloud.utils.common.AppLogicException;
import com.zysl.cloud.utils.common.MyPage;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.Bucket;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.CreateBucketResponse;
import software.amazon.awssdk.services.s3.model.ListBucketsRequest;
import software.amazon.awssdk.services.s3.model.ListBucketsResponse;
import software.amazon.awssdk.services.s3.model.PutBucketVersioningRequest;
import software.amazon.awssdk.services.s3.model.VersioningConfiguration;

@Service
@Slf4j
public class S3BucketServiceImpl implements IS3BucketService {

	@Autowired
	private IS3FactoryService s3FactoryService;

	@Override
	public List<Bucket> getBucketList(S3Client s3){
		log.info("=getBucketList=");
		ListBucketsRequest request = ListBucketsRequest.builder().build();
		ListBucketsResponse response = s3FactoryService.callS3Method(request,s3,S3Method.LIST_BUCKETS);

		return response.buckets();
	}

	@Override
	public List<String> getS3Buckets(String serviceNo){
		List<String> list = new ArrayList<>();
		Map<String, String> map = s3FactoryService.getBucketServerNoMap();
		if(map != null && map.size() > 0){
			for(String key:map.keySet()){
				if (StringUtils.isBlank(serviceNo) || map.get(key).equals(serviceNo)) {
				  list.add(key);
				}
			}
		}

		log.info("---buskets:{}", list.size());
		return list;
	}

	@Override
	public Boolean createBucket(String bucketName, String serviceNo) {
		log.info("---createBucket:---bucketName:{},serviceName:{}",bucketName, serviceNo);
		S3Client s3 = s3FactoryService.getS3ClientByServerNo(serviceNo);

		//判断bucket是否存在
		if(s3FactoryService.isExistBucket(bucketName)){
			log.info("---createBucket.is.exist--bucketName:{},serviceName:{}",bucketName, serviceNo);
			throw new AppLogicException(ErrCodeEnum.S3_CREATE_BUCKET_EXIST.getCode());
		}

		CreateBucketRequest s3r = CreateBucketRequest.builder().bucket(bucketName).build();
		CreateBucketResponse response = s3FactoryService.callS3Method(s3r,s3,S3Method.CREATE_BUCKETS);

		log.info("--createBucket.success--fileName:{}", response.location());
		s3FactoryService.addBucket(bucketName,serviceNo);

		//启用版本控制
		SetFileVersionRequest request = new SetFileVersionRequest();
		request.setBucketName(bucketName);
		request.setStatus(BucketVerStatusEnum.ENABLED.getCode());
		setBucketVersion(request);

		return Boolean.TRUE;
	}

	@Override
	public List<S3ObjectBO> getFilesByBucket(BucketFileRequest request, MyPage myPage) {
//TODO
//		PageHelper.startPage(request.getPageIndex(), request.getPageSize());
//		//数据库返回信息
//		List<S3File> fileList = fileService.queryFileBybucket(request);
//		List<FileInfo> fileInfoList = new ArrayList<>();
//		fileList.forEach(obj -> {
//			FileInfo fileInfo = BeanCopyUtil.copy(obj, FileInfo.class);
//			fileInfoList.add(fileInfo);
//		});
//
//		PageInfo<FileInfo> pageInfo = new PageInfo<>(fileInfoList);
//		log.info("-----objectList.contents().fileInfoList：{}", fileInfoList.size());
//
//		return fileInfoList;

		return null;
	}

	@Override
	public Boolean setBucketVersion(SetFileVersionRequest request) {
		log.info("setBucketVersion-param:",JSON.toJSONString(request));
		S3Client s3 = s3FactoryService.getS3ClientByBucket(request.getBucketName());
		//启动文件夹的版本控制
		PutBucketVersioningRequest s3r = PutBucketVersioningRequest.builder()
												.bucket(request.getBucketName())
												.versioningConfiguration(VersioningConfiguration.builder()
														.status(request.getStatus())//BucketVersioningStatus.ENABLED
														.build())
												.build();
		s3FactoryService.callS3Method(s3r,s3,S3Method.PUT_BUCKET_VERSIONING);
		log.info("修改版本控制成功:{}", request.getBucketName());

		return Boolean.TRUE;

	}



}
