package com.zysl.cloud.aws.biz.constant;

import com.zysl.cloud.aws.config.BizConfig;
import com.zysl.cloud.utils.StringUtils;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class BizConstants {

  // 权限默认标签
  //    public static final String TAG_OWNER = "owner";
  //    public static final String TAG_DOWNLOAD_AMOUT = "maxDownloadAmout";
  //    public static final String TAG_VALIDITY = "validity";

    @Autowired
    private BizConfig bizConfig;
    
    // 分享默认目录
    public static final String SHARE_DEFAULT_FOLDER = "share";
    
    // 分片下载单次最大字节数，bizconfig启动时会初始化
    public static long MULTI_DOWN_FILE_MAX_SIZE = 0L;
    
    @PostConstruct
    public void init() {
        log.info("init.multipartDownloadMaxFileSize:{}", bizConfig.getMultipartDownloadMaxFileSize());
        try{
            if(StringUtils.isNotBlank(bizConfig.getMultipartDownloadMaxFileSize())){
                String data = bizConfig.getMultipartDownloadMaxFileSize();
                if (data.endsWith("B")) {
                    MULTI_DOWN_FILE_MAX_SIZE = Long.parseLong(data.substring(0,data.length()-1));
                }else if (data.endsWith("KB")) {
                    MULTI_DOWN_FILE_MAX_SIZE = Long.parseLong(data.substring(0,data.length()-2)) * 1024;
                }else if(data.endsWith("MB")){
                    MULTI_DOWN_FILE_MAX_SIZE = Long.parseLong(data.substring(0,data.length()-2))  * 1024  * 1024;
                }
            }
        }catch (Exception e){
            log.error("init.multipartDownloadMaxFileSize.error:{}",bizConfig.getMultipartDownloadMaxFileSize());
        }
    }
    
}
