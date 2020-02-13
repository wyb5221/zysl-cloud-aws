package com.zysl.aws.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;

@Getter
@Setter
public class FileInfo implements Serializable {

    private static final long serialVersionUID = 8036080657342894989L;

    private String key;

    private Instant lastModified;

    private String eTag;

    private Long size;

    private String storageClass;

    @Override
    public String toString() {
        return "FileInfo{" +
                "key='" + key + '\'' +
                ", lastModified=" + lastModified +
                ", eTag='" + eTag + '\'' +
                ", size=" + size +
                ", storageClass='" + storageClass + '\'' +
                '}';
    }
}
