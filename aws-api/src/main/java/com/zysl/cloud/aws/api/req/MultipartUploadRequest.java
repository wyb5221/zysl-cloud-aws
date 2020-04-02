package com.zysl.cloud.aws.api.req;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class MultipartUploadRequest implements Serializable {
    private static final long serialVersionUID = -7557648465909702820L;

    //续传次数
    private Integer partNumber;
    //断点续传标记
    private String eTag;

    @Override
    public String toString() {
        return "MultipartUploadBO{" +
                "partNumber=" + partNumber +
                ", eTag='" + eTag + '\'' +
                '}';
    }
}
