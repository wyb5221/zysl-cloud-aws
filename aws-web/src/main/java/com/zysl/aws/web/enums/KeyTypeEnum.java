package com.zysl.aws.web.enums;

import lombok.Getter;

/**
 * 查询类型，0默认全部，1仅目录2仅文件
 */
@Getter
public enum KeyTypeEnum {

    DEFAULT(0, "默认"),
    FOLDER(1, "仅目录"),
    FILE(2, "仅文件");

    private Integer code;

    private String desc;

    KeyTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 根据code获取value
     * @param code
     * @return
     */
    public String getDesc(String code){
        for(KeyTypeEnum in : KeyTypeEnum.values()){
            if(code.equals(in.getCode())){
                return in.getDesc();
            }
        }
        return null;
    }
}
