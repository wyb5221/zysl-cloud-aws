package com.zysl.aws.web.service;

import com.zysl.aws.web.model.db.S3Service;
import software.amazon.awssdk.services.s3.S3Client;

import java.util.List;

public interface TestService {

    S3Client getClient(String name);

    List<S3Service> queryS3Service();

    int queryMd5(String content);

    int insertList();

    Object querySql(String sqlName);
}
