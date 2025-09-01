package com.jobapp.job.dto;

/**
 * DTO for job statistics response
 * Requirements: 3.2, 3.3
 */
public class JobStatisticsResponse {
    
    private long totalJobs;
    private long activeJobs;
    private long inactiveJobs;
    private int totalApplications;
    private int totalViews;
    
    // Constructors
    public JobStatisticsResponse() {}
    
    public JobStatisticsResponse(long totalJobs, long activeJobs, long inactiveJobs, 
                                int totalApplications, int totalViews) {
        this.totalJobs = totalJobs;
        this.activeJobs = activeJobs;
        this.inactiveJobs = inactiveJobs;
        this.totalApplications = totalApplications;
        this.totalViews = totalViews;
    }
    
    // Getters and Setters
    public long getTotalJobs() {
        return totalJobs;
    }
    
    public void setTotalJobs(long totalJobs) {
        this.totalJobs = totalJobs;
    }
    
    public long getActiveJobs() {
        return activeJobs;
    }
    
    public void setActiveJobs(long activeJobs) {
        this.activeJobs = activeJobs;
    }
    
    public long getInactiveJobs() {
        return inactiveJobs;
    }
    
    public void setInactiveJobs(long inactiveJobs) {
        this.inactiveJobs = inactiveJobs;
    }
    
    public int getTotalApplications() {
        return totalApplications;
    }
    
    public void setTotalApplications(int totalApplications) {
        this.totalApplications = totalApplications;
    }
    
    public int getTotalViews() {
        return totalViews;
    }
    
    public void setTotalViews(int totalViews) {
        this.totalViews = totalViews;
    }
    
    @Override
    public String toString() {
        return "JobStatisticsResponse{" +
                "totalJobs=" + totalJobs +
                ", activeJobs=" + activeJobs +
                ", inactiveJobs=" + inactiveJobs +
                ", totalApplications=" + totalApplications +
                ", totalViews=" + totalViews +
                '}';
    }
}