package com.zysl.aws.common.result;

import lombok.Getter;

@Getter
public class CodeMsg {

    //通用信息
    /** 成功 */
    public static CodeMsg SUCCESS = new CodeMsg(200, "success");
    /** 请求参数错误 */
    public static CodeMsg ILLEGAL_PARAMETER = new CodeMsg(400,"请求参数错误");
    /** 请求需要身份认证 */
    public static CodeMsg AUTHFAILED = new CodeMsg(401,"请求需要身份认证");
    /** 请求被禁止 */
    public static CodeMsg FORBIDDEN = new CodeMsg(403,"请求被禁止");
    /** 找不到资源 */
    public static CodeMsg NOT_EXISTED = new CodeMsg(404,"找不到资源");
    /** 服务器内部错误 */
    public static CodeMsg FAILED = new CodeMsg(500,"服务器内部错误");


    /** 返回码 */
    private int code;
    /** 返回信息 */
    private String msg;
    /** 无参构造方法 */
    private CodeMsg() {
    }
    /** 构造方法 */
    private CodeMsg(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
    /** 填充动态参数 */
    public CodeMsg fillArgs(Object... args) {
        int code = this.code;
        String message = String.format(this.msg, args);
        return new CodeMsg(code, message);
    }

}
