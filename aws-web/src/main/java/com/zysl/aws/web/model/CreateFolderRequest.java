package com.zysl.aws.web.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 创建文件夹入参对象
 */
@Setter
@Getter
public class CreateFolderRequest implements Serializable {
    private static final long serialVersionUID = -5869059344984997277L;

    /**
     * 存储桶名称
     */
    private String bucketName;
    /**
     * 文件夹名称
     */
    private String folderName;

    @Override
    public String toString() {
        return "CreateFolderRequest{" +
                "bucketName='" + bucketName + '\'' +
                ", folderName='" + folderName + '\'' +
                '}';
    }
}
