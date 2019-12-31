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
    AuthFail(4001,"权限校验失败"),
    PRIZE_PRODUCT_LOCK_FAIL(40002, "获取奖品锁失败"),
    PRIZE_GOODS_LOCK_FAIL(40003, "获取物品锁失败"),
    PRIZE_PRODUCT_STORE_NONE(40004, "奖品无库存"),
    PRIZE_GOODS_STORE_NONE(40004, "物品无库存"),
    PRIZE_REMAIN_NONE(40005, "无抽奖次数，明天再来吧！"),
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
