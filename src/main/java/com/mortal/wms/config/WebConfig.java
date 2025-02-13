package com.mortal.wms.config;

import com.mortal.wms.interceptor.CurrentUserMethodArgumentResolver;
import com.mortal.wms.interceptor.JwtInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Autowired
    private CurrentUserMethodArgumentResolver currentUserMethodArgumentResolver;

    @Autowired
    private JwtInterceptor jwtInterceptor;


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/**")//拦截所有
                .excludePathPatterns("/doc.html/**")
                .excludePathPatterns("/error","/favicon.ico")
                .excludePathPatterns("/swagger-resources/**", "/v3/**", "/swagger-ui.html/**")
                .excludePathPatterns("**/login")
        ;
    }

    @Override //解决跨域问题
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") //对所有接口生效
                .allowedOrigins("*") //允许的域名
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowedHeaders("Authorization", "Content-Type") //允许的请求头
                .maxAge(3600 * 24);
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        //注册 CurrentUserMethodArgumentResolver
        argumentResolvers.add(currentUserMethodArgumentResolver);
    }
}
