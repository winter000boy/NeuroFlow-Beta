package com.jobapp.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO for admin profile update request
 * Requirements: 5.1
 */
public class AdminProfileUpdateRequest {
    
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;
    
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String newPassword;
    
    private String currentPassword;
    
    // Constructors
    public AdminProfileUpdateRequest() {}
    
    public AdminProfileUpdateRequest(String name) {
        this.name = name;
    }
    
    public AdminProfileUpdateRequest(String name, String newPassword, String currentPassword) {
        this.name = name;
        this.newPassword = newPassword;
        this.currentPassword = currentPassword;
    }
    
    // Getters and Setters
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getNewPassword() {
        return newPassword;
    }
    
    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
    
    public String getCurrentPassword() {
        return currentPassword;
    }
    
    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }
    
    /**
     * Check if password change is requested
     * @return true if new password is provided
     */
    public boolean isPasswordChangeRequested() {
        return newPassword != null && !newPassword.trim().isEmpty();
    }
    
    @Override
    public String toString() {
        return "AdminProfileUpdateRequest{" +
                "name='" + name + '\'' +
                ", newPassword='" + (newPassword != null ? "[PROTECTED]" : null) + '\'' +
                ", currentPassword='" + (currentPassword != null ? "[PROTECTED]" : null) + '\'' +
                '}';
    }
}