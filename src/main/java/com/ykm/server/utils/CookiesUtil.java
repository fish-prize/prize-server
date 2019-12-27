package com.ykm.server.utils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;

/**
 * wenxy技有限公司
 * 功能：
 * 日期：2019/7/1-14:03
 * 版本       开发者     描述
 * 1.0.0     wenxy     ...
 */
@Component
public class CookiesUtil {

    @Value("${domain}")
    private String domain;

    public  String getValue(String key, Cookie[] cookies){
        if(cookies == null || cookies.length ==0 ) return null;
        for(Cookie cookie : cookies){
            String name = cookie.getName();
            if(name.equalsIgnoreCase(key)){
                return cookie.getValue();
            }
        }
        return null;
    }

    public  Cookie genCookie(String name, String value, String domain){
        return genCookie(name, value, null, null, domain);
    }

    public  Cookie genCookie(String name, String value){
        return genCookie(name, value, null, null, null);
    }

    public  Cookie genCookie(String name, String value, String path, Integer maxAge, String domain){
        if(StringUtils.isEmpty(name)) return null;
        Cookie cookie = new Cookie(name,value);
        if(StringUtils.isEmpty(path)) {
            cookie.setPath("/");
        }else{
            cookie.setPath(path);
        }
        if(maxAge == null) {
            cookie.setMaxAge(Integer.MAX_VALUE);
        }else{
            cookie.setMaxAge(maxAge);
        }

        if(StringUtils.isEmpty(domain)) {
            cookie.setDomain(this.domain);
        }else{
            cookie.setDomain(domain);
        }

        return cookie;
    }

}
