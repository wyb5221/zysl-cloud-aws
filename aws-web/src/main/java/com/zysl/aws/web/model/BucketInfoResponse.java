package com.zysl.aws.web.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * bucket列表返回对象
 */
@Setter
@Getter
public class BucketInfoResponse implements Serializable {
    private static final long serialVersionUID = 1238661426442756869L;

    private String bucketName;
    private String serviceNo;

    @Override
    public String toString() {
        return "BucketInfoResponse{" +
                "bucketName='" + bucketName + '\'' +
                ", serviceNo='" + serviceNo + '\'' +
                '}';
    }
}
