package com.jobapp.job.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Embedded document for salary range information
 * Requirements: 2.1, 2.2
 */
public class SalaryRange {
    
    @Min(value = 0, message = "Minimum salary must be non-negative")
    private Double min;
    
    @Min(value = 0, message = "Maximum salary must be non-negative")
    private Double max;
    
    @NotNull(message = "Currency is required")
    @Size(min = 3, max = 3, message = "Currency must be a 3-letter code")
    private String currency = "USD";
    
    @NotNull(message = "Salary period is required")
    private SalaryPeriod period = SalaryPeriod.YEARLY;
    
    private Boolean negotiable = false;
    
    // Constructors
    public SalaryRange() {}
    
    public SalaryRange(Double min, Double max, String currency, SalaryPeriod period) {
        this.min = min;
        this.max = max;
        this.currency = currency;
        this.period = period;
    }
    
    // Getters and Setters
    public Double getMin() {
        return min;
    }
    
    public void setMin(Double min) {
        this.min = min;
    }
    
    public Double getMax() {
        return max;
    }
    
    public void setMax(Double max) {
        this.max = max;
    }
    
    public String getCurrency() {
        return currency;
    }
    
    public void setCurrency(String currency) {
        this.currency = currency;
    }
    
    public SalaryPeriod getPeriod() {
        return period;
    }
    
    public void setPeriod(SalaryPeriod period) {
        this.period = period;
    }
    
    public Boolean getNegotiable() {
        return negotiable;
    }
    
    public void setNegotiable(Boolean negotiable) {
        this.negotiable = negotiable;
    }
    
    /**
     * Check if salary range is valid
     * @return true if min <= max (when both are provided)
     */
    public boolean isValid() {
        if (min != null && max != null) {
            return min <= max;
        }
        return true;
    }
    
    /**
     * Get formatted salary range string
     * @return formatted salary string
     */
    public String getFormattedRange() {
        StringBuilder sb = new StringBuilder();
        
        if (min != null && max != null) {
            sb.append(String.format("%.0f - %.0f", min, max));
        } else if (min != null) {
            sb.append(String.format("%.0f+", min));
        } else if (max != null) {
            sb.append(String.format("Up to %.0f", max));
        } else {
            return "Not specified";
        }
        
        sb.append(" ").append(currency);
        
        if (period != null) {
            sb.append(" ").append(period.getDisplayName());
        }
        
        if (Boolean.TRUE.equals(negotiable)) {
            sb.append(" (Negotiable)");
        }
        
        return sb.toString();
    }
    
    @Override
    public String toString() {
        return "SalaryRange{" +
                "min=" + min +
                ", max=" + max +
                ", currency='" + currency + '\'' +
                ", period=" + period +
                ", negotiable=" + negotiable +
                '}';
    }
}