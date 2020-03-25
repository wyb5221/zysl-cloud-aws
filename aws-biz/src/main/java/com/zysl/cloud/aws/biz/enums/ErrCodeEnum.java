package com.zysl.cloud.aws.biz.enums;

import com.zysl.cloud.utils.enums.RespCodeEnum;
import lombok.Getter;

//错误编号，在 com.zysl.cloud.utils.enums.RespCodeEnum 基础上扩展
//格式5xxyyzz:  xx、yy、xx表示3层分类编号，xx在这里定义，例如5010001
//大类定义
//501yyzz:s3及bucket相关
//502yyzz:s3的key相关
//503yyzz:
//504yyzz:文件类型转换相关，比如word、pdf
//505yyzz:
//506yyzz:
//507yyzz:
//508yyzz:
@Getter
public enum  ErrCodeEnum  {
	S3_SERVER_NO_NOT_EXIST(5010001, "不存在的服务器编号."),
	S3_BUCKET_NOT_EXIST(5010002, "不存在的bucket编号."),
	S3_CREATE_BUCKET_EXIST(5010003, "创建bucket已存在."),
	S3_BUCKET_OBJECT_NOT_EXIST(5020001, "创建对象已存在."),
	COVER(2, "base64进制的String");

	private Integer code;

	private String desc;


	ErrCodeEnum(Integer code, String desc) {
		this.code = code;
		this.desc = desc;
	}
}
