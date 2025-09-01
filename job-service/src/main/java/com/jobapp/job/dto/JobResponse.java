package com.jobapp.job.dto;

import com.jobapp.job.model.JobType;
import com.jobapp.job.model.ExperienceLevel;
import com.jobapp.job.model.SalaryRange;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for job response data
 * Requirements: 3.2, 3.3
 */
public class JobResponse {
    
    private String id;
    private String employerId;
    private String title;
    private String description;
    private SalaryRange salary;
    private String location;
    private JobType jobType;
    private List<String> requiredSkills;
    private List<String> preferredSkills;
    private ExperienceLevel experienceLevel;
    private String educationRequirement;
    private List<String> benefits;
    private LocalDateTime applicationDeadline;
    private Boolean isActive;
    private Boolean isFeatured;
    private Integer applicationCount;
    private Integer viewCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime expiresAt;
    
    // Constructors
    public JobResponse() {}
    
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
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
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
    
    public List<String> getRequiredSkills() {
        return requiredSkills;
    }
    
    public void setRequiredSkills(List<String> requiredSkills) {
        this.requiredSkills = requiredSkills;
    }
    
    public List<String> getPreferredSkills() {
        return preferredSkills;
    }
    
    public void setPreferredSkills(List<String> preferredSkills) {
        this.preferredSkills = preferredSkills;
    }
    
    public ExperienceLevel getExperienceLevel() {
        return experienceLevel;
    }
    
    public void setExperienceLevel(ExperienceLevel experienceLevel) {
        this.experienceLevel = experienceLevel;
    }
    
    public String getEducationRequirement() {
        return educationRequirement;
    }
    
    public void setEducationRequirement(String educationRequirement) {
        this.educationRequirement = educationRequirement;
    }
    
    public List<String> getBenefits() {
        return benefits;
    }
    
    public void setBenefits(List<String> benefits) {
        this.benefits = benefits;
    }
    
    public LocalDateTime getApplicationDeadline() {
        return applicationDeadline;
    }
    
    public void setApplicationDeadline(LocalDateTime applicationDeadline) {
        this.applicationDeadline = applicationDeadline;
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
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }
    
    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }
    
    @Override
    public String toString() {
        return "JobResponse{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", location='" + location + '\'' +
                ", jobType=" + jobType +
                ", isActive=" + isActive +
                ", applicationCount=" + applicationCount +
                '}';
    }
}