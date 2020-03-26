package com.zysl.cloud.aws.web.validator;

import com.zysl.cloud.utils.validator.IValidator;
import com.zysl.cloud.utils.validator.impl.EnumValue;
import java.util.List;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public class KeyRequestV implements IValidator {


	@NotBlank
	public String name;


	@Min(1)
	@Max(2)
	@EnumValue(target={"1","2"},message="字段不在枚举范围")
	public Integer age;

	@Override
	public void customizedValidate(List<String> errors, Integer userCase){
//		errors.add()
	}
}
