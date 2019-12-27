package com.ykm.server.common;

import com.ykm.server.common.exceptions.AuthorityException;
import com.ykm.server.controller.mng.dto.MngUserDto;
import com.ykm.server.entity.mng.MngUser;
import com.ykm.server.service.mng.MngUserService;
import com.ykm.server.utils.CookiesUtil;
import com.ykm.server.utils.JedisOpsUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class SecurityInterceptor extends HandlerInterceptorAdapter {
    private Logger log = LoggerFactory.getLogger(SecurityInterceptor.class);

    @Autowired
    JedisOpsUtil jedisOpsUtil;

    @Value("${redis.store.db}")
    Integer db;

    @Autowired
    MngUserService mngUserService;


    @Autowired
    CookiesUtil cookiesUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws AuthorityException {

        if (handler instanceof HandlerMethod) {

            HandlerMethod handlerMethod = (HandlerMethod) handler;

            Authority authority = handlerMethod.getBeanType().getAnnotation(Authority.class);
            if(authority == null) {
                authority = handlerMethod.getMethod().getAnnotation(Authority.class);
            }

            if( null == authority ) return true;

            if(authority.value().equalsIgnoreCase("mng")){
                String sid = cookiesUtil.getValue("sweet_mng_sid", request.getCookies());
                if(StringUtils.isEmpty(sid)){
                    throw new AuthorityException("未登录");
                }
                MngUserDto mngUserDto = jedisOpsUtil.hget(db, "mng_session", sid , MngUserDto.class);
                if(null == mngUserDto){
                    throw new AuthorityException("未登录");
                }
                MngUser mngUser = mngUserService.getById(mngUserDto.getId());
                if(null == mngUser){
                    throw new AuthorityException("用户不存在");
                }
                return true;
            }

            //非管理后台session检查
            //String sid = CookiesUtil.getValue("sweet_sid", request.getCookies());
            return true;
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
    }

}
