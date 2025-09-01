package com.jobapp.notification.dto;

import com.jobapp.notification.model.EmailNotification.NotificationType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.Map;

public class EmailRequest {
    
    @NotBlank(message = "Recipient email is required")
    @Email(message = "Invalid email format")
    private String recipientEmail;
    
    private String recipientName;
    
    @NotBlank(message = "Template name is required")
    private String templateName;
    
    @NotNull(message = "Notification type is required")
    private NotificationType type;
    
    private Map<String, Object> templateVariables;
    
    private LocalDateTime scheduledAt;
    
    // Optional fields for tracking
    private String userId;
    private String jobId;
    private String applicationId;
    
    public EmailRequest() {}
    
    public EmailRequest(String recipientEmail, String templateName, NotificationType type) {
        this.recipientEmail = recipientEmail;
        this.templateName = templateName;
        this.type = type;
    }
    
    // Getters and Setters
    public String getRecipientEmail() {
        return recipientEmail;
    }
    
    public void setRecipientEmail(String recipientEmail) {
        this.recipientEmail = recipientEmail;
    }
    
    public String getRecipientName() {
        return recipientName;
    }
    
    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }
    
    public String getTemplateName() {
        return templateName;
    }
    
    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }
    
    public NotificationType getType() {
        return type;
    }
    
    public void setType(NotificationType type) {
        this.type = type;
    }
    
    public Map<String, Object> getTemplateVariables() {
        return templateVariables;
    }
    
    public void setTemplateVariables(Map<String, Object> templateVariables) {
        this.templateVariables = templateVariables;
    }
    
    public LocalDateTime getScheduledAt() {
        return scheduledAt;
    }
    
    public void setScheduledAt(LocalDateTime scheduledAt) {
        this.scheduledAt = scheduledAt;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getJobId() {
        return jobId;
    }
    
    public void setJobId(String jobId) {
        this.jobId = jobId;
    }
    
    public String getApplicationId() {
        return applicationId;
    }
    
    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }
}