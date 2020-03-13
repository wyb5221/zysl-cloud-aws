package com.zysl.aws.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 上传文件返回对象
 */
@Setter
@Getter
public class UploadFieResponse implements Serializable {
    private static final long serialVersionUID = 3248967534340735425L;

    //文件夹名称
    private String folderName;
    //文件名称
    private String fileName;

    @Override
    public String toString() {
        return "UploadFieResponse{" +
                "folderName='" + folderName + '\'' +
                ", fileName='" + fileName + '\'' +
                '}';
    }
}
