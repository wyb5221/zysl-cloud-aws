package com.zysl.cloud.aws.biz.service;

import com.zysl.cloud.aws.api.req.CopyObjectsRequest;
import com.zysl.cloud.aws.api.req.ShareFileRequest;
import com.zysl.cloud.aws.domain.bo.UploadFieBO;
import java.util.List;

public interface IFolderService<T> {


	/**
	 * 新增目录
	 * @description
	 * @author miaomingming
	 * @date 11:52 2020/3/26
	 * @param t
	 * @return T
	 **/
	T create(T t);

	/**
	 * 删除目录
	 * @description
	 * @author miaomingming
	 * @date 11:52 2020/3/26
	 * @param t
	 * @return T
	 **/
	void delete(T t);

	/**
	 * 重命名目录
	 * @description
	 * @author miaomingming
	 * @date 13:10 2020/3/26
	 * @param src
	 * @param dest
	 * @return void
	 **/
	void rename(T src,T dest);

	/**
	 * 复制目录
	 * @description
	 * @author miaomingming
	 * @date 13:10 2020/3/26
	 * @param src
	 * @param dest
	 * @return void
	 **/
	void copy(T src,T dest);

	/**
	 * 移动目录
	 * @description
	 * @author miaomingming
	 * @date 13:10 2020/3/26
	 * @param src
	 * @param dest
	 * @return void
	 **/
	void move(T src,T dest);

	/**
	 * 修改目录
	 * @description
	 * @author miaomingming
	 * @date 11:52 2020/3/26
	 * @param t
	 * @return T
	 **/
	void modify(T t);

	/**
	 * 查询目录基础信息
	 * @description
	 * @author miaomingming
	 * @date 11:52 2020/3/26
	 * @param t
	 * @return T
	 **/
	T getBaseInfo(T t);

	/**
	 * 查询目录所有信息，包括权限信息、子目录及文件列表
	 * @description
	 * @author miaomingming
	 * @date 11:52 2020/3/26
	 * @param t
	 * @return T
	 **/
	T getDetailInfo(T t);

	/**
	 * 查询版本列表信息
	 * @description
	 * @author miaomingming
	 * @date 13:35 2020/3/26
	 * @param t
	 * @return java.util.List<T>
	 **/
	List<T> getVersions(T t);

}
