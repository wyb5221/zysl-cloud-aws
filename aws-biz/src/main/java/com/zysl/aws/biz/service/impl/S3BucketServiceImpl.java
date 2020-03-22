package com.zysl.aws.biz.service.impl;


import com.zysl.aws.biz.service.IS3BucketService;
import com.zysl.cloud.utils.common.AppLogicException;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.Bucket;
import software.amazon.awssdk.services.s3.model.ListBucketsRequest;
import software.amazon.awssdk.services.s3.model.ListBucketsResponse;

@Service
@Slf4j
public class S3BucketServiceImpl implements IS3BucketService {

	@Override
	public List<Bucket> getBucketList(S3Client s3){
		log.info("=getBucketList=");
		ListBucketsRequest listBucketsRequest = ListBucketsRequest.builder().build();
		ListBucketsResponse response = s3.listBuckets(listBucketsRequest);

		if(response == null){
			log.error("getBucketList.no.ressponse");
			throw new AppLogicException("getBucketList.no.ressponse");
		}

		return response.buckets();
	}

}
