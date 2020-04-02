package com.zysl.cloud.aws.web.validator;

import com.zysl.cloud.utils.validator.IValidator;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.List;

/**
 * 文件下载校验对象
 */
@Setter
@Getter
public class DownloadFileRequestV implements IValidator {
    private static final long serialVersionUID = -8651479291764968852L;

    //文件夹名称
    @NotBlank
    private String bucketName;
    //文件名称
    @NotBlank
    private String fileId;

    @Override
    public void customizedValidate(List<String> errors, Integer userCase) {

    }
}
