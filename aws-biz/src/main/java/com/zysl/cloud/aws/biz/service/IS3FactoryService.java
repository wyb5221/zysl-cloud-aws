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
	 * 是否存在bucketName
	 * @description
	 * @author miaomingming
	 * @date 10:17 2020/3/25
	 * @param [bucketName]
	 * @return java.lang.Boolean
	 **/
	Boolean isExistBucket(String bucketName);

	/**
	 * 添加bucket
	 * @description
	 * @author miaomingming
	 * @date 10:18 2020/3/25
	 * @param [bucketName, serverNo]
	 * @return java.lang.Boolean
	 **/
	void addBucket(String bucketName,String serverNo);

	/**
	 * 统一调用s3方法
	 * @description
	 * @author miaomingming
	 * @date 22:10 2020/3/23
	 * @param [r,  s3Client, methodName]
	 * @return T
	 **/
	<T extends S3Response,R extends S3Request>T callS3Method(R r,S3Client s3Client,String methodName) throws AppLogicException;


	/**
	 * 统一调用s3方法
	 * @description
	 * @author miaomingming
	 * @date 9:24 2020/3/26
	 * @param  * @param r
	 * @param s3Client
	 * @param methodName
	 * @param throwLogicException 是否抛出逻辑异常
	 * @return T
	 **/
	<T extends S3Response,R extends S3Request>T callS3Method(R r,S3Client s3Client,String methodName,Boolean throwLogicException);
}
