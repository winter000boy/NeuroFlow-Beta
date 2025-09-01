package com.jobapp.user.controller;

import com.jobapp.user.dto.EmployerRegistrationRequest;
import com.jobapp.user.dto.EmployerProfileUpdateRequest;
import com.jobapp.user.dto.EmployerResponse;
import com.jobapp.user.dto.MessageResponse;
import com.jobapp.user.service.EmployerService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.util.List;

/**
 * REST controller for employer management
 * Requirements: 3.1, 3.4
 */
@RestController
@RequestMapping("/api/employers")
@Tag(name = "Employer Management", description = "APIs for employer registration and profile management")
public class EmployerController {
    
    @Autowired
    private EmployerService employerService;
    
    /**
     * Register a new employer
     * Requirements: 3.1
     */
    @PostMapping("/register")
    @Operation(summary = "Register a new employer", description = "Register a new employer with company information validation")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Employer registered successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "409", description = "Email already exists")
    })
    public ResponseEntity<EmployerResponse> registerEmployer(@Valid @RequestBody EmployerRegistrationRequest request) {
        EmployerResponse response = employerService.registerEmployer(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    /**
     * Get employer profile by ID
     * Requirements: 3.1
     */
    @GetMapping("/{employerId}")
    @PreAuthorize("hasRole('EMPLOYER') or hasRole('ADMIN')")
    @Operation(summary = "Get employer profile", description = "Retrieve employer profile information by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Employer profile retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Employer not found"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<EmployerResponse> getEmployerProfile(@PathVariable String employerId) {
        EmployerResponse response = employerService.getEmployerProfile(employerId);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get employer profile by email
     * Requirements: 3.1
     */
    @GetMapping("/email/{email}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get employer by email", description = "Retrieve employer profile by email (admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Employer profile retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Employer not found"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<EmployerResponse> getEmployerByEmail(@PathVariable String email) {
        EmployerResponse response = employerService.getEmployerByEmail(email);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Update employer profile
     * Requirements: 3.4
     */
    @PutMapping("/{employerId}/profile")
    @PreAuthorize("hasRole('EMPLOYER') and #employerId == authentication.principal.id")
    @Operation(summary = "Update employer profile", description = "Update employer company profile information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Profile updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "404", description = "Employer not found"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<EmployerResponse> updateEmployerProfile(
            @PathVariable String employerId,
            @Valid @RequestBody EmployerProfileUpdateRequest request) {
        EmployerResponse response = employerService.updateEmployerProfile(employerId, request);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Update employer logo URL
     * Requirements: 3.4
     */
    @PutMapping("/{employerId}/logo")
    @PreAuthorize("hasRole('EMPLOYER') and #employerId == authentication.principal.id")
    @Operation(summary = "Update logo URL", description = "Update employer's logo URL after file upload")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Logo URL updated successfully"),
        @ApiResponse(responseCode = "404", description = "Employer not found"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<EmployerResponse> updateLogoUrl(
            @PathVariable String employerId,
            @RequestParam String logoUrl) {
        EmployerResponse response = employerService.updateLogoUrl(employerId, logoUrl);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Approve employer account (Admin only)
     * Requirements: 3.4
     */
    @PutMapping("/{employerId}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Approve employer", description = "Approve employer account for job posting (admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Employer approved successfully"),
        @ApiResponse(responseCode = "404", description = "Employer not found"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<EmployerResponse> approveEmployer(
            @PathVariable String employerId,
            @RequestParam String adminId) {
        EmployerResponse response = employerService.approveEmployer(employerId, adminId);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Reject employer account (Admin only)
     * Requirements: 3.4
     */
    @PutMapping("/{employerId}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Reject employer", description = "Reject employer account with reason (admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Employer rejected successfully"),
        @ApiResponse(responseCode = "404", description = "Employer not found"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<EmployerResponse> rejectEmployer(
            @PathVariable String employerId,
            @RequestParam String rejectionReason) {
        EmployerResponse response = employerService.rejectEmployer(employerId, rejectionReason);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get pending employers for approval (Admin only)
     * Requirements: 3.4
     */
    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get pending employers", description = "Get all employers pending approval (admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Pending employers retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<List<EmployerResponse>> getPendingEmployers() {
        List<EmployerResponse> response = employerService.getPendingEmployers();
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get approved employers
     * Requirements: 3.1
     */
    @GetMapping("/approved")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CANDIDATE')")
    @Operation(summary = "Get approved employers", description = "Get all approved employers")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Approved employers retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<List<EmployerResponse>> getApprovedEmployers() {
        List<EmployerResponse> response = employerService.getApprovedEmployers();
        return ResponseEntity.ok(response);
    }
    
    /**
     * Check if email exists
     */
    @GetMapping("/exists/{email}")
    @Operation(summary = "Check if email exists", description = "Check if an employer with the given email already exists")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Email existence check completed")
    })
    public ResponseEntity<MessageResponse> checkEmailExists(@PathVariable String email) {
        boolean exists = employerService.existsByEmail(email);
        String message = exists ? "Email already exists" : "Email is available";
        return ResponseEntity.ok(new MessageResponse(message));
    }
    
    /**
     * Check if employer can post jobs
     */
    @GetMapping("/{employerId}/can-post-jobs")
    @PreAuthorize("hasRole('EMPLOYER') and #employerId == authentication.principal.id")
    @Operation(summary = "Check job posting eligibility", description = "Check if employer can post jobs")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Job posting eligibility checked"),
        @ApiResponse(responseCode = "404", description = "Employer not found"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<MessageResponse> canPostJobs(@PathVariable String employerId) {
        boolean canPost = employerService.canPostJobs(employerId);
        String message = canPost ? "Employer can post jobs" : "Employer cannot post jobs - approval required";
        return ResponseEntity.ok(new MessageResponse(message));
    }
}