package com.zysl.cloud.aws.biz.service.s3;

import com.zysl.cloud.aws.biz.service.IFileService;

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

}
