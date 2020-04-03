package com.zysl.cloud.aws.api.req;

import com.zysl.cloud.utils.common.BaseReqeust;
import com.zysl.cloud.utils.constants.SwaggerConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * 断点续传完成确认入参
 */
@Setter
@Getter
@ApiModel(description = "取消分片上传对象")
public class AbortMultipartRequest extends BaseReqeust {
    private static final long serialVersionUID = 6528744834290825581L;

    //文件夹名称
    @ApiModelProperty(value = "bucket存储桶名称", name = "bucketName", required = true, dataType = SwaggerConstants.DATA_TYPE_STRING)
    private String bucketName;
    //文件名
    @ApiModelProperty(value = "文件名", name = "fileId", required = true, dataType = SwaggerConstants.DATA_TYPE_STRING)
    private String fileId;

    @ApiModelProperty(value = "断点续传id", name = "uploadId", required = true, dataType = SwaggerConstants.DATA_TYPE_STRING)
    private String uploadId;
    
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("{\"AbortMultipartRequest\":{");
        if (bucketName != null) {
            sb.append("bucketName='").append(bucketName).append('\'');
        }
        if (fileId != null) {
            sb.append(", fileId='").append(fileId).append('\'');
        }
        if (uploadId != null) {
            sb.append(", uploadId='").append(uploadId).append('\'');
        }
        sb.append("},\"super-AbortMultipartRequest\":")
            .append(super.toString()).append("}");
        return sb.toString();
    }
}
