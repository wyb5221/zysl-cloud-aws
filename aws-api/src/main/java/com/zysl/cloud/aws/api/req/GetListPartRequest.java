package com.zysl.cloud.aws.api.req;

import com.zysl.cloud.utils.common.BasePaginationRequest;
import com.zysl.cloud.utils.constants.SwaggerConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 查询分区上传记录入参
 */
@Setter
@Getter
@ApiModel(description = "查询分区上传记录入参对象")
public class GetListPartRequest extends BasePaginationRequest {

    private static final long serialVersionUID = 9034752905630620539L;

    //文件夹名称
    @ApiModelProperty(value = "bucket存储桶名称", name = "bucketName", required = true, dataType = SwaggerConstants.DATA_TYPE_STRING)
    private String bucketName;
    //文件名
    @ApiModelProperty(value = "文件名", name = "fileId", required = true, dataType = SwaggerConstants.DATA_TYPE_STRING)
    private String fileId;

//    @ApiModelProperty(value = "断点续传id", name = "uploadId", dataType = SwaggerConstants.DATA_TYPE_STRING)
//    private String uploadId;

    @Override
    public String toString() {
        return "GetListPartRequest{" +
                "bucketName='" + bucketName + '\'' +
                ", fileId='" + fileId + '\'' +
                '}';
    }
}
