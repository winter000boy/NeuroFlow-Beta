package com.jobapp.user.dto;

import com.jobapp.user.model.AdminRole;
import com.jobapp.user.model.AdminPermission;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for admin login response
 * Requirements: 5.1
 */
public class AdminLoginResponse {
    
    private String token;
    private String refreshToken;
    private String type = "Bearer";
    private String adminId;
    private String email;
    private String name;
    private AdminRole role;
    private List<AdminPermission> permissions;
    private LocalDateTime expiresAt;
    private LocalDateTime lastLogin;
    private Integer loginCount;
    
    // Constructors
    public AdminLoginResponse() {}
    
    public AdminLoginResponse(String token, String refreshToken, String adminId, 
                             String email, String name, AdminRole role, 
                             List<AdminPermission> permissions, LocalDateTime expiresAt) {
        this.token = token;
        this.refreshToken = refreshToken;
        this.adminId = adminId;
        this.email = email;
        this.name = name;
        this.role = role;
        this.permissions = permissions;
        this.expiresAt = expiresAt;
    }
    
    // Getters and Setters
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    public String getRefreshToken() {
        return refreshToken;
    }
    
    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getAdminId() {
        return adminId;
    }
    
    public void setAdminId(String adminId) {
        this.adminId = adminId;
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
    
    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }
    
    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
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
    
    @Override
    public String toString() {
        return "AdminLoginResponse{" +
                "adminId='" + adminId + '\'' +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", role=" + role +
                ", permissions=" + permissions +
                ", expiresAt=" + expiresAt +
                ", loginCount=" + loginCount +
                '}';
    }
}