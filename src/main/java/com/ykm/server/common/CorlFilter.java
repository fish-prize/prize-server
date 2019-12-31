package com.ykm.server.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * wenxy
 * 功能：
 * 日期：2019/12/29-20:59
 * 版本       开发者     描述
 * 1.0.0     wenxy     ...
 */
@Component
public class CorlFilter implements Filter {

    @Value("${prizeMngDomain}")
    private String prizeMngDomain;
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletResponse httpResponse = (HttpServletResponse)response;
        // 设置请求头，允许ajax跨域请求
        HttpServletRequest httpRequest = (HttpServletRequest)request;
        httpResponse.setHeader("Access-Control-Allow-Credentials", "true");
        httpResponse.setHeader("Access-Control-Allow-Headers", httpRequest.getHeader("Access-Control-Request-Headers"));
        httpResponse.setHeader("Access-Control-Allow-Origin", httpRequest.getHeader("Origin"));
        httpResponse.setHeader("Access-Control-Allow-Methods", "GET,POST,DELETE,PUT,OPTIONS");
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }
}
