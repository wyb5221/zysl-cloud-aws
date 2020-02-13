package com.zysl.aws.service;

import com.zysl.aws.model.UploadFileRequest;
import com.zysl.aws.model.db.S3File;
import com.zysl.aws.model.db.S3Folder;
import com.zysl.aws.model.db.S3Service;

import java.util.List;

public interface FileService {

    /**
     * 查询所有服务器信息
     * @return
     */
    List<S3Service> queryS3Service();

    /**
     * 查询所有文件夹
     * @return
     */
    List<S3Folder> queryS3FolderInfo();

    /**
     * 查询文件夹信息
     * @param folderName
     * @return
     */
    S3Folder queryS3Folder(String folderName);

    /**
     * 新增文件夹信息
     * @param folderName
     * @return
     */
    int insertFolderInfo(String folderName, String serviceNo);

    /**
     * 根据文件夹名称删除文件夹
     * @param folderName
     * @return
     */
    int deleteFolderByName(String folderName);

    /**
     * 根据md5值判断文件是否存在
     * @param content
     * @return
     */
    boolean queryFileByMd5(String content);

    /**
     * 根据md5值查询文件信息
     * @param content
     * @return
     */
    S3File queryFileInfoByMd5(String content);

    /**
     * 新增文件信息
     * @param request
     * @return
     */
    int addFileInfo(UploadFileRequest request);

}
