package com.jobapp.user.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * Employer entity representing companies and hiring managers in the system
 * Requirements: 3.1, 3.4
 */
@Document(collection = "employers")
public class Employer {
    
    @Id
    private String id;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Indexed(unique = true)
    private String email;
    
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;
    
    @NotBlank(message = "Company name is required")
    @Size(min = 2, max = 200, message = "Company name must be between 2 and 200 characters")
    @Field("company_name")
    private String companyName;
    
    @Pattern(regexp = "^https?://.*", message = "Website must be a valid URL")
    private String website;
    
    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;
    
    @Field("logo_url")
    private String logoUrl;
    
    @Size(max = 500, message = "Address must not exceed 500 characters")
    private String address;
    
    @Field("contact_person")
    private String contactPerson;
    
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Phone number should be valid")
    @Field("contact_phone")
    private String contactPhone;
    
    @Field("is_approved")
    private Boolean isApproved = false;
    
    @Field("is_active")
    private Boolean isActive = true;
    
    @Field("approval_date")
    private LocalDateTime approvalDate;
    
    @Field("approved_by")
    private String approvedBy; // Admin email who approved
    
    @Field("approved_at")
    private LocalDateTime approvedAt;
    
    @Field("approval_notes")
    private String approvalNotes;
    
    @Field("rejected_at")
    private LocalDateTime rejectedAt;
    
    @Field("rejected_by")
    private String rejectedBy; // Admin email who rejected
    
    @Field("rejection_reason")
    private String rejectionReason;
    
    @Field("rejection_notes")
    private String rejectionNotes;
    
    // Admin blocking fields
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
    
    @Field("created_at")
    private LocalDateTime createdAt;
    
    @Field("updated_at")
    private LocalDateTime updatedAt;
    
    // Constructors
    public Employer() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public Employer(String email, String password, String companyName, String website, String description) {
        this();
        this.email = email;
        this.password = password;
        this.companyName = companyName;
        this.website = website;
        this.description = description;
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
    
    public String getCompanyName() {
        return companyName;
    }
    
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getWebsite() {
        return website;
    }
    
    public void setWebsite(String website) {
        this.website = website;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getLogoUrl() {
        return logoUrl;
    }
    
    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getContactPerson() {
        return contactPerson;
    }
    
    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getContactPhone() {
        return contactPhone;
    }
    
    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
        this.updatedAt = LocalDateTime.now();
    }
    
    public Boolean getIsApproved() {
        return isApproved;
    }
    
    public void setIsApproved(Boolean isApproved) {
        this.isApproved = isApproved;
        this.updatedAt = LocalDateTime.now();
        if (isApproved != null && isApproved) {
            this.approvalDate = LocalDateTime.now();
        }
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
        this.updatedAt = LocalDateTime.now();
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
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getRejectionReason() {
        return rejectionReason;
    }
    
    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
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
    
    public LocalDateTime getApprovedAt() {
        return approvedAt;
    }
    
    public void setApprovedAt(LocalDateTime approvedAt) {
        this.approvedAt = approvedAt;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getApprovalNotes() {
        return approvalNotes;
    }
    
    public void setApprovalNotes(String approvalNotes) {
        this.approvalNotes = approvalNotes;
        this.updatedAt = LocalDateTime.now();
    }
    
    public LocalDateTime getRejectedAt() {
        return rejectedAt;
    }
    
    public void setRejectedAt(LocalDateTime rejectedAt) {
        this.rejectedAt = rejectedAt;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getRejectedBy() {
        return rejectedBy;
    }
    
    public void setRejectedBy(String rejectedBy) {
        this.rejectedBy = rejectedBy;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getRejectionNotes() {
        return rejectionNotes;
    }
    
    public void setRejectionNotes(String rejectionNotes) {
        this.rejectionNotes = rejectionNotes;
        this.updatedAt = LocalDateTime.now();
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
    
    /**
     * Check if employer is approved and active
     * @return true if employer can post jobs
     */
    public boolean canPostJobs() {
        return Boolean.TRUE.equals(isApproved) && Boolean.TRUE.equals(isActive);
    }
    
    /**
     * Approve the employer
     * @param adminId the ID of the admin approving
     */
    public void approve(String adminId) {
        this.isApproved = true;
        this.approvedBy = adminId;
        this.approvalDate = LocalDateTime.now();
        this.rejectionReason = null;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Reject the employer
     * @param reason the reason for rejection
     */
    public void reject(String reason) {
        this.isApproved = false;
        this.rejectionReason = reason;
        this.approvalDate = null;
        this.approvedBy = null;
        this.updatedAt = LocalDateTime.now();
    }
    
    @Override
    public String toString() {
        return "Employer{" +
                "id='" + id + '\'' +
                ", email='" + email + '\'' +
                ", companyName='" + companyName + '\'' +
                ", website='" + website + '\'' +
                ", isApproved=" + isApproved +
                ", isActive=" + isActive +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}