package com.jobapp.job.dto;

import com.jobapp.job.model.JobType;
import com.jobapp.job.model.ExperienceLevel;
import com.jobapp.job.model.SalaryRange;

import java.time.LocalDateTime;

/**
 * DTO for job summary data (used in listings)
 * Requirements: 3.2, 3.3
 */
public class JobSummaryResponse {
    
    private String id;
    private String employerId;
    private String title;
    private String shortDescription;
    private SalaryRange salary;
    private String location;
    private JobType jobType;
    private ExperienceLevel experienceLevel;
    private Boolean isActive;
    private Boolean isFeatured;
    private Integer applicationCount;
    private Integer viewCount;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private Boolean isAcceptingApplications;
    
    // Constructors
    public JobSummaryResponse() {}
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getEmployerId() {
        return employerId;
    }
    
    public void setEmployerId(String employerId) {
        this.employerId = employerId;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getShortDescription() {
        return shortDescription;
    }
    
    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }
    
    public SalaryRange getSalary() {
        return salary;
    }
    
    public void setSalary(SalaryRange salary) {
        this.salary = salary;
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    public JobType getJobType() {
        return jobType;
    }
    
    public void setJobType(JobType jobType) {
        this.jobType = jobType;
    }
    
    public ExperienceLevel getExperienceLevel() {
        return experienceLevel;
    }
    
    public void setExperienceLevel(ExperienceLevel experienceLevel) {
        this.experienceLevel = experienceLevel;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    public Boolean getIsFeatured() {
        return isFeatured;
    }
    
    public void setIsFeatured(Boolean isFeatured) {
        this.isFeatured = isFeatured;
    }
    
    public Integer getApplicationCount() {
        return applicationCount;
    }
    
    public void setApplicationCount(Integer applicationCount) {
        this.applicationCount = applicationCount;
    }
    
    public Integer getViewCount() {
        return viewCount;
    }
    
    public void setViewCount(Integer viewCount) {
        this.viewCount = viewCount;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }
    
    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }
    
    public Boolean getIsAcceptingApplications() {
        return isAcceptingApplications;
    }
    
    public void setIsAcceptingApplications(Boolean isAcceptingApplications) {
        this.isAcceptingApplications = isAcceptingApplications;
    }
    
    @Override
    public String toString() {
        return "JobSummaryResponse{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", location='" + location + '\'' +
                ", jobType=" + jobType +
                ", isActive=" + isActive +
                ", applicationCount=" + applicationCount +
                '}';
    }
}