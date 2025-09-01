package com.jobapp.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO for content moderation requests
 * Requirements: 5.5
 */
public class ContentModerationRequest {
    
    @NotBlank(message = "Action is required")
    private String action; // APPROVE, REJECT, FLAG, REMOVE
    
    @NotBlank(message = "Reason is required")
    @Size(min = 10, max = 500, message = "Reason must be between 10 and 500 characters")
    private String reason;
    
    @Size(max = 1000, message = "Notes cannot exceed 1000 characters")
    private String notes;
    
    private Boolean sendNotification = true;
    
    // Constructors
    public ContentModerationRequest() {}
    
    public ContentModerationRequest(String action, String reason) {
        this.action = action;
        this.reason = reason;
    }
    
    public ContentModerationRequest(String action, String reason, String notes) {
        this.action = action;
        this.reason = reason;
        this.notes = notes;
    }
    
    // Getters and Setters
    public String getAction() {
        return action;
    }
    
    public void setAction(String action) {
        this.action = action;
    }
    
    public String getReason() {
        return reason;
    }
    
    public void setReason(String reason) {
        this.reason = reason;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public Boolean getSendNotification() {
        return sendNotification;
    }
    
    public void setSendNotification(Boolean sendNotification) {
        this.sendNotification = sendNotification;
    }
    
    @Override
    public String toString() {
        return "ContentModerationRequest{" +
                "action='" + action + '\'' +
                ", reason='" + reason + '\'' +
                ", notes='" + notes + '\'' +
                ", sendNotification=" + sendNotification +
                '}';
    }
}