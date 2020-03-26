package com.zysl.cloud.aws.domain.bo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

//标签信息
@Setter
@Getter
public class TagsBO implements Serializable {
    private static final long serialVersionUID = -4816358533421480928L;

    private String key;
    private String value;

    @Override
    public String toString() {
        return "TagsBO{" +
                "key='" + key + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
