package com.zysl.cloud.aws.web;

import com.zysl.cloud.aws.biz.service.s3.IS3FileService;
import com.zysl.cloud.aws.domain.bo.S3ObjectBO;
import com.zysl.cloud.aws.domain.bo.TagsBO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class S3FileServiceTest {

    @Autowired
    IS3FileService fileService;

    @Test
    public void getDetailInfo(){
        S3ObjectBO t = new S3ObjectBO();
        t.setBucketName("test-yy06");
        t.setPath("test1-copy1/");
        t.setFileName("");
        System.out.println("接口返回："+fileService.getDetailInfo(t));
    }

    @Test
    public void copy(){
        S3ObjectBO src = new S3ObjectBO();
        src.setBucketName("test-yy06");
        src.setPath("");
        src.setFileName("txt001.txt");
        S3ObjectBO dest = new S3ObjectBO();
        dest.setBucketName("test-yy06");
        dest.setPath("copy/");
        dest.setFileName("txt001.txt");

        List<TagsBO> tagList = new ArrayList<>();
        TagsBO t1 = new TagsBO();
        t1.setKey("key1");
        t1.setValue("v1");
        TagsBO t2 = new TagsBO();
        t2.setKey("key2");
        t2.setValue("v2");
        tagList.add(t1);
        tagList.add(t2);
        dest.setTagList(tagList);
        fileService.copy(src, dest);
    }

    @Test
    public void a(){
        S3ObjectBO t = new S3ObjectBO();
        t.setBucketName("test-yy06");
        t.setFileName("测试文档.doc");
        List<TagsBO> tagList = new ArrayList<>();
        TagsBO t1 = new TagsBO();
        t1.setKey("k3");
        t1.setValue("v3");
        TagsBO t2 = new TagsBO();
        t2.setKey("k2");
        t2.setValue("v2");
        TagsBO t3 = new TagsBO();
        t3.setKey("k3");
        t3.setValue("v3");
        tagList.add(t1);
        tagList.add(t2);
        tagList.add(t3);
        t.setTagList(tagList);
        fileService.modify(t);
    }
}
