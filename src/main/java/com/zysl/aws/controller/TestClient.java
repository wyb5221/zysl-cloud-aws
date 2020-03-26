package com.zysl.aws.controller;

import com.zysl.aws.model.db.S3Service;
import com.zysl.aws.service.TestService;
import com.zysl.aws.service.mq.FileProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import software.amazon.awssdk.services.s3.S3Client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class TestClient {

    @Autowired
    TestService service;

    transient public S3Client s3;

    @GetMapping("/getClient")
    public Map<String, Object> getClient(String name){
        s3 = service.getClient(name);
        Map<String, Object> map = new HashMap<>();
        map.put("client", s3);
        return map;
    }

    @GetMapping("/queryService")
    public List<S3Service> queryS3Service(){
        return service.queryS3Service();
    }


    @GetMapping("/queryMd5")
    public int queryMd5(String content){
        return service.queryMd5(content);
    }

    @GetMapping("/insertList")
    public int insertList(){
        return service.insertList();
    }


    @Autowired
    private FileProvider fileProvider;

    @GetMapping("/sendMsg")
    public void sendMsg(){
        fileProvider.senFileInfo();

    }


}
