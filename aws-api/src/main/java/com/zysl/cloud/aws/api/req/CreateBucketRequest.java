package com.zysl.cloud.aws.api.req;

import com.zysl.cloud.utils.common.BaseReqeust;
import com.zysl.cloud.utils.constants.SwaggerConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel(description = "创建bucket请求对象")
public class CreateBucketRequest extends BaseReqeust {

	private static final long serialVersionUID = -7490764197092211455L;


	@ApiModelProperty(value = "服务器编号,由运维配置", name = "serverNo",required = true,dataType = SwaggerConstants.DATA_TYPE_STRING)
	private String serverNo;

	@ApiModelProperty(value = "存储桶名称", name = "bucketName",required = true,dataType = SwaggerConstants.DATA_TYPE_STRING)
	private String bucketName;

	@Override
	public String toString() {
		return "CreateBucketRequest{" +
				   "serverNo='" + serverNo + '\'' +
				   ", bucketName='" + bucketName + '\'' +
				   '}';
	}
}
