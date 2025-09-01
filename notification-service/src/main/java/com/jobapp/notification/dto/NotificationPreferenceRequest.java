package com.jobapp.notification.dto;

import com.jobapp.notification.model.NotificationPreference.NotificationFrequency;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.util.Map;

public class NotificationPreferenceRequest {
    
    @NotBlank(message = "User ID is required")
    private String userId;
    
    @Email(message = "Valid email is required")
    private String userEmail;
    
    private String userName;
    
    // Email notification preferences
    private boolean emailEnabled = true;
    private boolean registrationConfirmationEnabled = true;
    private boolean applicationStatusUpdateEnabled = true;
    private boolean jobApplicationReceivedEnabled = true;
    private boolean employerApprovalEnabled = true;
    private boolean systemNotificationEnabled = true;
    
    // Notification frequency settings
    private NotificationFrequency emailFrequency = NotificationFrequency.IMMEDIATE;
    
    // Quiet hours (when not to send notifications)
    @Min(value = 0, message = "Quiet hours start must be between 0 and 23")
    @Max(value = 23, message = "Quiet hours start must be between 0 and 23")
    private int quietHoursStart = 22;
    
    @Min(value = 0, message = "Quiet hours end must be between 0 and 23")
    @Max(value = 23, message = "Quiet hours end must be between 0 and 23")
    private int quietHoursEnd = 8;
    
    private String timezone = "UTC";
    
    // Custom preferences per notification type
    private Map<String, Boolean> customPreferences;
    
    public NotificationPreferenceRequest() {}
    
    public NotificationPreferenceRequest(String userId, String userEmail) {
        this.userId = userId;
        this.userEmail = userEmail;
    }
    
    // Getters and Setters
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
}