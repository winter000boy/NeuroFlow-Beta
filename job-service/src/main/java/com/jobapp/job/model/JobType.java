package com.jobapp.job.model;

/**
 * Enumeration for job types
 * Requirements: 2.1, 2.2
 */
public enum JobType {
    FULL_TIME("Full Time"),
    PART_TIME("Part Time"),
    CONTRACT("Contract"),
    TEMPORARY("Temporary"),
    INTERNSHIP("Internship"),
    REMOTE("Remote"),
    FREELANCE("Freelance");
    
    private final String displayName;
    
    JobType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    @Override
    public String toString() {
        return displayName;
    }
}