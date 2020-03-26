package com.zysl.cloud.aws.domain.bo;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

/**
 * 查询s3消息的基础对象，其他的继承此类
 * @description
 * @author miaomingming
 * @date 9:55 2020/3/26
 * @return
 **/
@Setter
@Getter
public class S3BaseBO extends BaseFileBO{

	private String bucketName;

	private String key;

	private String versionId;

	@Override
	public String toString() {
		return "S3BaseBO{" +
				   "bucketName='" + bucketName + '\'' +
				   ", key='" + key + '\'' +
				   ", versionId='" + versionId + '\'' +
				   '}';
	}
}
