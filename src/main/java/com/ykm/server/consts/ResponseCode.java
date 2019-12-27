package com.ykm.server.consts;

/**
 * wenxy技有限公司
 * 功能：
 * 日期：2019/6/13-20:01
 * 版本       开发者     描述
 * 1.0.0     wenxy     ...
 */
public enum ResponseCode {
    OK(1,"OK"),
    Fail(-1,"Fail"),
    AuthFail(4001,"权限校验失败")
    ;
    private int code;
    private String msg;
    private ResponseCode(int code,String msg){
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
