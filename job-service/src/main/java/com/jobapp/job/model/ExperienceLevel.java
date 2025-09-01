package com.jobapp.job.model;

/**
 * Enumeration for experience levels
 * Requirements: 2.1, 2.2
 */
public enum ExperienceLevel {
    ENTRY_LEVEL("Entry Level", 0, 2),
    JUNIOR("Junior", 1, 3),
    MID_LEVEL("Mid Level", 3, 5),
    SENIOR("Senior", 5, 8),
    LEAD("Lead", 7, 12),
    EXECUTIVE("Executive", 10, 20);
    
    private final String displayName;
    private final int minYears;
    private final int maxYears;
    
    ExperienceLevel(String displayName, int minYears, int maxYears) {
        this.displayName = displayName;
        this.minYears = minYears;
        this.maxYears = maxYears;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public int getMinYears() {
        return minYears;
    }
    
    public int getMaxYears() {
        return maxYears;
    }
    
    @Override
    public String toString() {
        return displayName;
    }
}