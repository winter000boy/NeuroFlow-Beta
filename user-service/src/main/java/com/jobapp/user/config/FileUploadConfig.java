package com.jobapp.user.config;

import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jakarta.servlet.MultipartConfigElement;

/**
 * Configuration for file upload settings
 * Requirements: 9.4
 */
@Configuration
public class FileUploadConfig implements WebMvcConfigurer {
    
    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        
        // Set max file size to 10MB (to accommodate both resume and logo limits)
        factory.setMaxFileSize(DataSize.ofMegabytes(10));
        
        // Set max request size to 15MB
        factory.setMaxRequestSize(DataSize.ofMegabytes(15));
        
        return factory.createMultipartConfig();
    }
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Serve uploaded files statically
        registry.addResourceHandler("/files/**")
                .addResourceLocations("file:uploads/")
                .setCachePeriod(3600); // Cache for 1 hour
    }
}