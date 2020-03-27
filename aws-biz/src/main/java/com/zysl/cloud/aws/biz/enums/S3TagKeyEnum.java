package com.zysl.cloud.aws.biz.enums;

import com.zysl.cloud.aws.api.enums.OPAuthTypeEnum;
import lombok.Getter;

@Getter
public enum S3TagKeyEnum {
	OWNER("owner", "所属用户，临时使用"),
	//用户权限列表的value=用户id:权限列表;用户id:权限列表;#角色ID:权限列表;角色ID:权限列表;#所有人的权限列表
	//权限参考：OPAuthTypeEnum
	USER_AUTH("userAuth", "用户权限列表"),
	FILE_NAME("fileName", "文件名"),
	;



	private String code;
	private String desc;

	S3TagKeyEnum(String code, String desc) {
		this.code = code;
		this.desc = desc;
	}

	/**
	 * 根据code获取desc
	 * @param code
	 * @return
	 */
	public String getDesc(String code){
		for(S3TagKeyEnum in : S3TagKeyEnum.values()){
			if(in.getCode().equals(code)){
				return in.getDesc();
			}
		}
		return null;
	}
}
