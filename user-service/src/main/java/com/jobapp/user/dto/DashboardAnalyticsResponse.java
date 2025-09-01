package com.jobapp.user.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * DTO for dashboard analytics response
 * Requirements: 5.4
 */
public class DashboardAnalyticsResponse {
    
    // User Statistics
    private UserStatistics userStatistics;
    
    // Job Statistics
    private JobStatistics jobStatistics;
    
    // Application Statistics
    private ApplicationStatistics applicationStatistics;
    
    // System Statistics
    private SystemStatistics systemStatistics;
    
    // Time-based analytics
    private List<TimeSeriesData> userRegistrationTrends;
    private List<TimeSeriesData> jobPostingTrends;
    private List<TimeSeriesData> applicationTrends;
    
    // Geographic data
    private Map<String, Long> jobsByLocation;
    private Map<String, Long> candidatesByLocation;
    
    // Generated timestamp
    private LocalDateTime generatedAt;
    
    // Constructors
    public DashboardAnalyticsResponse() {
        this.generatedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public UserStatistics getUserStatistics() {
        return userStatistics;
    }
    
    public void setUserStatistics(UserStatistics userStatistics) {
        this.userStatistics = userStatistics;
    }
    
    public JobStatistics getJobStatistics() {
        return jobStatistics;
    }
    
    public void setJobStatistics(JobStatistics jobStatistics) {
        this.jobStatistics = jobStatistics;
    }
    
    public ApplicationStatistics getApplicationStatistics() {
        return applicationStatistics;
    }
    
    public void setApplicationStatistics(ApplicationStatistics applicationStatistics) {
        this.applicationStatistics = applicationStatistics;
    }
    
    public SystemStatistics getSystemStatistics() {
        return systemStatistics;
    }
    
    public void setSystemStatistics(SystemStatistics systemStatistics) {
        this.systemStatistics = systemStatistics;
    }
    
    public List<TimeSeriesData> getUserRegistrationTrends() {
        return userRegistrationTrends;
    }
    
    public void setUserRegistrationTrends(List<TimeSeriesData> userRegistrationTrends) {
        this.userRegistrationTrends = userRegistrationTrends;
    }
    
    public List<TimeSeriesData> getJobPostingTrends() {
        return jobPostingTrends;
    }
    
    public void setJobPostingTrends(List<TimeSeriesData> jobPostingTrends) {
        this.jobPostingTrends = jobPostingTrends;
    }
    
    public List<TimeSeriesData> getApplicationTrends() {
        return applicationTrends;
    }
    
    public void setApplicationTrends(List<TimeSeriesData> applicationTrends) {
        this.applicationTrends = applicationTrends;
    }
    
    public Map<String, Long> getJobsByLocation() {
        return jobsByLocation;
    }
    
    public void setJobsByLocation(Map<String, Long> jobsByLocation) {
        this.jobsByLocation = jobsByLocation;
    }
    
    public Map<String, Long> getCandidatesByLocation() {
        return candidatesByLocation;
    }
    
    public void setCandidatesByLocation(Map<String, Long> candidatesByLocation) {
        this.candidatesByLocation = candidatesByLocation;
    }
    
    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }
    
    public void setGeneratedAt(LocalDateTime generatedAt) {
        this.generatedAt = generatedAt;
    }
    
    // Inner classes for statistics
    public static class UserStatistics {
        private long totalCandidates;
        private long activeCandidates;
        private long blockedCandidates;
        private long totalEmployers;
        private long approvedEmployers;
        private long pendingEmployers;
        private long rejectedEmployers;
        private long blockedEmployers;
        private long newCandidatesThisMonth;
        private long newEmployersThisMonth;
        
        // Constructors, getters and setters
        public UserStatistics() {}
        
        public long getTotalCandidates() { return totalCandidates; }
        public void setTotalCandidates(long totalCandidates) { this.totalCandidates = totalCandidates; }
        
        public long getActiveCandidates() { return activeCandidates; }
        public void setActiveCandidates(long activeCandidates) { this.activeCandidates = activeCandidates; }
        
        public long getBlockedCandidates() { return blockedCandidates; }
        public void setBlockedCandidates(long blockedCandidates) { this.blockedCandidates = blockedCandidates; }
        
        public long getTotalEmployers() { return totalEmployers; }
        public void setTotalEmployers(long totalEmployers) { this.totalEmployers = totalEmployers; }
        
        public long getApprovedEmployers() { return approvedEmployers; }
        public void setApprovedEmployers(long approvedEmployers) { this.approvedEmployers = approvedEmployers; }
        
        public long getPendingEmployers() { return pendingEmployers; }
        public void setPendingEmployers(long pendingEmployers) { this.pendingEmployers = pendingEmployers; }
        
        public long getRejectedEmployers() { return rejectedEmployers; }
        public void setRejectedEmployers(long rejectedEmployers) { this.rejectedEmployers = rejectedEmployers; }
        
