package com.zysl.aws.test;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@RestController
public class Test {

    public static void main(String[] args) {
//        String name = "tt01.doc";
//        System.out.println(name.toLowerCase());
//        System.out.println(name.toLowerCase().endsWith("doc"));
        List<String> list = Arrays.asList("1","2","3");
        System.out.println(list.get(list.size()-1));
    }

    @GetMapping("/downFile")
    public void downFile(HttpServletResponse response) throws IOException {
        String fileName = "tt.txt";

        String filePath = "D:\\tmp\\"+fileName;
        File file = new File(filePath);
        FileInputStream inputStream = new FileInputStream(file);

        byte[] bytes = new byte[(int) file.length()];
        inputStream.read(bytes);
        inputStream.close();

        //1下载文件流
        OutputStream outputStream = response.getOutputStream();
        response.setContentType("application/octet-stream");//告诉浏览器输出内容为流
        response.setHeader("Content-Disposition", "attachment;fileName="+fileName);
        response.setCharacterEncoding("UTF-8");

        outputStream.write(bytes);
        outputStream.flush();
        outputStream.close();
    }

    @GetMapping("/test")
    public byte[] getTest() throws IOException {
        String filePath = "D:\\tmp\\testFile\\test.png";

        File file = new File(filePath);
        FileInputStream inputStream = new FileInputStream(file);
        byte[] bytes = new byte[(int) file.length()];
        inputStream.read(bytes);
        inputStream.close();
        return bytes;
    }


    @GetMapping("/testVideo")
    public void getTestVideo(HttpServletResponse response) throws IOException {
//        String filePath = "D:\\tmp\\testFile\\video.mp4";
        String filePath = "D:\\tmp\\testFile\\001.webm";

        response.reset();
        //设置头部类型
        response.setContentType("video/webm;charset=UTF-8");

        File file = new File(filePath);
        FileInputStream inputStream = new FileInputStream(file);

        ServletOutputStream out = null;
        try {
            out = response.getOutputStream();
            if(inputStream != null){
                byte[] b = new byte[1024];
                int i = 0;
                while((i = inputStream.read(b)) > 0){
                    out.write(b, 0, i);
                }
                out.flush();
                inputStream.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {

                }
                inputStream = null;
            }
            if(out != null) {
                try {
                    out.close();
                } catch (IOException e) {

                }
                out = null;
            }
        }

//        byte[] bytes = new byte[(int) file.length()];
//        inputStream.read(bytes);
//        inputStream.close();
//        return bytes;
    }

