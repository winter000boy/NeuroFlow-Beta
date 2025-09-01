package com.jobapp.application.exception;

import com.jobapp.application.model.ApplicationStatus;

/**
 * Exception thrown when attempting an invalid status transition
 * Requirements: 4.1, 4.2, 4.3
 */
public class InvalidApplicationStatusException extends RuntimeException {
    
    public InvalidApplicationStatusException(String message) {
        super(message);
    }
    
    public InvalidApplicationStatusException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public InvalidApplicationStatusException(ApplicationStatus currentStatus, ApplicationStatus targetStatus) {
        super(String.format("Invalid status transition from %s to %s", currentStatus, targetStatus));
    }
}