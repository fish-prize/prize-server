package com.ykm.server.common.exceptions;

public class AuthorityException extends Exception {
    public AuthorityException(){
        super("无权限");
    }

    public AuthorityException(String msg){
        super(msg);
    }
}
