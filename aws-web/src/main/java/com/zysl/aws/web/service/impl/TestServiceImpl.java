package com.zysl.aws.web.service.impl;

import com.zysl.aws.web.mapper.S3FileMyMapper;
import com.zysl.aws.web.mapper.S3ServiceMapper;
import com.zysl.aws.web.model.db.S3File;
import com.zysl.aws.web.model.db.S3Service;
import com.zysl.aws.web.model.db.S3ServiceCriteria;
import com.zysl.aws.web.service.TestService;
import com.zysl.aws.web.utils.S3ClientFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class TestServiceImpl implements TestService {

    @Autowired
    private S3ClientFactory s3ClientFactory;

    @Autowired
    private S3ServiceMapper s3ServiceMapper;

    @Autowired
    private S3FileMyMapper s3FileMyMapper;

    @Override
    public S3Client getClient(String name) {
        return s3ClientFactory.getS3Client(name);
    }

    @Override
    public List<S3Service> queryS3Service() {
        S3ServiceCriteria example = new S3ServiceCriteria();
        return s3ServiceMapper.selectByExample(example);
    }

    @Override
    public int queryMd5(String content) {
        int a = s3FileMyMapper.queryFileByMd5(content);
        System.out.println("a:"+a);
        return a;

    }

    @Override
    public int insertList() {
        List<S3File> insertList = new ArrayList<>();
        S3File addS3File = new S3File();
        //服务器编号
        addS3File.setServiceNo("1");
        //文件名称
        addS3File.setFileName("1");
        //文件夹名称
        addS3File.setFolderName("1");
        //文件大小
        addS3File.setFileSize(1L);
        //创建时间
        addS3File.setCreateTime(new Date());

        //文件内容md5
        addS3File.setContentMd5("1");
        //添加list集合
        insertList.add(addS3File);

        S3File addS3File1 = new S3File();
        //服务器编号
        addS3File1.setServiceNo("2");
        //文件名称
        addS3File1.setFileName("2");
        //文件夹名称
        addS3File1.setFolderName("2");
        //文件大小
        addS3File1.setFileSize(2L);
        //创建时间
        addS3File1.setCreateTime(new Date());

        //文件内容md5
        addS3File1.setContentMd5("2");
        //添加list集合
        insertList.add(addS3File1);

        int num = s3FileMyMapper.insertBatch(insertList);
        System.out.println("num:"+num);
        return 0;
    }

    @Override
    public Object querySql(String sqlName) {
        Object obj = s3FileMyMapper.querySql(sqlName);
        return obj;
    }
}
