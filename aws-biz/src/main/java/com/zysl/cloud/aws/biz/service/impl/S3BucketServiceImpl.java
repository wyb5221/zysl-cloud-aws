package com.zysl.cloud.aws.biz.service.impl;


import com.alibaba.fastjson.JSON;
import com.zysl.cloud.aws.biz.constant.BizConstants;
import com.zysl.cloud.aws.biz.constant.S3Method;
import com.zysl.cloud.aws.biz.service.IS3BucketService;
import com.zysl.cloud.aws.biz.service.IS3FactoryService;
import com.zysl.cloud.utils.common.AppLogicException;
import java.util.List;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.Bucket;
import software.amazon.awssdk.services.s3.model.ListBucketsRequest;
import software.amazon.awssdk.services.s3.model.ListBucketsResponse;

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



}
