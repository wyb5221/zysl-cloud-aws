package com.zysl.cloud.aws.api.req;

import com.zysl.cloud.utils.common.BaseReqeust;
import com.zysl.cloud.utils.constants.SwaggerConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 创建断点续传入参
 */
@Setter
@Getter
@ApiModel(description = "创建断点续传入参对象")
public class CreateMultipartRequest extends BaseReqeust {
    private static final long serialVersionUID = 6528744834290825581L;

    //文件夹名称
    @ApiModelProperty(value = "bucket存储桶名称", name = "bucketName",required = true, dataType = SwaggerConstants.DATA_TYPE_STRING)
    private String bucketName;
    //文件名
    @ApiModelProperty(value = "文件名", name = "fileId",required = true, dataType = SwaggerConstants.DATA_TYPE_STRING)
    private String fileId;

    @ApiModelProperty(value = "标签中保存文件名称", name = "fileName",dataType = SwaggerConstants.DATA_TYPE_STRING)
    private String fileName;

    @Override
    public String toString() {
        return "CreateMultipartRequest{" +
                "bucketName='" + bucketName + '\'' +
                ", fileId='" + fileId + '\'' +
                ", fileName='" + fileName + '\'' +
                '}';
    }
}
