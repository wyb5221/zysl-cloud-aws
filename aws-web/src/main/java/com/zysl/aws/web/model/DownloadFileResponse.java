package com.zysl.aws.web.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 文件下载返回对象
 */
@Setter
@Getter
public class DownloadFileResponse implements Serializable {
    private static final long serialVersionUID = 8157807993635333073L;

    //文件流base64进制String
    private String data;
    private String reason;
    //下载耗时
    private Long usedTime;

    @Override
    public String toString() {
        return "DownloadFileResponse{" +
                "data='" + data + '\'' +
                ", reason='" + reason + '\'' +
                ", usedTime=" + usedTime +
                '}';
    }
}
