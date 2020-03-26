package com.zysl.cloud.aws.web.validator;

import com.zysl.cloud.utils.common.BaseReqeust;
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
public class BucketFileRequestV implements IValidator {

    @Min(3)
    @Max(63)
    @NotBlank
    private String bucketName;


    @Min(3)
    @Max(63)
    @NotBlank
    private String fileName;

    @Override
    public void customizedValidate(List<String> errors, Integer userCase){
        String pattern = "^[a-zA-Z0-9.\\-_]{3,60}$";
        //判断存储桶是否满足命名规则
        if(Pattern.compile(pattern).matcher(bucketName).matches()){
            errors.add("存储桶不满足命名规则.");
        }
    }
}
