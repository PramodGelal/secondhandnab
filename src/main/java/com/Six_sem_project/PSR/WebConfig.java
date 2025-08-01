package com.Six_sem_project.PSR;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Absolute path to your uploads/images folder
        String uploadPath = Paths.get("uploads/images").toAbsolutePath().toUri().toString();

        registry.addResourceHandler("/uploads/images/**")
                .addResourceLocations(uploadPath);
    }
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://127.0.0.1:3000")
                .allowedMethods("*")
                .allowCredentials(true);
    }
}