package com.jobapp.user.dto;

import java.time.LocalDateTime;

/**
 * DTO for application moderation response
 * Requirements: 5.5
 */
public class ApplicationModerationResponse {
    
    private String id;
    private String candidateId;
    private String candidateName;
    private String candidateEmail;
    private String jobId;
    private String jobTitle;
    private String employerId;
    private String companyName;
    private String status; // APPLIED, IN_REVIEW, HIRED, REJECTED
    private String moderationStatus; // PENDING, APPROVED, REJECTED, FLAGGED
    private String moderatedBy;
    private LocalDateTime moderatedAt;
    private String moderationReason;
    private String moderationNotes;
    private LocalDateTime appliedAt;
    private LocalDateTime updatedAt;
    private Integer reportCount;
    private Boolean requiresAttention;
    private String resumeUrl;
    private String coverLetter;
    
    // Constructors
    public ApplicationModerationResponse() {}
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getCandidateId() {
        return candidateId;
    }
    
    public void setCandidateId(String candidateId) {
        this.candidateId = candidateId;
    }
    
    public String getCandidateName() {
        return candidateName;
    }
    
    public void setCandidateName(String candidateName) {
        this.candidateName = candidateName;
    }
    
    public String getCandidateEmail() {
        return candidateEmail;
    }
    
    public void setCandidateEmail(String candidateEmail) {
        this.candidateEmail = candidateEmail;
    }
    
    public String getJobId() {
        return jobId;
    }
    
    public void setJobId(String jobId) {
        this.jobId = jobId;
    }
    
    public String getJobTitle() {
        return jobTitle;
    }
    
    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }
    
    public String getEmployerId() {
        return employerId;
    }
    
    public void setEmployerId(String employerId) {
        this.employerId = employerId;
    }
    
    public String getCompanyName() {
        return companyName;
    }
    
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
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
    
    public LocalDateTime getAppliedAt() {
        return appliedAt;
    }
    
    public void setAppliedAt(LocalDateTime appliedAt) {
        this.appliedAt = appliedAt;
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
    
    public String getResumeUrl() {
        return resumeUrl;
    }
    
    public void setResumeUrl(String resumeUrl) {
        this.resumeUrl = resumeUrl;
    }
    
    public String getCoverLetter() {
        return coverLetter;
    }
    
    public void setCoverLetter(String coverLetter) {
        this.coverLetter = coverLetter;
    }
    
    @Override
    public String toString() {
        return "ApplicationModerationResponse{" +
                "id='" + id + '\'' +
                ", candidateName='" + candidateName + '\'' +
                ", jobTitle='" + jobTitle + '\'' +
                ", companyName='" + companyName + '\'' +
                ", status='" + status + '\'' +
                ", moderationStatus='" + moderationStatus + '\'' +
                ", requiresAttention=" + requiresAttention +
                ", appliedAt=" + appliedAt +
                '}';
    }
}