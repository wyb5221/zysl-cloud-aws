package com.zysl.aws.common.result;

import lombok.Getter;

@Getter
public class CodeMsg {

    //通用信息
    /** 成功 */
    public static CodeMsg SUCCESS = new CodeMsg(0, "success");
    /** 服务器异常 */
    public static CodeMsg SERVER_ERROR = new CodeMsg(500000,"服务端异常:%s");
    /** 参数校验异常 */
    public static CodeMsg BIND_ERROR = new CodeMsg(500001,"参数校验异常:%s");
    /** 传入参数为空 */
    public static CodeMsg PARAMS_IS_EMPTY = new CodeMsg(500002,"传入参数为空:%s");
    /** 参数解析失败 */
    public static CodeMsg PARAMS_PARSE_ERROR = new CodeMsg(500003,"参数解析失败:%s");


    //登录模块 5001XX
    /** 账号不存在 */
    public static CodeMsg ACCOUNT_NOT_EXIST = new CodeMsg(500100,"账号不存在:%s");
    /** 账号已存在 */
    public static CodeMsg ACCOUNT_EXISTS = new CodeMsg(500101,"账号已存在:%s");
    /** 密码不正确 */
    public static CodeMsg PASSWORD_ERROR = new CodeMsg(500102,"密码不正确:%s");

    //权限模块 5002XX

    //云中间件模块 5003XX
    /** 云存储异常 */
    public static CodeMsg OSS_ERROR = new CodeMsg(500300,"云存储异常:%s");


    /**
     * 参数为空
     */
    public static CodeMsg PARAM_EMPTY = new CodeMsg(400001,"参数：%s不能为空！");
    /**
     * 表中已经存在该字段
     */
    public static CodeMsg FIELD_EXIST = new CodeMsg(400002,"%s");

    /**
     * 状态为已发布
     */
    public static CodeMsg PUBLIC_STATUS = new CodeMsg(400003,"状态为已发布，不允许修改或删除操作！");

    //执行数据库操作异常模块  5004XX

    /**执行新增时数据库异常*/
    public static CodeMsg MYSQL_INSERT_EXCEPTION = new CodeMsg(500401,"执行新增时数据库异常:%s");

    /**执行修改时数据库异常*/
    public static CodeMsg MYSQL_UPDATE_EXCEPTION = new CodeMsg(500402,"执行修改时数据库异常:%s");

    /**执行删除时数据库异常*/
    public static CodeMsg MYSQL_DELETE_EXCEPTION = new CodeMsg(500403,"执行删除时数据库异常:%s");

    /**执行查询时数据库异常*/
    public static CodeMsg MYSQL_QUERY_EXCEPTION = new CodeMsg(500404,"执行查询时数据库异常:%s");

    /**执行批量插入时插入条数小于入参条数*/
    public static CodeMsg MYSQL_BATCH_INSERT_EXCEPTION = new CodeMsg(500405,"批量插入数量不对:%s");

    /**数据状态不允许进行某些操作*/
    public static CodeMsg STATUS_ERROR = new CodeMsg(500406,"%s");

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
