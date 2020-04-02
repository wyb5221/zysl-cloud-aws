package com.zysl.cloud.aws.api.req;

import com.zysl.cloud.utils.common.BaseReqeust;
import com.zysl.cloud.utils.constants.SwaggerConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 断点续传完成确认入参
 */
@Setter
@Getter
@ApiModel(description = "断点续传完成确认入参对象")
public class CompleteMultipartRequest extends BaseReqeust {
    private static final long serialVersionUID = 6528744834290825581L;

    //文件夹名称
    @ApiModelProperty(value = "bucket存储桶名称", name = "bucketName", required = true, dataType = SwaggerConstants.DATA_TYPE_STRING)
    private String bucketName;
    //文件名
    @ApiModelProperty(value = "文件名", name = "fileId", required = true, dataType = SwaggerConstants.DATA_TYPE_STRING)
    private String fileId;

    @ApiModelProperty(value = "断点续传id", name = "uploadId", required = true, dataType = SwaggerConstants.DATA_TYPE_STRING)
    private String uploadId;

    @ApiModelProperty(value = "断点续传集合", name = "eTagList", required = true, dataType = SwaggerConstants.DATA_TYPE_ARRAY)
    private List<MultipartUploadRequest> eTagList;

    @Override
    public String toString() {
        return "CompleteMultipartRequest{" +
                "bucketName='" + bucketName + '\'' +
                ", fileId='" + fileId + '\'' +
                ", uploadId='" + uploadId + '\'' +
                ", eTagList=" + eTagList +
                '}';
    }
}
