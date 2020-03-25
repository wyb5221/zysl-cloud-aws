package com.zysl.aws.web.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 文件详细信息返回对象
 */
@Setter
@Getter
public class FileInfoRequest implements Serializable {
    private static final long serialVersionUID = 7916556936555709766L;

    private Boolean deleteMarker;
    private String acceptRanges;
    private String expiration;
    private String restore;
    private Date lastModified;
    private Long contentLength;
    private String eTag;
    private Integer missingMeta;
    private String versionId;
    private String cacheControl;
    private String contentDisposition;
    private String contentEncoding;
    private String contentLanguage;
    private String contentType;
    private Date expires;
    private String websiteRedirectLocation;
    private String serverSideEncryption;
    private Map<String, String> metadata;
    private String sseCustomerAlgorithm;
    private String sseCustomerKeyMD5;
    private String ssekmsKeyId;
    private String storageClass;
    private String requestCharged;
    private String replicationStatus;
    private Integer partsCount;
    private String objectLockMode;
    private Date objectLockRetainUntilDate;
    private String objectLockLegalHoldStatus;
    private List<TageDTO> tageList;

    @Override
    public String toString() {
        return "FileInfoRequest{" +
                "deleteMarker=" + deleteMarker +
                ", acceptRanges='" + acceptRanges + '\'' +
                ", expiration='" + expiration + '\'' +
                ", restore='" + restore + '\'' +
                ", lastModified=" + lastModified +
                ", contentLength=" + contentLength +
                ", eTag='" + eTag + '\'' +
                ", missingMeta=" + missingMeta +
                ", versionId='" + versionId + '\'' +
                ", cacheControl='" + cacheControl + '\'' +
                ", contentDisposition='" + contentDisposition + '\'' +
                ", contentEncoding='" + contentEncoding + '\'' +
                ", contentLanguage='" + contentLanguage + '\'' +
                ", contentType='" + contentType + '\'' +
                ", expires=" + expires +
                ", websiteRedirectLocation='" + websiteRedirectLocation + '\'' +
                ", serverSideEncryption='" + serverSideEncryption + '\'' +
                ", metadata=" + metadata +
                ", sseCustomerAlgorithm='" + sseCustomerAlgorithm + '\'' +
                ", sseCustomerKeyMD5='" + sseCustomerKeyMD5 + '\'' +
                ", ssekmsKeyId='" + ssekmsKeyId + '\'' +
                ", storageClass='" + storageClass + '\'' +
                ", requestCharged='" + requestCharged + '\'' +
                ", replicationStatus='" + replicationStatus + '\'' +
                ", partsCount=" + partsCount +
                ", objectLockMode='" + objectLockMode + '\'' +
                ", objectLockRetainUntilDate=" + objectLockRetainUntilDate +
                ", objectLockLegalHoldStatus='" + objectLockLegalHoldStatus + '\'' +
                ", tageList=" + tageList +
                '}';
    }
}
