package com.zysl.cloud.aws.biz.service.s3;

import com.zysl.cloud.utils.common.AppLogicException;
import java.util.Map;
import software.amazon.awssdk.core.sync.RequestBody;
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
	 * 获取s3连接
	 * @description
	 * @author miaomingming
	 * @date 14:46 2020/4/1
	 * @param bucketName
	 * @param isWrite
	 * @return software.amazon.awssdk.services.s3.S3Client
	 **/
	S3Client getS3ClientByBucket(String bucketName,Boolean isWrite) throws AppLogicException;
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
	 * 查询bucket和serverNo的map
	 * @description
	 * @author miaomingming
	 * @date 11:16 2020/3/26
	 * @param
	 * @return java.util.Map<java.lang.String,java.lang.String>
	 **/
	Map<String, String> getBucketServerNoMap();

	/**
	 * 判断两个bucket是否在同一服务器
	 * @param bucket1
	 * @param bucket2
	 * @return
	 */
	boolean judgeBucket(String bucket1, String bucket2);

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
	 * 统一调用s3方法，带数据流
	 * @description
	 * @author miaomingming
	 * @date 22:10 2020/3/23
	 * @param [r,  s3Client, methodName]
	 * @return T
	 **/
	<T extends S3Response,R extends S3Request>T callS3MethodWithBody(R r, RequestBody requestBody,S3Client s3Client,String methodName) throws AppLogicException;



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
