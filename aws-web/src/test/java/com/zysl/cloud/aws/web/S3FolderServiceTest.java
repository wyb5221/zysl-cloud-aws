package com.zysl.cloud.aws.web;

import com.zysl.cloud.aws.biz.service.s3.IS3FileService;
import com.zysl.cloud.aws.biz.service.s3.IS3FolderService;
import com.zysl.cloud.aws.domain.bo.S3ObjectBO;
import com.zysl.cloud.aws.domain.bo.TagBO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class S3FolderServiceTest {

    @Autowired
    IS3FolderService folderService;
    @Autowired
    IS3FileService filerService;

    @Test
    public void create(){
        S3ObjectBO t = new S3ObjectBO();
        t.setBucketName("test-yy06");
        t.setPath("test10/");

        List<TagBO> tagList = new ArrayList<>();
        TagBO t1 = new TagBO();
        t1.setKey("k1");
        t1.setValue("k1k1");
        TagBO t2 = new TagBO();
        t2.setKey("v2");
        t2.setValue("v2");
        tagList.add(t1);
        tagList.add(t2);
        t.setTagList(tagList);
        folderService.create(t);
    }

    @Test
    public void a(){
        S3ObjectBO t = new S3ObjectBO();
        t.setBucketName("test-yy06");
        t.setPath("test1001.txt");
        System.out.println("----:"+filerService.getTags(t));
    }

    @Test
    public void getDetailInfo(){
        S3ObjectBO src = new S3ObjectBO();
        src.setBucketName("test-yy06");
        src.setPath("测试");
        src.setFileName("");
        S3ObjectBO dest = new S3ObjectBO();
        dest.setBucketName("test-yy06");
        dest.setPath("测试001");
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
