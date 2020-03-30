package com.zysl.cloud.aws.api.req;

import com.zysl.cloud.utils.common.BaseReqeust;
import com.zysl.cloud.utils.constants.SwaggerConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 上传文件入参对象
 */
@Setter
@Getter
@ApiModel(description = "上传文件请求对象")
public class UploadFileRequest extends BaseReqeust {

    private static final long serialVersionUID = 1004628529931222879L;

    //文件夹名称
    @ApiModelProperty(value = "bucket存储桶名称", name = "bucketName",required = true,dataType = SwaggerConstants.DATA_TYPE_STRING)
    private String bucketName;
    //文件名
    @ApiModelProperty(value = "文件名", name = "fileId",dataType = SwaggerConstants.DATA_TYPE_STRING)
    private String fileId;
    //文件流
    @ApiModelProperty(value = "文件base64进制字符串", name = "data",required = true,dataType = SwaggerConstants.DATA_TYPE_STRING)
    private String data;
    //最大可下载次数
    @ApiModelProperty(value = "最大可下载次数", name = "maxAmount",dataType = SwaggerConstants.DATA_TYPE_INTEGER)
    private Integer maxAmount;
    //有效期，单位小时
    @ApiModelProperty(value = "有效期，单位小时", name = "validity",dataType = SwaggerConstants.DATA_TYPE_INTEGER)
    private Integer validity;
    //有效期，单位小时
    @ApiModelProperty(value = "标签中保存文件名称", name = "fileName",dataType = SwaggerConstants.DATA_TYPE_STRING)
    private String fileName;

    @Override
    public String toString() {
        return "UploadFileRequest{" +
                "bucketName='" + bucketName + '\'' +
                ", fileId='" + fileId + '\'' +
                ", data='" + data + '\'' +
                ", maxAmount=" + maxAmount +
                ", validity=" + validity +
                ", fileName='" + fileName + '\'' +
                '}';
    }
}
