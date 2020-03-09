package com.zysl.aws.service.impl;

import com.zysl.aws.config.BizConfig;
import com.zysl.aws.model.FileInfo;
import com.zysl.aws.model.db.S3File;
import com.zysl.aws.service.FileService;
import com.zysl.aws.utils.BatchListUtil;
import com.zysl.aws.utils.MD5Utils;
import com.zysl.aws.utils.S3ClientFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class FileInitServiceImpl {

    @Autowired
    private BizConfig bizConfig;

    @Autowired
    private S3ClientFactory s3ClientFactory;

    @Autowired
    private FileService fileService;

    /**
     * 服务器文件初始化
     */
    @PostConstruct
    public void fileInfoInit(){

        log.info("initFlag:"+ bizConfig.initFlag);

        if(bizConfig.initFlag){
            log.info("-----初始化开始:{}------", System.currentTimeMillis());
            String[] arrs = bizConfig.defaultName.split(",");
            log.info("---开启线程数thredaNum:{}----", bizConfig.thredaNum);
            for (int i = 0; i < arrs.length; i++) {
                S3Client s3 = s3ClientFactory.initS3Client(arrs[i]);

                ListObjectsResponse response = null;
                ListObjectsRequest listObjectsRequest = null;
                String key = "";
                do{
                    if(StringUtils.isEmpty(key)){
                        listObjectsRequest =
                                ListObjectsRequest.builder().bucket(arrs[i]).build();
                    }else{
                        listObjectsRequest =
                                ListObjectsRequest.builder().bucket(arrs[i]).marker(key).build();
                    }
                    log.info("--查询文件列表入参listObjectsRequest:{}", listObjectsRequest);

                    response = s3.listObjects(listObjectsRequest);
                    List<S3Object> list = response.contents();
                    key = list.get(list.size() - 1).key();
                    creatThread(list, s3, arrs[i]);

                }while (response.isTruncated());
            }
            log.info("-----初始化结束：{}------", System.currentTimeMillis());
        }
    }

    //创建线程处理初始化数据
    public void creatThread(List<S3Object> list, S3Client s3, String folderName){
        log.info("-----objectList.contents().list：{}", list.size());

        Map<Integer,List<S3Object>> itemMap = new BatchListUtil<S3Object>().batchList(list, bizConfig.thredaNum);
        log.info("-----objectList.contents().itemMap：{}", itemMap.size());

        //分批次更新
        for (int i = 0; i < itemMap.size(); i++) {
            log.info("---启动：{}---", i);
            int num = i+1;
            List<S3Object> fileList = itemMap.get(num);
            insertFile(fileList, folderName, s3);
            log.info("---执行结束：{}",  System.currentTimeMillis());

           /* new Thread(new Runnable() {
                @Override
                public void run() {
                    log.info("---线程：{}启动开始：{}", Thread.currentThread().getId(), System.currentTimeMillis());
                    List<S3Object> fileList = itemMap.get(num);
                    insertFile(fileList, bizConfig.defaultName, s3);
                    log.info("---线程：{}执行结束：{}", Thread.currentThread().getId(), System.currentTimeMillis());
                }
            }).start();*/
        }
    }

    /**
     * 将服务器文件信息保存数据库
     * @param fileList
     * @param defaultName
     */
    public void insertFile(List<S3Object> fileList, String defaultName, S3Client s3){
        String serverNo = s3ClientFactory.getServerNo(defaultName);
        List<S3File> insertList = new ArrayList<>();

        for (S3Object obj : fileList) {
            S3File addS3File = new S3File();
            //服务器编号
            addS3File.setServiceNo(serverNo);
            //文件名称
            addS3File.setFileName(obj.key());
            //文件夹名称
            addS3File.setFolderName(defaultName);
            //文件大小
            addS3File.setFileSize(obj.size());
            //上传时间
            addS3File.setUploadTime(Date.from(obj.lastModified()));
            //创建时间
            addS3File.setCreateTime(new Date());

//            //调用s3接口下载文件内容
//            String fileContent = getS3FileInfo(defaultName, obj.key(), s3);
//            //文件内容md5
//            String md5Content = MD5Utils.encode(fileContent);
            //文件内容md5
            addS3File.setContentMd5(null);
            //添加list集合
            insertList.add(addS3File);
            //每200条数据插入一次数据库
            if(insertList.size() == 200){
                int num = fileService.insertBatch(insertList);
                log.info("--线程：{}--插入数据num:--{}", Thread.currentThread().getId(), num);
                insertList = new ArrayList<>();
            }
        }
        //不足200条的时候，list循环完也插入一次数据库
        if(!CollectionUtils.isEmpty(insertList)){
            int num = fileService.insertBatch(insertList);
            log.info("--线程：{}--插入数据num:--{}", Thread.currentThread().getId(), num);
        }
    }

    /**
     * 调用s3接口下载文件内容
     * @param bucketName
     * @param key
     * @return
     */
    public String getS3FileInfo(String bucketName, String key, S3Client s3){
        log.info("--调用s3接口下载文件内容入参-bucketName:{},-key:{}", bucketName, key);
        try {
            ResponseBytes<GetObjectResponse> objectAsBytes = s3.getObject(b ->
                            b.bucket(bucketName).key(key),
                    ResponseTransformer.toBytes());
            byte[] bytes = objectAsBytes.asByteArray();
//            String a = new String(bytes);
//            byte[] aa = a.getBytes();
//            String str = objectAsBytes.asUtf8String();
            return new String(bytes);
        } catch (Exception e) {
            log.info("--s3接口下载文件信息异常：--{}", e);
            return null;
        }
    }

    @PostConstruct
    public void updateFileContet(){
        int currPage = 0;
        int pageSize = 1;
        log.info("updateFlag:"+ bizConfig.updateFlag);
        if(bizConfig.updateFlag){
            S3Client s3 = s3ClientFactory.initS3Client(bizConfig.defaultName);

            S3File s3File = fileService.queryPageFileInfo(currPage, pageSize);
            do{
                try {
                    //调用s3接口下载文件内容
                    String fileContent = getS3FileInfo(s3File.getFolderName(), s3File.getFileName(), s3);
                    //文件内容md5
                    String md5Content = MD5Utils.encode(fileContent);
                    s3File.setContentMd5(md5Content);
                    //修改文件信息
                    fileService.updateTempFileInfo(s3File);
                }catch (Exception e){
                    log.error("--updateFileContet修改文件md5内容异常：{}--", e);
                }

                currPage++;
                s3File = fileService.queryPageFileInfo(currPage, pageSize);
            }while (s3File != null);
        }
    }


}
