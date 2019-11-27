package com.zysl.aws.common.result;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Result<T> {

    /**
     * 返回代码
     */
    private int code;
    /**
     * 返回消息
     */
    private String msg;
    /**
     * 返回数据
     */
    private T data;

    public Result(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    /**
     * 成功时候的调用
     * */
    public static <T> Result<T> success(T data){
        return new  Result<T>(0,"success",data);
    }

    /**
     * 成功时候的调用
     * */
    public static Result success(){
        return new  Result(0,"success",null);
    }

    /**
     * 失败时候的调用
     * */
    public static <T> Result<T> error(CodeMsg codeMsg){
        if(codeMsg == null) {
            return null;
        }
        return new Result<T>(codeMsg.getCode(),codeMsg.getMsg(),null);
    }

    /**
     * 失败时候的调用
     * */
    public static <T> Result<T> error(String msg){
        if(msg == null) {
            return null;
        }
        return new Result<T>(-1, msg, null);
    }
}
