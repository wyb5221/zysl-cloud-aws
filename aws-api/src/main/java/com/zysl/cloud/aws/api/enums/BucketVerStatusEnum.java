package com.zysl.cloud.aws.api.enums;

import lombok.Getter;

/**
 * bukcet版本控制状态
 * @description
 * @author miaomingming
 * @date 11:04 2020/3/25
 * @param
 * @return
 **/
@Getter
public enum  BucketVerStatusEnum {
	ENABLED("Enabled", "启动"),
	SUSPENDED("Suspended", "关闭");

	private String code;

	private String desc;

	BucketVerStatusEnum(String code, String desc) {
		this.code = code;
		this.desc = desc;
	}

	/**
	 * 根据code获取value
	 * @param code
	 * @return
	 */
	public String getDesc(String code){
		for(BucketVerStatusEnum in : BucketVerStatusEnum.values()){
			if(in.getCode().equals(code)){
				return in.getDesc();
			}
		}
		return null;
	}
}
