package com.jobapp.user.dto;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Error response DTO
 */
public class ErrorResponse {
    
    private String code;
    private String message;
    private LocalDateTime timestamp;
    private Map<String, String> details;
    
    public ErrorResponse() {}
    
    public ErrorResponse(String code, String message, LocalDateTime timestamp, Map<String, String> details) {
        this.code = code;
        this.message = message;
        this.timestamp = timestamp;
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
    
    public Map<String, String> getDetails() {
        return details;
    }
    
    public void setDetails(Map<String, String> details) {
        this.details = details;
    }
}