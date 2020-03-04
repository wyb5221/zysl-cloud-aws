package com.zysl.aws.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;

@Setter
@Getter
public class FileVersionResponse implements Serializable {
    private static final long serialVersionUID = -8828827375217228417L;

    private String eTag;
    private Integer size;
    private String storageClass;
    private String key;
    private String versionId;
    private Boolean isLatest;
    private Instant lastModified;

    @Override
    public String toString() {
        return "FileVersionResponse{" +
                "eTag='" + eTag + '\'' +
                ", size=" + size +
                ", storageClass='" + storageClass + '\'' +
                ", key='" + key + '\'' +
                ", versionId='" + versionId + '\'' +
                ", isLatest=" + isLatest +
                ", lastModified=" + lastModified +
                '}';
    }
}
