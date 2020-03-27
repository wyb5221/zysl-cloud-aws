package com.zysl.cloud.aws.api.req;

import com.zysl.cloud.utils.common.BaseReqeust;
import com.zysl.cloud.utils.constants.SwaggerConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 创建目录folder请求对象
 */
@Getter
@Setter
@ApiModel(description = "创建目录folder请求对象")
public class CreateFolderRequest extends BaseReqeust {

	private static final long serialVersionUID = 518577308272634660L;

	@ApiModelProperty(value = "文件夹名称", name = "folderName",required = true,dataType = SwaggerConstants.DATA_TYPE_STRING)
	private String folderName;

	@ApiModelProperty(value = "存储桶名称", name = "bucketName",required = true,dataType = SwaggerConstants.DATA_TYPE_STRING)
	private String bucketName;

	@Override
	public String toString() {
		return "CreateFolderRequest{" +
				"folderName='" + folderName + '\'' +
				", bucketName='" + bucketName + '\'' +
				'}';
	}
}
