package com.jobapp.application.controller;

import com.jobapp.application.dto.ApplicationResponse;
import com.jobapp.application.dto.CreateApplicationRequest;
import com.jobapp.application.dto.PagedResponse;
import com.jobapp.application.dto.UpdateApplicationStatusRequest;
import com.jobapp.application.service.ApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for job application operations
 * Requirements: 2.3, 2.4, 2.5, 4.1, 4.2, 4.3
 */
@RestController
@RequestMapping("/api/applications")
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(name = "Job Applications", description = "APIs for job application management, status tracking, and analytics")
@SecurityRequirement(name = "Bearer Authentication")
public class ApplicationController {
    
    private static final Logger logger = LoggerFactory.getLogger(ApplicationController.class);
    
    @Autowired
    private ApplicationService applicationService;
    
    /**
     * Create a new job application
     * Requirements: 2.3, 2.5
     */
    @PostMapping
    @PreAuthorize("hasRole('CANDIDATE')")
    @Operation(
            summary = "Create job application",
            description = "Submit a new job application for the authenticated candidate. Prevents duplicate applications for the same job."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Application created successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApplicationResponse.class),
                            examples = @ExampleObject(
                                    name = "Successful application",
                                    value = """
                                    {
                                        "id": "app123",
                                        "jobId": "job456",
                                        "candidateId": "candidate789",
                                        "employerId": "employer101",
                                        "status": "APPLIED",
                                        "appliedAt": "2024-01-15T10:30:00Z",
                                        "updatedAt": "2024-01-15T10:30:00Z"
                                    }
                                    """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request or duplicate application",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Duplicate application",
                                    value = """
                                    {
                                        "message": "You have already applied for this job"
                                    }
                                    """
                            )
                    )
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing JWT token"),
            @ApiResponse(responseCode = "403", description = "Forbidden - requires CANDIDATE role")
    })
    public ResponseEntity<ApplicationResponse> createApplication(
            @Parameter(description = "Job application details", required = true)
            @Valid @RequestBody CreateApplicationRequest request,
            Authentication authentication) {
        
        logger.info("Creating application for job: {} by candidate: {}", 
                   request.getJobId(), authentication.getName());
        
        String candidateId = authentication.getName(); // Assuming username is the candidate ID
        ApplicationResponse response = applicationService.createApplication(candidateId, request);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    /**
     * Get application by ID
     * Requirements: 2.4, 4.1, 4.2
     */
    @GetMapping("/{applicationId}")
    @PreAuthorize("hasRole('CANDIDATE') or hasRole('EMPLOYER') or hasRole('ADMIN')")
    @Operation(
            summary = "Get application details",
            description = "Retrieve detailed information about a specific job application. Access is restricted to the candidate who applied, the employer who posted the job, or admin users."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Application details retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApplicationResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "403", description = "Forbidden - no access to this application"),
            @ApiResponse(responseCode = "404", description = "Application not found")
    })
    public ResponseEntity<ApplicationResponse> getApplication(
            @Parameter(description = "Application ID", required = true, example = "app123")
            @PathVariable String applicationId,
            Authentication authentication) {
        
        logger.debug("Fetching application: {} by user: {}", applicationId, authentication.getName());
        
        ApplicationResponse response = applicationService.getApplicationById(applicationId);
        
        // Additional authorization check based on user role
        String userId = authentication.getName();
        boolean hasAccess = authentication.getAuthorities().stream()
            .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")) ||
            response.getCandidateId().equals(userId) ||
            response.getEmployerId().equals(userId);
        
        if (!hasAccess) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get applications for the authenticated candidate
     * Requirements: 2.4
     */
    @GetMapping("/candidate/my-applications")
    @PreAuthorize("hasRole('CANDIDATE')")
    @Operation(
            summary = "Get candidate's applications",
            description = "Retrieve all job applications submitted by the authenticated candidate with optional status filtering and pagination."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Applications retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PagedResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing JWT token"),
            @ApiResponse(responseCode = "403", description = "Forbidden - requires CANDIDATE role")
    })
    public ResponseEntity<PagedResponse<ApplicationResponse>> getCandidateApplications(
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Filter by application status", example = "APPLIED")
            @RequestParam(required = false) String status,
            Authentication authentication) {
        
        String candidateId = authentication.getName();
        logger.debug("Fetching applications for candidate: {}, page: {}, size: {}, status: {}", 
                    candidateId, page, size, status);
        
        PagedResponse<ApplicationResponse> response = applicationService.getCandidateApplications(
            candidateId, page, size, status);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get applications for a specific job (for employers)
     * Requirements: 4.1, 4.2
     */
    @GetMapping("/job/{jobId}")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<PagedResponse<ApplicationResponse>> getJobApplications(
            @PathVariable String jobId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            Authentication authentication) {
        
        String employerId = authentication.getName();
        logger.debug("Fetching applications for job: {} by employer: {}, page: {}, size: {}, status: {}", 
                    jobId, employerId, page, size, status);
        
        PagedResponse<ApplicationResponse> response = applicationService.getJobApplications(
            jobId, employerId, page, size, status);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get all applications for the authenticated employer
     * Requirements: 4.1, 4.2
     */
    @GetMapping("/employer/my-applications")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<PagedResponse<ApplicationResponse>> getEmployerApplications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            Authentication authentication) {
        
        String employerId = authentication.getName();
        logger.debug("Fetching applications for employer: {}, page: {}, size: {}, status: {}", 
                    employerId, page, size, status);
        
        PagedResponse<ApplicationResponse> response = applicationService.getEmployerApplications(
            employerId, page, size, status);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Update application status (for employers)
     * Requirements: 4.1, 4.2, 4.3
     */
    @PutMapping("/{applicationId}/status")
    @PreAuthorize("hasRole('EMPLOYER')")
    @Operation(
            summary = "Update application status",
            description = "Update the status of a job application. Only the employer who posted the job can update application status. Triggers email notification to the candidate."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Application status updated successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApplicationResponse.class),
                            examples = @ExampleObject(
                                    name = "Status updated",
                                    value = """
                                    {
                                        "id": "app123",
                                        "jobId": "job456",
                                        "candidateId": "candidate789",
                                        "employerId": "employer101",
                                        "status": "IN_REVIEW",
                                        "appliedAt": "2024-01-15T10:30:00Z",
                                        "updatedAt": "2024-01-16T14:20:00Z"
                                    }
                                    """
                            )
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Invalid status or request data"),
            @ApiResponse(responseCode = "403", description = "Forbidden - not authorized to update this application"),
            @ApiResponse(responseCode = "404", description = "Application not found")
    })
    public ResponseEntity<ApplicationResponse> updateApplicationStatus(
            @Parameter(description = "Application ID", required = true, example = "app123")
            @PathVariable String applicationId,
            @Parameter(description = "Status update request", required = true)
            @Valid @RequestBody UpdateApplicationStatusRequest request,
            Authentication authentication) {
        
        String employerId = authentication.getName();
        logger.info("Updating application: {} status to: {} by employer: {}", 
                   applicationId, request.getStatus(), employerId);
        
        ApplicationResponse response = applicationService.updateApplicationStatus(
            applicationId, employerId, request);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Withdraw application (for candidates)
     * Requirements: 2.4
     */
    @PutMapping("/{applicationId}/withdraw")
    @PreAuthorize("hasRole('CANDIDATE')")
    public ResponseEntity<ApplicationResponse> withdrawApplication(
            @PathVariable String applicationId,
            Authentication authentication) {
        
        String candidateId = authentication.getName();
        logger.info("Withdrawing application: {} by candidate: {}", applicationId, candidateId);
        
        ApplicationResponse response = applicationService.withdrawApplication(applicationId, candidateId);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get application statistics for the authenticated candidate
     * Requirements: 2.4
     */
    @GetMapping("/candidate/statistics")
    @PreAuthorize("hasRole('CANDIDATE')")
    public ResponseEntity<ApplicationService.ApplicationStatistics> getCandidateStatistics(
            Authentication authentication) {
        
        String candidateId = authentication.getName();
        logger.debug("Fetching application statistics for candidate: {}", candidateId);
        
        ApplicationService.ApplicationStatistics statistics = 
            applicationService.getCandidateApplicationStatistics(candidateId);
        
        return ResponseEntity.ok(statistics);
    }
    
    /**
     * Get application statistics for the authenticated employer
     * Requirements: 4.1, 4.2
     */
    @GetMapping("/employer/statistics")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<ApplicationService.ApplicationStatistics> getEmployerStatistics(
            Authentication authentication) {
        
        String employerId = authentication.getName();
        logger.debug("Fetching application statistics for employer: {}", employerId);
        
        ApplicationService.ApplicationStatistics statistics = 
            applicationService.getEmployerApplicationStatistics(employerId);
        
        return ResponseEntity.ok(statistics);
    }
}