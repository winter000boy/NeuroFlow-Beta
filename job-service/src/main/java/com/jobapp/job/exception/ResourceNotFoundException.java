package com.jobapp.job.exception;

/**
 * Exception thrown when a requested resource is not found
 * Requirements: 3.2, 3.3
 */
public class ResourceNotFoundException extends RuntimeException {
    
    public ResourceNotFoundException(String message) {
        super(message);
    }
    
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}