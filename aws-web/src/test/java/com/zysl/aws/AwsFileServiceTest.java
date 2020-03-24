package com.zysl.aws;

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
import java.io.FileNotFoundException;
import java.io.IOException;
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
                ("test-yy08","test01/test01-02");
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
    public void abortMultipartUpload() throws IOException {
        String filePath = "D:\\tmp\\testFile\\www001.txt";
        File file = new File(filePath);
        FileInputStream inputStream = new FileInputStream(file);

        byte[] bytes = new byte[(int) file.length()];
        inputStream.read(bytes);
        inputStream.close();

        fileService.abortMultipartUpload(bytes);

    }
}
