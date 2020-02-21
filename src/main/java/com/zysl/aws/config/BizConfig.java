package com.zysl.aws.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
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

  //是否初始化
  @Value("${spring.s3.initFlag}")
  public boolean initFlag;
  //启动线程数
  @Value("${spring.s3.thredaNum}")
  public Integer thredaNum;
  //默认文件夹
  @Value("${spring.s3.defaultName}")
  public String defaultName;

}
