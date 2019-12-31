package com.ykm.server.common;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {
    //只有这样使用，在SecurityInterceptor才能@依赖配置等
    @Bean
    public SecurityInterceptor securityInterceptor(){
        return new SecurityInterceptor();
    }
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(securityInterceptor()).excludePathPatterns("/health/*").excludePathPatterns("/static/*").addPathPatterns("/**");
    }

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        AntPathMatcher matcher = new AntPathMatcher();
        // 路由忽略大小写
        matcher.setCaseSensitive(false);
        configurer.setPathMatcher(matcher);
    }
}
