package com.momento.organizer.config;

import com.momento.organizer.interceptor.OrganizerLoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class OrganizerWebConfig implements WebMvcConfigurer {

    @Autowired
    private OrganizerLoginInterceptor logininterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry){
        registry.addInterceptor(logininterceptor)
                .addPathPatterns("/organizer/**")
                .excludePathPatterns(
                        "/organizer/apply",
                        "/organizer/login",
                        "/organizer/forgot-password",
                        "/organizer/reset-password"
                );
    }
}
