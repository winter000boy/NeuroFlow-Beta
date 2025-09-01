package com.jobapp.user.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

/**
 * DTO for candidate profile update request
 * Requirements: 1.4, 1.5
 */
public class CandidateProfileUpdateRequest {
    
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;
    
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Phone number should be valid")
    private String phone;
    
    @Size(max = 100, message = "Degree must not exceed 100 characters")
    private String degree;
    
    @Min(value = 1950, message = "Graduation year must be after 1950")
    @Max(value = 2030, message = "Graduation year must be before 2030")
    private Integer graduationYear;
    
    @Pattern(regexp = "^https://([a-z]{2,3}\\.)?linkedin\\.com/.*", 
             message = "LinkedIn profile must be a valid LinkedIn URL")
    private String linkedinProfile;
    
    @Pattern(regexp = "^https?://.*", 
             message = "Portfolio URL must be a valid URL")
    private String portfolioUrl;
    
    // Constructors
    public CandidateProfileUpdateRequest() {}
    
    // Getters and Setters
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
}