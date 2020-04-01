package com.zysl.cloud.aws.api.req;

import com.zysl.cloud.utils.common.BaseReqeust;
import com.zysl.cloud.utils.constants.SwaggerConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.List;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@ApiModel(description = "查询文件是否存在请求对象")
public class FileExistRequest extends BaseReqeust {

	@ApiModelProperty(value = "bucket列表，默认公告列表(配置)", name = "bucketNames",dataType = SwaggerConstants.DATA_TYPE_ARRAY)
	private List<String> bucketNames;

	//文件名称
	@ApiModelProperty(value = "文件名称", name = "fileName",required = true,dataType = SwaggerConstants.DATA_TYPE_STRING)
	private String fileName;

	@Override
	public String toString() {
		final StringBuffer sb = new StringBuffer("{\"FileExistRequest\":{");
		sb.append("bucketNames=").append(bucketNames);
		sb.append(", fileName='").append(fileName).append('\'');
		sb.append("},\"super-FileExistRequest\":")
			.append(super.toString()).append("}");
		return sb.toString();
	}
}
