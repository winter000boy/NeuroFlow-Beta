package com.jobapp.job.model;

/**
 * Enumeration for salary periods
 * Requirements: 2.1, 2.2
 */
public enum SalaryPeriod {
    HOURLY("per hour"),
    DAILY("per day"),
    WEEKLY("per week"),
    MONTHLY("per month"),
    YEARLY("per year");
    
    private final String displayName;
    
    SalaryPeriod(String displayName) {
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