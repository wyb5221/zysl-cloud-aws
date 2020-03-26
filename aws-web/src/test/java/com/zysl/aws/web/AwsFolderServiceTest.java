
package com.zysl.aws.web;


import com.zysl.aws.web.model.*;
import com.zysl.aws.web.service.AwsFolderService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AwsFolderServiceTest {

    @Autowired
    private AwsFolderService folderService;

    @Test
    public void createFolder(){
        System.out.println("----");
        CreateFolderRequest request = new CreateFolderRequest();
        request.setBucketName("test-yy03");
        request.setFolderName("test03");
        boolean flag = folderService.createFolder(request);
        System.out.println("---flag:-"+flag);
    }

    @Test
    public void getS3FileList(){
        QueryObjectsRequest request = new QueryObjectsRequest();
        request.setBucketName("test-yy10");
        request.setKey("wyb01");
        request.setBucketName("test-yy07");
        request.setKey("");
//        request.setKeyType(1);
        List<FileInfo> list = folderService.getS3FileList(request);
        System.out.println("list:"+list);
    }


    @Test
    public void deleteFolder(){
        DelObjectRequest request = new DelObjectRequest();
        request.setBucketName("test-yy10");
        request.setKey("doc001");
        request.setVersionId("");
//        request.setDeleteStore(0);
        System.out.println(folderService.deleteFolder(request));
    }

    @Test
    public void moveFolder(){
        CopyFileRequest request = new CopyFileRequest();
        request.setSourceBucket("test-yy10");
        request.setSourceKey("yy");

        request.setDestBucket("test-yy10");
        request.setDestKey("QQ");

        folderService.moveFolder(request);
    }

    public static void main(String[] args) {
        String key = "wyb01/wyb0101";
        String destKey = key.replace("wyb01"+"/", "yy"+"/");
        System.out.println(destKey);

    }
}