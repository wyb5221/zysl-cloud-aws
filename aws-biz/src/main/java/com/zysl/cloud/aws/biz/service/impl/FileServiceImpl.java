package com.zysl.cloud.aws.biz.service.impl;

import com.zysl.cloud.aws.biz.service.IFileService;
import com.zysl.cloud.aws.biz.service.IS3BucketService;
import com.zysl.cloud.aws.domain.bo.FileDetailBO;
import com.zysl.cloud.aws.domain.bo.S3BaseBO;
import com.zysl.cloud.aws.domain.bo.S3ObjectBO;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.Bucket;

@Service
@Slf4j
public class FileServiceImpl implements IFileService {

	@Autowired
	IS3BucketService s3BucketService;

	@Override
	public List<String> getBuckets(String serviceNo){
		log.info("=getBuckets.serviceNo:{}=",serviceNo);
		S3Client s3Client = null;//TODO
		List<Bucket> bucketList = s3BucketService.getBucketList(s3Client);

		if(!CollectionUtils.isEmpty(bucketList)){
			List<String> buskets = new ArrayList<>();
			bucketList.forEach(obj -> buskets.add(obj.name()));
			return buskets;
		}
		return null;
	}

	@Override
	public String test(String name){
		return "success:" + name;
	}


	@Override
	public S3ObjectBO addS3Object(S3ObjectBO s3ObjectBO){
		return null;
	}

	@Override
	public S3ObjectBO getS3ObjectInfo(S3BaseBO queryBO){
		return null;
	}

	@Override
	public FileDetailBO getS3ObjectAllInfo(S3BaseBO queryBO){
		return null;
	}
}
