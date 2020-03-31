package com.zysl.cloud.aws.api.dto;

import com.zysl.cloud.utils.constants.SwaggerConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@ApiModel(description = "权限对象")
public class OPAuthDTO implements Serializable {
	@ApiModelProperty(value = "操作角色或用户", name = "key", required = true,dataType = SwaggerConstants.DATA_TYPE_STRING)
	private String key;

	@ApiModelProperty(value = "操作权限列表，参考OPAuthTypeEnum,例如：dm", name = "value", required = true,dataType = SwaggerConstants.DATA_TYPE_STRING)
	private String value;


	public OPAuthDTO(){}

	public OPAuthDTO(String key,String value){
		this.key = key;
		this.value = value;
	}

	@Override
	public String toString() {
		final StringBuffer sb = new StringBuffer("{\"OPAuthDTO\":{");
		sb.append("key='").append(key).append('\'');
		sb.append(", value='").append(value).append('\'');
		sb.append("}}");
		return sb.toString();
	}
}
