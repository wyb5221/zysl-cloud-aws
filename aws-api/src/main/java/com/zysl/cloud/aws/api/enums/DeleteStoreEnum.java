package com.zysl.cloud.aws.api.enums;

import lombok.Getter;

/**
 * 是否物理删除，1是0否
 */
@Getter
public enum DeleteStoreEnum {

    NOCOVER(0, "否"),
    COVER(1, "是");

    private Integer code;

    private String desc;

    DeleteStoreEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 根据code获取value
     * @param code
     * @return
     */
    public String getDesc(String code){
        for(DeleteStoreEnum in : DeleteStoreEnum.values()){
            if(code.equals(in.getCode())){
                return in.getDesc();
            }
        }
        return null;
    }
}
