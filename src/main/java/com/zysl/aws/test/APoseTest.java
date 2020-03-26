package com.zysl.aws.test;

import com.aspose.words.Document;
import com.aspose.words.License;
import com.aspose.words.SaveFormat;
import com.aspose.words.SaveOptions;
import com.zysl.cloud.utils.StringUtils;

import java.io.*;

public class APoseTest {

    public static void main(String[] args){
        APoseTest test = new APoseTest();
        test.saveDoc();
    }

    public void saveDoc(){
        String baseDir = "d:/data/tmp/";
        String src = baseDir + "1.doc";
        String outFilePath = baseDir + "2.pdf";
        String licenseFile = baseDir + "license.xml";

        FileOutputStream fileOS = null;
        InputStream is = null;

        try{
            getLicense(licenseFile);
            is = new FileInputStream(new File(src));
            Document doc = new Document(is);
            fileOS = new FileOutputStream(new File(outFilePath));
            // 保存转换的pdf文件
            doc.save(fileOS, SaveFormat.PDF);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try{
                fileOS.close();
                is.close();
            }catch (Exception e){

            }
        }

    }

    private boolean getLicense(String licenseFile) {
        boolean result = false;
        try {
            InputStream is = new FileInputStream(new File(licenseFile));
            License aposeLic = new License();
            aposeLic.setLicense(is);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
