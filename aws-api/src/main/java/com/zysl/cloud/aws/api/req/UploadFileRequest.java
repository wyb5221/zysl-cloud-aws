package com.zysl.cloud.aws.api.req;

import com.zysl.cloud.utils.common.BaseReqeust;
import lombok.Getter;
import lombok.Setter;

/**
 * 上传文件入参对象
 */
@Setter
@Getter
public class UploadFileRequest extends BaseReqeust {

    private static final long serialVersionUID = 1004628529931222879L;

    //文件夹名称
    private String bucketName;
    //文件名
    private String fileId;
    //文件流
    private String data;
    //最大可下载次数
    private Integer maxAmount;
    //有效期，单位小时
    private Integer validity;

    @Override
    public String toString() {
        return "UploadFileRequest{" +
                "bucketName='" + bucketName + '\'' +
                ", fileId='" + fileId + '\'' +
                ", data='" + data.length() + '\'' +
                ", maxAmount=" + maxAmount +
                ", validity=" + validity +
                '}';
    }
}
