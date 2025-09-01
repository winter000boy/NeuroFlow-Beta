package com.jobapp.application.model;

import java.time.LocalDateTime;

/**
 * Embedded document for tracking status changes in applications
 * Requirements: 4.2, 4.3
 */
public class StatusChange {
    
    private ApplicationStatus fromStatus;
    private ApplicationStatus toStatus;
    private String changedBy; // User ID who made the change
    private String reason; // Optional reason for the change
    private LocalDateTime changedAt;
    
    // Constructors
    public StatusChange() {}
    
    public StatusChange(ApplicationStatus fromStatus, ApplicationStatus toStatus, 
                       String changedBy, String reason, LocalDateTime changedAt) {
        this.fromStatus = fromStatus;
        this.toStatus = toStatus;
        this.changedBy = changedBy;
        this.reason = reason;
        this.changedAt = changedAt;
    }
    
    // Getters and Setters
    public ApplicationStatus getFromStatus() {
        return fromStatus;
    }
    
    public void setFromStatus(ApplicationStatus fromStatus) {
        this.fromStatus = fromStatus;
    }
    
    public ApplicationStatus getToStatus() {
        return toStatus;
    }
    
    public void setToStatus(ApplicationStatus toStatus) {
        this.toStatus = toStatus;
    }
    
    public String getChangedBy() {
        return changedBy;
    }
    
    public void setChangedBy(String changedBy) {
        this.changedBy = changedBy;
    }
    
    public String getReason() {
        return reason;
    }
    
    public void setReason(String reason) {
        this.reason = reason;
    }
    
    public LocalDateTime getChangedAt() {
        return changedAt;
    }
    
    public void setChangedAt(LocalDateTime changedAt) {
        this.changedAt = changedAt;
    }
    
    /**
     * Get a formatted description of the status change
     * @return formatted description
     */
    public String getFormattedDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append("Status changed from ");
        sb.append(fromStatus != null ? fromStatus.getDisplayName() : "Unknown");
        sb.append(" to ");
        sb.append(toStatus != null ? toStatus.getDisplayName() : "Unknown");
        
        if (reason != null && !reason.trim().isEmpty()) {
            sb.append(" - ").append(reason);
        }
        
        return sb.toString();
    }
    
    @Override
    public String toString() {
        return "StatusChange{" +
                "fromStatus=" + fromStatus +
                ", toStatus=" + toStatus +
                ", changedBy='" + changedBy + '\'' +
                ", reason='" + reason + '\'' +
                ", changedAt=" + changedAt +
                '}';
    }
}