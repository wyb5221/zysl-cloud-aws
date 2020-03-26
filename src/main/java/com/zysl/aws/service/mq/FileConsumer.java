package com.zysl.aws.service.mq;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.zysl.aws.model.db.S3File;
import com.zysl.aws.service.FileService;
import com.zysl.aws.utils.MD5Utils;
import com.zysl.aws.utils.S3ClientFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import java.io.IOException;

@Component
public class FileConsumer {

    @Autowired
    private S3ClientFactory s3ClientFactory;
    @Autowired
    private FileService fileService;

//    @RabbitListener(queues = "s3.file.write")
//    public void process(Message message, Channel channel){
//        String str = new String(message.getBody());
//        System.out.println("str:"+str);
//    }

//    @RabbitListener(queues = "s3.file.write")
    public void fileWrite(Message message, Channel channel) throws IOException {
//        ObjectMapper objectMapper = new ObjectMapper();
//        S3File s3File = objectMapper.readValue(message.getBody(),S3File.class);

        Long id = Long.parseLong(new String(message.getBody()));
        S3File s3File = fileService.getFileInfo(id);

        S3Client s3Client = s3ClientFactory.getS3Client("001");

        //调用s3接口下载文件内容
        String fileContent = getS3FileInfo(s3File.getFolderName(), s3File.getFileName(), s3Client);
        //文件内容md5
        String md5Content = MD5Utils.encode(fileContent);
        s3File.setContentMd5(md5Content);
        //修改文件信息
        int num = fileService.updateTempFileInfo(s3File);

        System.out.println("str:"+num);
    }

    /**
     * 调用s3接口下载文件内容
     * @param bucketName
     * @param key
     * @return
     */
    public String getS3FileInfo(String bucketName, String key, S3Client s3){
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
            System.out.println("--s3接口下载文件信息异常：--"+ e);
            return null;
        }
    }
}
