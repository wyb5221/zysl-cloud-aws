package com.zysl.aws.model;

import com.zysl.cloud.utils.common.BaseReqeust;
import lombok.Getter;
import lombok.Setter;

/**
 * 删除对象入参
 */
@Setter
@Getter
public class DelObjectRequest extends BaseReqeust {
    private static final long serialVersionUID = -5393931372818829200L;
    //存储桶名称
    private String bucketName;
    //对象名称，可以带目录，如a/b/1.doc
    private String key;
    //文件版本号
    private String versionId;
    //是否物理删除，1是0否，默认0
    private Integer deleteStore;

    @Override
    public String toString() {
        return "DelObjectRequest{" +
                "bucketName='" + bucketName + '\'' +
                ", key='" + key + '\'' +
                ", versionId='" + versionId + '\'' +
                ", deleteStore='" + deleteStore + '\'' +
                '}';
    }
}
