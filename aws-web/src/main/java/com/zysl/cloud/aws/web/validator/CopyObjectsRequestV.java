package com.zysl.cloud.aws.web.validator;

import com.zysl.cloud.utils.validator.IValidator;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Setter
@Getter
public class CopyObjectsRequestV implements IValidator {

    //源存储桶名称
    @NotBlank
    private String sourceBucket;
    //源目录名称，多级目录用/隔开，例如a/b.doc
    @NotBlank
    private String sourceKey;
    //目标存储桶名称
    @NotBlank
    private String destBucket;
    //目标目录名称，多级目录用/隔开，例如a/b.doc
    @NotBlank
    private String destKey;

    @Override
    public void customizedValidate(List<String> errors, Integer userCase) {

    }
}
