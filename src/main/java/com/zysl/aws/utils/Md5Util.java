package com.zysl.aws.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import sun.misc.BASE64Encoder;

import java.security.MessageDigest;

@Slf4j
public class Md5Util {

    /**
     *  md5加密，返回String
     * @param str
     * @return
     */
    public static String getMd5Content(String str) {
        String contentMd5 = "";
        //获取文件内容MD5值
        try {
            if(StringUtils.isEmpty(str)){
                log.info("--md5加密入参为空--str:{}", str);
                return null;
            }
            //java自带工具包MessageDigest
            MessageDigest md5 = MessageDigest.getInstance("md5");
            //实现Base64的编码
            BASE64Encoder base64 = new BASE64Encoder();
            //进行加密
            contentMd5 = base64.encode(md5.digest(str.getBytes("utf-8")));
        } catch (Exception e) {
            log.info("--M5获取异常--：{}", e);
        }
        return contentMd5;
    }
}
