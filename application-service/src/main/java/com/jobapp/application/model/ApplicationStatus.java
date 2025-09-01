package com.jobapp.application.model;

/**
 * Enumeration for application statuses
 * Requirements: 2.3, 2.5, 4.1, 4.2, 4.3
 */
public enum ApplicationStatus {
    APPLIED("Applied", "Application has been submitted"),
    IN_REVIEW("In Review", "Application is being reviewed by employer"),
    INTERVIEW_SCHEDULED("Interview Scheduled", "Interview has been scheduled"),
    INTERVIEWED("Interviewed", "Interview has been completed"),
    OFFER_MADE("Offer Made", "Job offer has been extended"),
    OFFER_ACCEPTED("Offer Accepted", "Candidate has accepted the offer"),
    OFFER_DECLINED("Offer Declined", "Candidate has declined the offer"),
    HIRED("Hired", "Candidate has been hired"),
    REJECTED("Rejected", "Application has been rejected"),
    WITHDRAWN("Withdrawn", "Application has been withdrawn by candidate");
    
    private final String displayName;
    private final String description;
    
    ApplicationStatus(String displayName, String description) {
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
     * Check if this status is a final status (no further changes expected)
     * @return true if this is a final status
     */
    public boolean isFinal() {
        return this == HIRED || this == REJECTED || this == WITHDRAWN || this == OFFER_DECLINED;
    }
    
    /**
     * Check if this status indicates a positive outcome
     * @return true if this is a positive status
     */
    public boolean isPositive() {
        return this == HIRED || this == OFFER_MADE || this == OFFER_ACCEPTED;
    }
    
    /**
     * Check if this status indicates a negative outcome
     * @return true if this is a negative status
     */
    public boolean isNegative() {
        return this == REJECTED || this == WITHDRAWN || this == OFFER_DECLINED;
    }
    
    /**
     * Check if this status is in progress
     * @return true if this status indicates ongoing process
     */
    public boolean isInProgress() {
        return !isFinal();
    }
    
    /**
     * Get the next possible statuses from current status
     * @return array of possible next statuses
     */
    public ApplicationStatus[] getNextPossibleStatuses() {
        switch (this) {
            case APPLIED:
                return new ApplicationStatus[]{IN_REVIEW, REJECTED, WITHDRAWN};
            case IN_REVIEW:
                return new ApplicationStatus[]{INTERVIEW_SCHEDULED, REJECTED, WITHDRAWN};
            case INTERVIEW_SCHEDULED:
                return new ApplicationStatus[]{INTERVIEWED, REJECTED, WITHDRAWN};
            case INTERVIEWED:
                return new ApplicationStatus[]{OFFER_MADE, REJECTED, WITHDRAWN};
            case OFFER_MADE:
                return new ApplicationStatus[]{OFFER_ACCEPTED, OFFER_DECLINED, WITHDRAWN};
            case OFFER_ACCEPTED:
                return new ApplicationStatus[]{HIRED};
            case OFFER_DECLINED:
            case HIRED:
            case REJECTED:
            case WITHDRAWN:
                return new ApplicationStatus[]{}; // Final statuses
            default:
                return new ApplicationStatus[]{};
        }
    }
    
    /**
     * Check if transition to another status is valid
     * @param targetStatus the target status
     * @return true if transition is valid
     */
    public boolean canTransitionTo(ApplicationStatus targetStatus) {
        ApplicationStatus[] possibleStatuses = getNextPossibleStatuses();
        for (ApplicationStatus status : possibleStatuses) {
            if (status == targetStatus) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public String toString() {
        return displayName;
    }
}