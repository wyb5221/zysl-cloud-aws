package com.zysl.cloud.aws.web.validator;

import com.zysl.cloud.aws.biz.constant.BizConstants;
import com.zysl.cloud.utils.validator.IValidator;
import java.util.List;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * 分片文件下载校验对象
 */
@Setter
@Getter
public class MultiDownloadFileRequestV implements IValidator {
    
    @NotBlank
    private String bucketName;
    
    @NotBlank
    private String fileId;

    @Override
    public void customizedValidate(List<String> errors, Integer userCase) {
    }
}
