package com.zysl.cloud.aws.web;

import com.zysl.cloud.aws.biz.service.s3.IS3FileService;
import com.zysl.cloud.aws.biz.service.s3.IS3FolderService;
import com.zysl.cloud.aws.domain.bo.S3ObjectBO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RunWith(SpringRunner.class)
@SpringBootTest
public class S3FolderServiceTest {

    @Autowired
    IS3FolderService folderService;

    @Test
    public void getDetailInfo(){
        S3ObjectBO src = new S3ObjectBO();
        src.setBucketName("test-yy06");
        src.setPath("测试1/");
        src.setFileName("");
        S3ObjectBO dest = new S3ObjectBO();
        dest.setBucketName("test-yy06");
        dest.setPath("测试1001/");
        dest.setFileName("");

        folderService.copy(src, dest);
    }

    public static void main(String[] args) {

        String encodedUrl = null;
        try {
            encodedUrl = URLEncoder.encode("test-yy06" + "/" + "测试1/", StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            System.out.println("URL could not be encoded: " + e.getMessage());
        }
        System.out.println("encodedUrl:"+encodedUrl);
    }

}
