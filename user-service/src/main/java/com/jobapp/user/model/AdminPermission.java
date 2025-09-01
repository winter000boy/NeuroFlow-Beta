package com.jobapp.user.model;

/**
 * Enumeration for admin permissions
 * Requirements: 5.1, 5.4
 */
public enum AdminPermission {
    // User Management
    VIEW_USERS("View Users", "View candidate and employer profiles"),
    MANAGE_USERS("Manage Users", "Create, update, and delete user accounts"),
    APPROVE_EMPLOYERS("Approve Employers", "Approve or reject employer registrations"),
    BLOCK_USERS("Block Users", "Block or unblock user accounts"),
    
    // Job Management
    VIEW_JOBS("View Jobs", "View all job postings"),
    MANAGE_JOBS("Manage Jobs", "Create, update, and delete job postings"),
    MODERATE_JOBS("Moderate Jobs", "Review and moderate job content"),
    FEATURE_JOBS("Feature Jobs", "Mark jobs as featured"),
    
    // Application Management
    VIEW_APPLICATIONS("View Applications", "View all job applications"),
    MANAGE_APPLICATIONS("Manage Applications", "Manage application statuses"),
    
    // Analytics and Reporting
    VIEW_ANALYTICS("View Analytics", "Access dashboard analytics and reports"),
    EXPORT_DATA("Export Data", "Export system data and reports"),
    
    // System Administration
    MANAGE_ADMINS("Manage Admins", "Create and manage admin accounts"),
    SYSTEM_SETTINGS("System Settings", "Modify system configuration"),
    VIEW_AUDIT_LOGS("View Audit Logs", "Access system audit logs"),
    
    // Content Moderation
    MODERATE_CONTENT("Moderate Content", "Review and moderate user-generated content"),
    HANDLE_REPORTS("Handle Reports", "Process user reports and complaints"),
    
    // Communication
    SEND_NOTIFICATIONS("Send Notifications", "Send system-wide notifications"),
    MANAGE_EMAILS("Manage Emails", "Manage email templates and campaigns");
    
    private final String displayName;
    private final String description;
    
    AdminPermission(String displayName, String description) {
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
     * Get permissions by category
     * @param category the permission category
     * @return array of permissions in the category
     */
    public static AdminPermission[] getByCategory(PermissionCategory category) {
        switch (category) {
            case USER_MANAGEMENT:
                return new AdminPermission[]{VIEW_USERS, MANAGE_USERS, APPROVE_EMPLOYERS, BLOCK_USERS};
            case JOB_MANAGEMENT:
                return new AdminPermission[]{VIEW_JOBS, MANAGE_JOBS, MODERATE_JOBS, FEATURE_JOBS};
            case APPLICATION_MANAGEMENT:
                return new AdminPermission[]{VIEW_APPLICATIONS, MANAGE_APPLICATIONS};
            case ANALYTICS:
                return new AdminPermission[]{VIEW_ANALYTICS, EXPORT_DATA};
            case SYSTEM_ADMIN:
                return new AdminPermission[]{MANAGE_ADMINS, SYSTEM_SETTINGS, VIEW_AUDIT_LOGS};
            case CONTENT_MODERATION:
                return new AdminPermission[]{MODERATE_CONTENT, HANDLE_REPORTS};
            case COMMUNICATION:
                return new AdminPermission[]{SEND_NOTIFICATIONS, MANAGE_EMAILS};
            default:
                return new AdminPermission[]{};
        }
    }
    
    /**
     * Get all permissions for super admin
     * @return array of all permissions
     */
    public static AdminPermission[] getAllPermissions() {
        return values();
    }
    
    @Override
    public String toString() {
        return displayName;
    }
    
    /**
     * Permission categories for grouping
     */
    public enum PermissionCategory {
        USER_MANAGEMENT,
        JOB_MANAGEMENT,
        APPLICATION_MANAGEMENT,
        ANALYTICS,
        SYSTEM_ADMIN,
        CONTENT_MODERATION,
        COMMUNICATION
    }
}