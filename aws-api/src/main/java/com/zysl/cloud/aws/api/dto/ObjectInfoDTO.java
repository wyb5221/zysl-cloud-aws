package com.zysl.cloud.aws.api.dto;

import com.zysl.cloud.utils.common.BasePaginationRequest;
import com.zysl.cloud.utils.constants.SwaggerConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;

@Getter
@Setter
@ApiModel(description = "查询目录下信息返回对象")
public class ObjectInfoDTO extends BasePaginationRequest {

    private static final long serialVersionUID = 8036080657342894989L;

    //对象名称
    @ApiModelProperty(value = "对象名称", name = "key", dataType = SwaggerConstants.DATA_TYPE_STRING)
    private String key;
    //文件上传时间
    @ApiModelProperty(value = "文件上传时间", name = "uploadTime", dataType = SwaggerConstants.DATA_TYPE_STRING)
    private Instant uploadTime;
    //文件大小
    @ApiModelProperty(value = "文件大小", name = "fileSize", dataType = SwaggerConstants.DATA_TYPE_INTEGER)
    private Long fileSize;
    //文件内容md5
    @ApiModelProperty(value = "文件内容md5", name = "contentMd5", dataType = SwaggerConstants.DATA_TYPE_STRING)
    private String contentMd5;

    @Override
    public String toString() {
        return "FileInfo{" +
                "key='" + key + '\'' +
                ", uploadTime=" + uploadTime +
                ", fileSize=" + fileSize +
                ", contentMd5='" + contentMd5 + '\'' +
                '}';
    }
}
