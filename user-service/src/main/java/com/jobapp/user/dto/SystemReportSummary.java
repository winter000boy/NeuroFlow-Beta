package com.jobapp.user.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * DTO for system reports summary
 * Requirements: 5.4
 */
public class SystemReportSummary {
    
    private LocalDateTime generatedAt;
    private int reportPeriodDays;
    
    // Performance metrics
    private PerformanceMetrics performance;
    
    // Security metrics
    private SecurityMetrics security;
    
    // Content metrics
    private ContentMetrics content;
    
    // User engagement metrics
    private EngagementMetrics engagement;
    
    // System health indicators
    private List<HealthIndicator> healthIndicators;
    
    // Alerts and warnings
    private List<SystemAlert> alerts;
    
    // Constructors
    public SystemReportSummary() {
        this.generatedAt = LocalDateTime.now();
    }
    
    public SystemReportSummary(int reportPeriodDays) {
        this();
        this.reportPeriodDays = reportPeriodDays;
    }
    
    // Getters and Setters
    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }
    
    public void setGeneratedAt(LocalDateTime generatedAt) {
        this.generatedAt = generatedAt;
    }
    
    public int getReportPeriodDays() {
        return reportPeriodDays;
    }
    
    public void setReportPeriodDays(int reportPeriodDays) {
        this.reportPeriodDays = reportPeriodDays;
    }
    
    public PerformanceMetrics getPerformance() {
        return performance;
    }
    
    public void setPerformance(PerformanceMetrics performance) {
        this.performance = performance;
    }
    
    public SecurityMetrics getSecurity() {
        return security;
    }
    
    public void setSecurity(SecurityMetrics security) {
        this.security = security;
    }
    
    public ContentMetrics getContent() {
        return content;
    }
    
    public void setContent(ContentMetrics content) {
        this.content = content;
    }
    
    public EngagementMetrics getEngagement() {
        return engagement;
    }
    
    public void setEngagement(EngagementMetrics engagement) {
        this.engagement = engagement;
    }
    
    public List<HealthIndicator> getHealthIndicators() {
        return healthIndicators;
    }
    
    public void setHealthIndicators(List<HealthIndicator> healthIndicators) {
        this.healthIndicators = healthIndicators;
    }
    
    public List<SystemAlert> getAlerts() {
        return alerts;
    }
    
    public void setAlerts(List<SystemAlert> alerts) {
        this.alerts = alerts;
    }
    
    // Inner classes
    public static class PerformanceMetrics {
        private double averageResponseTime;
        private long totalRequests;
        private double errorRate;
        private long peakConcurrentUsers;
        private Map<String, Long> endpointUsage;
        
        // Constructors, getters and setters
        public PerformanceMetrics() {}
        
        public double getAverageResponseTime() { return averageResponseTime; }
        public void setAverageResponseTime(double averageResponseTime) { this.averageResponseTime = averageResponseTime; }
        
        public long getTotalRequests() { return totalRequests; }
        public void setTotalRequests(long totalRequests) { this.totalRequests = totalRequests; }
        
        public double getErrorRate() { return errorRate; }
        public void setErrorRate(double errorRate) { this.errorRate = errorRate; }
        
        public long getPeakConcurrentUsers() { return peakConcurrentUsers; }
        public void setPeakConcurrentUsers(long peakConcurrentUsers) { this.peakConcurrentUsers = peakConcurrentUsers; }
        
        public Map<String, Long> getEndpointUsage() { return endpointUsage; }
        public void setEndpointUsage(Map<String, Long> endpointUsage) { this.endpointUsage = endpointUsage; }
    }
    
    public static class SecurityMetrics {
        private long failedLoginAttempts;
        private long blockedIPs;
        private long suspiciousActivities;
        private long dataBreachAttempts;
        private Map<String, Long> securityEvents;
        
        // Constructors, getters and setters
        public SecurityMetrics() {}
        
        public long getFailedLoginAttempts() { return failedLoginAttempts; }
        public void setFailedLoginAttempts(long failedLoginAttempts) { this.failedLoginAttempts = failedLoginAttempts; }
        
        public long getBlockedIPs() { return blockedIPs; }
        public void setBlockedIPs(long blockedIPs) { this.blockedIPs = blockedIPs; }
        
        public long getSuspiciousActivities() { return suspiciousActivities; }
        public void setSuspiciousActivities(long suspiciousActivities) { this.suspiciousActivities = suspiciousActivities; }
        
        public long getDataBreachAttempts() { return dataBreachAttempts; }
        public void setDataBreachAttempts(long dataBreachAttempts) { this.dataBreachAttempts = dataBreachAttempts; }
        
        public Map<String, Long> getSecurityEvents() { return securityEvents; }
        public void setSecurityEvents(Map<String, Long> securityEvents) { this.securityEvents = securityEvents; }
    }
    
    public static class ContentMetrics {
        private long totalContentItems;
        private long moderatedContent;
        private long flaggedContent;
        private long removedContent;
        private double moderationAccuracy;
        private Map<String, Long> contentByCategory;
        
        // Constructors, getters and setters
        public ContentMetrics() {}
        
        public long getTotalContentItems() { return totalContentItems; }
        public void setTotalContentItems(long totalContentItems) { this.totalContentItems = totalContentItems; }
        
        public long getModeratedContent() { return moderatedContent; }
        public void setModeratedContent(long moderatedContent) { this.moderatedContent = moderatedContent; }
        
        public long getFlaggedContent() { return flaggedContent; }
        public void setFlaggedContent(long flaggedContent) { this.flaggedContent = flaggedContent; }
        
        public long getRemovedContent() { return removedContent; }
        public void setRemovedContent(long removedContent) { this.removedContent = removedContent; }
        
        public double getModerationAccuracy() { return moderationAccuracy; }
        public void setModerationAccuracy(double moderationAccuracy) { this.moderationAccuracy = moderationAccuracy; }
        
        public Map<String, Long> getContentByCategory() { return contentByCategory; }
        public void setContentByCategory(Map<String, Long> contentByCategory) { this.contentByCategory = contentByCategory; }
    }
    
    public static class EngagementMetrics {
        private long activeUsers;
        private double userRetentionRate;
        private double jobApplicationRate;
        private double employerSatisfactionScore;
        private double candidateSatisfactionScore;
        private Map<String, Double> engagementByFeature;
        
        // Constructors, getters and setters
        public EngagementMetrics() {}
        
        public long getActiveUsers() { return activeUsers; }
        public void setActiveUsers(long activeUsers) { this.activeUsers = activeUsers; }
        
        public double getUserRetentionRate() { return userRetentionRate; }
        public void setUserRetentionRate(double userRetentionRate) { this.userRetentionRate = userRetentionRate; }
        
        public double getJobApplicationRate() { return jobApplicationRate; }
        public void setJobApplicationRate(double jobApplicationRate) { this.jobApplicationRate = jobApplicationRate; }
        
        public double getEmployerSatisfactionScore() { return employerSatisfactionScore; }
        public void setEmployerSatisfactionScore(double employerSatisfactionScore) { this.employerSatisfactionScore = employerSatisfactionScore; }
        
        public double getCandidateSatisfactionScore() { return candidateSatisfactionScore; }
        public void setCandidateSatisfactionScore(double candidateSatisfactionScore) { this.candidateSatisfactionScore = candidateSatisfactionScore; }
        
        public Map<String, Double> getEngagementByFeature() { return engagementByFeature; }
        public void setEngagementByFeature(Map<String, Double> engagementByFeature) { this.engagementByFeature = engagementByFeature; }
    }
    
    public static class HealthIndicator {
        private String component;
        private String status; // HEALTHY, WARNING, CRITICAL
        private String message;
        private LocalDateTime lastChecked;
        
        public HealthIndicator() {}
        
        public HealthIndicator(String component, String status, String message) {
            this.component = component;
            this.status = status;
            this.message = message;
            this.lastChecked = LocalDateTime.now();
        }
        
        public String getComponent() { return component; }
        public void setComponent(String component) { this.component = component; }
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        
        public LocalDateTime getLastChecked() { return lastChecked; }
        public void setLastChecked(LocalDateTime lastChecked) { this.lastChecked = lastChecked; }
    }
    
    public static class SystemAlert {
        private String type; // INFO, WARNING, ERROR, CRITICAL
        private String title;
        private String message;
        private LocalDateTime timestamp;
        private Boolean acknowledged;
        
        public SystemAlert() {}
        
        public SystemAlert(String type, String title, String message) {
            this.type = type;
            this.title = title;
            this.message = message;
            this.timestamp = LocalDateTime.now();
            this.acknowledged = false;
        }
        
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        
        public LocalDateTime getTimestamp() { return timestamp; }
        public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
        
        public Boolean getAcknowledged() { return acknowledged; }
        public void setAcknowledged(Boolean acknowledged) { this.acknowledged = acknowledged; }
    }
}