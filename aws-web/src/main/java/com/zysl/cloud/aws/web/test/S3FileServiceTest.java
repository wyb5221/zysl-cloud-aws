package com.zysl.cloud.aws.web.test;

import com.zysl.cloud.aws.biz.service.s3.IS3FileService;
import com.zysl.cloud.aws.domain.bo.S3ObjectBO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
public class S3FileServiceTest {

    @Autowired
    IS3FileService fileService;

    @Test
    public void getDetailInfo(){
        S3ObjectBO t = new S3ObjectBO();
        t.setBucketName("test-yy06");
        t.setPath("test1-copy1/");
        t.setFileName("");
        fileService.getDetailInfo(t);
        System.out.println();
    }

}
