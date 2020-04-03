package com.zysl.cloud.aws.web;

import com.zysl.cloud.aws.biz.service.s3.IS3BucketService;
import com.zysl.cloud.aws.domain.bo.S3ObjectBO;
import com.zysl.cloud.aws.domain.bo.TagBO;
import org.assertj.core.util.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class S3BucketServiceTest {

    @Autowired
    private IS3BucketService bucketService;

    @Test
    public void getBucketTag(){
        S3ObjectBO t = (S3ObjectBO)bucketService.getBucketTag("test-yy06");
        System.out.println("--:"+t.getTagList());
    }

    @Test
    public void putBucketTag(){
        S3ObjectBO t = new S3ObjectBO();
        t.setBucketName("test-yy06");

        //标签 List
        List<TagBO> tagList = Lists.newArrayList();
        TagBO tagBO = new TagBO();
        tagBO.setKey("test");
        tagBO.setValue("www");
        tagList.add(tagBO);
        t.setTagList(tagList);

        System.out.println("--:"+bucketService.putBucketTag(t));
    }

}
