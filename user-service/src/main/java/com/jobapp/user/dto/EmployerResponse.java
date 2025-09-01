package com.jobapp.user.dto;

import java.time.LocalDateTime;

/**
 * DTO for employer response (without sensitive information)
 * Requirements: 3.1, 3.4
 */
public class EmployerResponse {
    
    private String id;
    private String email;
    private String companyName;
    private String website;
    private String description;
    private String logoUrl;
    private String address;
    private String contactPerson;
    private String contactPhone;
    private Boolean isApproved;
    private Boolean isActive;
    private LocalDateTime approvalDate;
    private String approvedBy;
    private String rejectionReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Constructors
    public EmployerResponse() {}
    
    public EmployerResponse(String id, String email, String companyName, String website, 
                          String description, String logoUrl, String address, String contactPerson,
                          String contactPhone, Boolean isApproved, Boolean isActive, 
                          LocalDateTime approvalDate, String approvedBy, String rejectionReason,
                          LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.email = email;
        this.companyName = companyName;
        this.website = website;
        this.description = description;
        this.logoUrl = logoUrl;
        this.address = address;
        this.contactPerson = contactPerson;
        this.contactPhone = contactPhone;
        this.isApproved = isApproved;
        this.isActive = isActive;
        this.approvalDate = approvalDate;
        this.approvedBy = approvedBy;
        this.rejectionReason = rejectionReason;
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
    
    public String getCompanyName() {
        return companyName;
    }
    
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
    
    public String getWebsite() {
        return website;
    }
    
    public void setWebsite(String website) {
        this.website = website;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
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
    
    public String getContactPerson() {
        return contactPerson;
    }
    
    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }
    
    public String getContactPhone() {
        return contactPhone;
    }
    
    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
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
    
    public LocalDateTime getApprovalDate() {
        return approvalDate;
    }
    
    public void setApprovalDate(LocalDateTime approvalDate) {
        this.approvalDate = approvalDate;
    }
    
    public String getApprovedBy() {
        return approvedBy;
    }
    
    public void setApprovedBy(String approvedBy) {
        this.approvedBy = approvedBy;
    }
    
    public String getRejectionReason() {
        return rejectionReason;
    }
    
    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
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
    
    /**
     * Check if employer can post jobs
     */
    public boolean canPostJobs() {
        return Boolean.TRUE.equals(isApproved) && Boolean.TRUE.equals(isActive);
    }
}