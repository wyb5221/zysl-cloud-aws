package com.zysl.cloud.aws.biz.service;

import com.zysl.cloud.utils.common.AppLogicException;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.S3Request;
import software.amazon.awssdk.services.s3.model.S3Response;

public interface IS3FactoryService {

	/**
	 * 查询服务器编号
	 * @description
	 * @author miaomingming
	 * @date 17:10 2020/3/23
	 * @param [bucketName]
	 * @return java.lang.String
	 **/
	String getServerNo(String bucketName);

	/**
	 * 获取s3连接
	 * @description
	 * @author miaomingming
	 * @date 17:10 2020/3/23
	 * @param [serverNo]
	 * @return software.amazon.awssdk.services.s3.S3Client
	 **/
	S3Client getS3ClientByServerNo(String serverNo);

	/**
	 * 获取s3连接
	 * @description
	 * @author miaomingming
	 * @date 17:11 2020/3/23
	 * @param [bucketName]
	 * @return software.amazon.awssdk.services.s3.S3Client
	 **/
	S3Client getS3ClientByBucket(String bucketName);

	/**
	 * 统一调用s3方法
	 * @description
	 * @author miaomingming
	 * @date 22:10 2020/3/23
	 * @param [r,  s3Client, methodName]
	 * @return T
	 **/
	<T extends S3Response,R extends S3Request>T callS3Method(R r,S3Client s3Client,String methodName) throws AppLogicException;
}
