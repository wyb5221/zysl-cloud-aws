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

	private String versionId;
	
	private String range;
	
	@Override
	public String toString() {
		final StringBuffer sb = new StringBuffer("{\"BaseFileBO\":{");
		sb.append("path='").append(path).append('\'');
		sb.append(", fileName='").append(fileName).append('\'');
		sb.append(", acl='").append(acl).append('\'');
		sb.append(", contentEncoding='").append(contentEncoding).append('\'');
		sb.append(", contentLanguage='").append(contentLanguage).append('\'');
		sb.append(", contentLength=").append(contentLength);
		sb.append(", contentMD5='").append(contentMD5).append('\'');
		sb.append(", contentType='").append(contentType).append('\'');
		sb.append(", expires=").append(expires);
		if (bodys == null) {
			sb.append(", bodys=");
			sb.append("null");
		} else {
			sb.append(", bodys.length=");
			sb.append(bodys.length);
		}
		sb.append(", versionId='").append(versionId).append('\'');
		sb.append(", range='").append(range).append('\'');
		sb.append("}}");
		return sb.toString();
	}
}
