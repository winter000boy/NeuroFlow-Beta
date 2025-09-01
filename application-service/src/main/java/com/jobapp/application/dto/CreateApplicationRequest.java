package com.jobapp.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * Request DTO for creating a job application
 * Requirements: 2.3, 2.5
 */
public class CreateApplicationRequest {
    
    @NotBlank(message = "Job ID is required")
    private String jobId;
    
    @Size(max = 1000, message = "Cover letter must not exceed 1000 characters")
    private String coverLetter;
    
    private String resumeUrl;
    
    private List<String> additionalDocuments;
    
    // Constructors
    public CreateApplicationRequest() {}
    
    public CreateApplicationRequest(String jobId) {
        this.jobId = jobId;
    }
    
    public CreateApplicationRequest(String jobId, String coverLetter, String resumeUrl) {
        this.jobId = jobId;
        this.coverLetter = coverLetter;
        this.resumeUrl = resumeUrl;
    }
    
    // Getters and Setters
    public String getJobId() {
        return jobId;
    }
    
    public void setJobId(String jobId) {
        this.jobId = jobId;
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
    
    @Override
    public String toString() {
        return "CreateApplicationRequest{" +
                "jobId='" + jobId + '\'' +
                ", coverLetter='" + (coverLetter != null ? "[PRESENT]" : null) + '\'' +
                ", resumeUrl='" + resumeUrl + '\'' +
                ", additionalDocuments=" + additionalDocuments +
                '}';
    }
}