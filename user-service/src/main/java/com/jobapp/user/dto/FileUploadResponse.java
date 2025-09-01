package com.jobapp.user.dto;

/**
 * DTO for file upload response
 * Requirements: 1.4, 3.4, 9.4
 */
public class FileUploadResponse {
    
    private String fileName;
    private String fileUrl;
    private String fileType;
    private long fileSize;
    private String message;
    
    // Constructors
    public FileUploadResponse() {}
    
    public FileUploadResponse(String fileName, String fileUrl, String fileType, long fileSize, String message) {
        this.fileName = fileName;
        this.fileUrl = fileUrl;
        this.fileType = fileType;
        this.fileSize = fileSize;
        this.message = message;
    }
    
    // Getters and Setters
    public String getFileName() {
        return fileName;
    }
    
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
    public String getFileUrl() {
        return fileUrl;
    }
    
    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }
    
    public String getFileType() {
        return fileType;
    }
    
    public void setFileType(String fileType) {
        this.fileType = fileType;
    }
    
    public long getFileSize() {
        return fileSize;
    }
    
    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
}