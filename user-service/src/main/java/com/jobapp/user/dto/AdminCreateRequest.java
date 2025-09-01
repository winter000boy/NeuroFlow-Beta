package com.jobapp.user.dto;

import com.jobapp.user.model.AdminRole;
import com.jobapp.user.model.AdminPermission;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

/**
 * DTO for admin creation request
 * Requirements: 5.1
 */
public class AdminCreateRequest {
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;
    
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;
    
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;
    
    @NotNull(message = "Role is required")
    private AdminRole role;
    
    private List<AdminPermission> permissions;
    
    private Boolean isActive = true;
    
    // Constructors
    public AdminCreateRequest() {}
    
    public AdminCreateRequest(String email, String password, String name, AdminRole role) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.role = role;
    }
    
    public AdminCreateRequest(String email, String password, String name, AdminRole role, 
                             List<AdminPermission> permissions) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.role = role;
        this.permissions = permissions;
    }
    
    // Getters and Setters
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
    
    @Override
    public String toString() {
        return "AdminCreateRequest{" +
                "email='" + email + '\'' +
                ", password='[PROTECTED]'" +
                ", name='" + name + '\'' +
                ", role=" + role +
                ", permissions=" + permissions +
                ", isActive=" + isActive +
                '}';
    }
}