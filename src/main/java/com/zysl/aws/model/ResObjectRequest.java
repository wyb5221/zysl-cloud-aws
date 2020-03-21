package com.zysl.aws.model;

import com.zysl.cloud.utils.common.BaseReqeust;
import lombok.Getter;
import lombok.Setter;

/**
 * 还原已删除对象入参
 */
@Setter
@Getter
public class ResObjectRequest extends BaseReqeust {
    private static final long serialVersionUID = 6145758558281330107L;

    private String bucketName;
    private String key;
    private String versionId;

    @Override
    public String toString() {
        return "ResObjectRequest{" +
                "bucketName='" + bucketName + '\'' +
                ", key='" + key + '\'' +
                ", versionId='" + versionId + '\'' +
                '}';
    }
}
