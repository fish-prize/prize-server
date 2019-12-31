package com.ykm.server.common.exceptions;

import org.springframework.util.StringUtils;

public enum MessageCode {

    SYSTEM_ERROR("服务器内部错误", 500),
    PRIZE_ILLEGAL_0("抽奖不存在", 4000),
    PRIZE_ILLEGAL_1("秘钥不匹配", 4001),
    PRIZE_ILLEGAL_2("设备ID不存在", 4002),
    ;

    private String msg;

    private int msgCode;

    private MessageCode(String msg, int msgCode) {
        this.msg = msg;
        this.msgCode = msgCode;
    }

    public String msg() {
        return msg;
    }

    public int msgCode() {
        return msgCode;
    }

    public String toString() {
        return Integer.valueOf(this.msgCode).toString();
    }

    public static MessageCode getMessageCode(String msgKey) {
        if (StringUtils.isEmpty(msgKey)) {
            return null;
        }
        for (MessageCode msgCode : MessageCode.values()) {
            if (msgCode.msg().equals(msgKey)) {
                return msgCode;
            }
        }
        return null;
    }
}
