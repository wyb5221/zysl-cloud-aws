package com.zysl.cloud.aws.biz.service;

public interface IWordService {

    /**
     * word转pdf
     * @param fileName word文件名
     * @param inBuff word文件数据
     * @param imgMarkSign 图片水印标记
     * @param textMark 水印文字
     */
    public byte[] changeWordToPDF(String fileName, byte[] inBuff, Boolean imgMarkSign,
		String textMark);

    /**
     * word转pdf
     * @param inBuff
     * @param outFilePath
     */
    public void changeWordToPDFByApose(byte[] inBuff, String outFilePath);

    /**
     * word转成其他格式
     * @param inBuff
     * @param outFilePath
     * @param formarType
     */
    public void saveWordTo(byte[] inBuff, String outFilePath, Integer formarType);

}
