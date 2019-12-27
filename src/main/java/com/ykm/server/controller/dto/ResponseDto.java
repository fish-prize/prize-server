package com.ykm.server.controller.dto;

import com.ykm.server.consts.ResponseCode;

/**
 * 技有限公司
 * 功能：
 * 日期：2019/6/13-19:56
 * 版本       开发者     描述
 * 1.0.0     wenxy     ...
 */
public class ResponseDto<T> {

    private T data;
    private int code;
    private String msg;

    public ResponseDto(T t,int code,String msg){
        this.data = t;
        this.code = code;
        this.msg = msg;
    }

    public ResponseDto(T t, ResponseCode responseCode){
        this.data = t;
        this.code = responseCode.getCode();
        this.msg = responseCode.getMsg();
    }

    public T getData() {
        return data;
    }

    public void setData(T t) {
        this.data = t;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

}
