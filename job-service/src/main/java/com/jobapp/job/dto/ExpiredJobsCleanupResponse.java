package com.jobapp.job.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for expired jobs cleanup response
 * Requirements: 2.2, 7.4
 */
public class ExpiredJobsCleanupResponse {
    
    private Integer totalExpiredJobs;
    private Integer deletedJobs;
    private Integer deactivatedJobs;
    private LocalDateTime cleanupTimestamp;
    private List<String> deletedJobIds;
    private List<String> deactivatedJobIds;
    
    // Constructors
    public ExpiredJobsCleanupResponse() {
        this.cleanupTimestamp = LocalDateTime.now();
    }
    
    public ExpiredJobsCleanupResponse(Integer totalExpiredJobs, Integer deletedJobs, 
                                    Integer deactivatedJobs) {
        this();
        this.totalExpiredJobs = totalExpiredJobs;
        this.deletedJobs = deletedJobs;
        this.deactivatedJobs = deactivatedJobs;
    }
    
    // Getters and Setters
    public Integer getTotalExpiredJobs() {
        return totalExpiredJobs;
    }
    
    public void setTotalExpiredJobs(Integer totalExpiredJobs) {
        this.totalExpiredJobs = totalExpiredJobs;
    }
    
    public Integer getDeletedJobs() {
        return deletedJobs;
    }
    
    public void setDeletedJobs(Integer deletedJobs) {
        this.deletedJobs = deletedJobs;
    }
    
    public Integer getDeactivatedJobs() {
        return deactivatedJobs;
    }
    
    public void setDeactivatedJobs(Integer deactivatedJobs) {
        this.deactivatedJobs = deactivatedJobs;
    }
    
    public LocalDateTime getCleanupTimestamp() {
        return cleanupTimestamp;
    }
    
    public void setCleanupTimestamp(LocalDateTime cleanupTimestamp) {
        this.cleanupTimestamp = cleanupTimestamp;
    }
    
    public List<String> getDeletedJobIds() {
        return deletedJobIds;
    }
    
    public void setDeletedJobIds(List<String> deletedJobIds) {
        this.deletedJobIds = deletedJobIds;
    }
    
    public List<String> getDeactivatedJobIds() {
        return deactivatedJobIds;
    }
    
    public void setDeactivatedJobIds(List<String> deactivatedJobIds) {
        this.deactivatedJobIds = deactivatedJobIds;
    }
    
    @Override
    public String toString() {
        return "ExpiredJobsCleanupResponse{" +
                "totalExpiredJobs=" + totalExpiredJobs +
                ", deletedJobs=" + deletedJobs +
                ", deactivatedJobs=" + deactivatedJobs +
                ", cleanupTimestamp=" + cleanupTimestamp +
                '}';
    }
}