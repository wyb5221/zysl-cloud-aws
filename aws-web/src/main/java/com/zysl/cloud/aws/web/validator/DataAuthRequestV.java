package com.zysl.cloud.aws.web.validator;

import com.zysl.cloud.aws.api.dto.OPAuthDTO;
import com.zysl.cloud.aws.api.dto.TagDTO;
import com.zysl.cloud.utils.StringUtils;
import com.zysl.cloud.utils.constants.SwaggerConstants;
import com.zysl.cloud.utils.validator.IValidator;
import io.swagger.annotations.ApiModelProperty;
import java.util.List;
import javax.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.CollectionUtils;

@Setter
@Getter
public class DataAuthRequestV implements IValidator {
	
	//文件夹名称
	@NotBlank
	private String bucketName;
	//文件名称
	private String fileName;
	
	private List<OPAuthDTO> userAuths;

	private List<OPAuthDTO> groupAuths;

	private String everyOneAuths;


	@Override
	public void customizedValidate(List<String> errors, Integer userCase) {



		if(!CollectionUtils.isEmpty(userAuths)){
			for(OPAuthDTO dto:userAuths){
				if(!formatCheck(dto.getKey()) || !formatCheck(dto.getValue())){
					errors.add("用户名或权限列表不能包含以下字符(括号内): ( = ) ( _ )  ( : ) ");
					break;
				}
			}
		}

		if(!CollectionUtils.isEmpty(groupAuths)){
			for(OPAuthDTO dto:groupAuths){
				if(!formatCheck(dto.getKey()) || !formatCheck(dto.getValue())){
					errors.add("组名或权限列表不能包含以下字符(括号内): ( = ) ( _ )  ( : ) ");
					break;
				}
			}
		}

		if(!formatCheck(everyOneAuths)){
			errors.add("所有人的权限列表不能包含以下字符(括号内): ( = ) ( _ )  ( : ) ");
		}
	}

	private boolean formatCheck(String str){
		//分类间隔符
		final String CLASS_SEPARATOR = ":";
		//数据组间隔符
		final String ITEM_SEPARATOR = "_";
		//key-value间隔符
		final String KV_SEPARATOR = "=";

		if(StringUtils.isBlank(str)
			   || str.contains(CLASS_SEPARATOR)
			   || str.contains(ITEM_SEPARATOR)
			   || str.contains(KV_SEPARATOR)){
			return Boolean.FALSE;
		}
		return Boolean.TRUE;
	}
}
