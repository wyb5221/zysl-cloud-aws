package com.zysl.aws.service;

import com.zysl.aws.model.FileInfo;
import com.zysl.aws.model.ShareFileRequest;
import com.zysl.aws.model.UploadFileRequest;
import com.zysl.aws.model.db.S3File;
import com.zysl.aws.model.db.S3Folder;
import com.zysl.aws.model.db.S3Service;

import javax.imageio.stream.IIOByteBuffer;
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
    Integer addFileInfo(UploadFileRequest request);

    
    
     
    /**
     * 根据文件夹名和文件名查询文件信息
     * @description
     * @author miaomingming
     * @date 17:48 2020/2/13
     * @param [folderName, fileName]
     * @return com.zysl.aws.model.db.S3File
     **/
    S3File getFileInfo(String folderName,String fileName);

    /**
     * 查询文件
     * @description
     * @author miaomingming
     * @date 18:01 2020/2/13
     * @param [fileKey]
     * @return com.zysl.aws.model.db.S3File
     **/
    S3File getFileInfo(Long fileKey);

    /**
     * 新增文件
     * @description
     * @author miaomingming
     * @date 9:42 2020/2/14
     * @param [s3File]
     * @return java.lang.Long
     **/
    Long addFileInfo(S3File s3File);

    /**
     * 修改文件的最大可下载次数
     * @param maxAmount
     * @param fileKey
     * @return
     */
    int updateFileAmount(Integer maxAmount, Long fileKey);

    /**
     * 批量插入数据
     * @param fileList
     * @return
     */
    int insertBatch(List<S3File> fileList);

    /**
     * 修改文件信息
     * @param s3File
     * @return
     */
    int updateFileInfo(S3File s3File);

    /**
     * 分页查询文件信息
     * @param currPage
     * @param pageSize
     * @return
     */
    S3File queryPageFileInfo(int currPage, int pageSize);

    /**
     * 修改文件信息
     * @param s3File
     * @return
     */
    int updateTempFileInfo(S3File s3File);
}