        public long getBlockedEmployers() { return blockedEmployers; }
        public void setBlockedEmployers(long blockedEmployers) { this.blockedEmployers = blockedEmployers; }
        
        public long getNewCandidatesThisMonth() { return newCandidatesThisMonth; }
        public void setNewCandidatesThisMonth(long newCandidatesThisMonth) { this.newCandidatesThisMonth = newCandidatesThisMonth; }
        
        public long getNewEmployersThisMonth() { return newEmployersThisMonth; }
        public void setNewEmployersThisMonth(long newEmployersThisMonth) { this.newEmployersThisMonth = newEmployersThisMonth; }
    }
    
    public static class JobStatistics {
        private long totalJobs;
        private long activeJobs;
        private long inactiveJobs;
        private long jobsThisMonth;
        private Map<String, Long> jobsByType;
        private Map<String, Long> jobsBySalaryRange;
        
        // Constructors, getters and setters
        public JobStatistics() {}
        
        public long getTotalJobs() { return totalJobs; }
        public void setTotalJobs(long totalJobs) { this.totalJobs = totalJobs; }
        
        public long getActiveJobs() { return activeJobs; }
        public void setActiveJobs(long activeJobs) { this.activeJobs = activeJobs; }
        
        public long getInactiveJobs() { return inactiveJobs; }
        public void setInactiveJobs(long inactiveJobs) { this.inactiveJobs = inactiveJobs; }
        
        public long getJobsThisMonth() { return jobsThisMonth; }
        public void setJobsThisMonth(long jobsThisMonth) { this.jobsThisMonth = jobsThisMonth; }
        
        public Map<String, Long> getJobsByType() { return jobsByType; }
        public void setJobsByType(Map<String, Long> jobsByType) { this.jobsByType = jobsByType; }
        
        public Map<String, Long> getJobsBySalaryRange() { return jobsBySalaryRange; }
        public void setJobsBySalaryRange(Map<String, Long> jobsBySalaryRange) { this.jobsBySalaryRange = jobsBySalaryRange; }
    }
    
    public static class ApplicationStatistics {
        private long totalApplications;
        private long applicationsThisMonth;
        private Map<String, Long> applicationsByStatus;
        private double averageApplicationsPerJob;
        private double applicationSuccessRate;
        
        // Constructors, getters and setters
        public ApplicationStatistics() {}
        
        public long getTotalApplications() { return totalApplications; }
        public void setTotalApplications(long totalApplications) { this.totalApplications = totalApplications; }
        
        public long getApplicationsThisMonth() { return applicationsThisMonth; }
        public void setApplicationsThisMonth(long applicationsThisMonth) { this.applicationsThisMonth = applicationsThisMonth; }
        
        public Map<String, Long> getApplicationsByStatus() { return applicationsByStatus; }
        public void setApplicationsByStatus(Map<String, Long> applicationsByStatus) { this.applicationsByStatus = applicationsByStatus; }
        
        public double getAverageApplicationsPerJob() { return averageApplicationsPerJob; }
        public void setAverageApplicationsPerJob(double averageApplicationsPerJob) { this.averageApplicationsPerJob = averageApplicationsPerJob; }
        
        public double getApplicationSuccessRate() { return applicationSuccessRate; }
        public void setApplicationSuccessRate(double applicationSuccessRate) { this.applicationSuccessRate = applicationSuccessRate; }
    }
    
    public static class SystemStatistics {
        private long totalAdmins;
        private long activeAdmins;
        private long totalLogins;
        private long totalAdminActions;
        private LocalDateTime lastSystemUpdate;
        
        // Constructors, getters and setters
        public SystemStatistics() {}
        
        public long getTotalAdmins() { return totalAdmins; }
        public void setTotalAdmins(long totalAdmins) { this.totalAdmins = totalAdmins; }
        
        public long getActiveAdmins() { return activeAdmins; }
        public void setActiveAdmins(long activeAdmins) { this.activeAdmins = activeAdmins; }
        
        public long getTotalLogins() { return totalLogins; }
        public void setTotalLogins(long totalLogins) { this.totalLogins = totalLogins; }
        
        public long getTotalAdminActions() { return totalAdminActions; }
        public void setTotalAdminActions(long totalAdminActions) { this.totalAdminActions = totalAdminActions; }
        
        public LocalDateTime getLastSystemUpdate() { return lastSystemUpdate; }
        public void setLastSystemUpdate(LocalDateTime lastSystemUpdate) { this.lastSystemUpdate = lastSystemUpdate; }
    }
    
    public static class TimeSeriesData {
        private String date;
        private long count;
        
        public TimeSeriesData() {}
        
        public TimeSeriesData(String date, long count) {
            this.date = date;
            this.count = count;
        }
        
        public String getDate() { return date; }
        public void setDate(String date) { this.date = date; }
        
        public long getCount() { return count; }
        public void setCount(long count) { this.count = count; }
    }
}