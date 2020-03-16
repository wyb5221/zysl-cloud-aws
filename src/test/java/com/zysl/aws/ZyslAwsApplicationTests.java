package com.zysl.aws;

import com.zysl.aws.model.BucketFileRequest;
import com.zysl.aws.model.DownloadFileRequest;
import com.zysl.aws.model.FileInfo;
import com.zysl.aws.service.AmasonService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ZyslAwsApplicationTests {

    @Autowired
    private AmasonService amasonService;

    @Test
    public void contextLoads() {
    }

    @Test
    public void getFilesByBucket(){
        BucketFileRequest request = new BucketFileRequest();
        request.setPageIndex(3);
        request.setPageSize(5);
        request.setBucketName("test-yy01");
        List<FileInfo> list = amasonService.getFilesByBucket(request);
        System.out.println("list:"+list);
        System.out.println("");
        System.out.println("----");
    }

    @Test
    public void createFolder(){
        System.out.println("----");
        amasonService.createFolder();
        System.out.println("----");
    }

    @Test
    public void downloadFile(){
        System.out.println("----");
        HttpServletResponse response = null;
        DownloadFileRequest request = new DownloadFileRequest();
        request.setFileId("tt-txt.txt");
        request.setBucketName("test-yy05/txt");
        String str = amasonService.downloadFile(response, request);
        System.out.println("str:" + str);
    }
}
