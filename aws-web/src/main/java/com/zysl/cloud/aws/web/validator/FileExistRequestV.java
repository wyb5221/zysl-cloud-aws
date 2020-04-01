package com.zysl.cloud.aws.web.validator;

import com.zysl.cloud.utils.validator.IValidator;
import java.util.List;
import javax.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FileExistRequestV implements IValidator {

	//文件名称
	@NotBlank
	private String fileName;

	@Override
	public void customizedValidate(List<String> errors, Integer userCase) {

	}
}
