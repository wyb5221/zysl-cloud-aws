package com.zysl.aws.web;

import com.zysl.aws.web.model.*;
import com.zysl.aws.web.service.AwsFileService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AwsFileServiceTest {

    @Autowired
    private AwsFileService fileService;

    @Test
    public void downloadFile(){
        System.out.println("----");
        HttpServletResponse response = null;
        DownloadFileRequest request = new DownloadFileRequest();
        request.setFileId("tt-02.txt");//txt/tt-txt.txt
        request.setBucketName("test-yy03");
        request.setVersionId("BSowS.3XrD.g2asx0mWzVD0KOSq6eguB");
//        String str = fileService.downloadFile(request);
//        System.out.println("str:" + str);
    }
    @Test
    public void getFileVersion1(){
        List<FileVersionResponse> result = fileService.getS3FileVersion
                ("test-yy10","wyb01/");
        System.out.println("result:"+result);
    }
    @Test
    public void deleteFile(){
        DelObjectRequest request = new DelObjectRequest();
        request.setBucketName("test-yy10");
        request.setKey("test01-01");
        request.setVersionId("");
        request.setDeleteStore(0);
        System.out.println(fileService.deleteFile(request));
    }

    @Test
    public void restoreObject(){
        ResObjectRequest request = new ResObjectRequest();
        request.setBucketName("test-yy03");
        request.setKey("tt-txt.txt");
        fileService.restoreObject(request);
    }

    @Test
    public void copyFile(){
        CopyFileRequest request = new CopyFileRequest();
        request.setSourceBucket("test-yy08");
        request.setSourceKey("doc/tt001.txt");

        request.setDestBucket("test-yy08");
        request.setDestKey("doc/tt001-1.txt");

        fileService.copyFile(request);
    }
    @Test
    public void getFileVersion(){
        List<FileVersionResponse> result = fileService.getS3FileVersion
                ("test-yy08","www001.txt");
        System.out.println("result:"+result);
    }
    @Test
    public void uploadPartCopy(){
        CopyFileRequest request = new CopyFileRequest();
        request.setSourceBucket("test-yy08");
        request.setSourceKey("doc/tt001.txt");

        request.setDestBucket("test-yy08");
        request.setDestKey("doc/tt001-2.txt");

        fileService.uploadPartCopy(request);
    }

    @Test
    public void moveFile(){
        CopyFileRequest request = new CopyFileRequest();
        request.setSourceBucket("test-yy03");
        request.setSourceKey("test002.txt");
        request.setDestBucket("test-yy03");
        request.setDestKey("txt/txt-01/test002.txt");

        fileService.moveFile(request);
    }

    @Test
    public void getS3FileSize(){
        Long a = fileService.getS3FileSize("test-yy10", "tt01.doc", "");
        System.out.println("a:"+a);
    }

    @Test
    public void getS3ToFileInfo(){
        FileInfoRequest a = fileService.getS3ToFileInfo(
                "test-yy08",
                "www001.txt", "8hxGS5G.1xIDbIosjfNqM7j9aeOA28lf");
        System.out.println("a:"+a);
    }

    @Test
    public void updateFileTage(){
        UpdateFileTageRequest request = new UpdateFileTageRequest();
        request.setBucket("test-yy08");
        request.setKey("www001.txt");
        request.setVersionId("8hxGS5G.1xIDbIosjfNqM7j9aeOA28lf");
        List<TageDTO> tageList = new ArrayList<>();
        TageDTO tage1 = new TageDTO();
        tage1.setKey("create");
        tage1.setValue("wyb");
        tageList.add(tage1);
        TageDTO tage = new TageDTO();
        tage.setKey("maxAmount");
        tage.setValue("10");
        tageList.add(tage);
        TageDTO tage2 = new TageDTO();
        tage2.setKey("validityTime");
        tage2.setValue("2020-04-01");
        tageList.add(tage2);
        request.setTageList(tageList);

        System.out.println("a:"+fileService.updateFileTage(request));
    }

    @Test
    public void uploadFile(){
        UploadFileRequest request = new UploadFileRequest();
        request.setBucketName("test-yy08");
        request.setFileId("www002.txt");
        request.setData("5rmW5Y2X6ZW/5rKZ77yM5aW955qE5ZOI5ZOI6L+Y5aW9DQp3d+mXrumXrg0K5L2g55qEDQpieXRl\n" +
                "DQoNCg0KMQ0KMg==");
        fileService.uploadFile(request);


    }

    @Test
    public void abortMultipartUpload() throws IOException {
        String filePath = "D:\\tmp\\testFile\\www003.txt";
        File file = new File(filePath);
        FileInputStream inputStream = new FileInputStream(file);

        byte[] bytes = new byte[(int) file.length()];
        inputStream.read(bytes);
        inputStream.close();

        fileService.abortMultipartUpload(bytes);

    }
}
