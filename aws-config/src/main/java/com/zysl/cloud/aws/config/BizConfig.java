package com.zysl.cloud.aws.config;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class BizConfig {

    //word转pdf用到的字体
    @Value("${spring.pdf.FONT_FILE}")
    public String FONT_FILE;

    //临时文件存放地址
    @Value("${spring.pdf.FILE_PATH}")
    public String PDF_TMP_FILE_PATH;

    //s3文件存放word转pdf的文件夹
    @Value("${spring.pdf.BUCKET_NAME}")
    public String WORD_TO_PDF_BUCKET_NAME;

    @Value("${spring.download.date}")
    public String DOWNLOAD_TIME;


    @Value("${share.file.bucket.name}")
    public String shareFileBucket;

    @Value("#{'${announcement.buckets}'.split(',')}")
    public List<String> announcementBuckets;

    /**
     * 版本号，测试用，可以不配置
     **/
    @Value("${application.version}")
    public String curVer;
}
