package com.jobapp.user.service;

import com.jobapp.user.model.Candidate;
import com.jobapp.user.model.Employer;

/**
 * Service interface for notification operations
 * Requirements: 5.3
 */
public interface NotificationService {
    
    /**
     * Send employer approval notification
     * @param employer the approved employer
     */
    void sendEmployerApprovalNotification(Employer employer);
    
    /**
     * Send employer rejection notification
     * @param employer the rejected employer
     * @param reason the rejection reason
     */
    void sendEmployerRejectionNotification(Employer employer, String reason);
    
    /**
     * Send candidate block notification
     * @param candidate the blocked candidate
     * @param reason the block reason
     */
    void sendCandidateBlockNotification(Candidate candidate, String reason);
    
    /**
     * Send candidate unblock notification
     * @param candidate the unblocked candidate
     */
    void sendCandidateUnblockNotification(Candidate candidate);
    
    /**
     * Send employer block notification
     * @param employer the blocked employer
     * @param reason the block reason
     */
    void sendEmployerBlockNotification(Employer employer, String reason);
    
    /**
     * Send employer unblock notification
     * @param employer the unblocked employer
     */
    void sendEmployerUnblockNotification(Employer employer);
}