package com.jobapp.application.exception;

/**
 * Exception thrown when a requested resource is not found
 * Requirements: 2.3, 2.5, 4.1, 4.2, 4.3
 */
public class ResourceNotFoundException extends RuntimeException {
    
    public ResourceNotFoundException(String message) {
        super(message);
    }
    
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public ResourceNotFoundException(String resourceType, String identifier) {
        super(String.format("%s not found with identifier: %s", resourceType, identifier));
    }
}