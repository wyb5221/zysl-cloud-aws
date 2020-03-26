package com.zysl.cloud.aws.web.validator;

import com.zysl.cloud.utils.validator.IValidator;
import com.zysl.cloud.utils.validator.impl.LengthChar;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.util.List;


@Setter
@Getter
public class GetBucketsRequestV implements IValidator {

    @LengthChar(min = 1, max = 32)
    @NotBlank
    private String serverNo;


    @Override
    public void customizedValidate(List<String> errors, Integer userCase){
    }
}
