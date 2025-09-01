package com.jobapp.notification.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.StringTemplateResolver;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
@EnableScheduling
public class EmailConfig {
    
    @Bean
    public TemplateEngine emailTemplateEngine() {
        TemplateEngine templateEngine = new TemplateEngine();
        
        // String template resolver for processing email templates stored in database
        StringTemplateResolver stringTemplateResolver = new StringTemplateResolver();
        stringTemplateResolver.setTemplateMode(TemplateMode.HTML);
        stringTemplateResolver.setCacheable(false); // Disable caching for dynamic templates
        
        templateEngine.setTemplateResolver(stringTemplateResolver);
        
        return templateEngine;
    }
    
    @Bean(name = "emailTaskExecutor")
    public Executor emailTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("Email-");
        executor.initialize();
        return executor;
    }
}