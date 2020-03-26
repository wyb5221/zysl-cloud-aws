package com.zysl.cloud.aws.web.validator;

import com.zysl.cloud.utils.common.BaseReqeust;
import com.zysl.cloud.utils.validator.IValidator;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * 删除对象入参
 */
@Setter
@Getter
public class DelObjectRequestV implements IValidator {
    //存储桶名称
    @NotBlank
    private String bucketName;
    //对象名称，可以带目录，如a/b/1.doc
    @NotBlank
    private String key;

    @Override
    public void customizedValidate(List<String> errors, Integer userCase) {

    }
}
