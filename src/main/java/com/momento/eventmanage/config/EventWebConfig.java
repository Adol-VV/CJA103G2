package com.momento.eventmanage.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
public class EventWebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Map /uploads/** to the uploads directory in the project root
        // This is required for Event Banner images to be previewable
        String uploadPath = "file:" + Paths.get("uploads").toAbsolutePath().toString() + "/";

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(uploadPath);
    }
}
