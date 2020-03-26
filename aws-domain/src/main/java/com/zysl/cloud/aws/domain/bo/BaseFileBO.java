package com.zysl.cloud.aws.domain.bo;

import java.io.Serializable;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

//文件信息
@Getter
@Setter
public class BaseFileBO implements Serializable {

	private String path;

	private String fileName;

	private  String acl;
	
	private  String contentEncoding;

	private  String contentLanguage;

	private  Long contentLength;

	private  String contentMD5;

	private  String contentType;

	private Date expires;

	//数据主体
	private byte[] bodys;

	@Override
	public String toString() {
		return "BaseFileBO{" +
				   "path='" + path + '\'' +
				   ", fileName='" + fileName + '\'' +
				   ", acl='" + acl + '\'' +
				   ", contentEncoding='" + contentEncoding + '\'' +
				   ", contentLanguage='" + contentLanguage + '\'' +
				   ", contentLength=" + contentLength +
				   ", contentMD5='" + contentMD5 + '\'' +
				   ", contentType='" + contentType + '\'' +
				   ", expires=" + expires +
				   '}';
	}
}
