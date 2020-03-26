package com.zysl.cloud.aws.api.req;

import com.zysl.cloud.utils.common.BaseReqeust;
import com.zysl.cloud.utils.validator.IValidator;
import com.zysl.cloud.utils.validator.impl.EnumValue;
import java.util.List;
import java.util.regex.Pattern;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * 设置文件版本接口入参
 */
@Setter
@Getter
public class SetFileVersionRequest extends BaseReqeust {


    private static final long serialVersionUID = -4385451165654540779L;

    private String bucketName;

    //版本权限状态  Enabled：启动 Suspended：关闭
    private String status;

    @Override
    public String toString() {
        return "SetFileVersionRequest{" +
                   "bucketName='" + bucketName + '\'' +
                   ", status='" + status + '\'' +
                   '}';
    }
}
