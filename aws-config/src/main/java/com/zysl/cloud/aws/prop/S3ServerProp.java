package com.zysl.cloud.aws.prop;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class S3ServerProp implements Serializable {

	private String serverNo;

	private String endpoint;

	private String accessKey;

	private String secretKey;

	@Override
	public String toString() {
		return "S3ServerProp{" +
				   "serverNo='" + serverNo + '\'' +
				   ", endpoint='" + endpoint + '\'' +
				   ", accessKey='" + accessKey + '\'' +
				   ", secretKey='" + secretKey + '\'' +
				   '}';
	}
}
