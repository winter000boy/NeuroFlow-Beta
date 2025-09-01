package com.jobapp.job.controller;

import com.jobapp.job.dto.*;
import com.jobapp.job.model.JobType;
import com.jobapp.job.model.ExperienceLevel;
import com.jobapp.job.service.JobService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * REST controller for job posting and management operations
 * Requirements: 3.2, 3.3
 */
@RestController
@RequestMapping("/api/jobs")
@Tag(name = "Job Management", description = "APIs for job posting, search, and management with full-text search capabilities and caching")
@SecurityRequirement(name = "Bearer Authentication")
public class JobController {
    
    private final JobService jobService;
    
    @Autowired
    public JobController(JobService jobService) {
        this.jobService = jobService;
    }
    
    /**
     * Create a new job posting
     */
    @PostMapping
    @PreAuthorize("hasRole('EMPLOYER')")
    @Operation(
            summary = "Create a new job posting", 
            description = "Create a new job posting for the authenticated employer. The job will be created in active status and indexed for search."
    )
    @ApiResponses(value = {
        @ApiResponse(
                responseCode = "201", 
                description = "Job created successfully",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = JobResponse.class),
                        examples = @ExampleObject(
                                name = "Successful job creation",
                                value = """
                                {
                                    "id": "job123",
                                    "title": "Senior Software Engineer",
                                    "description": "We are looking for an experienced software engineer...",
                                    "employerId": "employer456",
                                    "location": "San Francisco, CA",
                                    "jobType": "FULL_TIME",
                                    "experienceLevel": "SENIOR",
                                    "salary": {
                                        "min": 120000,
                                        "max": 180000,
                                        "currency": "USD"
                                    },
                                    "skills": ["Java", "Spring Boot", "MongoDB"],
                                    "isActive": true,
                                    "createdAt": "2024-01-15T10:30:00Z"
                                }
                                """
                        )
                )
        ),
        @ApiResponse(responseCode = "400", description = "Invalid job data - validation errors"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing JWT token"),
        @ApiResponse(responseCode = "403", description = "Forbidden - requires EMPLOYER role")
    })
    public ResponseEntity<JobResponse> createJob(
            @Parameter(description = "Job creation request with all required details", required = true)
            @Valid @RequestBody CreateJobRequest request,
            Authentication authentication) {
        
        String employerId = authentication.getName(); // Assuming the principal is the user ID
        JobResponse jobResponse = jobService.createJob(employerId, request);
        
        return new ResponseEntity<>(jobResponse, HttpStatus.CREATED);
    }
    
    /**
     * Update an existing job posting
     */
    @PutMapping("/{jobId}")
    @PreAuthorize("hasRole('EMPLOYER')")
    @Operation(summary = "Update a job posting", description = "Update an existing job posting")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Job updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid job data"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Not the job owner"),
        @ApiResponse(responseCode = "404", description = "Job not found")
    })
    public ResponseEntity<JobResponse> updateJob(
            @Parameter(description = "Job ID") @PathVariable String jobId,
            @Valid @RequestBody UpdateJobRequest request,
            Authentication authentication) {
        
        String employerId = authentication.getName();
        JobResponse jobResponse = jobService.updateJob(jobId, employerId, request);
        
        return ResponseEntity.ok(jobResponse);
    }
    
    /**
     * Activate a job posting
     */
    @PutMapping("/{jobId}/activate")
    @PreAuthorize("hasRole('EMPLOYER')")
    @Operation(summary = "Activate a job posting", description = "Activate a job posting to make it visible to candidates")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Job activated successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Not the job owner"),
        @ApiResponse(responseCode = "404", description = "Job not found")
    })
    public ResponseEntity<JobResponse> activateJob(
            @Parameter(description = "Job ID") @PathVariable String jobId,
            Authentication authentication) {
        
        String employerId = authentication.getName();
        JobResponse jobResponse = jobService.toggleJobStatus(jobId, employerId, true);
        
        return ResponseEntity.ok(jobResponse);
    }
    
    /**
     * Deactivate a job posting
     */
    @PutMapping("/{jobId}/deactivate")
    @PreAuthorize("hasRole('EMPLOYER')")
    @Operation(summary = "Deactivate a job posting", description = "Deactivate a job posting to hide it from candidates")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Job deactivated successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Not the job owner"),
        @ApiResponse(responseCode = "404", description = "Job not found")
    })
    public ResponseEntity<JobResponse> deactivateJob(
            @Parameter(description = "Job ID") @PathVariable String jobId,
            Authentication authentication) {
        
        String employerId = authentication.getName();
        JobResponse jobResponse = jobService.toggleJobStatus(jobId, employerId, false);
        
        return ResponseEntity.ok(jobResponse);
    }
    
    /**
     * Get jobs by employer with pagination
     */
    @GetMapping("/employer")
    @PreAuthorize("hasRole('EMPLOYER')")
    @Operation(summary = "Get jobs by employer", description = "Get all jobs posted by the authenticated employer")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Jobs retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Employer role required")
    })
    public ResponseEntity<PagedResponse<JobSummaryResponse>> getJobsByEmployer(
            @Parameter(description = "Page number (0-based)") 
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @Parameter(description = "Page size") 
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size,
            @Parameter(description = "Sort field") 
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction (asc/desc)") 
            @RequestParam(defaultValue = "desc") String sortDir,
            Authentication authentication) {
        
        String employerId = authentication.getName();
        PagedResponse<JobSummaryResponse> response = jobService.getJobsByEmployer(
                employerId, page, size, sortBy, sortDir);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get active jobs by employer with pagination
     */
    @GetMapping("/employer/active")
    @PreAuthorize("hasRole('EMPLOYER')")
    @Operation(summary = "Get active jobs by employer", description = "Get all active jobs posted by the authenticated employer")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Active jobs retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Employer role required")
    })
    public ResponseEntity<PagedResponse<JobSummaryResponse>> getActiveJobsByEmployer(
            @Parameter(description = "Page number (0-based)") 
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @Parameter(description = "Page size") 
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size,
            @Parameter(description = "Sort field") 
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction (asc/desc)") 
            @RequestParam(defaultValue = "desc") String sortDir,
            Authentication authentication) {
        
        String employerId = authentication.getName();
        PagedResponse<JobSummaryResponse> response = jobService.getActiveJobsByEmployer(
                employerId, page, size, sortBy, sortDir);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get job by ID for employer
     */
    @GetMapping("/employer/{jobId}")
    @PreAuthorize("hasRole('EMPLOYER')")
    @Operation(summary = "Get job by ID for employer", description = "Get job details for the authenticated employer")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Job retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Not the job owner"),
        @ApiResponse(responseCode = "404", description = "Job not found")
    })
    public ResponseEntity<JobResponse> getJobByIdForEmployer(
            @Parameter(description = "Job ID") @PathVariable String jobId,
            Authentication authentication) {
        
        String employerId = authentication.getName();
        JobResponse jobResponse = jobService.getJobByIdForEmployer(jobId, employerId);
        
        return ResponseEntity.ok(jobResponse);
    }
    
    /**
     * Delete a job posting
     */
    @DeleteMapping("/{jobId}")
    @PreAuthorize("hasRole('EMPLOYER')")
    @Operation(summary = "Delete a job posting", description = "Delete a job posting")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Job deleted successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Not the job owner"),
        @ApiResponse(responseCode = "404", description = "Job not found")
    })
    public ResponseEntity<Void> deleteJob(
            @Parameter(description = "Job ID") @PathVariable String jobId,
            Authentication authentication) {
        
        String employerId = authentication.getName();
        jobService.deleteJob(jobId, employerId);
        
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Get job statistics for employer
     */
    @GetMapping("/employer/statistics")
    @PreAuthorize("hasRole('EMPLOYER')")
    @Operation(summary = "Get job statistics", description = "Get job statistics for the authenticated employer")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Employer role required")
    })
    public ResponseEntity<JobStatisticsResponse> getJobStatistics(Authentication authentication) {
        String employerId = authentication.getName();
        JobStatisticsResponse statistics = jobService.getJobStatistics(employerId);
        
        return ResponseEntity.ok(statistics);
    }
    
    /**
     * Search jobs with filters and full-text search (public endpoint)
     */
    @GetMapping("/search")
    @Operation(
            summary = "Search jobs with advanced filtering", 
            description = "Search jobs using full-text search on title and description with advanced filtering options. Results are cached for performance. This is a public endpoint that doesn't require authentication."
    )
    @ApiResponses(value = {
        @ApiResponse(
                responseCode = "200", 
                description = "Jobs retrieved successfully",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = PagedResponse.class),
                        examples = @ExampleObject(
                                name = "Search results",
                                value = """
                                {
                                    "content": [
                                        {
                                            "id": "job123",
                                            "title": "Senior Software Engineer",
                                            "companyName": "Tech Corp",
                                            "location": "San Francisco, CA",
                                            "jobType": "FULL_TIME",
                                            "salary": {
                                                "min": 120000,
                                                "max": 180000,
                                                "currency": "USD"
                                            },
                                            "createdAt": "2024-01-15T10:30:00Z"
                                        }
                                    ],
                                    "page": 0,
                                    "size": 10,
                                    "totalElements": 1,
                                    "totalPages": 1
                                }
                                """
                        )
                )
        ),
        @ApiResponse(responseCode = "400", description = "Invalid search parameters")
    })
    public ResponseEntity<PagedResponse<JobSummaryResponse>> searchJobs(
            @Parameter(description = "Search text for title and description") 
            @RequestParam(required = false) String search,
            @Parameter(description = "Location filter") 
            @RequestParam(required = false) String location,
            @Parameter(description = "Job type filter") 
            @RequestParam(required = false) JobType jobType,
            @Parameter(description = "Experience level filter") 
            @RequestParam(required = false) ExperienceLevel experienceLevel,
            @Parameter(description = "Minimum salary") 
            @RequestParam(required = false) Double minSalary,
            @Parameter(description = "Maximum salary") 
            @RequestParam(required = false) Double maxSalary,
            @Parameter(description = "Required skills (comma-separated)") 
            @RequestParam(required = false) String skills,
            @Parameter(description = "Page number (0-based)") 
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @Parameter(description = "Page size") 
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size,
            @Parameter(description = "Sort field") 
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction (asc/desc)") 
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        PagedResponse<JobSummaryResponse> response = jobService.searchJobs(
                search, location, jobType, experienceLevel, minSalary, maxSalary, 
                skills, page, size, sortBy, sortDir);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get featured jobs (public endpoint)
     */
    @GetMapping("/featured")
    @Operation(summary = "Get featured jobs", description = "Get featured job listings")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Featured jobs retrieved successfully")
    })
    public ResponseEntity<PagedResponse<JobSummaryResponse>> getFeaturedJobs(
            @Parameter(description = "Page number (0-based)") 
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @Parameter(description = "Page size") 
            @RequestParam(defaultValue = "10") @Min(1) @Max(50) int size) {
        
        PagedResponse<JobSummaryResponse> response = jobService.getFeaturedJobs(page, size);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get recent jobs (public endpoint)
     */
    @GetMapping("/recent")
    @Operation(summary = "Get recent jobs", description = "Get recently posted job listings")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Recent jobs retrieved successfully")
    })
    public ResponseEntity<PagedResponse<JobSummaryResponse>> getRecentJobs(
            @Parameter(description = "Number of days to look back") 
            @RequestParam(defaultValue = "7") @Min(1) @Max(30) int days,
            @Parameter(description = "Page number (0-based)") 
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @Parameter(description = "Page size") 
            @RequestParam(defaultValue = "10") @Min(1) @Max(50) int size) {
        
        PagedResponse<JobSummaryResponse> response = jobService.getRecentJobs(days, page, size);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get popular jobs (public endpoint)
     */
    @GetMapping("/popular")
    @Operation(summary = "Get popular jobs", description = "Get popular job listings based on application count")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Popular jobs retrieved successfully")
    })
    public ResponseEntity<PagedResponse<JobSummaryResponse>> getPopularJobs(
            @Parameter(description = "Page number (0-based)") 
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @Parameter(description = "Page size") 
            @RequestParam(defaultValue = "10") @Min(1) @Max(50) int size) {
        
        PagedResponse<JobSummaryResponse> response = jobService.getPopularJobs(page, size);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get job details by ID (public endpoint for SEO)
     */
    @GetMapping("/{jobId}")
    @Operation(summary = "Get job details", description = "Get detailed job information by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Job details retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Job not found")
    })
    public ResponseEntity<JobResponse> getJobDetails(
            @Parameter(description = "Job ID") @PathVariable String jobId) {
        
        JobResponse jobResponse = jobService.getJobById(jobId);
        
        return ResponseEntity.ok(jobResponse);
    }
    
    /**
     * Get job details with company information (public endpoint)
     */
    @GetMapping("/{jobId}/details")
    @Operation(summary = "Get job details with company info", description = "Get detailed job information with company details")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Job details with company info retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Job not found")
    })
    public ResponseEntity<JobDetailResponse> getJobDetailsWithCompany(
            @Parameter(description = "Job ID") @PathVariable String jobId) {
        
        JobDetailResponse jobDetailResponse = jobService.getJobDetailsWithCompany(jobId);
        
        return ResponseEntity.ok(jobDetailResponse);
    }
    
    /**
     * Get jobs by company (public endpoint)
     */
    @GetMapping("/company/{employerId}")
    @Operation(summary = "Get jobs by company", description = "Get all active jobs posted by a specific company")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Company jobs retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Company not found")
    })
    public ResponseEntity<PagedResponse<JobSummaryResponse>> getJobsByCompany(
            @Parameter(description = "Employer/Company ID") @PathVariable String employerId,
            @Parameter(description = "Page number (0-based)") 
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @Parameter(description = "Page size") 
            @RequestParam(defaultValue = "10") @Min(1) @Max(50) int size,
            @Parameter(description = "Sort field") 
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction (asc/desc)") 
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        PagedResponse<JobSummaryResponse> response = jobService.getActiveJobsByCompany(
                employerId, page, size, sortBy, sortDir);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get company profile information (public endpoint)
     */
    @GetMapping("/company/{employerId}/profile")
    @Operation(summary = "Get company profile", description = "Get company profile information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Company profile retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Company not found")
    })
    public ResponseEntity<CompanyProfileResponse> getCompanyProfile(
            @Parameter(description = "Employer/Company ID") @PathVariable String employerId) {
        
        CompanyProfileResponse companyProfile = jobService.getCompanyProfile(employerId);
        
        return ResponseEntity.ok(companyProfile);
    }
    
    /**
     * Clean up expired jobs (admin endpoint)
     */
    @DeleteMapping("/expired")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Clean up expired jobs", description = "Remove expired job postings")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Expired jobs cleaned up successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required")
    })
    public ResponseEntity<ExpiredJobsCleanupResponse> cleanupExpiredJobs() {
        
        ExpiredJobsCleanupResponse response = jobService.cleanupExpiredJobs();
        
        return ResponseEntity.ok(response);
    }
}