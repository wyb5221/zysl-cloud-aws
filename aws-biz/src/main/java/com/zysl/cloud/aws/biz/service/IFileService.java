package com.zysl.cloud.aws.biz.service;

import com.zysl.cloud.aws.domain.bo.BaseFileBO;
import com.zysl.cloud.aws.api.req.CopyObjectsRequest;
import com.zysl.cloud.aws.domain.bo.FileDetailBO;
import com.zysl.cloud.aws.domain.bo.S3BaseBO;
import com.zysl.cloud.aws.domain.bo.S3ObjectBO;

import com.zysl.cloud.aws.api.req.ShareFileRequest;
import com.zysl.cloud.aws.domain.bo.UploadFieBO;

import java.util.List;

/**
 * 所有文件存储的接口
 * @description
 * @author miaomingming
 * @date 11:26 2020/3/26
 * @return
 **/
public interface IFileService<T> {


	/**
	 * 新增文件
	 * @description
	 * @author miaomingming
	 * @date 11:52 2020/3/26
	 * @param t
	 * @return T
	 **/
	 void create(T t);

	/**
	 * 删除文件
	 * @description
	 * @author miaomingming
	 * @date 11:52 2020/3/26
	 * @param t
	 * @return T
	 **/
	void delete(T t);


	/**
	 * 修改文件
	 * 分享文件
	 * @param request
	 * @return
	 */
	UploadFieBO shareFile(ShareFileRequest request);

	/**
	 * 文件复制
	 * @param request
	 */
	boolean copyFile(CopyObjectsRequest request);



	/**
	 * 新增文件
	 * @description
	 * @author miaomingming
	 * @date 11:52 2020/3/26
	 * @param t
	 * @return T
	 **/
	 void modify(T t);

	/**
	 * 查询文件基础信息
	 * @description
	 * @author miaomingming
	 * @date 11:52 2020/3/26
	 * @param t
	 * @return T
	 **/
	 T getBaseInfo(T t);

	/**
	 * 查询文件所有信息，包括权限信息
	 * @description
	 * @author miaomingming
	 * @date 11:52 2020/3/26
	 * @param t
	 * @return T
	 **/
	 T getDetailInfo(T t);

	/**
	 * 查询文件信息及内容
	 * @description
	 * @author miaomingming
	 * @date 11:52 2020/3/26
	 * @param t
	 * @return T
	 **/
	 T getInfoAndBody(T t);



}
