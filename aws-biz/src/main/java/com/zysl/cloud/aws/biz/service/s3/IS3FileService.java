package com.zysl.cloud.aws.biz.service.s3;

import com.zysl.cloud.aws.biz.service.IFileService;
import com.zysl.cloud.aws.domain.bo.S3ObjectBO;
import com.zysl.cloud.aws.domain.bo.TagBO;

import java.util.List;

public interface IS3FileService<T> extends IFileService<T> {

  /**
   * 数据操作权限校验
   * * 有的操作，比如复制，需要调2次判断
   * * 旧目录/文件的读取权限；新目录的写入权限
   * @description
   * @author miaomingming
   * @date 16:32 2020/3/30
   * @param bucket
   * @param path
   * @param fileName
   * @param fileVersionId
   * @param opAuthTypes
   * @return void
   */
//  void checkDataOpAuth(
//	  String bucket, String path, String fileName, String fileVersionId, String opAuthTypes);
	void checkDataOpAuth(S3ObjectBO s3ObjectBO, String opAuthTypes);

	/**
	 * 查询对象标签信息
	 * @param t
	 * @return
	 */
	List<TagBO> getTags(S3ObjectBO t);

	/**
	 * 查询指定标签key的value
	 * @param tagList
	 * @param key
	 * @return
	 */
	String getTagValue(List<TagBO> tagList, String key);

	/**
	 * 合并标签计算
	 * @description
	 * @author miaomingming
	 * @date 15:07 2020/3/31
	 * @param oldTagList
	 * @param tagList
	 * @return java.util.List<com.zysl.cloud.aws.domain.bo.TagBO>
	 **/
	List<TagBO> mergeTags(List<TagBO> oldTagList, List<TagBO> tagList);

	/**
	 * 新增标签
	 * @description
	 * @author miaomingming
	 * @date 15:07 2020/3/31
	 * @param t
	 * @param tagList
	 * @return java.util.List<com.zysl.cloud.aws.domain.bo.TagBO>
	 **/
	List<TagBO> addTags(S3ObjectBO t, List<TagBO> tagList);

	/**
	 * 创建断点续传id
	 * @param t
	 * @return
	 */
	String createMultipartUpload(T t);

	/**
	 * 断点续传
	 * @param t
	 * @return
	 */
	T uploadPart(T t);

	/**
	 * 断点续传完成
	 * @param t
	 * @return
	 */
	T completeMultipartUpload(T t);
	
	/**
	 * 取消分片上传，将删除该对象
	 * @description
	 * @author miaomingming
	 * @date 16:53 2020/4/2
	 * @param t
	 * @return T
	 **/
	void abortMultipartUpload(T t);


	/**
	 * 查询最新对象的版本号
	 * @param t
	 * @return
	 */
	String getLastVersion(T t);

}
