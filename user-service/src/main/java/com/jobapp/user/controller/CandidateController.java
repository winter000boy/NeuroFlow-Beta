package com.jobapp.user.controller;

import com.jobapp.user.dto.CandidateRegistrationRequest;
import com.jobapp.user.dto.CandidateProfileUpdateRequest;
import com.jobapp.user.dto.CandidateResponse;
import com.jobapp.user.dto.MessageResponse;
import com.jobapp.user.service.CandidateService;

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

/**
 * REST controller for candidate management
 * Requirements: 1.1, 1.2, 1.4, 1.5
 */
@RestController
@RequestMapping("/api/candidates")
@Tag(name = "Candidate Management", description = "APIs for candidate registration and profile management")
public class CandidateController {
    
    @Autowired
    private CandidateService candidateService;
    
    /**
     * Register a new candidate
     * Requirements: 1.1, 1.2
     */
    @PostMapping("/register")
    @Operation(summary = "Register a new candidate", description = "Register a new candidate with email validation and password hashing")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Candidate registered successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "409", description = "Email already exists")
    })
    public ResponseEntity<CandidateResponse> registerCandidate(@Valid @RequestBody CandidateRegistrationRequest request) {
        CandidateResponse response = candidateService.registerCandidate(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    /**
     * Get candidate profile by ID
     * Requirements: 1.1
     */
    @GetMapping("/{candidateId}")
    @PreAuthorize("hasRole('CANDIDATE') or hasRole('EMPLOYER') or hasRole('ADMIN')")
    @Operation(summary = "Get candidate profile", description = "Retrieve candidate profile information by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Candidate profile retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Candidate not found"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<CandidateResponse> getCandidateProfile(@PathVariable String candidateId) {
        CandidateResponse response = candidateService.getCandidateProfile(candidateId);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get candidate profile by email
     * Requirements: 1.1
     */
    @GetMapping("/email/{email}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get candidate by email", description = "Retrieve candidate profile by email (admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Candidate profile retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Candidate not found"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<CandidateResponse> getCandidateByEmail(@PathVariable String email) {
        CandidateResponse response = candidateService.getCandidateByEmail(email);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Update candidate profile
     * Requirements: 1.4, 1.5
     */
    @PutMapping("/{candidateId}/profile")
    @PreAuthorize("hasRole('CANDIDATE') and #candidateId == authentication.principal.id")
    @Operation(summary = "Update candidate profile", description = "Update candidate profile information including social links")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Profile updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "404", description = "Candidate not found"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<CandidateResponse> updateCandidateProfile(
            @PathVariable String candidateId,
            @Valid @RequestBody CandidateProfileUpdateRequest request) {
        CandidateResponse response = candidateService.updateCandidateProfile(candidateId, request);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Update candidate resume URL
     * Requirements: 1.4
     */
    @PutMapping("/{candidateId}/resume")
    @PreAuthorize("hasRole('CANDIDATE') and #candidateId == authentication.principal.id")
    @Operation(summary = "Update resume URL", description = "Update candidate's resume URL after file upload")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Resume URL updated successfully"),
        @ApiResponse(responseCode = "404", description = "Candidate not found"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<CandidateResponse> updateResumeUrl(
            @PathVariable String candidateId,
            @RequestParam String resumeUrl) {
        CandidateResponse response = candidateService.updateResumeUrl(candidateId, resumeUrl);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Check if email exists
     */
    @GetMapping("/exists/{email}")
    @Operation(summary = "Check if email exists", description = "Check if a candidate with the given email already exists")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Email existence check completed")
    })
    public ResponseEntity<MessageResponse> checkEmailExists(@PathVariable String email) {
        boolean exists = candidateService.existsByEmail(email);
        String message = exists ? "Email already exists" : "Email is available";
        return ResponseEntity.ok(new MessageResponse(message));
    }
}