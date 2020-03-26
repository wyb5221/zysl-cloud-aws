package com.zysl.aws.web.utils;


import com.aspose.words.SaveFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class BizUtil {

    /**
     * 无后缀的临时文件名：tmp_原文件名_yyyyMMddHHmmssSSS_3位随机数_
     * @param fileName
     * @return
     */
    public static String getTmpFileNameWithoutSuffix(String fileName){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String dateStr = sdf.format(new Date());
        Random random = new Random();

        if(fileName.indexOf(".") > -1){
            fileName = fileName.substring(0,fileName.lastIndexOf("."));
        }
        fileName = "tmp_" + fileName + "_"+ dateStr + "_" + random.nextInt(1000) + "_";

        return fileName;
    }

    /**
     * 获取pdf文件名
     * @param fileName
     * @return
     */
    public static String parseFName(String fileName,Integer formatType){
        if(fileName.indexOf(".") > -1){
            return fileName.substring(0,fileName.lastIndexOf(".") + 1) + SaveFormat.getName(formatType).toLowerCase();
        }
        return null;
    }

    /**
     * 分享文件名：源路径+link_时间戳_源文件名
     * @param fileName
     * @return
     */
    public static String getLinkFileNameWithoutSuffix(String fileName){
        Long dateStr = new Date().getTime();
        String[] names = fileName.split("/");
        if(null != names && names.length>0){
            String str = names[names.length-1];
            String str1 = "link_" + dateStr + "_"  + str;
            fileName = fileName.replace(str, str1);
        }else{
            fileName = "link_" + dateStr + "_" + fileName;
        }

        return fileName;
    }

    public static void main(String[] args) {
        System.out.println(getLinkFileNameWithoutSuffix("test.txt"));
    }
}