    @GetMapping("/testVideo1")
    public void getTestVideo1(HttpServletResponse response) throws IOException {
//        String filePath = "D:\\tmp\\testFile\\video.mp4";
        String filePath = "D:\\tmp\\testFile\\001.webm";

        response.reset();
        //设置头部类型
        response.setContentType("video/webm;charset=UTF-8");

        File file = new File(filePath);
        FileInputStream inputStream = new FileInputStream(file);

        ServletOutputStream out = null;
        try {
            out = response.getOutputStream();
            byte[] bytes = new byte[(int) file.length()];
            inputStream.read(bytes);

            out.write(bytes);
            out.flush();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {

                }
                inputStream = null;
            }
            if(out != null) {
                try {
                    out.close();
                } catch (IOException e) {

                }
                out = null;
            }
        }

    }

    @GetMapping("/getVideo2")
    public void getVideo(HttpServletResponse response, HttpServletRequest request){
        String filePath = "D:\\tmp\\testFile\\001.webm";
        File file = new File(filePath);

        long fileLength = file.length();// 记录文件大小
        long pastLength = 0;// 记录已下载文件大小
        int rangeSwitch = 0;// 0：从头开始的全文下载；1：从某字节开始的下载（bytes=27000-）；2：从某字节开始到某字节结束的下载（bytes=27000-39000）
        long contentLength = 0;// 客户端请求的字节总量
        String rangeBytes = "";// 记录客户端传来的形如“bytes=27000-”或者“bytes=27000-39000”的内容
        RandomAccessFile raf = null;// 负责读取数据
        OutputStream os = null;// 写出数据
        OutputStream out = null;// 缓冲
        int bsize = 1024;// 缓冲区大小
        byte b[] = new byte[bsize];// 暂存容器

        String range = request.getHeader("Range");
        int responseStatus = 206;
        if (range != null && range.trim().length() > 0 && !"null".equals(range)) {// 客户端请求的下载的文件块的开始字节
            responseStatus = javax.servlet.http.HttpServletResponse.SC_PARTIAL_CONTENT;
            System.out.println("request.getHeader(\"Range\")=" + range);
            rangeBytes = range.replaceAll("bytes=", "");
            if (rangeBytes.endsWith("-")) {
                rangeSwitch = 1;
                rangeBytes = rangeBytes.substring(0, rangeBytes.indexOf('-'));
                pastLength = Long.parseLong(rangeBytes.trim());
                contentLength = fileLength - pastLength;
            } else {
                rangeSwitch = 2;
                String temp0 = rangeBytes.substring(0, rangeBytes.indexOf('-'));
                String temp2 = rangeBytes.substring(rangeBytes.indexOf('-') + 1, rangeBytes.length());
                pastLength = Long.parseLong(temp0.trim());
            }
        } else {
            contentLength = fileLength;// 客户端要求全文下载
        }

        // 清除首部的空白行
        response.reset();
        // 告诉客户端允许断点续传多线程连接下载,响应的格式是:Accept-Ranges: bytes
        response.setHeader("Accept-Ranges", "bytes");

        // 如果是第一次下,还没有断点续传,状态是默认的 200,无需显式设置;响应的格式是:HTTP/1.1
        if (rangeSwitch != 0) {
            response.setStatus(responseStatus);
            // 不是从最开始下载，断点下载响应号为206
            // 响应的格式是:
            // Content-Range: bytes [文件块的开始字节]-[文件的总大小 - 1]/[文件的总大小]
            switch (rangeSwitch) {
                case 1: {
                    String contentRange = new StringBuffer("bytes ")
                            .append(new Long(pastLength).toString()).append("-")
                            .append(new Long(fileLength - 1).toString())
                            .append("/").append(new Long(fileLength).toString())
                            .toString();
                    response.setHeader("Content-Range", contentRange);
                    break;
                }
                case 2: {
                    String contentRange = new Long(fileLength).toString();
                    response.setHeader("Content-Range", contentRange);
                    break;
                }
                default: {
                    break;
                }
            }
        }else{
            String contentRange = new StringBuffer("bytes ").append("0-")
                    .append(fileLength - 1).append("/").append(fileLength)
                    .toString();
            response.setHeader("Content-Range", contentRange);
        }

        byte[] bytes = new byte[(int) file.length()];
        try {
            FileInputStream inputStream = new FileInputStream(file);
            inputStream.read(bytes);
            inputStream.close();
        }catch (Exception e){
            e.printStackTrace();
        }


        try {
            response.setContentType("video/mp4;charset=UTF-8");
            response.setHeader("Content-Length", String.valueOf(contentLength));
            os = response.getOutputStream();
            out = new BufferedOutputStream(os);
            raf = new RandomAccessFile(file, "r");

            try {
                long outLength = 0;// 实际输出字节数
                switch (rangeSwitch) {
                    case 0: {
                    }
                    case 1: {
                        out.write(bytes);
//                        raf.seek(pastLength);
//                        int n = 0;
//                        while ((n = raf.read(b)) != -1) {
//                            out.write(b, 0, n);
//                            outLength += n;
//                        }
                        break;
                    }
                    case 2: {
                        raf.seek(pastLength);
                        int n = 0;
                        long readLength = 0;// 记录已读字节数
                        while (readLength <= contentLength - bsize) {// 大部分字节在这里读取
                            n = raf.read(b);
                            readLength += n;
                            out.write(b, 0, n);
                            outLength += n;
                        }
                        if (readLength <= contentLength) {// 余下的不足 1024 个字节在这里读取
                            n = raf.read(b, 0, (int) (contentLength - readLength));
                            out.write(b, 0, n);
                            outLength += n;
                        }
                        break;
                    }
                    default: {
                        break;
                    }
                }
                System.out.println("Content-Length为：" + contentLength + "；实际输出字节数：" + outLength);
                out.flush();
            }catch (Exception e){
                e.printStackTrace();
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (raf != null) {
                try {
                    raf.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @GetMapping("/getVideo1")
    private void sendVideo(HttpServletRequest request, HttpServletResponse response) throws FileNotFoundException, IOException {
//        String filePath = "D:\\tmp\\testFile\\001.webm";
        String filePath = "D:\\tmp\\testFile\\001.mp4";
        File file = new File(filePath);
//        byte[] bytes = new byte[(int) file.length()];
//        FileInputStream inputStream = new FileInputStream(file);
//        inputStream.read(bytes);
//        inputStream.close();

        RandomAccessFile randomFile = new RandomAccessFile(file, "r");//只读模式
        long contentLength = randomFile.length();
//        long contentLength = bytes.length;
        String range = request.getHeader("Range");
        int start = 0, end = 0;
        if(range != null && range.startsWith("bytes=")){
            String[] values = range.split("=")[1].split("-");
            start = Integer.parseInt(values[0]);
            if(values.length > 1){
                end = Integer.parseInt(values[1]);
            }
        }
        int requestSize = 0;
        if(end != 0 && end > start){
            requestSize = end - start + 1;
        } else {
            requestSize = Integer.MAX_VALUE;
        }

        response.setContentType("video/mp4");
        response.setHeader("Accept-Ranges", "bytes");
//        response.setHeader("ETag", fileName);
        response.setHeader("Last-Modified", new Date().toString());
        //第一次请求只返回content length来让客户端请求多次实际数据
        if(range == null){
            response.setHeader("Content-length", contentLength + "");
        }else{
            //以后的多次以断点续传的方式来返回视频数据
            response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);//206
            long requestStart = 0, requestEnd = 0;
            String[] ranges = range.split("=");
            if(ranges.length > 1){
                String[] rangeDatas = ranges[1].split("-");
                requestStart = Integer.parseInt(rangeDatas[0]);
                if(rangeDatas.length > 1){
                    requestEnd = Integer.parseInt(rangeDatas[1]);
                }
            }
            long length = 0;
            if(requestEnd > 0){
                length = requestEnd - requestStart + 1;
                response.setHeader("Content-length", "" + length);
                response.setHeader("Content-Range", "bytes " + requestStart + "-" + requestEnd + "/" + contentLength);
            }else{
                length = contentLength - requestStart;
                response.setHeader("Content-length", "" + length);
                response.setHeader("Content-Range", "bytes "+ requestStart + "-" + (contentLength - 1) + "/" + contentLength);
            }
        }
        ServletOutputStream out = response.getOutputStream();
        int needSize = requestSize;
        //2147483647
        //2
        randomFile.seek(start);
        while(needSize > 0){
//            out.write(bytes);
            byte[] buffer = new byte[4096];
            int len = randomFile.read(buffer);
            if(needSize < buffer.length){
                out.write(buffer, 0, needSize);
            } else {
                out.write(buffer, 0, len);
                if(len < buffer.length){
                    break;
                }
            }
            needSize -= buffer.length;
        }
        randomFile.close();
        out.close();

    }
/*
    @GetMapping("/getVideo2")
    private void sendVideo2(HttpServletRequest request, HttpServletResponse response) throws FileNotFoundException, IOException {
//        String filePath = "D:\\tmp\\testFile\\001.webm";
        String filePath = "D:\\tmp\\testFile\\001.mp4";
        File file = new File(filePath);
        byte[] bytes = new byte[(int) file.length()];
        FileInputStream inputStream = new FileInputStream(file);
        inputStream.read(bytes);
        inputStream.close();

        RandomAccessFile randomFile = new RandomAccessFile(file, "r");//只读模式
//        long contentLength = randomFile.length();
        long contentLength = bytes.length;
        String range = request.getHeader("Range");
        int start = 0, end = 0;
        if(range != null && range.startsWith("bytes=")){
            String[] values = range.split("=")[1].split("-");
            start = Integer.parseInt(values[0]);
            if(values.length > 1){
                end = Integer.parseInt(values[1]);
            }
        }
        int requestSize = 0;
        if(end != 0 && end > start){
            requestSize = end - start + 1;
        } else {
            requestSize = Integer.MAX_VALUE;
        }

        response.setContentType("video/mp4");
        response.setHeader("Accept-Ranges", "bytes");
//        response.setHeader("ETag", fileName);
        response.setHeader("Last-Modified", new Date().toString());
        //第一次请求只返回content length来让客户端请求多次实际数据


        if(range == null){
            response.setHeader("Content-length", contentLength + "");
        }else{
            //以后的多次以断点续传的方式来返回视频数据
            response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);//206
            long requestStart = 0, requestEnd = 0;
            String[] ranges = range.split("=");
            if(ranges.length > 1){
                String[] rangeDatas = ranges[1].split("-");
                requestStart = Integer.parseInt(rangeDatas[0]);
                if(rangeDatas.length > 1){
                    requestEnd = Integer.parseInt(rangeDatas[1]);
                }
            }
            long length = 0;
            if(requestEnd > 0){
                length = requestEnd - requestStart + 1;
                response.setHeader("Content-length", "" + length);
                response.setHeader("Content-Range", "bytes " + requestStart + "-" + requestEnd + "/" + contentLength);
            }else{
                length = contentLength - requestStart;
                response.setHeader("Content-length", "" + length);
                response.setHeader("Content-Range", "bytes "+ requestStart + "-" + (contentLength - 1) + "/" + contentLength);
            }
        }
        ServletOutputStream out = response.getOutputStream();
        int needSize = requestSize;
        //2147483647
        //2
        randomFile.seek(start);
        while(needSize > 0){
//            out.write(bytes, 0, bytes.length);
//            break;
            byte[] buffer = new byte[4096];
            buffer = strChange(bytes, requestStart, requestStart+buffer.length);
            int len = randomFile.read(buffer);
            int len = bytes.length;
            if(needSize < len){
                out.write(bytes, 0, needSize);
            } else {
                out.write(bytes, 0, len);
                break;
                if(len < buffer.length){
                    break;
                }
            }
            needSize -= buffer.length;
        }
        randomFile.close();
        out.close();

    }

    public byte[] strChange(byte str1[],  long start, long end){

        int k = end - start;
        byte str2[] = new byte[k];
        for(long i = start, j=0 ; i<end && j<k; i++,j++){
            str2[j] = str1[i];
        }

        return str2;
    }*/

}
