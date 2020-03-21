package com.zysl.aws;

import com.zysl.aws.model.BucketFileRequest;
import com.zysl.aws.model.CreateFolderRequest;
import com.zysl.aws.model.DownloadFileRequest;
import com.zysl.aws.model.FileInfo;
import com.zysl.aws.service.AmasonService;
import com.zysl.aws.service.FileService;
import com.zysl.aws.service.TestService;
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
//    @Test
//    public void querySql(){
//        String str = "select * from s3_folder where folder_name='test-yy05'";
//        Object obj = testService.querySql(str);
//        System.out.println("obj:"+obj.toString());
//    }

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
    public void getS3FileList(){
        BucketFileRequest request = new BucketFileRequest();
        request.setBucketName("test-yy03");
        List<FileInfo> list = amasonService.getS3FileList(request);
        System.out.println("list:"+list);
    }

    @Test
    public void createFolder(){
        System.out.println("----");
        CreateFolderRequest request = new CreateFolderRequest();
        amasonService.createFolder(request);
        System.out.println("----");
    }

    @Test
    public void createBucket(){
        String bucket = amasonService.createBucket("test-yy03/doc", "001");
        System.out.println("bucket:"+bucket);
    }

    @Test
    public void downloadFile(){
        System.out.println("----");
        HttpServletResponse response = null;
        DownloadFileRequest request = new DownloadFileRequest();
        request.setFileId("tt-txt.txt");//txt/tt-txt.txt
        request.setBucketName("test-yy05");
        String str = amasonService.downloadFile(response, request);
        System.out.println("str:" + str);
    }

    @Test
    public void deleteFile(){
        amasonService.deleteFile("test-yy06", "111");
    }

    @Test
    public void copyObject(){
        amasonService.copyObject();
    }
}
