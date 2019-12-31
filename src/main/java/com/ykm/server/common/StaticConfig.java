package com.ykm.server.common;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * wenxy
 * 功能：
 * 日期：2019/12/29-20:17
 * 版本       开发者     描述
 * 1.0.0     wenxy     ...
 */
@Configuration
public class StaticConfig implements WebMvcConfigurer {
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/js/**").addResourceLocations("classpath:/static/js/");
        registry.addResourceHandler("/css/**").addResourceLocations("classpath:/static/css/");
        // registry.addResourceHandler("/mng/**").addResourceLocations("classpath:/static/mng/");
        registry.addResourceHandler("/images/**").addResourceLocations("classpath:/static/images/");
    }
}
