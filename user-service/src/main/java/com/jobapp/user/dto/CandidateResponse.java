package com.jobapp.user.dto;

import java.time.LocalDateTime;

/**
 * DTO for candidate response (without sensitive information)
 * Requirements: 1.1, 1.2, 1.4, 1.5
 */
public class CandidateResponse {
    
    private String id;
    private String email;
    private String name;
    private String phone;
    private String degree;
    private Integer graduationYear;
    private String resumeUrl;
    private String linkedinProfile;
    private String portfolioUrl;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Constructors
    public CandidateResponse() {}
    
    public CandidateResponse(String id, String email, String name, String phone, 
                           String degree, Integer graduationYear, String resumeUrl,
                           String linkedinProfile, String portfolioUrl, Boolean isActive,
                           LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.phone = phone;
        this.degree = degree;
        this.graduationYear = graduationYear;
        this.resumeUrl = resumeUrl;
        this.linkedinProfile = linkedinProfile;
        this.portfolioUrl = portfolioUrl;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
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
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public String getDegree() {
        return degree;
    }
    
    public void setDegree(String degree) {
        this.degree = degree;
    }
    
    public Integer getGraduationYear() {
        return graduationYear;
    }
    
    public void setGraduationYear(Integer graduationYear) {
        this.graduationYear = graduationYear;
    }
    
    public String getResumeUrl() {
        return resumeUrl;
    }
    
    public void setResumeUrl(String resumeUrl) {
        this.resumeUrl = resumeUrl;
    }
    
    public String getLinkedinProfile() {
        return linkedinProfile;
    }
    
    public void setLinkedinProfile(String linkedinProfile) {
        this.linkedinProfile = linkedinProfile;
    }
    
    public String getPortfolioUrl() {
        return portfolioUrl;
    }
    
    public void setPortfolioUrl(String portfolioUrl) {
        this.portfolioUrl = portfolioUrl;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
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
}