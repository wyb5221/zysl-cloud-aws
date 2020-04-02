package com.zysl.cloud.aws.api.req;

import com.zysl.cloud.utils.common.BasePaginationRequest;
import com.zysl.cloud.utils.common.BaseReqeust;
import com.zysl.cloud.utils.constants.SwaggerConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 查询子目录对象列表入参对象
 */
@Setter
@Getter
@ApiModel(description = "查询子目录列表请求对象")
public class QueryObjectsRequest extends BasePaginationRequest {
    private static final long serialVersionUID = -3261527596066639380L;

    //存储桶名称
    @ApiModelProperty(value = "存储桶名称", name = "bucketName",required = true,dataType = SwaggerConstants.DATA_TYPE_STRING)
    private String bucketName;
    //目录名称，多级目录用/隔开，例如a/b
    @ApiModelProperty(value = "目录名称，多级目录用/隔开，例如a/b", name = "key",required = true,dataType = SwaggerConstants.DATA_TYPE_STRING)
    private String key;
    //0默认全部，1仅目录2仅文件
    @ApiModelProperty(value = "0默认全部，1仅目录2仅文件", name = "keyType",dataType = SwaggerConstants.DATA_TYPE_INTEGER)
    private Integer keyType;
    //文件标签
    @ApiModelProperty(value = "文件标签", name = "userId",dataType = SwaggerConstants.DATA_TYPE_STRING)
    private String userId;

    @Override
    public String toString() {
        return "QueryObjectsRequest{" +
                "bucketName='" + bucketName + '\'' +
                ", key='" + key + '\'' +
                ", keyType=" + keyType +
                ", userId='" + userId + '\'' +
                '}';
    }
}
