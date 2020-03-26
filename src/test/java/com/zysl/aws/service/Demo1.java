package com.zysl.aws.service;

import com.zysl.aws.model.db.S3File;
import org.apache.ibatis.cache.TransactionalCacheManager;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@SpringBootTest
public class Demo1 {

    @Autowired
    private FileService fileService;

    @Test
    public void select1(){
        S3File s3File1 = fileService.getFileInfo(227L);
        System.out.println(s3File1);
//        S3File s3File = new S3File();
//        s3File.setId(242L);
//        s3File.setFileName("wyb.");
//        fileService.updateFileInfo(s3File);
//        S3File s3File2 = fileService.getFileInfo(227L);
//        System.out.println(s3File1 == s3File2);
    }


}
