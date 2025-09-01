package com.jobapp.application.dto;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Error response DTO for API error handling
 * Requirements: 2.3, 2.5, 4.1, 4.2, 4.3
 */
public class ErrorResponse {
    
    private String code;
    private String message;
    private LocalDateTime timestamp;
    private String path;
    private Map<String, String> details;
    
    // Constructors
    public ErrorResponse() {
        this.timestamp = LocalDateTime.now();
    }
    
    public ErrorResponse(String code, String message) {
        this();
        this.code = code;
        this.message = message;
    }
    
    public ErrorResponse(String code, String message, String path) {
        this(code, message);
        this.path = path;
    }
    
    public ErrorResponse(String code, String message, String path, Map<String, String> details) {
        this(code, message, path);
        this.details = details;
    }
    
    // Getters and Setters
    public String getCode() {
        return code;
    }
    
    public void setCode(String code) {
        this.code = code;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    public String getPath() {
        return path;
    }
    
    public void setPath(String path) {
        this.path = path;
    }
    
    public Map<String, String> getDetails() {
        return details;
    }
    
    public void setDetails(Map<String, String> details) {
        this.details = details;
    }
    
    @Override
    public String toString() {
        return "ErrorResponse{" +
                "code='" + code + '\'' +
                ", message='" + message + '\'' +
                ", timestamp=" + timestamp +
                ", path='" + path + '\'' +
                ", details=" + details +
                '}';
    }
}