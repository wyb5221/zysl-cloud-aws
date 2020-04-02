package com.zysl.cloud.aws.web.validator;

import com.zysl.cloud.utils.validator.IValidator;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 断点续传入参
 */
@Setter
@Getter
public class UploadPartRequestV implements IValidator {
    private static final long serialVersionUID = 6528744834290825581L;

    //文件夹名称
    @NotBlank
    private String bucketName;
    //文件名
    @NotBlank
    private String fileId;
    @NotBlank
    private String uploadId;
    @NotNull
    private Integer partNumber;

    @Override
    public void customizedValidate(List<String> errors, Integer userCase) {

    }
}
