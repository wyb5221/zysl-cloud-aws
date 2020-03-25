package com.zysl.cloud.aws.biz.service;

import com.zysl.cloud.aws.api.req.BucketFileRequest;
import com.zysl.cloud.aws.api.req.SetFileVersionRequest;
import com.zysl.cloud.aws.domain.bo.S3ObjectBO;
import com.zysl.cloud.utils.common.MyPage;
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

	/**
	 * 查询s3服务器的bucket列表
	 * @description
	 * @author miaomingming
	 * @date 10:26 2020/3/25
	 * @param [serviceNo]
	 * @return java.util.List<java.lang.String>
	 **/
	List<String> getS3Buckets(String serviceNo);

	/**
	 * 创建存储桶bucket
	 * @description
	 * @author miaomingming
	 * @date 9:38 2020/3/25
	 * @param [bucketName, serviceNo]
	 * @return java.lang.Boolean
	 **/
	Boolean createBucket(String bucketName, String serviceNo);

	/**
	 * 查询bucket下所有对象
	 * @description
	 * @author miaomingming
	 * @date 9:38 2020/3/25
	 * @param [request]
	 * @return java.util.List<com.zysl.aws.web.model.FileInfo>
	 **/
	List<S3ObjectBO> getFilesByBucket(BucketFileRequest request, MyPage myPage);

	/**
	 * 设置bucket版本控制
	 * @description
	 * @author miaomingming
	 * @date 11:01 2020/3/25
	 * @param [request]
	 * @return java.lang.Boolean
	 **/
	Boolean setBucketVersion(SetFileVersionRequest request);

}
