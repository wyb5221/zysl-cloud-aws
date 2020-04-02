package com.zysl.cloud.aws.web.validator;

import com.zysl.cloud.aws.api.req.MultipartUploadRequest;
import com.zysl.cloud.utils.common.BaseReqeust;
import com.zysl.cloud.utils.constants.SwaggerConstants;
import com.zysl.cloud.utils.validator.IValidator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.CollectionUtils;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * 断点续传完成确认入参
 */
@Setter
@Getter
public class CompleteMultipartRequestV implements IValidator {
    private static final long serialVersionUID = 6528744834290825581L;

    //文件夹名称
    @NotBlank
    private String bucketName;
    //文件名
    @NotBlank
    private String fileId;
    @NotBlank
    private String uploadId;
    private List<MultipartUploadRequest> eTagList;

    @Override
    public void customizedValidate(List<String> errors, Integer userCase) {
        if(CollectionUtils.isEmpty(eTagList)){
            errors.add("断点续传内容不能为空");
        }

    }
}
