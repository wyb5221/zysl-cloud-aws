package com.zysl.cloud.aws.biz.service;

import java.util.List;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.Bucket;

public interface IS3BucketService {

	/**
	 * 查询s3服务器的bucket列表
	 * @description
	 * @author miaomingming
	 * @date 21:29 2020/3/22
	 * @param [serviceNo]
	 * @return java.util.List<software.amazon.awssdk.services.s3.model.Bucket>
	 **/
	List<Bucket> getBucketList(S3Client s3);

}
