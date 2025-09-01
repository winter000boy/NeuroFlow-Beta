package com.jobapp.notification.config;

import com.jobapp.notification.service.EmailTemplateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {
    
    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);
    
    @Autowired
    private EmailTemplateService templateService;
    
    @Override
    public void run(String... args) throws Exception {
        logger.info("Initializing default email templates...");
        try {
            templateService.initializeDefaultTemplates();
            logger.info("Default email templates initialized successfully");
        } catch (Exception e) {
            logger.error("Failed to initialize default email templates: {}", e.getMessage(), e);
        }
    }
}