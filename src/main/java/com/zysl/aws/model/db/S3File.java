package com.zysl.aws.model.db;

import java.util.Date;

public class S3File {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column s3_file.id
     *
     * @mbggenerated Fri Feb 14 12:02:46 CST 2020
     */
    private Long id;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column s3_file.service_no
     *
     * @mbggenerated Fri Feb 14 12:02:46 CST 2020
     */
    private String serviceNo;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column s3_file.file_name
     *
     * @mbggenerated Fri Feb 14 12:02:46 CST 2020
     */
    private String fileName;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column s3_file.content_md5
     *
     * @mbggenerated Fri Feb 14 12:02:46 CST 2020
     */
    private String contentMd5;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column s3_file.folder_name
     *
     * @mbggenerated Fri Feb 14 12:02:46 CST 2020
     */
    private String folderName;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column s3_file.down_url
     *
     * @mbggenerated Fri Feb 14 12:02:46 CST 2020
     */
    private String downUrl;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column s3_file.file_size
     *
     * @mbggenerated Fri Feb 14 12:02:46 CST 2020
     */
    private Long fileSize;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column s3_file.max_amount
     *
     * @mbggenerated Fri Feb 14 12:02:46 CST 2020
     */
    private Integer maxAmount;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column s3_file.down_amount
     *
     * @mbggenerated Fri Feb 14 12:02:46 CST 2020
     */
    private Integer downAmount;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column s3_file.validity_time
     *
     * @mbggenerated Fri Feb 14 12:02:46 CST 2020
     */
    private Date validityTime;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column s3_file.upload_time
     *
     * @mbggenerated Fri Feb 14 12:02:46 CST 2020
     */
    private Date uploadTime;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column s3_file.create_time
     *
     * @mbggenerated Fri Feb 14 12:02:46 CST 2020
     */
    private Date createTime;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column s3_file.source_file_id
     *
     * @mbggenerated Fri Feb 14 12:02:46 CST 2020
     */
    private Long sourceFileId;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column s3_file.id
     *
     * @return the value of s3_file.id
     *
     * @mbggenerated Fri Feb 14 12:02:46 CST 2020
     */
    public Long getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column s3_file.id
     *
     * @param id the value for s3_file.id
     *
     * @mbggenerated Fri Feb 14 12:02:46 CST 2020
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column s3_file.service_no
     *
     * @return the value of s3_file.service_no
     *
     * @mbggenerated Fri Feb 14 12:02:46 CST 2020
     */
    public String getServiceNo() {
        return serviceNo;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column s3_file.service_no
     *
     * @param serviceNo the value for s3_file.service_no
     *
     * @mbggenerated Fri Feb 14 12:02:46 CST 2020
     */
    public void setServiceNo(String serviceNo) {
        this.serviceNo = serviceNo;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column s3_file.file_name
     *
     * @return the value of s3_file.file_name
     *
     * @mbggenerated Fri Feb 14 12:02:46 CST 2020
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column s3_file.file_name
     *
     * @param fileName the value for s3_file.file_name
     *
     * @mbggenerated Fri Feb 14 12:02:46 CST 2020
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column s3_file.content_md5
     *
     * @return the value of s3_file.content_md5
     *
     * @mbggenerated Fri Feb 14 12:02:46 CST 2020
     */
    public String getContentMd5() {
        return contentMd5;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column s3_file.content_md5
     *
     * @param contentMd5 the value for s3_file.content_md5
     *
     * @mbggenerated Fri Feb 14 12:02:46 CST 2020
     */
    public void setContentMd5(String contentMd5) {
        this.contentMd5 = contentMd5;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column s3_file.folder_name
     *
     * @return the value of s3_file.folder_name
     *
     * @mbggenerated Fri Feb 14 12:02:46 CST 2020
     */
    public String getFolderName() {
        return folderName;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column s3_file.folder_name
     *
     * @param folderName the value for s3_file.folder_name
     *
     * @mbggenerated Fri Feb 14 12:02:46 CST 2020
     */
    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column s3_file.down_url
     *
     * @return the value of s3_file.down_url
     *
     * @mbggenerated Fri Feb 14 12:02:46 CST 2020
     */
    public String getDownUrl() {
        return downUrl;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column s3_file.down_url
     *
     * @param downUrl the value for s3_file.down_url
     *
     * @mbggenerated Fri Feb 14 12:02:46 CST 2020
     */
    public void setDownUrl(String downUrl) {
        this.downUrl = downUrl;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column s3_file.file_size
     *
     * @return the value of s3_file.file_size
     *
     * @mbggenerated Fri Feb 14 12:02:46 CST 2020
     */
    public Long getFileSize() {
        return fileSize;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column s3_file.file_size
     *
     * @param fileSize the value for s3_file.file_size
     *
     * @mbggenerated Fri Feb 14 12:02:46 CST 2020
     */
    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column s3_file.max_amount
     *
     * @return the value of s3_file.max_amount
     *
     * @mbggenerated Fri Feb 14 12:02:46 CST 2020
     */
    public Integer getMaxAmount() {
        return maxAmount;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column s3_file.max_amount
     *
     * @param maxAmount the value for s3_file.max_amount
     *
     * @mbggenerated Fri Feb 14 12:02:46 CST 2020
     */
    public void setMaxAmount(Integer maxAmount) {
        this.maxAmount = maxAmount;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column s3_file.down_amount
     *
     * @return the value of s3_file.down_amount
     *
     * @mbggenerated Fri Feb 14 12:02:46 CST 2020
     */
    public Integer getDownAmount() {
        return downAmount;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column s3_file.down_amount
     *
     * @param downAmount the value for s3_file.down_amount
     *
     * @mbggenerated Fri Feb 14 12:02:46 CST 2020
     */
    public void setDownAmount(Integer downAmount) {
        this.downAmount = downAmount;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column s3_file.validity_time
     *
     * @return the value of s3_file.validity_time
     *
     * @mbggenerated Fri Feb 14 12:02:46 CST 2020
     */
    public Date getValidityTime() {
        return validityTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column s3_file.validity_time
     *
     * @param validityTime the value for s3_file.validity_time
     *
     * @mbggenerated Fri Feb 14 12:02:46 CST 2020
     */
    public void setValidityTime(Date validityTime) {
        this.validityTime = validityTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column s3_file.upload_time
     *
     * @return the value of s3_file.upload_time
     *
     * @mbggenerated Fri Feb 14 12:02:46 CST 2020
     */
    public Date getUploadTime() {
        return uploadTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column s3_file.upload_time
     *
     * @param uploadTime the value for s3_file.upload_time
     *
     * @mbggenerated Fri Feb 14 12:02:46 CST 2020
     */
    public void setUploadTime(Date uploadTime) {
        this.uploadTime = uploadTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column s3_file.create_time
     *
     * @return the value of s3_file.create_time
     *
     * @mbggenerated Fri Feb 14 12:02:46 CST 2020
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column s3_file.create_time
     *
     * @param createTime the value for s3_file.create_time
     *
     * @mbggenerated Fri Feb 14 12:02:46 CST 2020
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column s3_file.source_file_id
     *
     * @return the value of s3_file.source_file_id
     *
     * @mbggenerated Fri Feb 14 12:02:46 CST 2020
     */
    public Long getSourceFileId() {
        return sourceFileId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column s3_file.source_file_id
     *
     * @param sourceFileId the value for s3_file.source_file_id
     *
     * @mbggenerated Fri Feb 14 12:02:46 CST 2020
     */
    public void setSourceFileId(Long sourceFileId) {
        this.sourceFileId = sourceFileId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table s3_file
     *
     * @mbggenerated Fri Feb 14 12:02:46 CST 2020
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", serviceNo=").append(serviceNo);
        sb.append(", fileName=").append(fileName);
        sb.append(", contentMd5=").append(contentMd5);
        sb.append(", folderName=").append(folderName);
        sb.append(", downUrl=").append(downUrl);
        sb.append(", fileSize=").append(fileSize);
        sb.append(", maxAmount=").append(maxAmount);
        sb.append(", downAmount=").append(downAmount);
        sb.append(", validityTime=").append(validityTime);
        sb.append(", uploadTime=").append(uploadTime);
        sb.append(", createTime=").append(createTime);
        sb.append(", sourceFileId=").append(sourceFileId);
        sb.append("]");
        return sb.toString();
    }
}