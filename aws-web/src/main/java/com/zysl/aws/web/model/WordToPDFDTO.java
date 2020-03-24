package com.zysl.aws.web.model;

import com.zysl.cloud.utils.Constants.SwaggerConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@ApiModel(description = "word转pdf返回对象")
public class WordToPDFDTO implements Serializable {
    private static final long serialVersionUID = -2403682161678847220L;
    @ApiModelProperty(value = "文件夹名称", name = "bucketName", dataType = SwaggerConstants.DATA_TYPE_STRING)
    private String bucketName;

    @ApiModelProperty(value = "文件名", name = "fileName", dataType = SwaggerConstants.DATA_TYPE_STRING)
    private String fileName;

    @ApiModelProperty(value = "文件版本id", name = "versionId", dataType = SwaggerConstants.DATA_TYPE_STRING)
    private String versionId;

    @Override
    public String toString() {
        return "WordToPDFDTO{" +
                "bucketName='" + bucketName + '\'' +
                ", fileName='" + fileName + '\'' +
                ", versionId='" + versionId + '\'' +
                '}';
    }
}
