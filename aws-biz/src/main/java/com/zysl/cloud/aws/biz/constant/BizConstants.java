package com.zysl.cloud.aws.biz.constant;

import java.util.HashMap;
import java.util.Map;
import software.amazon.awssdk.services.s3.S3Client;

public class BizConstants {
	//s3服务器连接信息 key为SERVER_NO
	public static Map<String, S3Client> S3_SERVER_CLIENT_MAP = new HashMap<>();

	//s3服务器的bucket--serverNo
	public static Map<String, String> S3_BUCKET_SERVER_MAP = new HashMap<>();
}
