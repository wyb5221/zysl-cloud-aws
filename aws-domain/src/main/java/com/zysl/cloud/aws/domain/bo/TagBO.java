package com.zysl.cloud.aws.domain.bo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

//标签信息
@Setter
@Getter
public class TagBO implements Serializable {
    private static final long serialVersionUID = -4816358533421480928L;

    public TagBO(){}

    public TagBO(String key,String value){
        this.key = key;
        this.value = value;
    }

    private String key;
    private String value;

    @Override
    public String toString() {
        return "TagBO{" +
                "key='" + key + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
