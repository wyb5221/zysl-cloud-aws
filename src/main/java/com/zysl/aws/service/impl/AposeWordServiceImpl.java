package com.zysl.aws.service.impl;

import com.aspose.words.Document;
import com.aspose.words.License;
import com.aspose.words.SaveFormat;
import com.zysl.aws.config.BizConfig;
import com.zysl.aws.service.IPDFService;
import com.zysl.aws.service.IWordService;
import com.zysl.cloud.utils.FileUtils;
import com.zysl.cloud.utils.StringUtils;
import com.zysl.cloud.utils.common.AppLogicException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;

@Slf4j
@Service
public class AposeWordServiceImpl implements IWordService {

    @Autowired
    IPDFService pdfService;
    @Autowired
    BizConfig bizConfig;

    @Override
    public byte[] changeWordToPDF(String fileName, byte[] inBuff,Boolean imgMarkSign, String textMark){
        log.info("===changeWordToPDF===fileName:{},imgMarkSign:{},textMark:{},inBuff.length",fileName,imgMarkSign,textMark, inBuff == null ? 0: inBuff.length);

        if(StringUtils.isBlank(fileName) || inBuff == null || inBuff.length == 0){
            return null;
        }
        String outFname = null;
        try{
           // step 1.word转换pdf
            outFname = bizConfig.PDF_TMP_FILE_PATH + fileName + "pdf.pdf";
            String outImgFname = bizConfig.PDF_TMP_FILE_PATH + fileName + "img.pdf";
            String outTextFname = bizConfig.PDF_TMP_FILE_PATH + fileName + "text.pdf";

            //word转pdf
            changeWordToPDFByApose(inBuff,outFname);

            //step 2.加文字水印
            if(textMark != null && !"".equals(textMark)){
                pdfService.addPdfTextMark(outFname,outTextFname,textMark,300,300);
                FileUtils.deleteFile(outFname);
                outFname = outTextFname;
            }

//            //step 3.加图片水印
//            if(imgMarkSign != null && imgMarkSign){
//                pdfService.addPdfImgMark(outFname,outImgFname,bizConfig.getDefaultImgMarkFile(),100,100);
//                FileUtils.deleteFile(outFname);
//                outFname = outImgFname;
//            }

            //step 4.返回数据
            return FileUtils.fileToByte(outFname);
        }catch (Exception e){
            log.error("转换pdf时office异常:{}",e);
        }finally {
            FileUtils.deleteFile(outFname);
        }
        return null;
    }

    @Override
    public void changeWordToPDFByApose(byte[] inBuff, String outFilePath){
        saveWordTo(inBuff,outFilePath,SaveFormat.PDF);
    }


    @Override
    public void saveWordTo(byte[] inBuff, String outFilePath,Integer formarType){
        log.info("===saveWordTo===outFilePath:{}，buff.length:{}",outFilePath, inBuff == null ? 0 : inBuff.length);
        if(inBuff == null || inBuff.length == 0 || StringUtils.isBlank(outFilePath)){
            log.error("===saveWordTo===validator.error:outFilePath:{}",outFilePath);
            return;
        }


        FileOutputStream fileOS = null;
        InputStream is = null;
        // 验证License
        if (!getLicense()) {
            log.error("===未获取到apose.license===");
            return;
        }

        try{
            is = new ByteArrayInputStream(inBuff);
            Document doc = new Document(is);
            //创建文件
            fileOS = new FileOutputStream(new File(outFilePath));
            // 保存转换的pdf文件
            doc.save(fileOS, formarType);
        }catch (Exception e){
            log.error("===saveWordTo=== error ：{}", e);
            throw new AppLogicException("saveWordTo error.");
        }finally {
            try {
                if(fileOS != null){
                    fileOS.close();
                }
                if(is != null){
                    is.close();
                }
            } catch (IOException e) {
                log.error("===saveWordTo===stream close error ：{}", e);
            }
        }
    }



    private boolean getLicense() {
        boolean result = false;
        try {
            InputStream is = this.getClass().getClassLoader().getResourceAsStream("license.xml");
            License aposeLic = new License();
            aposeLic.setLicense(is);
            result = true;
        } catch (Exception e) {
            log.error("--apose校验异常：{}--", e);
        }
        return result;
    }
}
