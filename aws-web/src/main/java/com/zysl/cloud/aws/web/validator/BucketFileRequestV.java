package com.zysl.cloud.aws.web.validator;

import com.zysl.cloud.utils.validator.IValidator;
import com.zysl.cloud.utils.validator.impl.LengthChar;
import java.util.List;
import javax.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class BucketFileRequestV implements IValidator {

    @LengthChar(min = 3, max = 63)
    @NotBlank
    private String bucketName;

    @LengthChar(min = 1, max = 1024)
    @NotBlank
    private String fileName;

    @Override
    public void customizedValidate(List<String> errors, Integer userCase) {}
}
