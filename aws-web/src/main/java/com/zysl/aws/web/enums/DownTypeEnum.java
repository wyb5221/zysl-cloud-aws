package com.zysl.aws.web.enums;

import lombok.Getter;

/**
 * 0 默认下载二进制流
 * 1 下载文件流
 */
@Getter
public enum DownTypeEnum {

    NOCOVER("0", "默认,下载文件流"),
    COVER("1", "base64进制的String");

    private String code;

    private String desc;

    DownTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 根据code获取value
     * @param code
     * @return
     */
    public String getDesc(String code){
        for(DownTypeEnum in : DownTypeEnum.values()){
            if(code.equals(in.getCode())){
                return in.getDesc();
            }
        }
        return null;
    }
}
