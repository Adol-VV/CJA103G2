package com.momento.member.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.momento.member.interceptor.LoginInterceptor;

@Configuration
public class WebConfig implements WebMvcConfigurer {

	@Autowired
	private LoginInterceptor loginInterceptor;

	@org.springframework.beans.factory.annotation.Value("${upload.path}")
	private String uploadPath;

	@Override
	public void addResourceHandlers(
			@io.micrometer.common.lang.NonNull org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/uploads/**")
				.addResourceLocations("file:" + uploadPath);
	}

	@Override
	public void addInterceptors(@io.micrometer.common.lang.NonNull InterceptorRegistry registry) {
		registry.addInterceptor(loginInterceptor)
				.addPathPatterns("/member/**")
				.excludePathPatterns(
						"/member/login",
						"/member/register",
						"/member/forgot-password",
						"/member/reset-password",
						"/uploads/**" // 排除圖片路徑
				);
	}
}
