package com.jobapp.user.model;

/**
 * Enumeration for admin roles
 * Requirements: 5.1
 */
public enum AdminRole {
    SUPER_ADMIN("Super Admin", "Full system access with all permissions"),
    ADMIN("Admin", "Standard admin with limited permissions"),
    MODERATOR("Moderator", "Content moderation and user management"),
    SUPPORT("Support", "Customer support and basic user assistance");
    
    private final String displayName;
    private final String description;
    
    AdminRole(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * Check if this role has higher privileges than another role
     * @param other the other role to compare
     * @return true if this role has higher privileges
     */
    public boolean hasHigherPrivilegesThan(AdminRole other) {
        return this.ordinal() < other.ordinal();
    }
    
    /**
     * Check if this role can manage another role
     * @param other the other role
     * @return true if this role can manage the other role
     */
    public boolean canManage(AdminRole other) {
        return this == SUPER_ADMIN || this.hasHigherPrivilegesThan(other);
    }
    
    @Override
    public String toString() {
        return displayName;
    }
}