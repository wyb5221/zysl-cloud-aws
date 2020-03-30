package com.zysl.cloud.aws.biz.service.s3;

import com.zysl.cloud.aws.biz.service.IFileService;
import com.zysl.cloud.aws.domain.bo.S3ObjectBO;
import com.zysl.cloud.aws.domain.bo.TagsBO;

import java.util.List;

public interface IS3FileService<T> extends IFileService<T> {

	/**
	 * 数据操作权限校验
	 * 有的操作，比如复制，需要调2次判断
	 * 旧目录/文件的读取权限；新目录的写入权限
	 * @description
	 * @author miaomingming
	 * @date 21:16 2020/3/27
	 * @param path
	 * @param fileName
	 * @param opAuthTypes
	 * @return void
	 **/
	void checkDataOpAuth(String path,String fileName,String opAuthTypes);

	/**
	 * 查询对象标签信息
	 * @param t
	 * @return
	 */
	List<TagsBO> getTag(S3ObjectBO t);

	/**
	 * 查询指定标签key的value
	 * @param tagList
	 * @param key
	 * @return
	 */
	String getTagValue(List<TagsBO> tagList, String key);

	List<TagsBO> setTags(List<TagsBO> oldTagList, List<TagsBO> tagList);

	List<TagsBO> setTags(S3ObjectBO t, List<TagsBO> tagList);
}
