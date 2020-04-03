package com.zysl.cloud.aws.api.dto;

import com.zysl.cloud.utils.constants.SwaggerConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * 查询分区上传记录返回对象
 */
@Setter
@Getter
@ApiModel(description = "查询分区上传记录返回对象")
public class FilePartInfoDTO implements Serializable {
    private static final long serialVersionUID = 8589091988763950331L;
    @ApiModelProperty(value = "分区上传次数", name = "partNumber", dataType = SwaggerConstants.DATA_TYPE_INTEGER)
    private Integer partNumber;
    @ApiModelProperty(value = "文件上传时间", name = "lastModified", dataType = SwaggerConstants.DATA_TYPE_STRING)
    private Date lastModified;
    @ApiModelProperty(value = "分区文件内容", name = "eTag", dataType = SwaggerConstants.DATA_TYPE_STRING)
    private String eTag;
    @ApiModelProperty(value = "分区文件大小", name = "size", dataType = SwaggerConstants.DATA_TYPE_INTEGER)
    private Integer size;

    @Override
    public String toString() {
        return "FilePartInfoDTO{" +
                "partNumber=" + partNumber +
                ", lastModified=" + lastModified +
                ", eTag='" + eTag + '\'' +
                ", size=" + size +
                '}';
    }
}
