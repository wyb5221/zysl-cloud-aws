package com.zysl.aws.web.model;

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
    //文件版本id
    private String versionId;

    @Override
    public String toString() {
        return "UploadFieResponse{" +
                "folderName='" + folderName + '\'' +
                ", fileName='" + fileName + '\'' +
                ", versionId='" + versionId + '\'' +
                '}';
    }
}
