package com.zysl.cloud.aws.biz.service;

public interface IPDFService {
    /**
     * pdf加图片水印
     * @param inPdfFile
     * @param outPdfFile
     * @param markImagePath
     * @param imgWidth
     * @param imgHeight
     */
    public void addPdfImgMark(String inPdfFile, String outPdfFile, String markImagePath,
		int imgWidth, int imgHeight);

    /**
     * pdf加文字水印
     * @param inPdfFile
     * @param outPdfFile
     * @param textMark
     * @param textWidth
     * @param textHeight
     */
    public void addPdfTextMark(String inPdfFile, String outPdfFile, String textMark, int textWidth,
		int textHeight);

    /**
     * pdf文件加密码
     * @param inBuff
     * @param userPwd
     * @param ownerPwd
     */
    public byte[] addPwd(byte[] inBuff, String userPwd, String ownerPwd);
}
