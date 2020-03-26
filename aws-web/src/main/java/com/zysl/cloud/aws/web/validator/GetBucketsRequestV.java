package com.zysl.cloud.aws.web.validator;

import com.zysl.cloud.utils.validator.IValidator;
import com.zysl.cloud.utils.validator.impl.LengthChar;
import java.util.List;
import java.util.regex.Pattern;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;


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
