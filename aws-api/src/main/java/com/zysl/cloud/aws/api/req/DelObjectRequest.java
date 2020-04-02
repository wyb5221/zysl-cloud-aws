package com.zysl.cloud.aws.api.req;

import com.zysl.cloud.utils.common.BaseReqeust;
import com.zysl.cloud.utils.constants.SwaggerConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 删除对象入参
 */
@Setter
@Getter
@ApiModel(description = "文件下载请求对象")
public class DelObjectRequest extends BaseReqeust {
    private static final long serialVersionUID = -5393931372818829200L;
    //存储桶名称
    @ApiModelProperty(value = "存储桶名称", name = "bucketName",required = true,dataType = SwaggerConstants.DATA_TYPE_STRING)
    private String bucketName;
    //对象名称，可以带目录，如a/b/1.doc
    @ApiModelProperty(value = "对象名称，可以带目录，如a/b/1.doc", name = "key",required = true,dataType = SwaggerConstants.DATA_TYPE_STRING)
    private String key;
    //文件版本号
    @ApiModelProperty(value = "文件版本号", name = "versionId",dataType = SwaggerConstants.DATA_TYPE_STRING)
    private String versionId;
    //是否物理删除，1是0否，默认0
    @ApiModelProperty(value = "是否物理删除，1是0否，默认0", name = "deleteStore",dataType = SwaggerConstants.DATA_TYPE_STRING)
    private Integer deleteStore;

    @Override
    public String toString() {
        return "DelObjectRequest{" +
                "bucketName='" + bucketName + '\'' +
                ", key='" + key + '\'' +
                ", versionId='" + versionId + '\'' +
                ", deleteStore='" + deleteStore + '\'' +
                '}';
    }
}
