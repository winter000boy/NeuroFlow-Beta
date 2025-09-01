package com.jobapp.application.dto;

import com.jobapp.application.model.ApplicationStatus;
import com.jobapp.application.model.StatusChange;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Response DTO for application data
 * Requirements: 2.3, 2.4, 4.1, 4.2
 */
public class ApplicationResponse {
    
    private String id;
    private String candidateId;
    private String jobId;
    private String employerId;
    private ApplicationStatus status;
    private String coverLetter;
    private String resumeUrl;
    private List<String> additionalDocuments;
    private String notes;
    private LocalDateTime interviewScheduled;
    private String interviewNotes;
    private String rejectionReason;
    private Double salaryOffered;
    private String offerDetails;
    private LocalDateTime offerExpiresAt;
    private LocalDateTime appliedAt;
    private LocalDateTime updatedAt;
    private List<StatusChange> statusHistory;
    
    // Job information (populated when needed)
    private String jobTitle;
    private String companyName;
    
    // Candidate information (populated when needed)
    private String candidateName;
    private String candidateEmail;
    
    // Constructors
    public ApplicationResponse() {}
    
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
    
    public String getJobId() {
        return jobId;
    }
    
    public void setJobId(String jobId) {
        this.jobId = jobId;
    }
    
    public String getEmployerId() {
        return employerId;
    }
    
    public void setEmployerId(String employerId) {
        this.employerId = employerId;
    }
    
    public ApplicationStatus getStatus() {
        return status;
    }
    
    public void setStatus(ApplicationStatus status) {
        this.status = status;
    }
    
    public String getCoverLetter() {
        return coverLetter;
    }
    
    public void setCoverLetter(String coverLetter) {
        this.coverLetter = coverLetter;
    }
    
    public String getResumeUrl() {
        return resumeUrl;
    }
    
    public void setResumeUrl(String resumeUrl) {
        this.resumeUrl = resumeUrl;
    }
    
    public List<String> getAdditionalDocuments() {
        return additionalDocuments;
    }
    
    public void setAdditionalDocuments(List<String> additionalDocuments) {
        this.additionalDocuments = additionalDocuments;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
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
    
    public String getRejectionReason() {
        return rejectionReason;
    }
    
    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
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
    
    public List<StatusChange> getStatusHistory() {
        return statusHistory;
    }
    
    public void setStatusHistory(List<StatusChange> statusHistory) {
        this.statusHistory = statusHistory;
    }
    
    public String getJobTitle() {
        return jobTitle;
    }
    
    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }
    
    public String getCompanyName() {
        return companyName;
    }
    
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
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
    
    /**
     * Check if application is in a final state
     * @return true if application is hired or rejected
     */
    public boolean isFinalStatus() {
        return status != null && status.isFinal();
    }
    
    /**
     * Check if application is active (not in final state)
     * @return true if application is still being processed
     */
    public boolean isActive() {
        return !isFinalStatus();
    }
    
    /**
     * Check if offer has expired
     * @return true if offer has expired
     */
    public boolean isOfferExpired() {
        return offerExpiresAt != null && offerExpiresAt.isBefore(LocalDateTime.now());
    }
    
    @Override
    public String toString() {
        return "ApplicationResponse{" +
                "id='" + id + '\'' +
                ", candidateId='" + candidateId + '\'' +
                ", jobId='" + jobId + '\'' +
                ", employerId='" + employerId + '\'' +
                ", status=" + status +
                ", jobTitle='" + jobTitle + '\'' +
                ", companyName='" + companyName + '\'' +
                ", candidateName='" + candidateName + '\'' +
                ", appliedAt=" + appliedAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}