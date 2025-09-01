package com.jobapp.job.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.Valid;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Job entity representing job postings in the system
 * Requirements: 2.1, 2.2, 4.1, 4.2
 */
@Document(collection = "jobs")
public class Job {
    
    @Id
    private String id;
    
    @NotBlank(message = "Employer ID is required")
    @Indexed
    @Field("employer_id")
    private String employerId;
    
    @NotBlank(message = "Job title is required")
    @Size(min = 5, max = 200, message = "Job title must be between 5 and 200 characters")
    @TextIndexed(weight = 2) // Higher weight for title in text search
    private String title;
    
    @NotBlank(message = "Job description is required")
    @Size(min = 50, max = 5000, message = "Job description must be between 50 and 5000 characters")
    @TextIndexed
    private String description;
    
    @Valid
    private SalaryRange salary;
    
    @NotBlank(message = "Location is required")
    @Size(max = 200, message = "Location must not exceed 200 characters")
    @Indexed
    private String location;
    
    @NotNull(message = "Job type is required")
    @Indexed
    @Field("job_type")
    private JobType jobType;
    
    @Field("required_skills")
    private List<String> requiredSkills;
    
    @Field("preferred_skills")
    private List<String> preferredSkills;
    
    @Field("experience_level")
    private ExperienceLevel experienceLevel;
    
    @Field("education_requirement")
    private String educationRequirement;
    
    @Field("benefits")
    private List<String> benefits;
    
    @Field("application_deadline")
    private LocalDateTime applicationDeadline;
    
    @Field("is_active")
    @Indexed
    private Boolean isActive = true;
    
    @Field("is_featured")
    private Boolean isFeatured = false;
    
    @Field("application_count")
    private Integer applicationCount = 0;
    
    @Field("view_count")
    private Integer viewCount = 0;
    
    @Field("created_at")
    @Indexed
    private LocalDateTime createdAt;
    
    @Field("updated_at")
    private LocalDateTime updatedAt;
    
    @Field("expires_at")
    private LocalDateTime expiresAt;
    
    // Constructors
    public Job() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        // Default expiration is 30 days from creation
        this.expiresAt = LocalDateTime.now().plusDays(30);
    }
    
    public Job(String employerId, String title, String description, String location, JobType jobType) {
        this();
        this.employerId = employerId;
        this.title = title;
        this.description = description;
        this.location = location;
        this.jobType = jobType;
    }
    
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
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
        this.updatedAt = LocalDateTime.now();
    }
    
    public SalaryRange getSalary() {
        return salary;
    }
    
    public void setSalary(SalaryRange salary) {
        this.salary = salary;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
        this.updatedAt = LocalDateTime.now();
    }
    
    public JobType getJobType() {
        return jobType;
    }
    
    public void setJobType(JobType jobType) {
        this.jobType = jobType;
        this.updatedAt = LocalDateTime.now();
    }
    
    public List<String> getRequiredSkills() {
        return requiredSkills;
    }
    
    public void setRequiredSkills(List<String> requiredSkills) {
        this.requiredSkills = requiredSkills;
        this.updatedAt = LocalDateTime.now();
    }
    
    public List<String> getPreferredSkills() {
        return preferredSkills;
    }
    
    public void setPreferredSkills(List<String> preferredSkills) {
        this.preferredSkills = preferredSkills;
        this.updatedAt = LocalDateTime.now();
    }
    
    public ExperienceLevel getExperienceLevel() {
        return experienceLevel;
    }
    
    public void setExperienceLevel(ExperienceLevel experienceLevel) {
        this.experienceLevel = experienceLevel;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getEducationRequirement() {
        return educationRequirement;
    }
    
    public void setEducationRequirement(String educationRequirement) {
        this.educationRequirement = educationRequirement;
        this.updatedAt = LocalDateTime.now();
    }
    
    public List<String> getBenefits() {
        return benefits;
    }
    
    public void setBenefits(List<String> benefits) {
        this.benefits = benefits;
        this.updatedAt = LocalDateTime.now();
    }
    
    public LocalDateTime getApplicationDeadline() {
        return applicationDeadline;
    }
    
    public void setApplicationDeadline(LocalDateTime applicationDeadline) {
        this.applicationDeadline = applicationDeadline;
        this.updatedAt = LocalDateTime.now();
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
        this.updatedAt = LocalDateTime.now();
    }
    
    public Boolean getIsFeatured() {
        return isFeatured;
    }
    
    public void setIsFeatured(Boolean isFeatured) {
        this.isFeatured = isFeatured;
        this.updatedAt = LocalDateTime.now();
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
    
    /**
     * Check if job is currently accepting applications
     * @return true if job is active and not expired
     */
    public boolean isAcceptingApplications() {
        LocalDateTime now = LocalDateTime.now();
        return Boolean.TRUE.equals(isActive) && 
               (expiresAt == null || expiresAt.isAfter(now)) &&
               (applicationDeadline == null || applicationDeadline.isAfter(now));
    }
    
    /**
     * Check if job has expired
     * @return true if job has expired
     */
    public boolean isExpired() {
        LocalDateTime now = LocalDateTime.now();
        return (expiresAt != null && expiresAt.isBefore(now)) ||
               (applicationDeadline != null && applicationDeadline.isBefore(now));
    }
    
    /**
     * Increment application count
     */
    public void incrementApplicationCount() {
        this.applicationCount = (this.applicationCount == null) ? 1 : this.applicationCount + 1;
    }
    
    /**
     * Increment view count
     */
    public void incrementViewCount() {
        this.viewCount = (this.viewCount == null) ? 1 : this.viewCount + 1;
    }
    
    @Override
    public String toString() {
        return "Job{" +
                "id='" + id + '\'' +
                ", employerId='" + employerId + '\'' +
                ", title='" + title + '\'' +
                ", location='" + location + '\'' +
                ", jobType=" + jobType +
                ", isActive=" + isActive +
                ", applicationCount=" + applicationCount +
                ", createdAt=" + createdAt +
                '}';
    }
}