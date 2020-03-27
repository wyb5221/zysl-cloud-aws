package com.zysl.cloud.aws.api.req;

import com.zysl.cloud.utils.common.BaseReqeust;
import com.zysl.cloud.utils.constants.SwaggerConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 文件分享请求对象
 */
@Setter
@Getter
@ApiModel(description = "文件分享请求对象")
public class ShareFileRequest extends BaseReqeust {
  //文件夹名称
  @ApiModelProperty(value = "bucket存储桶名称", name = "bucketName",required = true,dataType = SwaggerConstants.DATA_TYPE_STRING)
  private String bucketName;
  //文件名
  @ApiModelProperty(value = "文件名", name = "fileName",required = true,dataType = SwaggerConstants.DATA_TYPE_STRING)
  private String fileName;
  //最大下载次数
  @ApiModelProperty(value = "最大下载次数", name = "maxDownloadAmout",dataType = SwaggerConstants.DATA_TYPE_INTEGER)
  private Integer maxDownloadAmout;
  //最大有效时长(小时)
  @ApiModelProperty(value = "最大有效时长(小时)", name = "maxHours",dataType = SwaggerConstants.DATA_TYPE_INTEGER)
  private Integer maxHours;

  @Override
  public String toString() {
    return "ShareFileRequest{" +
        "bucketName='" + bucketName + '\'' +
        ", fileName='" + fileName + '\'' +
        ", maxDownloadAmout=" + maxDownloadAmout +
        ", maxHours=" + maxHours +
        '}';
  }
}
