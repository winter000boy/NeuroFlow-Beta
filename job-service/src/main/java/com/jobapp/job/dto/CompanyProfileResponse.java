package com.jobapp.job.dto;

import java.time.LocalDateTime;

/**
 * DTO for company profile response
 * Requirements: 2.2, 7.4
 */
public class CompanyProfileResponse {
    
    private String id;
    private String companyName;
    private String description;
    private String website;
    private String logoUrl;
    private String address;
    private Boolean isApproved;
    private Boolean isActive;
    private LocalDateTime createdAt;
    
    // Job statistics
    private Integer totalJobs;
    private Integer activeJobs;
    private Integer totalApplications;
    private Integer totalViews;
    
    // Recent jobs
    private PagedResponse<JobSummaryResponse> recentJobs;
    
    // Constructors
    public CompanyProfileResponse() {}
    
    public CompanyProfileResponse(String id, String companyName, String description, 
                                 String website, String logoUrl, String address) {
        this.id = id;
        this.companyName = companyName;
        this.description = description;
        this.website = website;
        this.logoUrl = logoUrl;
        this.address = address;
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getCompanyName() {
        return companyName;
    }
    
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getWebsite() {
        return website;
    }
    
    public void setWebsite(String website) {
        this.website = website;
    }
    
    public String getLogoUrl() {
        return logoUrl;
    }
    
    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public Boolean getIsApproved() {
        return isApproved;
    }
    
    public void setIsApproved(Boolean isApproved) {
        this.isApproved = isApproved;
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
    
    public Integer getTotalJobs() {
        return totalJobs;
    }
    
    public void setTotalJobs(Integer totalJobs) {
        this.totalJobs = totalJobs;
    }
    
    public Integer getActiveJobs() {
        return activeJobs;
    }
    
    public void setActiveJobs(Integer activeJobs) {
        this.activeJobs = activeJobs;
    }
    
    public Integer getTotalApplications() {
        return totalApplications;
    }
    
    public void setTotalApplications(Integer totalApplications) {
        this.totalApplications = totalApplications;
    }
    
    public Integer getTotalViews() {
        return totalViews;
    }
    
    public void setTotalViews(Integer totalViews) {
        this.totalViews = totalViews;
    }
    
    public PagedResponse<JobSummaryResponse> getRecentJobs() {
        return recentJobs;
    }
    
    public void setRecentJobs(PagedResponse<JobSummaryResponse> recentJobs) {
        this.recentJobs = recentJobs;
    }
    
    @Override
    public String toString() {
        return "CompanyProfileResponse{" +
                "id='" + id + '\'' +
                ", companyName='" + companyName + '\'' +
                ", website='" + website + '\'' +
                ", totalJobs=" + totalJobs +
                ", activeJobs=" + activeJobs +
                ", isApproved=" + isApproved +
                '}';
    }
}