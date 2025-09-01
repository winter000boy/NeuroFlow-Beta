package com.jobapp.notification.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Document(collection = "email_notifications")
public class EmailNotification {
    
    @Id
    private String id;
    
    @Indexed
    private String recipientEmail;
    
    private String recipientName;
    private String subject;
    private String htmlContent;
    private String textContent;
    private String templateName;
    private Map<String, Object> templateVariables;
    
    @Indexed
    private NotificationStatus status;
    
    @Indexed
    private NotificationType type;
    
    private String errorMessage;
    private int retryCount;
    private int maxRetries;
    
    @Indexed
    private LocalDateTime scheduledAt;
    
    private LocalDateTime sentAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Related entity information
    private String userId;
    private String jobId;
    private String applicationId;
    
    public EmailNotification() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.status = NotificationStatus.PENDING;
        this.retryCount = 0;
        this.maxRetries = 3;
        this.scheduledAt = LocalDateTime.now();
    }
    
    public enum NotificationStatus {
        PENDING,
        SENT,
        FAILED,
        CANCELLED
    }
    
    public enum NotificationType {
        REGISTRATION_CONFIRMATION,
        APPLICATION_STATUS_UPDATE,
        JOB_APPLICATION_RECEIVED,
        EMPLOYER_APPROVAL,
        PASSWORD_RESET,
        SYSTEM_NOTIFICATION
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
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
    
    public String getTemplateName() {
        return templateName;
    }
    
    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }
    
    public Map<String, Object> getTemplateVariables() {
        return templateVariables;
    }
    
    public void setTemplateVariables(Map<String, Object> templateVariables) {
        this.templateVariables = templateVariables;
    }
    
    public NotificationStatus getStatus() {
        return status;
    }
    
    public void setStatus(NotificationStatus status) {
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }
    
    public NotificationType getType() {
        return type;
    }
    
    public void setType(NotificationType type) {
        this.type = type;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    
    public int getRetryCount() {
        return retryCount;
    }
    
    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }
    
    public int getMaxRetries() {
        return maxRetries;
    }
    
    public void setMaxRetries(int maxRetries) {
        this.maxRetries = maxRetries;
    }
    
    public LocalDateTime getScheduledAt() {
        return scheduledAt;
    }
    
    public void setScheduledAt(LocalDateTime scheduledAt) {
        this.scheduledAt = scheduledAt;
    }
    
    public LocalDateTime getSentAt() {
        return sentAt;
    }
    
    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
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