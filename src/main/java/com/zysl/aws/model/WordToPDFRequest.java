package com.zysl.aws.model;

import com.zysl.cloud.utils.Constants.SwaggerConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@ApiModel(description = "word转pdf的请求对象")
public class WordToPDFRequest implements Serializable {

  private static final long serialVersionUID = 3231659254522990103L;
  @ApiModelProperty(value = "文件夹名称", name = "bucketName", dataType = SwaggerConstants.DATA_TYPE_STRING, required = true)
  private String bucketName;

  @ApiModelProperty(value = "文件名", name = "fileName", dataType = SwaggerConstants.DATA_TYPE_STRING, required = true)
  private String fileName;

  @ApiModelProperty(value = "文件版本id", name = "versionId", dataType = SwaggerConstants.DATA_TYPE_STRING)
  private String versionId;

  @ApiModelProperty(value = "文字水印", name = "textMark", dataType = SwaggerConstants.DATA_TYPE_STRING)
  private String textMark;

  @ApiModelProperty(value = "用户密码", name = "userPwd", dataType = SwaggerConstants.DATA_TYPE_STRING)
  private String userPwd;

  @ApiModelProperty(value = "所有者密码", name = "ownerPwd", dataType = SwaggerConstants.DATA_TYPE_STRING)
  private String ownerPwd;

  @Override
  public String toString() {
    return "WordToPDFRequest{" +
        "bucketName='" + bucketName + '\'' +
        ", fileName='" + fileName + '\'' +
        ", textMark='" + textMark + '\'' +
        ", userPwd='" + userPwd + '\'' +
        ", ownerPwd='" + ownerPwd + '\'' +
        '}';
  }
}
