package com.jobapp.user.service;

import com.jobapp.user.dto.FileUploadResponse;
import com.jobapp.user.exception.FileUploadException;
import com.jobapp.user.exception.InvalidFileTypeException;
import com.jobapp.user.exception.FileSizeExceededException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Service for handling file uploads (resumes and logos)
 * Requirements: 1.4, 3.4, 9.4
 */
@Service
public class FileUploadService {
    
    // File type constants
    private static final List<String> ALLOWED_RESUME_TYPES = Arrays.asList(
        "application/pdf",
        "application/msword",
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
    );
    
    private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList(
        "image/jpeg",
        "image/jpg",
        "image/png",
        "image/gif",
        "image/webp"
    );
    
    // File size limits (in bytes)
    private static final long MAX_RESUME_SIZE = 5 * 1024 * 1024; // 5MB
    private static final long MAX_IMAGE_SIZE = 2 * 1024 * 1024;  // 2MB
    
    @Value("${app.file.upload.dir:uploads}")
    private String uploadDir;
    
    @Value("${app.file.base-url:http://localhost:8082}")
    private String baseUrl;
    
    /**
     * Upload resume file for candidate
     * Requirements: 1.4
     */
    public FileUploadResponse uploadResume(MultipartFile file, String candidateId) {
        validateFile(file, ALLOWED_RESUME_TYPES, MAX_RESUME_SIZE, "resume");
        
        String fileName = generateFileName(file.getOriginalFilename(), candidateId, "resume");
        String filePath = "resumes/" + fileName;
        
        return uploadFile(file, filePath, "Resume uploaded successfully");
    }
    
    /**
     * Upload logo file for employer
     * Requirements: 3.4
     */
    public FileUploadResponse uploadLogo(MultipartFile file, String employerId) {
        validateFile(file, ALLOWED_IMAGE_TYPES, MAX_IMAGE_SIZE, "logo");
        
        String fileName = generateFileName(file.getOriginalFilename(), employerId, "logo");
        String filePath = "logos/" + fileName;
        
        return uploadFile(file, filePath, "Logo uploaded successfully");
    }
    
    /**
     * Delete file from storage
     * Requirements: 9.4
     */
    public boolean deleteFile(String fileUrl) {
        try {
            // Extract file path from URL
            String filePath = fileUrl.replace(baseUrl + "/files/", "");
            Path targetPath = Paths.get(uploadDir, filePath);
            
            if (Files.exists(targetPath)) {
                Files.delete(targetPath);
                return true;
            }
            return false;
        } catch (IOException e) {
            throw new FileUploadException("Failed to delete file: " + e.getMessage(), e);
        }
    }
    
    /**
     * Validate uploaded file
     */
    private void validateFile(MultipartFile file, List<String> allowedTypes, long maxSize, String fileCategory) {
        if (file == null || file.isEmpty()) {
            throw new FileUploadException("File is empty or null");
        }
        
        // Check file size
        if (file.getSize() > maxSize) {
            throw new FileSizeExceededException(
                String.format("File size exceeds maximum allowed size of %d bytes for %s", maxSize, fileCategory)
            );
        }
        
        // Check file type
        String contentType = file.getContentType();
        if (contentType == null || !allowedTypes.contains(contentType.toLowerCase())) {
            throw new InvalidFileTypeException(
                String.format("Invalid file type for %s. Allowed types: %s", fileCategory, allowedTypes)
            );
        }
        
        // Check file extension
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.trim().isEmpty()) {
            throw new FileUploadException("File name is required");
        }
        
        String extension = getFileExtension(originalFilename).toLowerCase();
        if (!isValidExtension(extension, fileCategory)) {
            throw new InvalidFileTypeException(
                String.format("Invalid file extension '%s' for %s", extension, fileCategory)
            );
        }
        
        // Additional security checks
        if (containsSuspiciousContent(originalFilename)) {
            throw new FileUploadException("File name contains suspicious content");
        }
    }
    
    /**
     * Upload file to storage
     */
    private FileUploadResponse uploadFile(MultipartFile file, String filePath, String successMessage) {
        try {
            // Create upload directory if it doesn't exist
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            // Create subdirectory for file type
            Path targetDir = uploadPath.resolve(Paths.get(filePath).getParent());
            if (!Files.exists(targetDir)) {
                Files.createDirectories(targetDir);
            }
            
            // Save file
            Path targetPath = uploadPath.resolve(filePath);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            
            // Generate file URL
            String fileUrl = baseUrl + "/files/" + filePath;
            
            return new FileUploadResponse(
                Paths.get(filePath).getFileName().toString(),
                fileUrl,
                file.getContentType(),
                file.getSize(),
                successMessage
            );
            
        } catch (IOException e) {
            throw new FileUploadException("Failed to upload file: " + e.getMessage(), e);
        }
    }
    
    /**
     * Generate unique file name
     */
    private String generateFileName(String originalFilename, String userId, String category) {
        String extension = getFileExtension(originalFilename);
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String uniqueId = UUID.randomUUID().toString().substring(0, 8);
        
        return String.format("%s_%s_%s_%s.%s", category, userId, timestamp, uniqueId, extension);
    }
    
    /**
     * Get file extension from filename
     */
    private String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1 || lastDotIndex == filename.length() - 1) {
            return "";
        }
        return filename.substring(lastDotIndex + 1);
    }
    
    /**
     * Check if file extension is valid for the category
     */
    private boolean isValidExtension(String extension, String category) {
        switch (category.toLowerCase()) {
            case "resume":
                return Arrays.asList("pdf", "doc", "docx").contains(extension);
            case "logo":
                return Arrays.asList("jpg", "jpeg", "png", "gif", "webp").contains(extension);
            default:
                return false;
        }
    }
    
    /**
     * Check for suspicious content in filename
     */
    private boolean containsSuspiciousContent(String filename) {
        String lowerFilename = filename.toLowerCase();
        
        // Check for suspicious extensions
        List<String> suspiciousExtensions = Arrays.asList(
            ".exe", ".bat", ".cmd", ".com", ".pif", ".scr", ".vbs", ".js", ".jar", ".sh"
        );
        
        for (String ext : suspiciousExtensions) {
            if (lowerFilename.contains(ext)) {
                return true;
            }
        }
        
        // Check for path traversal attempts
        if (filename.contains("..") || filename.contains("/") || filename.contains("\\")) {
            return true;
        }
        
        // Check for null bytes
        if (filename.contains("\0")) {
            return true;
        }
        
        return false;
    }
    
    /**
     * Get file info without uploading
     */
    public void validateFileForUpload(MultipartFile file, String fileCategory) {
        if ("resume".equalsIgnoreCase(fileCategory)) {
            validateFile(file, ALLOWED_RESUME_TYPES, MAX_RESUME_SIZE, "resume");
        } else if ("logo".equalsIgnoreCase(fileCategory)) {
            validateFile(file, ALLOWED_IMAGE_TYPES, MAX_IMAGE_SIZE, "logo");
        } else {
            throw new InvalidFileTypeException("Invalid file category: " + fileCategory);
        }
    }
}