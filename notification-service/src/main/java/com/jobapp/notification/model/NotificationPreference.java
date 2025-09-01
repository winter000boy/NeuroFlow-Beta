package com.jobapp.notification.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.HashMap;

@Document(collection = "notification_preferences")
public class NotificationPreference {
    
    @Id
    private String id;
    
    @Indexed(unique = true)
    private String userId;
    
    private String userEmail;
    private String userName;
    
    // Email notification preferences
    private boolean emailEnabled;
    private boolean registrationConfirmationEnabled;
    private boolean applicationStatusUpdateEnabled;
    private boolean jobApplicationReceivedEnabled;
    private boolean employerApprovalEnabled;
    private boolean systemNotificationEnabled;
    
    // Notification frequency settings
    private NotificationFrequency emailFrequency;
    
    // Quiet hours (when not to send notifications)
    private int quietHoursStart; // 0-23 (hour of day)
    private int quietHoursEnd;   // 0-23 (hour of day)
    private String timezone;
    
    // Custom preferences per notification type
    private Map<String, Boolean> customPreferences;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public enum NotificationFrequency {
        IMMEDIATE,
        HOURLY,
        DAILY,
        WEEKLY
    }
    
    public NotificationPreference() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.customPreferences = new HashMap<>();
        
        // Default settings - all notifications enabled
        this.emailEnabled = true;
        this.registrationConfirmationEnabled = true;
        this.applicationStatusUpdateEnabled = true;
        this.jobApplicationReceivedEnabled = true;
        this.employerApprovalEnabled = true;
        this.systemNotificationEnabled = true;
        this.emailFrequency = NotificationFrequency.IMMEDIATE;
        
        // Default quiet hours: 10 PM to 8 AM
        this.quietHoursStart = 22;
        this.quietHoursEnd = 8;
        this.timezone = "UTC";
    }
    
    public NotificationPreference(String userId, String userEmail) {
        this();
        this.userId = userId;
        this.userEmail = userEmail;
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getUserEmail() {
        return userEmail;
    }
    
    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
    
    public String getUserName() {
        return userName;
    }
    
    public void setUserName(String userName) {
        this.userName = userName;
    }
    
    public boolean isEmailEnabled() {
        return emailEnabled;
    }
    
    public void setEmailEnabled(boolean emailEnabled) {
        this.emailEnabled = emailEnabled;
    }
    
    public boolean isRegistrationConfirmationEnabled() {
        return registrationConfirmationEnabled;
    }
    
    public void setRegistrationConfirmationEnabled(boolean registrationConfirmationEnabled) {
        this.registrationConfirmationEnabled = registrationConfirmationEnabled;
    }
    
    public boolean isApplicationStatusUpdateEnabled() {
        return applicationStatusUpdateEnabled;
    }
    
    public void setApplicationStatusUpdateEnabled(boolean applicationStatusUpdateEnabled) {
        this.applicationStatusUpdateEnabled = applicationStatusUpdateEnabled;
    }
    
    public boolean isJobApplicationReceivedEnabled() {
        return jobApplicationReceivedEnabled;
    }
    
    public void setJobApplicationReceivedEnabled(boolean jobApplicationReceivedEnabled) {
        this.jobApplicationReceivedEnabled = jobApplicationReceivedEnabled;
    }
    
    public boolean isEmployerApprovalEnabled() {
        return employerApprovalEnabled;
    }
    
    public void setEmployerApprovalEnabled(boolean employerApprovalEnabled) {
        this.employerApprovalEnabled = employerApprovalEnabled;
    }
    
    public boolean isSystemNotificationEnabled() {
        return systemNotificationEnabled;
    }
    
    public void setSystemNotificationEnabled(boolean systemNotificationEnabled) {
        this.systemNotificationEnabled = systemNotificationEnabled;
    }
    
    public NotificationFrequency getEmailFrequency() {
        return emailFrequency;
    }
    
    public void setEmailFrequency(NotificationFrequency emailFrequency) {
        this.emailFrequency = emailFrequency;
    }
    
    public int getQuietHoursStart() {
        return quietHoursStart;
    }
    
    public void setQuietHoursStart(int quietHoursStart) {
        this.quietHoursStart = quietHoursStart;
    }
    
    public int getQuietHoursEnd() {
        return quietHoursEnd;
    }
    
    public void setQuietHoursEnd(int quietHoursEnd) {
        this.quietHoursEnd = quietHoursEnd;
    }
    
    public String getTimezone() {
        return timezone;
    }
    
    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }
    
    public Map<String, Boolean> getCustomPreferences() {
        return customPreferences;
    }
    
    public void setCustomPreferences(Map<String, Boolean> customPreferences) {
        this.customPreferences = customPreferences;
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
    
    // Helper methods
    public boolean isNotificationTypeEnabled(EmailNotification.NotificationType type) {
        switch (type) {
            case REGISTRATION_CONFIRMATION:
                return registrationConfirmationEnabled;
            case APPLICATION_STATUS_UPDATE:
                return applicationStatusUpdateEnabled;
            case JOB_APPLICATION_RECEIVED:
                return jobApplicationReceivedEnabled;
            case EMPLOYER_APPROVAL:
                return employerApprovalEnabled;
            case SYSTEM_NOTIFICATION:
                return systemNotificationEnabled;
            default:
                return true;
        }
    }
    
    public void updateTimestamp() {
        this.updatedAt = LocalDateTime.now();
    }
}