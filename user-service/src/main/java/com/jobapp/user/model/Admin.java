package com.jobapp.user.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Admin entity representing system administrators
 * Requirements: 5.1, 5.4
 */
@Document(collection = "admins")
public class Admin {
    
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
    
    @NotNull(message = "Role is required")
    private AdminRole role = AdminRole.SUPER_ADMIN;
    
    @Field("permissions")
    private List<AdminPermission> permissions;
    
    @Field("is_active")
    private Boolean isActive = true;
    
    @Field("last_login")
    private LocalDateTime lastLogin;
    
    @Field("login_count")
    private Integer loginCount = 0;
    
    @Field("created_by")
    private String createdBy; // ID of admin who created this admin
    
    @Field("created_at")
    @Indexed
    private LocalDateTime createdAt;
    
    @Field("updated_at")
    private LocalDateTime updatedAt;
    
    // Audit fields for tracking admin actions
    @Field("last_action")
    private String lastAction;
    
    @Field("last_action_at")
    private LocalDateTime lastActionAt;
    
    @Field("actions_performed")
    private Integer actionsPerformed = 0;
    
    // Constructors
    public Admin() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public Admin(String email, String password, String name) {
        this();
        this.email = email;
        this.password = password;
        this.name = name;
    }
    
    public Admin(String email, String password, String name, AdminRole role) {
        this(email, password, name);
        this.role = role;
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
    
    public AdminRole getRole() {
        return role;
    }
    
    public void setRole(AdminRole role) {
        this.role = role;
        this.updatedAt = LocalDateTime.now();
    }
    
    public List<AdminPermission> getPermissions() {
        return permissions;
    }
    
    public void setPermissions(List<AdminPermission> permissions) {
        this.permissions = permissions;
        this.updatedAt = LocalDateTime.now();
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
        this.updatedAt = LocalDateTime.now();
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
     * Record a login event
     */
    public void recordLogin() {
        this.lastLogin = LocalDateTime.now();
        this.loginCount = (this.loginCount == null) ? 1 : this.loginCount + 1;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Record an admin action
     * @param action description of the action performed
     */
    public void recordAction(String action) {
        this.lastAction = action;
        this.lastActionAt = LocalDateTime.now();
        this.actionsPerformed = (this.actionsPerformed == null) ? 1 : this.actionsPerformed + 1;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Check if admin has a specific permission
     * @param permission the permission to check
     * @return true if admin has the permission
     */
    public boolean hasPermission(AdminPermission permission) {
        if (role == AdminRole.SUPER_ADMIN) {
            return true; // Super admin has all permissions
        }
        return permissions != null && permissions.contains(permission);
    }
    
    /**
     * Add a permission to the admin
     * @param permission the permission to add
     */
    public void addPermission(AdminPermission permission) {
        if (permissions == null) {
            permissions = new java.util.ArrayList<>();
        }
        if (!permissions.contains(permission)) {
            permissions.add(permission);
            this.updatedAt = LocalDateTime.now();
        }
    }
    
    /**
     * Remove a permission from the admin
     * @param permission the permission to remove
     */
    public void removePermission(AdminPermission permission) {
        if (permissions != null) {
            permissions.remove(permission);
            this.updatedAt = LocalDateTime.now();
        }
    }
    
    /**
     * Check if admin is currently active and can perform actions
     * @return true if admin is active
     */
    public boolean canPerformActions() {
        return Boolean.TRUE.equals(isActive) && role != null;
    }
    
    /**
     * Get display name for the admin
     * @return formatted display name
     */
    public String getDisplayName() {
        return name + " (" + role.getDisplayName() + ")";
    }
    
    @Override
    public String toString() {
        return "Admin{" +
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