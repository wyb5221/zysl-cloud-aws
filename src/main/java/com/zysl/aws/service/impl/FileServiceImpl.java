package com.zysl.aws.service.impl;

import com.zysl.aws.mapper.S3FileMapper;
import com.zysl.aws.mapper.S3FileMyMapper;
import com.zysl.aws.mapper.S3FolderMapper;
import com.zysl.aws.mapper.S3ServiceMapper;
import com.zysl.aws.model.UploadFileRequest;
import com.zysl.aws.model.db.*;
import com.zysl.aws.service.FileService;
import com.zysl.aws.utils.DateUtil;
import com.zysl.aws.utils.MD5Utils;
import com.zysl.aws.utils.S3ClientFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class FileServiceImpl implements FileService {

    @Autowired
    private S3ClientFactory s3ClientFactory;

    @Autowired
    private S3ServiceMapper s3ServiceMapper;

    @Autowired
    private S3FolderMapper s3FolderMapper;

    @Autowired
    private S3FileMapper s3FileMapper;

    @Autowired
    S3FileMyMapper s3FileMyMapper;

    @Override
    public List<S3Service> queryS3Service() {
        S3ServiceCriteria example = new S3ServiceCriteria();
        return s3ServiceMapper.selectByExample(example);
//        return s3ServiceMapper.selectAll();
    }

    @Override
    public List<S3Folder> queryS3FolderInfo() {
        S3FolderCriteria example = new S3FolderCriteria();
        return s3FolderMapper.selectByExample(example);
//        return s3FolderMapper.selectAll();
    }

    @Override
    public S3Folder queryS3Folder(String folderName) {
        return s3FileMyMapper.queryByName(folderName);
    }

    @Override
    public int insertFolderInfo(String folderName, String serviceNo) {
        log.info("---新增文件夹信息insertFolderInfo---folderName:{},serviceNo：{}",
                folderName, serviceNo);
        S3Folder record = new S3Folder();
        record.setFolderName(folderName);//文件夹名称
        record.setCreateTime(new Date());//创建时间
        record.setServiceNo(serviceNo);//服务器编号

        int insetNum = 0;
        try {
            insetNum = s3FolderMapper.insert(record);
        } catch (DuplicateKeyException e) {
            log.error("--文件夹名称唯一索引异常：--{}", e);
        }
        return insetNum;
    }

    @Override
    public int deleteFolderByName(String folderName) {
        log.info("--deleteFolderByName入参--folderName:{}", folderName);

        int num = s3FileMyMapper.deleteFolderByName(folderName);
        log.info("--deleteFolderByName删除文件夹信息返回--num:{}", num);
        return num;
    }

    @Override
    public boolean queryFileByMd5(String content) {
        log.info("--queryFileByMd5--content:{}", content);
        int count = s3FileMyMapper.queryFileByMd5(content);
        log.info("--queryFileByMd5--根据Md5查询文件数返回count:{}", count);
        return count > 0;
    }

    @Override
    public S3File queryFileInfoByMd5(String content) {
        log.info("--queryFileByMd5--content:{}", content);
        S3File s3File = s3FileMyMapper.queryFileInfoByMd5(content);
        log.info("--queryFileByMd5--根据Md5查询文件信息返回s3File:{}", s3File);
        return s3File;
    }

    @Override
    public Integer addFileInfo(UploadFileRequest request){
        //上传时间
        Date uploadTime = new Date();
        //根据文件夹名称获取服务器编号
        String serverNo = s3ClientFactory.getServerNo(request.getBucketName());
        //文件大小
        long fileSize = request.getData().length();

        S3File s3File = new S3File();
        //服务器编号
        s3File.setServiceNo(serverNo);
        //文件名称
        s3File.setFileName(request.getFileId());
        //文件夹名称
        s3File.setFolderName(request.getBucketName());
        //文件大小
        s3File.setFileSize(fileSize);
        //最大可下载次数
        s3File.setDownAmount(request.getMaxAmount());

        //上传时间
        s3File.setUploadTime(uploadTime);

        //获取下载有效截至时间
        if(!StringUtils.isEmpty(request.getValidity())){
            Date validityTime = DateUtil.addDateHour(uploadTime, request.getValidity());
            s3File.setValidityTime(validityTime);
        }
        //文件内容md5
        String md5Content = MD5Utils.encode(request.getData());
        s3File.setContentMd5(md5Content);

        Integer num = s3FileMapper.insert(s3File);
        log.info("--保存文件信息返回--num：{}", num);

        return num;
    }

    @Override
    public S3File getFileInfo(String folderName,String fileName){
        return s3FileMyMapper.queryOneFile(folderName,fileName);
    }



    @Override
    public S3File getFileInfo(Long fileKey){
        return s3FileMapper.selectByPrimaryKey(fileKey);
    }


    @Override
    public Long addFileInfo(S3File s3File){
        s3FileMapper.insert(s3File);
        log.info("--添加文件信息返回id:--{}", s3File.getId());
        return s3File.getId();
    }

    @Override
    public int updateFileAmount(Integer maxAmount, Long fileKey) {
        log.info("--updateFileAmount修改文件最大下载次数入参--maxAmount:{},fileKey:{}", maxAmount, fileKey);
        S3File record = new S3File();
        record.setMaxAmount(maxAmount);
        record.setId(fileKey);
        int upNum = s3FileMapper.updateByPrimaryKeySelective(record);
        log.info("--修改文件信息upNum:--{}", upNum);
        return upNum;
    }

    @Override
    public int insertBatch(List<S3File> fileList) {
        try {
            int insetNum = s3FileMyMapper.insertBatch(fileList);
            log.info("--批量插入数据返回：--{}", insetNum);
            return insetNum;
        } catch (Exception e){
            log.error("--数据批量插入失败--", e);
        }
        return 0;
    }

    @Override
    public S3Service queryServiceInfo(String folderName) {
        return s3FileMyMapper.queryServiceInfo(folderName);
    }
}
