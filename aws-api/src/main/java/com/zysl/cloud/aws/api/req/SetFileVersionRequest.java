package com.zysl.cloud.aws.api.req;

import com.zysl.cloud.utils.common.BaseReqeust;
import com.zysl.cloud.utils.constants.SwaggerConstants;
import com.zysl.cloud.utils.validator.IValidator;
import com.zysl.cloud.utils.validator.impl.EnumValue;
import java.util.List;
import java.util.regex.Pattern;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 设置文件版本接口入参
 */
@Setter
@Getter
@ApiModel(description = "设置文件版本请求对象")
public class SetFileVersionRequest extends BaseReqeust {

    private static final long serialVersionUID = -4385451165654540779L;
    @ApiModelProperty(value = "bucket存储桶名称", name = "bucketName",required = true,dataType = SwaggerConstants.DATA_TYPE_STRING)
    private String bucketName;

    //版本权限状态  Enabled：启动 Suspended：关闭
    @ApiModelProperty(value = "版本权限状态  Enabled：启动 Suspended：关闭", name = "status",required = true,dataType = SwaggerConstants.DATA_TYPE_STRING)
    private String status;

    @Override
    public String toString() {
        return "SetFileVersionRequest{" +
                   "bucketName='" + bucketName + '\'' +
                   ", status='" + status + '\'' +
                   '}';
    }
}
