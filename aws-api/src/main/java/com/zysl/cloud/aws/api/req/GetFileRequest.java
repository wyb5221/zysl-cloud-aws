package com.zysl.cloud.aws.api.req;

import com.zysl.cloud.utils.common.BaseReqeust;
import com.zysl.cloud.utils.constants.SwaggerConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 文件信息查询入参对象
 */
@Setter
@Getter
@ApiModel(description = "文件信息查询请求对象")
public class GetFileRequest extends BaseReqeust {
    private static final long serialVersionUID = -8651479291764968852L;

    //文件夹名称
    @ApiModelProperty(value = "文件夹名称", name = "bucketName",required = true,dataType = SwaggerConstants.DATA_TYPE_STRING)
    private String bucketName;
    //文件名称
    @ApiModelProperty(value = "文件名称", name = "fileName",required = true,dataType = SwaggerConstants.DATA_TYPE_STRING)
    private String fileName;
    //文件版本id
    @ApiModelProperty(value = "文件版本id", name = "versionId",dataType = SwaggerConstants.DATA_TYPE_STRING)
    private String versionId;

    @Override
    public String toString() {
        return "GetFileRequest{" +
                "bucketName='" + bucketName + '\'' +
                ", fileName='" + fileName + '\'' +
                ", versionId='" + versionId + '\'' +
                '}';
    }
}
