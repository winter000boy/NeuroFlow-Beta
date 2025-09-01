package com.jobapp.user.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

import java.time.LocalDateTime;

/**
 * Candidate entity representing job seekers in the system
 * Requirements: 1.1, 1.2, 1.5
 */
@Document(collection = "candidates")
public class Candidate {
    
    @Id
    private String id;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Indexed(unique = true)
    private String email;
    
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;
    
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;
    
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Phone number should be valid")
    private String phone;
    
    @NotBlank(message = "Degree is required")
    @Size(max = 100, message = "Degree must not exceed 100 characters")
    private String degree;
    
    @NotNull(message = "Graduation year is required")
    @Min(value = 1950, message = "Graduation year must be after 1950")
    @Max(value = 2030, message = "Graduation year must be before 2030")
    @Field("graduation_year")
    private Integer graduationYear;
    
    @Field("resume_url")
    private String resumeUrl;
    
    @Pattern(regexp = "^https://([a-z]{2,3}\\.)?linkedin\\.com/.*", 
             message = "LinkedIn profile must be a valid LinkedIn URL")
    @Field("linkedin_profile")
    private String linkedinProfile;
    
    @Pattern(regexp = "^https?://.*", 
             message = "Portfolio URL must be a valid URL")
    @Field("portfolio_url")
    private String portfolioUrl;
    
    @Field("is_active")
    private Boolean isActive = true;
    
    @Field("created_at")
    private LocalDateTime createdAt;
    
    @Field("updated_at")
    private LocalDateTime updatedAt;
    
    // Admin action fields
    @Field("blocked_at")
    private LocalDateTime blockedAt;
    
    @Field("blocked_by")
    private String blockedBy; // Admin email who blocked
    
    @Field("block_reason")
    private String blockReason;
    
    @Field("block_notes")
    private String blockNotes;
    
    @Field("unblocked_at")
    private LocalDateTime unblockedAt;
    
    @Field("unblocked_by")
    private String unblockedBy; // Admin email who unblocked
    
    // Constructors
    public Candidate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public Candidate(String email, String password, String name, String phone, 
                    String degree, Integer graduationYear) {
        this();
        this.email = email;
        this.password = password;
        this.name = name;
        this.phone = phone;
        this.degree = degree;
        this.graduationYear = graduationYear;
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getDegree() {
        return degree;
    }
    
    public void setDegree(String degree) {
        this.degree = degree;
        this.updatedAt = LocalDateTime.now();
    }
    
    public Integer getGraduationYear() {
        return graduationYear;
    }
    
    public void setGraduationYear(Integer graduationYear) {
        this.graduationYear = graduationYear;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getResumeUrl() {
        return resumeUrl;
    }
    
    public void setResumeUrl(String resumeUrl) {
        this.resumeUrl = resumeUrl;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getLinkedinProfile() {
        return linkedinProfile;
    }
    
    public void setLinkedinProfile(String linkedinProfile) {
        this.linkedinProfile = linkedinProfile;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getPortfolioUrl() {
        return portfolioUrl;
    }
    
    public void setPortfolioUrl(String portfolioUrl) {
        this.portfolioUrl = portfolioUrl;
        this.updatedAt = LocalDateTime.now();
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
        this.updatedAt = LocalDateTime.now();
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
    
    public LocalDateTime getBlockedAt() {
        return blockedAt;
    }
    
    public void setBlockedAt(LocalDateTime blockedAt) {
        this.blockedAt = blockedAt;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getBlockedBy() {
        return blockedBy;
    }
    
    public void setBlockedBy(String blockedBy) {
        this.blockedBy = blockedBy;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getBlockReason() {
        return blockReason;
    }
    
    public void setBlockReason(String blockReason) {
        this.blockReason = blockReason;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getBlockNotes() {
        return blockNotes;
    }
    
    public void setBlockNotes(String blockNotes) {
        this.blockNotes = blockNotes;
        this.updatedAt = LocalDateTime.now();
    }
    
    public LocalDateTime getUnblockedAt() {
        return unblockedAt;
    }
    
    public void setUnblockedAt(LocalDateTime unblockedAt) {
        this.unblockedAt = unblockedAt;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getUnblockedBy() {
        return unblockedBy;
    }
    
    public void setUnblockedBy(String unblockedBy) {
        this.unblockedBy = unblockedBy;
        this.updatedAt = LocalDateTime.now();
    }
    
    @Override
    public String toString() {
        return "Candidate{" +
                "id='" + id + '\'' +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", degree='" + degree + '\'' +
                ", graduationYear=" + graduationYear +
                ", isActive=" + isActive +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}