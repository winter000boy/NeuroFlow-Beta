package com.jobapp.notification.dto;

import com.jobapp.notification.model.NotificationPreference.NotificationFrequency;

import java.time.LocalDateTime;
import java.util.Map;

public class NotificationPreferenceResponse {
    
    private String id;
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
    
    // Quiet hours
    private int quietHoursStart;
    private int quietHoursEnd;
    private String timezone;
    
    // Custom preferences
    private Map<String, Boolean> customPreferences;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public NotificationPreferenceResponse() {}
    
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
}