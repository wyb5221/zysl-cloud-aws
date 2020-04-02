package com.zysl.cloud.aws.biz.enums;

import com.zysl.cloud.utils.enums.RespCodeEnum;
import lombok.Getter;

//错误编号，在 com.zysl.cloud.utils.enums.RespCodeEnum 基础上扩展
//格式5xxyyzz:  xx、yy、xx表示3层分类编号，xx在这里定义，例如5010001
//大类定义
//501yyzz:s3调用异常
//50200zz:s3及bucket相关
//50201zz:s3的key相关
//504yyzz:
//505yyzz:
//506yyzz:
//507yyzz:
//508yyzz:
//511yyzz:文件类型转换相关，比如word、pdf
//521yyzz:数据权限
@Getter
public enum  ErrCodeEnum  {

	S3_SERVER_CALL_METHOD_NO_RESPONSE(5010001, "调用s3接口:无返回."),
	S3_SERVER_CALL_METHOD_RESPONSE_STATUS_ERROR(5010002, "调用s3接口:返回状态码异常."),
	S3_SERVER_CALL_METHOD_INVOKE_ERROR(5010003, "调用s3接口:反射处理异常."),
	S3_SERVER_CALL_METHOD_NO_SUCH(5010004, "调用s3接口:无此接口."),
	S3_SERVER_CALL_METHOD_ERROR(5010005, "调用s3接口:其他异常."),
	S3_SERVER_CALL_METHOD_NO_SUCH_KEY(5010005, "调用s3接口:无此key."),
	S3_SERVER_CALL_METHOD_AWS_SERVICE_EXCEPTION(5010006, "调用s3接口:awsServer异常"),
	S3_SERVER_CALL_METHOD_S3_EXCEPTION(5010007, "调用s3接口:s3异常."),

	S3_SERVER_NO_NOT_EXIST(5020001, "不存在的服务器编号."),
	S3_BUCKET_NOT_EXIST(5020002, "不存在的bucket编号."),
	S3_CREATE_BUCKET_EXIST(5020003, "创建bucket已存在."),
	S3_COPY_SOURCE_ENCODE_ERROR(5020004, "复制对象时encode源url异常."),
	S3_NO_SPACE_WARN(5020005, "s3服务器没有存储空间."),
	
	S3_BUCKET_OBJECT_NOT_EXIST(5020101, "创建对象已存在."),
	DOWNLOAD_FILE_ERROR(5020102, "下载文件异常."),


	WORD_FILE_NOT_EXIST(5110001, "word转pdf：找不到原始文件."),
	WORD_FILE_TO_PDF_ERROR(5110002, "word转pdf：转换异常."),
	WORD_FILE_TO_PDF_SIZE_ZERO(5110003, "word转pdf：转换后文件大小为0."),
	WORD_FILE_TO_PDF_ENCRYPTION_SIZE_ZERO(5110004, "word转pdf：加密后文件大小为0."),

	OBJECT_OP_AUTH_CHECK_FAILED(5210001, "数据操作权限：无权限"),
	OBJECT_OP_AUTH_CHECK_DATA_FORMAT_ERROR(5210002, "数据操作权限：配置参数或head参数格式异常"),
	OBJECT_OP_AUTH_CHECK_ERROR(5210003, "数据操作权限：计算权限异常"),
	;

	private Integer code;

	private String desc;


	ErrCodeEnum(Integer code, String desc) {
		this.code = code;
		this.desc = desc;
	}
}
