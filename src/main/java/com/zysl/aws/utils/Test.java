package com.zysl.aws.utils;

import com.zysl.aws.model.UploadFileRequest;
import org.springframework.util.FileCopyUtils;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

public class Test {

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {

//        String a = new String("a");

//        String fileName = "tt-002";
//        System.out.println(fileName.indexOf("."));
        /*if(fileName == null){
            System.out.println(System.currentTimeMillis() + "");
        }else if(fileName.indexOf(".") == -1){
            System.out.println(fileName + "_" + System.currentTimeMillis());
        }else{
            System.out.println(fileName.substring(0,fileName.lastIndexOf("."))
                    + "_" + System.currentTimeMillis()
                    + fileName.substring(fileName.lastIndexOf(".")));
        }*/


//        String old = "wwwww";
//        //java自带工具包MessageDigest
//        MessageDigest md5 = MessageDigest.getInstance("md5");
//        //实现Base64的编码
//        BASE64Encoder base64 = new BASE64Encoder();
//        //进行加密
//        String newStr = base64.encode(md5.digest(old.getBytes("utf-8")));
//        System.out.println(newStr);

//        Date date1 = new Date();//小
//        Date date2 = DateUtil.addDateHour(date1, 1);//大
//        System.out.println(DateUtil.doCompareDate(date2, date1));
        readFile();
//        writeFileVideo(readFileVideo());
//        String md5Content = Md5Util.getMd5Content(readFile());
//        System.out.println("md5Content:"+md5Content);
//        writeFile("5rGq5YuH5b2sDQoxMjMNCmFiYw0K5rex5ZyzDQrvvIFAIw0K5L2g5aW977yM5rex5Zyz");
//        writeFile("MTIxMw0KMTIzDQoxMg0KMzEyMTI=");
//        writeFile(readFile());
    }

    public static String readFile() throws IOException {
        String filePath = "D:\\tmp\\testFile\\tt.txt";
//        String filePath = "D:\\tmp\\testFile\\tmp_tt-2_20200227172011341_561_text.pdf";

        File file = new File(filePath);
        FileInputStream inputStream = new FileInputStream(file);

        System.out.println("----:"+file.length());
//        InputStream fis = new FileInputStream(file);
//        byte[] aa = FileCopyUtils.copyToByteArray(fis);
//        String bb = new String(aa,"ISO-8859-1");
//        System.out.println("bb:"+bb);

        byte[] bytes = new byte[(int) file.length()];
        inputStream.read(bytes);
        inputStream.close();

        BASE64Encoder encoder = new BASE64Encoder();
        String str = encoder.encode(bytes);
        System.out.println(str);


        new ObjectInputStream(new FileInputStream(""));
        new BufferedReader(new FileReader("a"));
        return str;
    }

    public static void writeFile(String str) throws IOException {
        String filePath = "D:\\tmp";
        File file = new File(filePath);
        if(file.exists()==false){
            file.mkdirs();
        }
//        BASE64Decoder decoder = new BASE64Decoder();
//        byte[] bytes = decoder.decodeBuffer(str);
        byte[] bytes = str.getBytes();
        FileOutputStream outputStream = new FileOutputStream(filePath+"\\tt.txt");
        outputStream.write(bytes);
        outputStream.close();
    }


    public static byte[] readFileVideo() throws IOException {
        String filePath = "D:\\tmp\\testFile\\video.mp4";

        File file = new File(filePath);
        FileInputStream inputStream = new FileInputStream(file);

        byte[] bytes = new byte[(int) file.length()];
        inputStream.read(bytes);
        inputStream.close();

        return bytes;
    }

    public static void writeFileVideo(byte[] bytes) throws IOException {
        String filePath = "D:\\tmp";
        File file = new File(filePath);
        if(file.exists()==false){
            file.mkdirs();
        }

        FileOutputStream outputStream = new FileOutputStream(filePath+"\\tt.mp4");
        outputStream.write(bytes);
        outputStream.close();
    }


}
