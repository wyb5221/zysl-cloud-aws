package com.zysl.cloud.aws.web.validator;

import com.zysl.cloud.utils.common.BaseReqeust;
import com.zysl.cloud.utils.validator.IValidator;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * 文件重命名入参对象
 */
@Setter
@Getter
public class ObjectRenameRequestV implements IValidator {

    @NotBlank
    private String bucketName;
    @NotBlank
    private String sourcekey;
    @NotBlank
    private String destKey;

    @Override
    public void customizedValidate(List<String> errors, Integer userCase) {

    }
}
