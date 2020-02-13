package com.zysl.aws.mapper;

import com.zysl.aws.model.db.S3File;
import com.zysl.aws.model.db.S3Folder;

public interface S3FileMyMapper {

    /**
     * 根据文件夹名称查询信息
     * @param folderName
     * @return
     */
    S3Folder queryByName(String folderName);

    /**
     *  根据文件夹名称删除文件夹
     * @param folderName
     * @return
     */
    int deleteFolderByName(String folderName);

    /**
     * 根据md5值判断文件是否存在
     * @param content
     * @return
     */
    int queryFileByMd5(String content);

    /**
     * 根据md5值查询文件信息
     * @param content
     * @return
     */
    S3File queryFileInfoByMd5(String content);

}
