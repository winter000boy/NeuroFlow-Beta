package com.jobapp.notification.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.LocalDateTime;
import java.util.Map;

@Document(collection = "email_templates")
public class EmailTemplate {
    
    @Id
    private String id;
    
    @Indexed(unique = true)
    private String name;
    
    private String subject;
    private String htmlContent;
    private String textContent;
    private Map<String, String> defaultVariables;
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public EmailTemplate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.isActive = true;
    }
    
    public EmailTemplate(String name, String subject, String htmlContent, String textContent) {
        this();
        this.name = name;
        this.subject = subject;
        this.htmlContent = htmlContent;
        this.textContent = textContent;
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getSubject() {
        return subject;
    }
    
    public void setSubject(String subject) {
        this.subject = subject;
    }
    
    public String getHtmlContent() {
        return htmlContent;
    }
    
    public void setHtmlContent(String htmlContent) {
        this.htmlContent = htmlContent;
    }
    
    public String getTextContent() {
        return textContent;
    }
    
    public void setTextContent(String textContent) {
        this.textContent = textContent;
    }
    
    public Map<String, String> getDefaultVariables() {
        return defaultVariables;
    }
    
    public void setDefaultVariables(Map<String, String> defaultVariables) {
        this.defaultVariables = defaultVariables;
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public void setActive(boolean active) {
        isActive = active;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}