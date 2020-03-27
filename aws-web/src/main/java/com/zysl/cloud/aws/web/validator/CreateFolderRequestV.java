package com.zysl.cloud.aws.web.validator;

import com.zysl.cloud.utils.common.BaseReqeust;
import com.zysl.cloud.utils.constants.SwaggerConstants;
import com.zysl.cloud.utils.validator.IValidator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * 创建目录folder请求对象
 */
@Getter
@Setter
public class CreateFolderRequestV implements IValidator {

	@NotBlank
	private String folderName;
	@NotBlank
	private String bucketName;

	@Override
	public void customizedValidate(List<String> errors, Integer userCase) {

	}
}
