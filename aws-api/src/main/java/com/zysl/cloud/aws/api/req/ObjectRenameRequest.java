package com.zysl.cloud.aws.api.req;

import com.zysl.cloud.utils.common.BaseReqeust;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

/**
 * 文件重命名入参对象
 */
@Setter
@Getter
@ApiModel(description = "文件下载请求对象")
public class ObjectRenameRequest extends BaseReqeust {
    private static final long serialVersionUID = 100128087087200329L;

    private String bucketName;

    private String sourcekey;

    private String destKey;

    @Override
    public String toString() {
        return "ObjectRenameRequest{" +
                "bucketName='" + bucketName + '\'' +
                ", sourcekey='" + sourcekey + '\'' +
                ", destKey='" + destKey + '\'' +
                '}';
    }
}
