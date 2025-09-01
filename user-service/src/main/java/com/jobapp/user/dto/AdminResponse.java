package com.jobapp.user.dto;

import com.jobapp.user.model.AdminRole;
import com.jobapp.user.model.AdminPermission;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for admin profile response
 * Requirements: 5.1
 */
public class AdminResponse {
    
    private String id;
    private String email;
    private String name;
    private AdminRole role;
    private List<AdminPermission> permissions;
    private Boolean isActive;
    private LocalDateTime lastLogin;
    private Integer loginCount;
    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String lastAction;
    private LocalDateTime lastActionAt;
    private Integer actionsPerformed;
    
    // Constructors
    public AdminResponse() {}
    
    public AdminResponse(String id, String email, String name, AdminRole role, 
                        List<AdminPermission> permissions, Boolean isActive) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.role = role;
        this.permissions = permissions;
        this.isActive = isActive;
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
    
    public AdminRole getRole() {
        return role;
    }
    
    public void setRole(AdminRole role) {
        this.role = role;
    }
    
    public List<AdminPermission> getPermissions() {
        return permissions;
    }
    
    public void setPermissions(List<AdminPermission> permissions) {
        this.permissions = permissions;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    public LocalDateTime getLastLogin() {
        return lastLogin;
    }
    
    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }
    
    public Integer getLoginCount() {
        return loginCount;
    }
    
    public void setLoginCount(Integer loginCount) {
        this.loginCount = loginCount;
    }
    
    public String getCreatedBy() {
        return createdBy;
    }
    
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
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
    
    public String getLastAction() {
        return lastAction;
    }
    
    public void setLastAction(String lastAction) {
        this.lastAction = lastAction;
    }
    
    public LocalDateTime getLastActionAt() {
        return lastActionAt;
    }
    
    public void setLastActionAt(LocalDateTime lastActionAt) {
        this.lastActionAt = lastActionAt;
    }
    
    public Integer getActionsPerformed() {
        return actionsPerformed;
    }
    
    public void setActionsPerformed(Integer actionsPerformed) {
        this.actionsPerformed = actionsPerformed;
    }
    
    /**
     * Get display name for the admin
     * @return formatted display name
     */
    public String getDisplayName() {
        return name + " (" + (role != null ? role.getDisplayName() : "Unknown") + ")";
    }
    
    @Override
    public String toString() {
        return "AdminResponse{" +
                "id='" + id + '\'' +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", role=" + role +
                ", isActive=" + isActive +
                ", loginCount=" + loginCount +
                ", actionsPerformed=" + actionsPerformed +
                ", createdAt=" + createdAt +
                '}';
    }
}