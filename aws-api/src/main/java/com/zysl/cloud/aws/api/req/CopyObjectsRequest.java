package com.zysl.cloud.aws.api.req;

import com.zysl.cloud.utils.common.BaseReqeust;
import com.zysl.cloud.utils.constants.SwaggerConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 文件复制入参对象
 */
@Setter
@Getter
@ApiModel(description = "文件复制请求对象")
public class CopyObjectsRequest extends BaseReqeust {
    private static final long serialVersionUID = -1272324062190153756L;

    //源存储桶名称
    @ApiModelProperty(value = "源存储桶名称", name = "sourceBucket",required = true,dataType = SwaggerConstants.DATA_TYPE_STRING)
    private String sourceBucket;
    //源目录名称，多级目录用/隔开，例如a/b.doc
    @ApiModelProperty(value = "源目录名称，多级目录用/隔开，例如a/b.doc", name = "sourceKey",required = true,dataType = SwaggerConstants.DATA_TYPE_STRING)
    private String sourceKey;
    //目标存储桶名称
    @ApiModelProperty(value = "目标存储桶名称", name = "destBucket",required = true,dataType = SwaggerConstants.DATA_TYPE_STRING)
    private String destBucket;
    //目标目录名称，多级目录用/隔开，例如a/b.doc
    @ApiModelProperty(value = "目标目录名称，多级目录用/隔开，例如a/b.doc", name = "destKey",required = true,dataType = SwaggerConstants.DATA_TYPE_STRING)
    private String destKey;

    @Override
    public String toString() {
        return "CopyFileRequest{" +
                "sourceBucket='" + sourceBucket + '\'' +
                ", sourceKey='" + sourceKey + '\'' +
                ", destBucket='" + destBucket + '\'' +
                ", destKey='" + destKey + '\'' +
                '}';
    }
}
