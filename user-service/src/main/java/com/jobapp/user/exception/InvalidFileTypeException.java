package com.jobapp.user.exception;

/**
 * Exception thrown when an invalid file type is uploaded
 */
public class InvalidFileTypeException extends RuntimeException {
    
    public InvalidFileTypeException(String message) {
        super(message);
    }
    
    public InvalidFileTypeException(String message, Throwable cause) {
        super(message, cause);
    }
}