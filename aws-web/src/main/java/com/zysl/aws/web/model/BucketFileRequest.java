package com.zysl.aws.web.model;

import com.zysl.cloud.utils.common.BasePaginationRequest;
import lombok.Getter;
import lombok.Setter;

/**
 * 查询 文件夹下文件信息入参对象
 */
@Setter
@Getter
public class BucketFileRequest extends BasePaginationRequest {
    private static final long serialVersionUID = 3230937616934606779L;
    //文件夹名称
    private String bucketName;
    //文件名称(模糊匹配)
    private String fileName;
    @Override
    public String toString() {
        return "BucketFileRequest{" +
                "bucketName='" + bucketName + '\'' +
                ", fileName='" + fileName + '\'' +
                '}';
    }
}
