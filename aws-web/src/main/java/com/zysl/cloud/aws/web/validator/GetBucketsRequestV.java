package com.zysl.cloud.aws.web.validator;

import com.zysl.cloud.utils.validator.IValidator;
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

    @Min(1)
    @Max(32)
    @NotBlank
    private String serverNo;


    @Override
    public void customizedValidate(List<String> errors, Integer userCase){
    }
}
