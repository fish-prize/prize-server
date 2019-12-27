package com.ykm.server.common.exceptions;


/**
 * Created by Coming on 2018/9/4.
 */
public class BusinessException extends Exception {

    private String message;

    private Integer code;

    @Deprecated
    public BusinessException(String message) {
        super(message);
        this.code = 500;
        this.message = message;
    }

    @Deprecated
    public BusinessException(MessageCode msgCode) {
        super(msgCode.msg());
        this.code = msgCode.msgCode();
        this.message = msgCode.msg();
    }

    public static BusinessException throw400(String msg) throws BusinessException {
        throw new BusinessException(400, msg);
    }

    public static BusinessException throw500(String msg) throws BusinessException {
        throw new BusinessException(500, msg);
    }

    public static BusinessException throwIt(MessageCode msgCode) throws BusinessException {
        throw new BusinessException(msgCode);
    }

    @Deprecated
    public BusinessException(Integer code, String msg) {
        super(msg);
        this.code = code;
        this.message = msg;
    }

    public String getMessage() {
        return this.message;
    }

    public Integer getCode() {
        return this.code;
    }

}
