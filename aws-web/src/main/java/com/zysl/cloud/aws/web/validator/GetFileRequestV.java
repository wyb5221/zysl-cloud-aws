package com.zysl.cloud.aws.web.validator;

import com.zysl.cloud.utils.validator.IValidator;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * 文件信息查询入参对象
 */
@Setter
@Getter
public class GetFileRequestV implements IValidator {

    //文件夹名称
    @NotBlank
    private String bucketName;
    //文件名称
    @NotBlank
    private String fileName;

    @Override
    public void customizedValidate(List<String> errors, Integer userCase) {

    }
}
