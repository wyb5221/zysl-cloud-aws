package com.zysl.aws.service.impl;

import com.zysl.aws.mapper.S3FileMapper;
import com.zysl.aws.mapper.S3FileMyMapper;
import com.zysl.aws.mapper.S3FolderMapper;
import com.zysl.aws.mapper.S3ServiceMapper;
import com.zysl.aws.model.UploadFileRequest;
import com.zysl.aws.model.db.S3File;
import com.zysl.aws.model.db.S3Folder;
import com.zysl.aws.model.db.S3Service;
import com.zysl.aws.service.FileService;
import com.zysl.aws.utils.DateUtil;
import com.zysl.aws.utils.Md5Util;
import com.zysl.aws.utils.S3ClientFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import sun.misc.BASE64Encoder;

import java.security.MessageDigest;
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
        return s3ServiceMapper.selectAll();
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

        return s3FolderMapper.insert(record);
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
    public int addFileInfo(UploadFileRequest request){
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
        String md5Content = Md5Util.getMd5Content(request.getData());
        s3File.setContentMd5(md5Content);

        int num = s3FileMapper.insert(s3File);
        log.info("--保存文件信息返回--num：{}", num);

        return num;
    }
}
