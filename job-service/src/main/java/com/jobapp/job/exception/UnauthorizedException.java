package com.jobapp.job.exception;

/**
 * Exception thrown when user is not authorized to perform an action
 * Requirements: 3.2, 3.3
 */
public class UnauthorizedException extends RuntimeException {
    
    public UnauthorizedException(String message) {
        super(message);
    }
    
    public UnauthorizedException(String message, Throwable cause) {
        super(message, cause);
    }
}