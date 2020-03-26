package com.zysl.cloud.aws.api.dto;

import com.zysl.cloud.utils.constants.SwaggerConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 上传文件返回对象
 */
@Setter
@Getter
@ApiModel(description = "上传文件返回对象")
public class UploadFieDTO implements Serializable {
    private static final long serialVersionUID = 3248967534340735425L;

    //文件夹名称
    @ApiModelProperty(value = "文件夹名称", name = "folderName", dataType = SwaggerConstants.DATA_TYPE_STRING)
    private String folderName;
    //文件名称
    @ApiModelProperty(value = "文件名称", name = "fileName", dataType = SwaggerConstants.DATA_TYPE_STRING)
    private String fileName;
    //文件版本id
    @ApiModelProperty(value = "版本id", name = "versionId", dataType = SwaggerConstants.DATA_TYPE_STRING)
    private String versionId;

    @Override
    public String toString() {
        return "UploadFieResponse{" +
                "folderName='" + folderName + '\'' +
                ", fileName='" + fileName + '\'' +
                ", versionId='" + versionId + '\'' +
                '}';
    }
}
