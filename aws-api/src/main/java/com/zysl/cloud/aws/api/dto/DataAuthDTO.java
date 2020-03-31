package com.zysl.cloud.aws.api.dto;

import com.zysl.cloud.aws.api.req.GetFileRequest;
import com.zysl.cloud.utils.constants.SwaggerConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@ApiModel(description = "数据权限设置返回对象")
public class DataAuthDTO extends GetFileRequest {

	private static final long serialVersionUID = -6854624204356933095L;

	@ApiModelProperty(value = "用户的操作权限列表，多个直接合并字符串，参考OPAuthTypeEnum", name = "userAuths",dataType = SwaggerConstants.DATA_TYPE_ARRAY)
	private List<OPAuthDTO> userAuths;

	@ApiModelProperty(value = "组的操作权限列表，多个直接合并字符串，参考OPAuthTypeEnum", name = "groupAuths",dataType = SwaggerConstants.DATA_TYPE_ARRAY)
	private List<OPAuthDTO> groupAuths;

	@ApiModelProperty(value = "所有人的操作权限列表，多个直接合并字符串，参考OPAuthTypeEnum", name = "everyOneAuths",dataType = SwaggerConstants.DATA_TYPE_STRING)
	private String everyOneAuths;

	@Override
	public String toString() {
		final StringBuffer sb = new StringBuffer("{\"DataAuthDTO\":{");
		sb.append("userAuths=").append(userAuths);
		sb.append(", groupAuths=").append(groupAuths);
		sb.append(", everyOneAuths='").append(everyOneAuths).append('\'');
		sb.append("},\"super-DataAuthDTO\":")
			.append(super.toString()).append("}");
		return sb.toString();
	}
}
