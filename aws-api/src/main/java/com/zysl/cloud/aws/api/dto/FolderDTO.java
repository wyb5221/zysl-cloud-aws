package com.zysl.cloud.aws.api.dto;

import com.zysl.cloud.utils.constants.SwaggerConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
@ApiModel(description = "目录返回对象")
public class FolderDTO implements Serializable {
    private static final long serialVersionUID = 3324842608631745430L;

    //文件夹名称
    @ApiModelProperty(value = "文件夹名称", name = "folderName", dataType = SwaggerConstants.DATA_TYPE_STRING)
    private String folderName;
    //文件版本id
    @ApiModelProperty(value = "版本id", name = "versionId", dataType = SwaggerConstants.DATA_TYPE_STRING)
    private String versionId;
    //标签中文件名称
    @ApiModelProperty(value = "标签中文件名称", name = "tagFileName", dataType = SwaggerConstants.DATA_TYPE_STRING)
    private String tagFileName;

    @Override
    public String toString() {
        return "FolderDTO{" +
                "folderName='" + folderName + '\'' +
                ", versionId='" + versionId + '\'' +
                ", tagFileName='" + tagFileName + '\'' +
                '}';
    }
}
