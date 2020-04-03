package com.zysl.cloud.aws.web.validator;

import com.zysl.cloud.utils.validator.IValidator;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Setter
@Getter
public class GetListPartRequestV implements IValidator {

    private static final long serialVersionUID = 1510472767207028221L;
    //文件夹名称
    @NotBlank
    private String bucketName;
    //文件名
    @NotBlank
    private String fileId;
    @NotBlank
    private String uploadId;

    @Override
    public void customizedValidate(List<String> errors, Integer userCase) {

    }
}
