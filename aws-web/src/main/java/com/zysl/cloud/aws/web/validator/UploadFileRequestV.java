package com.zysl.cloud.aws.web.validator;

import com.zysl.cloud.utils.common.BaseReqeust;
import com.zysl.cloud.utils.validator.IValidator;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * 上传文件入参对象
 */
@Setter
@Getter
public class UploadFileRequestV implements IValidator {

    private static final long serialVersionUID = 1004628529931222879L;

    //文件夹名称
    @NotBlank
    private String bucketName;
    //文件名
    @NotBlank
    private String fileId;
    //文件流
    @NotBlank
    private String data;

    @Override
    public void customizedValidate(List<String> errors, Integer userCase) {

    }
}
