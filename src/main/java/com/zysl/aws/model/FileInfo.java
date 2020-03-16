package com.zysl.aws.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
public class FileInfo implements Serializable {

    private static final long serialVersionUID = 8036080657342894989L;

    private Long id;
    //文件名称
    private String fileName;
    //文件夹名称
    private String folderName;
    //文件上传时间
    private Date uploadTime;
    //文件大小
    private Long fileSize;
    //文件内容md5
    private String contentMd5;
    //最大可下载次数
    private Integer maxAmount;
    //已下载次数
    private Integer downAmount;
    //下载有效截至时间
    private Date validity_time;

    @Override
    public String toString() {
        return "FileInfo{" +
                "id=" + id +
                ", fileName='" + fileName + '\'' +
                ", folderName='" + folderName + '\'' +
                ", uploadTime=" + uploadTime +
                ", fileSize=" + fileSize +
                ", contentMd5='" + contentMd5 + '\'' +
                ", maxAmount=" + maxAmount +
                ", downAmount=" + downAmount +
                ", validity_time=" + validity_time +
                '}';
    }
}
