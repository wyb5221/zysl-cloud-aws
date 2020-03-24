package com.zysl.aws.web;

import com.zysl.aws.web.model.BucketFileRequest;
import com.zysl.aws.web.model.FileInfo;
import com.zysl.aws.web.service.AwsBucketService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AwsBucketServiceTest {

    @Autowired
    private AwsBucketService bucketService;

    @Test
    public void createBucket(){
        String bucket = bucketService.createBucket("test-yy03/doc", "001");
        System.out.println("bucket:"+bucket);
    }


    @Test
    public void getFilesByBucket(){
        BucketFileRequest request = new BucketFileRequest();
        request.setPageIndex(3);
        request.setPageSize(5);
        request.setBucketName("test-yy01");
        List<FileInfo> list = bucketService.getFilesByBucket(request);
        System.out.println("list:"+list);
        System.out.println("");
        System.out.println("----");
    }

    @Test
    public void copyObject(){
        bucketService.copyObject();
    }

}
