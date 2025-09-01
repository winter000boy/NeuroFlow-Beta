package com.jobapp.job.dto;

import com.jobapp.job.model.JobType;
import com.jobapp.job.model.ExperienceLevel;
import com.jobapp.job.model.SalaryRange;

import jakarta.validation.constraints.Size;
import jakarta.validation.Valid;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for updating an existing job posting
 * Requirements: 3.2, 3.3
 */
public class UpdateJobRequest {
    
    @Size(min = 5, max = 200, message = "Job title must be between 5 and 200 characters")
    private String title;
    
    @Size(min = 50, max = 5000, message = "Job description must be between 50 and 5000 characters")
    private String description;
    
    @Valid
    private SalaryRange salary;
    
    @Size(max = 200, message = "Location must not exceed 200 characters")
    private String location;
    
    private JobType jobType;
    
    private List<String> requiredSkills;
    
    private List<String> preferredSkills;
    
    private ExperienceLevel experienceLevel;
    
    private String educationRequirement;
    
    private List<String> benefits;
    
    private LocalDateTime applicationDeadline;
    
    private Boolean isFeatured;
    
    // Constructors
    public UpdateJobRequest() {}
    
    // Getters and Setters
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
    
    public Boolean getIsFeatured() {
        return isFeatured;
    }
    
    public void setIsFeatured(Boolean isFeatured) {
        this.isFeatured = isFeatured;
    }
    
    @Override
    public String toString() {
        return "UpdateJobRequest{" +
                "title='" + title + '\'' +
                ", location='" + location + '\'' +
                ", jobType=" + jobType +
                ", experienceLevel=" + experienceLevel +
                '}';
    }
}