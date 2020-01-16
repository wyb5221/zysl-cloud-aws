package com.zysl.aws.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Arrays;

/**
 * 上传文件入参对象
 */
@Setter
@Getter
public class UploadFileRequest implements Serializable {

    private static final long serialVersionUID = 1004628529931222879L;

    //文件夹名称
    private String bucketName;
    //文件名
    private String fileId;
    //是否覆盖 0不覆盖 1覆盖
    private String inplace;
    //文件流
    private String data;

    @Override
    public String toString() {
        return "UploadFileRequest{" +
                "bucketName='" + bucketName + '\'' +
                ", fileId='" + fileId + '\'' +
                ", inplace='" + inplace + '\'' +
                ", data='" + data + '\'' +
                '}';
    }
}
