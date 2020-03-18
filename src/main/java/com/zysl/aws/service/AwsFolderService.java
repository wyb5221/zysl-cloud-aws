package com.zysl.aws.service;

import com.zysl.aws.model.BucketFileRequest;
import com.zysl.aws.model.CreateFolderRequest;
import com.zysl.aws.model.FileInfo;

import java.util.List;

public interface AwsFolderService {

    /**
     * 创建目录folder
     */
    boolean createFolder(CreateFolderRequest request);

    /**
     * 删除目录（文件夹）
     */
    boolean deleteFolder(String bucketName, String key);

    /**
     * 调用s3接口查询文件夹下的对象
     * @param request
     * @return
     */
    List<FileInfo> getS3FileList(BucketFileRequest request);

}
