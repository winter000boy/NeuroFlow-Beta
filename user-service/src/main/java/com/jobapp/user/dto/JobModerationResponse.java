package com.jobapp.user.dto;

import java.time.LocalDateTime;

/**
 * DTO for job moderation response
 * Requirements: 5.5
 */
public class JobModerationResponse {
    
    private String id;
    private String title;
    private String description;
    private String employerId;
    private String employerName;
    private String companyName;
    private String location;
    private String jobType;
    private String salaryRange;
    private Boolean isActive;
    private String moderationStatus; // PENDING, APPROVED, REJECTED, FLAGGED
    private String moderatedBy;
    private LocalDateTime moderatedAt;
    private String moderationReason;
    private String moderationNotes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer reportCount;
    private Boolean requiresAttention;
    
    // Constructors
    public JobModerationResponse() {}
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getEmployerId() {
        return employerId;
    }
    
    public void setEmployerId(String employerId) {
        this.employerId = employerId;
    }
    
    public String getEmployerName() {
        return employerName;
    }
    
    public void setEmployerName(String employerName) {
        this.employerName = employerName;
    }
    
    public String getCompanyName() {
        return companyName;
    }
    
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    public String getJobType() {
        return jobType;
    }
    
    public void setJobType(String jobType) {
        this.jobType = jobType;
    }
    
    public String getSalaryRange() {
        return salaryRange;
    }
    
    public void setSalaryRange(String salaryRange) {
        this.salaryRange = salaryRange;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    public String getModerationStatus() {
        return moderationStatus;
    }
    
    public void setModerationStatus(String moderationStatus) {
        this.moderationStatus = moderationStatus;
    }
    
    public String getModeratedBy() {
        return moderatedBy;
    }
    
    public void setModeratedBy(String moderatedBy) {
        this.moderatedBy = moderatedBy;
    }
    
    public LocalDateTime getModeratedAt() {
        return moderatedAt;
    }
    
    public void setModeratedAt(LocalDateTime moderatedAt) {
        this.moderatedAt = moderatedAt;
    }
    
    public String getModerationReason() {
        return moderationReason;
    }
    
    public void setModerationReason(String moderationReason) {
        this.moderationReason = moderationReason;
    }
    
    public String getModerationNotes() {
        return moderationNotes;
    }
    
    public void setModerationNotes(String moderationNotes) {
        this.moderationNotes = moderationNotes;
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
    
    public Integer getReportCount() {
        return reportCount;
    }
    
    public void setReportCount(Integer reportCount) {
        this.reportCount = reportCount;
    }
    
    public Boolean getRequiresAttention() {
        return requiresAttention;
    }
    
    public void setRequiresAttention(Boolean requiresAttention) {
        this.requiresAttention = requiresAttention;
    }
    
    @Override
    public String toString() {
        return "JobModerationResponse{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", companyName='" + companyName + '\'' +
                ", moderationStatus='" + moderationStatus + '\'' +
                ", requiresAttention=" + requiresAttention +
                ", createdAt=" + createdAt +
                '}';
    }
}