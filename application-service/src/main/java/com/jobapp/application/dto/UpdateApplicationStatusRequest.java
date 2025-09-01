package com.jobapp.application.dto;

import com.jobapp.application.model.ApplicationStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * Request DTO for updating application status
 * Requirements: 4.1, 4.2, 4.3
 */
public class UpdateApplicationStatusRequest {
    
    @NotNull(message = "Status is required")
    private ApplicationStatus status;
    
    @Size(max = 2000, message = "Notes must not exceed 2000 characters")
    private String notes;
    
    @Size(max = 500, message = "Reason must not exceed 500 characters")
    private String reason;
    
    // For interview scheduling
    private LocalDateTime interviewScheduled;
    private String interviewNotes;
    
    // For job offers
    private Double salaryOffered;
    private String offerDetails;
    private LocalDateTime offerExpiresAt;
    
    // For rejection
    private String rejectionReason;
    
    // Constructors
    public UpdateApplicationStatusRequest() {}
    
    public UpdateApplicationStatusRequest(ApplicationStatus status) {
        this.status = status;
    }
    
    public UpdateApplicationStatusRequest(ApplicationStatus status, String reason) {
        this.status = status;
        this.reason = reason;
    }
    
    // Getters and Setters
    public ApplicationStatus getStatus() {
        return status;
    }
    
    public void setStatus(ApplicationStatus status) {
        this.status = status;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public String getReason() {
        return reason;
    }
    
    public void setReason(String reason) {
        this.reason = reason;
    }
    
    public LocalDateTime getInterviewScheduled() {
        return interviewScheduled;
    }
    
    public void setInterviewScheduled(LocalDateTime interviewScheduled) {
        this.interviewScheduled = interviewScheduled;
    }
    
    public String getInterviewNotes() {
        return interviewNotes;
    }
    
    public void setInterviewNotes(String interviewNotes) {
        this.interviewNotes = interviewNotes;
    }
    
    public Double getSalaryOffered() {
        return salaryOffered;
    }
    
    public void setSalaryOffered(Double salaryOffered) {
        this.salaryOffered = salaryOffered;
    }
    
    public String getOfferDetails() {
        return offerDetails;
    }
    
    public void setOfferDetails(String offerDetails) {
        this.offerDetails = offerDetails;
    }
    
    public LocalDateTime getOfferExpiresAt() {
        return offerExpiresAt;
    }
    
    public void setOfferExpiresAt(LocalDateTime offerExpiresAt) {
        this.offerExpiresAt = offerExpiresAt;
    }
    
    public String getRejectionReason() {
        return rejectionReason;
    }
    
    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }
    
    @Override
    public String toString() {
        return "UpdateApplicationStatusRequest{" +
                "status=" + status +
                ", notes='" + (notes != null ? "[PRESENT]" : null) + '\'' +
                ", reason='" + reason + '\'' +
                ", interviewScheduled=" + interviewScheduled +
                ", salaryOffered=" + salaryOffered +
                ", offerExpiresAt=" + offerExpiresAt +
                ", rejectionReason='" + rejectionReason + '\'' +
                '}';
    }
}