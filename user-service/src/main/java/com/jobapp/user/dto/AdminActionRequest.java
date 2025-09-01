package com.jobapp.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO for admin action requests (approve, reject, block, etc.)
 * Requirements: 5.2, 5.3
 */
public class AdminActionRequest {
    
    @NotBlank(message = "Reason is required")
    @Size(min = 10, max = 500, message = "Reason must be between 10 and 500 characters")
    private String reason;
    
    @Size(max = 1000, message = "Notes cannot exceed 1000 characters")
    private String notes;
    
    private Boolean sendNotification = true;
    
    // Constructors
    public AdminActionRequest() {}
    
    public AdminActionRequest(String reason) {
        this.reason = reason;
    }
    
    public AdminActionRequest(String reason, String notes) {
        this.reason = reason;
        this.notes = notes;
    }
    
    public AdminActionRequest(String reason, String notes, Boolean sendNotification) {
        this.reason = reason;
        this.notes = notes;
        this.sendNotification = sendNotification;
    }
    
    // Getters and Setters
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
        return "AdminActionRequest{" +
                "reason='" + reason + '\'' +
                ", notes='" + notes + '\'' +
                ", sendNotification=" + sendNotification +
                '}';
    }
}