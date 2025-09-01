package com.jobapp.user.exception;

/**
 * Exception thrown when uploaded file size exceeds the limit
 */
public class FileSizeExceededException extends RuntimeException {
    
    public FileSizeExceededException(String message) {
        super(message);
    }
    
    public FileSizeExceededException(String message, Throwable cause) {
        super(message, cause);
    }
}