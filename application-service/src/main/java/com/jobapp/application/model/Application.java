package com.jobapp.application.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Application entity representing job applications in the system
 * Requirements: 2.3, 2.5, 4.1, 4.2, 4.3
 */
@Document(collection = "applications")
@CompoundIndexes({
    @CompoundIndex(name = "candidate_job_idx", def = "{'candidate_id': 1, 'job_id': 1}", unique = true),
    @CompoundIndex(name = "employer_status_idx", def = "{'employer_id': 1, 'status': 1}"),
    @CompoundIndex(name = "job_status_idx", def = "{'job_id': 1, 'status': 1}"),
    @CompoundIndex(name = "candidate_status_idx", def = "{'candidate_id': 1, 'status': 1}")
})
public class Application {
    
    @Id
    private String id;
    
    @NotBlank(message = "Candidate ID is required")
    @Indexed
    @Field("candidate_id")
    private String candidateId;
    
    @NotBlank(message = "Job ID is required")
    @Indexed
    @Field("job_id")
    private String jobId;
    
    @NotBlank(message = "Employer ID is required")
    @Indexed
    @Field("employer_id")
    private String employerId;
    
    @NotNull(message = "Application status is required")
    @Indexed
    private ApplicationStatus status = ApplicationStatus.APPLIED;
    
    @Size(max = 1000, message = "Cover letter must not exceed 1000 characters")
    @Field("cover_letter")
    private String coverLetter;
    
    @Field("resume_url")
    private String resumeUrl;
    
    @Field("additional_documents")
    private List<String> additionalDocuments;
    
    @Size(max = 2000, message = "Notes must not exceed 2000 characters")
    private String notes; // Employer notes about the application
    
    @Field("interview_scheduled")
    private LocalDateTime interviewScheduled;
    
    @Field("interview_notes")
    private String interviewNotes;
    
    @Field("rejection_reason")
    private String rejectionReason;
    
    @Field("salary_offered")
    private Double salaryOffered;
    
    @Field("offer_details")
    private String offerDetails;
    
    @Field("offer_expires_at")
    private LocalDateTime offerExpiresAt;
    
    @Field("applied_at")
    @Indexed
    private LocalDateTime appliedAt;
    
    @Field("updated_at")
    private LocalDateTime updatedAt;
    
    @Field("status_history")
    private List<StatusChange> statusHistory;
    
    // Constructors
    public Application() {
        this.appliedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public Application(String candidateId, String jobId, String employerId) {
        this();
        this.candidateId = candidateId;
        this.jobId = jobId;
        this.employerId = employerId;
    }
    
    public Application(String candidateId, String jobId, String employerId, String coverLetter, String resumeUrl) {
        this(candidateId, jobId, employerId);
        this.coverLetter = coverLetter;
        this.resumeUrl = resumeUrl;
    }
    
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
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getCoverLetter() {
        return coverLetter;
    }
    
    public void setCoverLetter(String coverLetter) {
        this.coverLetter = coverLetter;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getResumeUrl() {
        return resumeUrl;
    }
    
    public void setResumeUrl(String resumeUrl) {
        this.resumeUrl = resumeUrl;
        this.updatedAt = LocalDateTime.now();
    }
    
    public List<String> getAdditionalDocuments() {
        return additionalDocuments;
    }
    
    public void setAdditionalDocuments(List<String> additionalDocuments) {
        this.additionalDocuments = additionalDocuments;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
        this.updatedAt = LocalDateTime.now();
    }
    
    public LocalDateTime getInterviewScheduled() {
        return interviewScheduled;
    }
    
    public void setInterviewScheduled(LocalDateTime interviewScheduled) {
        this.interviewScheduled = interviewScheduled;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getInterviewNotes() {
        return interviewNotes;
    }
    
    public void setInterviewNotes(String interviewNotes) {
        this.interviewNotes = interviewNotes;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getRejectionReason() {
        return rejectionReason;
    }
    
    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
        this.updatedAt = LocalDateTime.now();
    }
    
    public Double getSalaryOffered() {
        return salaryOffered;
    }
    
    public void setSalaryOffered(Double salaryOffered) {
        this.salaryOffered = salaryOffered;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getOfferDetails() {
        return offerDetails;
    }
    
    public void setOfferDetails(String offerDetails) {
        this.offerDetails = offerDetails;
        this.updatedAt = LocalDateTime.now();
    }
    
    public LocalDateTime getOfferExpiresAt() {
        return offerExpiresAt;
    }
    
    public void setOfferExpiresAt(LocalDateTime offerExpiresAt) {
        this.offerExpiresAt = offerExpiresAt;
        this.updatedAt = LocalDateTime.now();
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
    
    /**
     * Update application status with history tracking
     * @param newStatus the new status
     * @param changedBy who changed the status (employer ID or system)
     * @param reason optional reason for the change
     */
    public void updateStatus(ApplicationStatus newStatus, String changedBy, String reason) {
        ApplicationStatus oldStatus = this.status;
        this.status = newStatus;
        this.updatedAt = LocalDateTime.now();
        
        // Add to status history
        if (statusHistory == null) {
            statusHistory = new java.util.ArrayList<>();
        }
        
        StatusChange statusChange = new StatusChange(oldStatus, newStatus, changedBy, reason, LocalDateTime.now());
        statusHistory.add(statusChange);
        
        // Set specific fields based on status
        switch (newStatus) {
            case REJECTED:
                if (reason != null) {
                    this.rejectionReason = reason;
                }
                break;
            case HIRED:
                // Clear rejection reason if hired
                this.rejectionReason = null;
                break;
        }
    }
    
    /**
     * Check if application is in a final state
     * @return true if application is hired or rejected
     */
    public boolean isFinalStatus() {
        return status == ApplicationStatus.HIRED || status == ApplicationStatus.REJECTED;
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
    
    /**
     * Schedule interview
     * @param interviewTime the interview time
     */
    public void scheduleInterview(LocalDateTime interviewTime) {
        this.interviewScheduled = interviewTime;
        this.updatedAt = LocalDateTime.now();
        
        // Update status to interview scheduled if currently in review
        if (this.status == ApplicationStatus.IN_REVIEW) {
            updateStatus(ApplicationStatus.INTERVIEW_SCHEDULED, "system", "Interview scheduled");
        }
    }
    
    /**
     * Make job offer
     * @param salary the salary offered
     * @param details offer details
     * @param expiresAt when the offer expires
     * @param offeredBy who made the offer
     */
    public void makeOffer(Double salary, String details, LocalDateTime expiresAt, String offeredBy) {
        this.salaryOffered = salary;
        this.offerDetails = details;
        this.offerExpiresAt = expiresAt;
        updateStatus(ApplicationStatus.OFFER_MADE, offeredBy, "Job offer made");
    }
    
    @Override
    public String toString() {
        return "Application{" +
                "id='" + id + '\'' +
                ", candidateId='" + candidateId + '\'' +
                ", jobId='" + jobId + '\'' +
                ", employerId='" + employerId + '\'' +
                ", status=" + status +
                ", appliedAt=" + appliedAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}