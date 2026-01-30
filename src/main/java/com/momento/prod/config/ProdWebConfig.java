package com.momento.prod.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ProdWebConfig implements WebMvcConfigurer{
	    @Override
	    public void addResourceHandlers(ResourceHandlerRegistry registry) {
	        // 1. 網址開頭匹配：/product/
	        registry.addResourceHandler("/product/**")
	                // 2. 實體目錄：注意 file: 前綴與最後的斜線
	                .addResourceLocations("file:///C:/momento-uploads/product/");
	}
}
