package com.zysl.cloud.aws.api.req;

import com.zysl.cloud.utils.common.BaseReqeust;
import com.zysl.cloud.utils.constants.SwaggerConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 文件下载入参对象
 */
@Setter
@Getter
@ApiModel(description = "文件下载请求对象")
public class DownloadFileRequest extends BaseReqeust {
    private static final long serialVersionUID = -8651479291764968852L;

    //文件夹名称
    @ApiModelProperty(value = "文件夹名称", name = "bucketName",required = true,dataType = SwaggerConstants.DATA_TYPE_STRING)
    private String bucketName;
    //文件名称
    @ApiModelProperty(value = "文件名称", name = "fileId",required = true,dataType = SwaggerConstants.DATA_TYPE_STRING)
    private String fileId;
    //文件版本id
    @ApiModelProperty(value = "文件版本id", name = "versionId",dataType = SwaggerConstants.DATA_TYPE_STRING)
    private String versionId;
    //下载文件类型 0：默认，下载文件 1：下载二进制流
    @ApiModelProperty(value = "下载文件类型 0：默认，下载文件 1：下载二进制流", name = "type",required = true,dataType = SwaggerConstants.DATA_TYPE_STRING)
    private String type;

    @Override
    public String toString() {
        return "DownloadFileRequest{" +
                "bucketName='" + bucketName + '\'' +
                ", fileId='" + fileId + '\'' +
                ", versionId='" + versionId + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
