package com.zysl.aws.service.mq;

import com.zysl.aws.model.db.S3File;
import com.zysl.aws.service.FileService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FileProvider {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private FileService fileService;

    public void sendMess(){
        String str = "--hello--";
        for (int i = 0; i < 10; i++) {
            rabbitTemplate.convertAndSend("s3.file.exchange","file.read", str);
        }
        System.out.println("---");
    }

    public void senFileInfo(){
        int currPage = 0;
        int pageSize = 1;

        S3File s3File = null;
        do{
            s3File = fileService.queryPageFileInfo(currPage, pageSize);
            if(null != s3File){
                Long id = s3File.getId();
                rabbitTemplate.convertAndSend("s3.file.exchange","file.read", id);
                System.out.println("currPage："+currPage);
                currPage++;
            }
        }while (s3File !=null);

    }

}
