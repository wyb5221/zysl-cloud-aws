package com.zysl.cloud.aws.biz.service;

import com.zysl.cloud.aws.domain.bo.FileDetailBO;
import com.zysl.cloud.aws.domain.bo.S3BaseBO;
import com.zysl.cloud.aws.domain.bo.S3ObjectBO;
import com.zysl.cloud.aws.api.req.ShareFileRequest;
import com.zysl.cloud.aws.domain.bo.UploadFieBO;

import java.util.List;

public interface IFileService {

	/**
	 * 查询bucket列表，如果没有传入serviceNo则查所有服务器
	 * @description
	 * @author miaomingming
	 * @date 22:29 2020/3/22
	 * @param [serviceNo]
	 * @return java.util.List<java.lang.String>
	 **/
	List<String> getBuckets(String serviceNo);

	String test(String name);

	/**
	 * 分享文件
	 * @param request
	 * @return
	 */
	UploadFieBO shareFile(ShareFileRequest request);



	/**
	 * 新增文件
	 * @description
	 * @author miaomingming
	 * @date 10:10 2020/3/26
	 * @param s3ObjectBO
	 * @return com.zysl.cloud.aws.domain.bo.S3ObjectBO
	 **/
	S3ObjectBO addS3Object(S3ObjectBO s3ObjectBO);

	/**
	 * 查询文件信息
	 * @description
	 * @author miaomingming
	 * @date 10:01 2020/3/26
	 * @param queryBO
	 * @return com.zysl.cloud.aws.domain.bo.S3ObjectBO
	 **/
	S3ObjectBO getS3ObjectInfo(S3BaseBO queryBO);

	/**
	 * 查询文件所有信息，包括权限信息
	 * @description
	 * @author miaomingming
	 * @date 10:01 2020/3/26
	 * @param queryBO
	 * @return com.zysl.cloud.aws.domain.bo.FileDetailBO
	 **/
	FileDetailBO getS3ObjectAllInfo(S3BaseBO queryBO);

}
