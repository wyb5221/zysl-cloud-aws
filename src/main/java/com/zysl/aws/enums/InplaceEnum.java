package com.zysl.aws.enums;

import lombok.Getter;

/**
 * 是否覆盖 0不覆盖 1覆盖
 */
@Getter
public enum InplaceEnum {

    NOCOVER("0", "不覆盖"),
    COVER("1", "覆盖");

    private String code;

    private String desc;

    InplaceEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 根据code获取value
     * @param code
     * @return
     */
    public String getDesc(String code){
        for(InplaceEnum in : InplaceEnum.values()){
            if(code.equals(in.getCode())){
                return in.getDesc();
            }
        }
        return null;
    }
}
