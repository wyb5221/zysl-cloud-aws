package com.zysl.aws.utils;


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
}
