package com.zysl.aws.web.model;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ShareFileRequest implements Serializable {
  //文件夹名称
  private String bucketName;
  //文件名
  private String fileName;
  //最大下载次数
  private Integer maxDownloadAmout;
  //最大有效时长(小时)
  private Integer maxHours;

  @Override
  public String toString() {
    return "ShareFileRequest{" +
        "bucketName='" + bucketName + '\'' +
        ", fileName='" + fileName + '\'' +
        ", maxDownloadAmout=" + maxDownloadAmout +
        ", maxHours=" + maxHours +
        '}';
  }
}
