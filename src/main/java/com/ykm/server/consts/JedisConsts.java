package com.ykm.server.consts;

public class JedisConsts {

    public enum SetParams {

        NX("NX"), // 只在键不存在时， 才对键进行设置操作
        XX("XX"), // 只在键已经存在时， 才对键进行设置操作

        EX("EX"), // 将键的过期时间设置为 seconds 秒
        PX("PX"); // 将键的过期时间设置为 milliseconds 毫秒

        SetParams(String param) {
            this.param = param;
        }

        private String param;

        public String getParam() {
            return param;
        }

    }
}
