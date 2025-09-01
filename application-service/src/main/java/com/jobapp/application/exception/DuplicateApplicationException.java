package com.jobapp.application.exception;

/**
 * Exception thrown when attempting to create a duplicate application
 * Requirements: 2.3, 2.5
 */
public class DuplicateApplicationException extends RuntimeException {
    
    public DuplicateApplicationException(String message) {
        super(message);
    }
    
    public DuplicateApplicationException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public DuplicateApplicationException(String candidateId, String jobId) {
        super(String.format("Application already exists for candidate %s and job %s", candidateId, jobId));
    }
}