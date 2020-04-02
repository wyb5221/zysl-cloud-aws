package com.zysl.cloud.aws.api.dto;

import com.zysl.cloud.utils.constants.SwaggerConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 文件下载返回对象
 */
@Setter
@Getter
@ApiModel(description = "文件下载返回对象")
public class DownloadFileDTO implements Serializable {
    private static final long serialVersionUID = 8157807993635333073L;

    //文件流base64进制String
    @ApiModelProperty(value = "件流base64进制String", name = "data", dataType = SwaggerConstants.DATA_TYPE_STRING)
    private String data;

    private String reason;
    //下载耗时
    private Long usedTime;

    @Override
    public String toString() {
        return "DownloadFileResponse{" +
                "data='" + data + '\'' +
                ", reason='" + reason + '\'' +
                ", usedTime=" + usedTime +
                '}';
    }
}
