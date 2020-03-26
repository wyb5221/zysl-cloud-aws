package com.zysl.cloud.aws.biz.service;

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
}
