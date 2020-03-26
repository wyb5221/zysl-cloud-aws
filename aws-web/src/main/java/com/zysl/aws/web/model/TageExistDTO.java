package com.zysl.aws.web.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class TageExistDTO implements Serializable {
    private static final long serialVersionUID = -5544062749372817767L;

    private String userId;
    private String bucket;
    private String key;
    private String versionId;

    @Override
    public String toString() {
        return "TageExistDTO{" +
                "userId='" + userId + '\'' +
                ", bucket='" + bucket + '\'' +
                ", key='" + key + '\'' +
                ", versionId='" + versionId + '\'' +
                '}';
    }
}
