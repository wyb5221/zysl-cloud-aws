package com.zysl.aws.web.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * key
 */
@Setter
@Getter
public class KeyVersionDTO implements Serializable {
    private static final long serialVersionUID = -8278125396536138315L;

    private String key;
    private String versionId;

    @Override
    public String toString() {
        return "KeyVersionDTO{" +
                "key='" + key + '\'' +
                ", versionId='" + versionId + '\'' +
                '}';
    }
}
