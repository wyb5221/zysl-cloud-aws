package com.zysl.cloud.aws.api.req;

import com.zysl.cloud.utils.common.BasePaginationRequest;
import com.zysl.cloud.utils.common.BaseReqeust;
import com.zysl.cloud.utils.constants.SwaggerConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel(description = "查询bucket请求对象")
public class GetBucketsRequest extends BasePaginationRequest {

	@ApiModelProperty(value = "服务器编号,由运维配置", name = "serverNo",required = true,dataType = SwaggerConstants.DATA_TYPE_STRING)
	private String serverNo;

	@Override
	public String toString() {
		return "GetBucketsRequest{" +
				   "serverNo='" + serverNo + '\'' +
				   '}';
	}
}
