package com.zysl.cloud.aws.api.req;

import com.zysl.cloud.utils.common.BaseReqeust;
import com.zysl.cloud.utils.constants.SwaggerConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 断点续传入参
 */
@Setter
@Getter
@ApiModel(description = "断点续传入参对象")
public class UploadPartRequest extends BaseReqeust {
    private static final long serialVersionUID = 6528744834290825581L;

    //文件夹名称
    @ApiModelProperty(value = "bucket存储桶名称", name = "bucketName", required = true, dataType = SwaggerConstants.DATA_TYPE_STRING)
    private String bucketName;
    //文件名
    @ApiModelProperty(value = "文件名", name = "fileId", required = true, dataType = SwaggerConstants.DATA_TYPE_STRING)
    private String fileId;

    @ApiModelProperty(value = "断点续传id", name = "uploadId", required = true, dataType = SwaggerConstants.DATA_TYPE_STRING)
    private String uploadId;

    @ApiModelProperty(value = "断点续传次数，从1开始", name = "partNumber", required = true, dataType = SwaggerConstants.DATA_TYPE_INTEGER)
    private Integer partNumber;

    @Override
    public String toString() {
        return "UploadPartRequest{" +
                "bucketName='" + bucketName + '\'' +
                ", fileId='" + fileId + '\'' +
                ", uploadId='" + uploadId + '\'' +
                ", partNumber=" + partNumber +
                '}';
    }
}
