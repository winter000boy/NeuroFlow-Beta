package com.jobapp.user.controller;

import com.jobapp.user.dto.FileUploadResponse;
import com.jobapp.user.dto.MessageResponse;
import com.jobapp.user.service.FileUploadService;
import com.jobapp.user.service.CandidateService;
import com.jobapp.user.service.EmployerService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * REST controller for file upload operations
 * Requirements: 1.4, 3.4, 9.4
 */
@RestController
@RequestMapping("/api/files")
@Tag(name = "File Upload", description = "APIs for file upload and management")
public class FileUploadController {
    
    @Autowired
    private FileUploadService fileUploadService;
    
    @Autowired
    private CandidateService candidateService;
    
    @Autowired
    private EmployerService employerService;
    
    /**
     * Upload resume for candidate
     * Requirements: 1.4
     */
    @PostMapping("/upload/resume/{candidateId}")
    @PreAuthorize("hasRole('CANDIDATE') and #candidateId == authentication.principal.id")
    @Operation(summary = "Upload resume", description = "Upload resume file for candidate")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Resume uploaded successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid file or file validation failed"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "413", description = "File size exceeds limit")
    })
    public ResponseEntity<FileUploadResponse> uploadResume(
            @PathVariable String candidateId,
            @RequestParam("file") MultipartFile file) {
        
        FileUploadResponse response = fileUploadService.uploadResume(file, candidateId);
        
        // Update candidate's resume URL
        candidateService.updateResumeUrl(candidateId, response.getFileUrl());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Upload logo for employer
     * Requirements: 3.4
     */
    @PostMapping("/upload/logo/{employerId}")
    @PreAuthorize("hasRole('EMPLOYER') and #employerId == authentication.principal.id")
    @Operation(summary = "Upload logo", description = "Upload logo file for employer")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Logo uploaded successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid file or file validation failed"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "413", description = "File size exceeds limit")
    })
    public ResponseEntity<FileUploadResponse> uploadLogo(
            @PathVariable String employerId,
            @RequestParam("file") MultipartFile file) {
        
        FileUploadResponse response = fileUploadService.uploadLogo(file, employerId);
        
        // Update employer's logo URL
        employerService.updateLogoUrl(employerId, response.getFileUrl());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Validate file before upload
     * Requirements: 9.4
     */
    @PostMapping("/validate")
    @Operation(summary = "Validate file", description = "Validate file without uploading")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "File is valid"),
        @ApiResponse(responseCode = "400", description = "File validation failed")
    })
    public ResponseEntity<MessageResponse> validateFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("category") String category) {
        
        fileUploadService.validateFileForUpload(file, category);
        return ResponseEntity.ok(new MessageResponse("File is valid for upload"));
    }
    
    /**
     * Download/serve uploaded files
     * Requirements: 9.4
     */
    @GetMapping("/{category}/{filename:.+}")
    @Operation(summary = "Download file", description = "Download or serve uploaded files")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "File served successfully"),
        @ApiResponse(responseCode = "404", description = "File not found")
    })
    public ResponseEntity<Resource> downloadFile(
            @PathVariable String category,
            @PathVariable String filename) {
        
        try {
            Path filePath = Paths.get("uploads", category, filename);
            Resource resource = new UrlResource(filePath.toUri());
            
            if (resource.exists() && resource.isReadable()) {
                String contentType = Files.probeContentType(filePath);
                if (contentType == null) {
                    contentType = "application/octet-stream";
                }
                
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION, 
                               "inline; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (IOException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Delete uploaded file
     * Requirements: 9.4
     */
    @DeleteMapping("/delete")
    @PreAuthorize("hasRole('CANDIDATE') or hasRole('EMPLOYER') or hasRole('ADMIN')")
    @Operation(summary = "Delete file", description = "Delete uploaded file")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "File deleted successfully"),
        @ApiResponse(responseCode = "404", description = "File not found"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<MessageResponse> deleteFile(@RequestParam String fileUrl) {
        boolean deleted = fileUploadService.deleteFile(fileUrl);
        
        if (deleted) {
            return ResponseEntity.ok(new MessageResponse("File deleted successfully"));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new MessageResponse("File not found"));
        }
    }
    
    /**
     * Get file upload limits and allowed types
     */
    @GetMapping("/limits")
    @Operation(summary = "Get upload limits", description = "Get file upload limits and allowed types")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Upload limits retrieved successfully")
    })
    public ResponseEntity<Object> getUploadLimits() {
        return ResponseEntity.ok(new Object() {
            public final Object resume = new Object() {
                public final String[] allowedTypes = {"application/pdf", "application/msword", 
                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document"};
                public final String[] allowedExtensions = {"pdf", "doc", "docx"};
                public final long maxSizeBytes = 5 * 1024 * 1024; // 5MB
                public final String maxSizeDisplay = "5MB";
            };
            
            public final Object logo = new Object() {
                public final String[] allowedTypes = {"image/jpeg", "image/jpg", "image/png", 
                    "image/gif", "image/webp"};
                public final String[] allowedExtensions = {"jpg", "jpeg", "png", "gif", "webp"};
                public final long maxSizeBytes = 2 * 1024 * 1024; // 2MB
                public final String maxSizeDisplay = "2MB";
            };
        });
    }
}