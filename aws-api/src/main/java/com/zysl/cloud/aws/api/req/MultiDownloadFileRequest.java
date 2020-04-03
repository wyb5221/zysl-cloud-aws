package com.zysl.cloud.aws.api.req;

import com.zysl.cloud.utils.common.BaseReqeust;
import com.zysl.cloud.utils.constants.SwaggerConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 文件下载入参对象
 */
@Setter
@Getter
@ApiModel(description = "分片文件下载请求对象")
public class MultiDownloadFileRequest extends BaseReqeust {
    
    private static final long serialVersionUID = -1586706876359805729L;
    //文件夹名称
    @ApiModelProperty(value = "文件夹名称", name = "bucketName",required = true,dataType = SwaggerConstants.DATA_TYPE_STRING)
    private String bucketName;
    //文件名称
    @ApiModelProperty(value = "文件名称", name = "fileId",required = true,dataType = SwaggerConstants.DATA_TYPE_STRING)
    private String fileId;
    //文件版本id
    @ApiModelProperty(value = "文件版本id", name = "versionId",dataType = SwaggerConstants.DATA_TYPE_STRING)
    private String versionId;
    
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("{\"MultiDownloadFileRequest\":{");
        if (bucketName != null) {
            sb.append("bucketName='").append(bucketName).append('\'');
        }
        if (fileId != null) {
            sb.append(", fileId='").append(fileId).append('\'');
        }
        if (versionId != null) {
            sb.append(", versionId='").append(versionId).append('\'');
        }
        sb.append("},\"super-MultiDownloadFileRequest\":")
            .append(super.toString()).append("}");
        return sb.toString();
    }
}
