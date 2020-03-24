package com.zysl.aws.web.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 设置文件版本接口入参
 */
@Setter
@Getter
public class SetFileVersionRequest implements Serializable {
    private static final long serialVersionUID = -8298654892090559103L;

    //文件夹名称
    private String bucketName;
    //版本权限状态  Enabled：启动 Suspended：关闭
    private String status;

    @Override
    public String toString() {
        return "SetFileVersionRequest{" +
                "bucketName='" + bucketName + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
