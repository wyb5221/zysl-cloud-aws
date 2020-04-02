package com.zysl.cloud.aws.domain.bo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class MultipartUploadBO implements Serializable {
    private static final long serialVersionUID = -7557648465909702820L;

    //续传次数
    private Integer partNumber;
    //断点续传标记
    private String eTag;

    public MultipartUploadBO(Integer partNumber, String eTag) {
        this.partNumber = partNumber;
        this.eTag = eTag;
    }

    @Override
    public String toString() {
        return "MultipartUploadBO{" +
                "partNumber=" + partNumber +
                ", eTag='" + eTag + '\'' +
                '}';
    }
}
