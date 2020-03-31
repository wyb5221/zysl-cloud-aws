package com.zysl.cloud.aws.api.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 文件tage对象
 */
@Setter
@Getter
public class TagDTO implements Serializable {
    private static final long serialVersionUID = -1757458336891053584L;
    private String key;
    private String value;

    @Override
    public String toString() {
        return "TagDTO{" +
                "key='" + key + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
