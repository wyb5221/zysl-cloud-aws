package com.zysl.aws.web.controller;

import com.zysl.aws.web.common.result.Result;
import com.zysl.aws.web.model.db.S3Service;
import com.zysl.aws.web.service.TestService;
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

    @GetMapping("/querySql")
    public void querySql(String sqlName){

    }


}
