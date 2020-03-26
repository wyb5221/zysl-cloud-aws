package com.zysl.cloud.aws.biz.service;

import com.zysl.cloud.aws.domain.bo.S3ObjectBO;

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
	T create(T t);

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
	 * 文件下载
	 * @param t
	 * @return
	 */
	T download(T t);

	/**
	 * 重命名文件
	 * @description
	 * @author miaomingming
	 * @date 13:10 2020/3/26
	 * @param src
	 * @param dest
	 * @return void
	 **/
	void rename(T src,T dest);

	/**
	 * 复制文件
	 * @description
	 * @author miaomingming
	 * @date 13:10 2020/3/26
	 * @param src
	 * @param dest
	 * @return void
	 **/
	T copy(T src, T dest);

	/**
	 * 移动文件
	 * @description
	 * @author miaomingming
	 * @date 13:10 2020/3/26
	 * @param src
	 * @param dest
	 * @return void
	 **/
	void move(T src,T dest);

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
