package com.zysl.cloud.aws.api.req;

import com.zysl.cloud.utils.common.BaseReqeust;
import lombok.Getter;
import lombok.Setter;

/**
 * 文件复制入参对象
 */
@Setter
@Getter
public class CopyObjectsRequest extends BaseReqeust {
    private static final long serialVersionUID = -1272324062190153756L;

    //源存储桶名称
    private String sourceBucket;
    //源目录名称，多级目录用/隔开，例如a/b.doc
    private String sourceKey;
    //目标存储桶名称
    private String destBucket;
    //目标目录名称，多级目录用/隔开，例如a/b.doc
    private String destKey;

    @Override
    public String toString() {
        return "CopyFileRequest{" +
                "sourceBucket='" + sourceBucket + '\'' +
                ", sourceKey='" + sourceKey + '\'' +
                ", destBucket='" + destBucket + '\'' +
                ", destKey='" + destKey + '\'' +
                '}';
    }
}
