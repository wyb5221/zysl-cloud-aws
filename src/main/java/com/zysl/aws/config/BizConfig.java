package com.zysl.aws.config;

public class BizConfig {
  //TODO，改成到配置文件
  //word转pdf用到的字体
  public static final String FONT_FILE = "simsun.ttc,1";

  //临时文件存放地址
  public static final String PDF_TMP_FILE_PATH = "d:/data/tmp/";

  //s3文件存放word转pdf的文件夹
  public static final String WORD_TO_PDF_BUCKET_NAME = "temp-001";
}
