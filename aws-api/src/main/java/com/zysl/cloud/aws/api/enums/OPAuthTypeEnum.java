package com.zysl.cloud.aws.api.enums;

import lombok.Getter;

/**
 * 操作权限类型
 * @description
 * @author miaomingming
 * @date 8:45 2020/3/27
 * @return
 **/
@Getter
public enum  OPAuthTypeEnum {

	ALL("a", "完全控制"),
	READ("r", "读取"),
	DELETE("d", "删除"),
	WRITE("w", "写入"),
	MODIFY("m", "修改"),
	EXECUTE("e", "执行"),
	LIST("l", "列出目录内容"),
	;



	private String code;
	private String desc;

	OPAuthTypeEnum(String code, String desc) {
		this.code = code;
		this.desc = desc;
	}

	/**
	 * 根据code获取desc
	 * @param code
	 * @return
	 */
	public String getDesc(String code){
		for(OPAuthTypeEnum in : OPAuthTypeEnum.values()){
			if(in.getCode().equals(code)){
				return in.getDesc();
			}
		}
		return null;
	}
}
