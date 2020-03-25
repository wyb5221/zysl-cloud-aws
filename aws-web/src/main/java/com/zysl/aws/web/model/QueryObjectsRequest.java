package com.zysl.aws.web.model;

import com.zysl.cloud.utils.common.BaseReqeust;
import lombok.Getter;
import lombok.Setter;

/**
 * 查询子目录对象列表入参对象
 */
@Setter
@Getter
public class QueryObjectsRequest extends BaseReqeust {
    private static final long serialVersionUID = -3261527596066639380L;

    //存储桶名称
    private String bucketName;
    //目录名称，多级目录用/隔开，例如a/b
    private String key;
    //0默认全部，1仅目录2仅文件
    private Integer keyType;
    //文件标签
    private String userId;

    @Override
    public String toString() {
        return "QueryObjectsRequest{" +
                "bucketName='" + bucketName + '\'' +
                ", key='" + key + '\'' +
                ", keyType=" + keyType +
                ", userId='" + userId + '\'' +
                '}';
    }
}
