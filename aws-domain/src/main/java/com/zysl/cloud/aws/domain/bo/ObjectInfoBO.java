package com.zysl.cloud.aws.domain.bo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;

/**
 * 对象信息
 */
@Getter
@Setter
public class ObjectInfoBO implements Serializable {

    private static final long serialVersionUID = 8036080657342894989L;

    //对象名称
    private String key;
    //文件上传时间
    private Instant uploadTime;
    //文件大小
    private Long fileSize;
    //文件内容md5
    private String contentMd5;

    @Override
    public String toString() {
        return "FileInfo{" +
                "key='" + key + '\'' +
                ", uploadTime=" + uploadTime +
                ", fileSize=" + fileSize +
                ", contentMd5='" + contentMd5 + '\'' +
                '}';
    }
}
