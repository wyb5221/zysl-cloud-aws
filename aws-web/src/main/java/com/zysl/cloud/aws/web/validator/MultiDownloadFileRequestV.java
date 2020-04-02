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
    
    @Min(0)
    private Long start;
    
    private Long pageSize;

    @Override
    public void customizedValidate(List<String> errors, Integer userCase) {
        //分片下载最大范围
        if(pageSize != null && pageSize > BizConstants.MULTI_DOWN_FILE_MAX_SIZE){
            errors.add("分片范围不能超过(byte):" + BizConstants.MULTI_DOWN_FILE_MAX_SIZE);
        }
    }
}
