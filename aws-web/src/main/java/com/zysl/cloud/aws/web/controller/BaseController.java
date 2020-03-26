package com.zysl.cloud.aws.web.controller;

import com.zysl.cloud.aws.domain.bo.S3ObjectBO;
import com.zysl.cloud.utils.StringUtils;

public class BaseController extends com.zysl.cloud.utils.common.BaseController {

	/**
	 * 切个s3的key为路径+文件名
	 * @description
	 * @author miaomingming
	 * @date 14:43 2020/3/26
	 * @param s3ObjectBO
	 * @param s3Key
	 * @return void
	 **/
	public void setPathAndFileName(S3ObjectBO s3ObjectBO,String s3Key){
		if(StringUtils.isBlank(s3Key)){
			return;
		}
		if(s3ObjectBO == null){
			s3ObjectBO = new S3ObjectBO();
		}
		if(s3Key.startsWith("/")){
			s3Key = s3Key.substring(1);
		}
		if(s3Key.endsWith("/")){
			s3ObjectBO.setPath(s3Key);
		}else{
			s3ObjectBO.setPath(s3Key.substring(0,s3Key.lastIndexOf("/")+1));
			s3ObjectBO.setFileName(s3Key.substring(s3Key.lastIndexOf("/")+1));
		}
	}

}
