package com.zysl.aws.web.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 文件下载入参对象
 */
@Setter
@Getter
public class DownloadFileRequest implements Serializable {
    private static final long serialVersionUID = -8651479291764968852L;

    //文件夹名称
    private String bucketName;
    //文件名称
    private String fileId;
    //文件版本id
    private String versionId;
    //下载文件类型 0：默认，下载文件 1：下载二进制流
    private String type;

    @Override
    public String toString() {
        return "DownloadFileRequest{" +
                "bucketName='" + bucketName + '\'' +
                ", fileId='" + fileId + '\'' +
                ", versionId='" + versionId + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
