package com.zysl.cloud.aws.domain.bo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Setter
@Getter
public class FilePartInfoBO implements Serializable {
    private static final long serialVersionUID = -3016264913382019065L;

    private Integer partNumber;
    private Date lastModified;
    private String eTag;
    private Integer size;

    @Override
    public String toString() {
        return "FilePartInfoBO{" +
                "partNumber=" + partNumber +
                ", lastModified=" + lastModified +
                ", eTag='" + eTag + '\'' +
                ", size=" + size +
                '}';
    }
}
