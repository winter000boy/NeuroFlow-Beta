package com.jobapp.user.controller;

import com.jobapp.user.dto.*;
import com.jobapp.user.model.Admin;
import com.jobapp.user.model.AdminRole;
import com.jobapp.user.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.security.Principal;

/**
 * Admin Controller for admin authentication and user management
 * Requirements: 5.1, 5.2, 5.3
 */
@RestController
@RequestMapping("/api/admin")
@Tag(name = "Admin Management", description = "Admin authentication and user management APIs")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AdminController {

    @Autowired
    private AdminService adminService;

    @PostMapping("/login")
    @Operation(summary = "Admin login", description = "Authenticate admin with SUPER_ADMIN role verification")
    public ResponseEntity<AdminLoginResponse> adminLogin(@Valid @RequestBody AdminLoginRequest request) {
        AdminLoginResponse response = adminService.authenticateAdmin(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/profile")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get admin profile", description = "Get current admin profile information")
    public ResponseEntity<AdminResponse> getAdminProfile(Principal principal) {
        AdminResponse response = adminService.getAdminProfile(principal.getName());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/profile")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update admin profile", description = "Update admin profile information")
    public ResponseEntity<AdminResponse> updateAdminProfile(
            @Valid @RequestBody AdminProfileUpdateRequest request,
            Principal principal) {
        AdminResponse response = adminService.updateAdminProfile(principal.getName(), request);
        return ResponseEntity.ok(response);
    }

    // User Management Endpoints

    @GetMapping("/users/candidates")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all candidates", description = "View all candidate profiles with pagination")
    public ResponseEntity<PagedResponse<CandidateResponse>> getAllCandidates(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "desc") String sortDir,
            @Parameter(description = "Search by name or email") @RequestParam(required = false) String search,
            @Parameter(description = "Filter by active status") @RequestParam(required = false) Boolean isActive,
            Principal principal) {
        
        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        
        PagedResponse<CandidateResponse> response = adminService.getAllCandidates(
            pageable, search, isActive, principal.getName());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/users/employers")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all employers", description = "View all employer profiles with pagination")
    public ResponseEntity<PagedResponse<EmployerResponse>> getAllEmployers(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "desc") String sortDir,
            @Parameter(description = "Search by company name or email") @RequestParam(required = false) String search,
            @Parameter(description = "Filter by approval status") @RequestParam(required = false) Boolean isApproved,
            @Parameter(description = "Filter by active status") @RequestParam(required = false) Boolean isActive,
            Principal principal) {
        
        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        
        PagedResponse<EmployerResponse> response = adminService.getAllEmployers(
            pageable, search, isApproved, isActive, principal.getName());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/users/candidates/{candidateId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get candidate details", description = "Get detailed candidate profile")
    public ResponseEntity<CandidateResponse> getCandidateDetails(
            @PathVariable String candidateId,
            Principal principal) {
        CandidateResponse response = adminService.getCandidateDetails(candidateId, principal.getName());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/users/employers/{employerId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get employer details", description = "Get detailed employer profile")
    public ResponseEntity<EmployerResponse> getEmployerDetails(
            @PathVariable String employerId,
            Principal principal) {
        EmployerResponse response = adminService.getEmployerDetails(employerId, principal.getName());
        return ResponseEntity.ok(response);
    }

    // User Approval/Rejection and Blocking

    @PutMapping("/users/employers/{employerId}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Approve employer", description = "Approve employer registration")
    public ResponseEntity<MessageResponse> approveEmployer(
            @PathVariable String employerId,
            @RequestBody(required = false) AdminActionRequest request,
            Principal principal) {
        MessageResponse response = adminService.approveEmployer(employerId, request, principal.getName());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/users/employers/{employerId}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Reject employer", description = "Reject employer registration")
    public ResponseEntity<MessageResponse> rejectEmployer(
            @PathVariable String employerId,
            @Valid @RequestBody AdminActionRequest request,
            Principal principal) {
        MessageResponse response = adminService.rejectEmployer(employerId, request, principal.getName());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/users/candidates/{candidateId}/block")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Block candidate", description = "Block candidate account")
    public ResponseEntity<MessageResponse> blockCandidate(
            @PathVariable String candidateId,
            @Valid @RequestBody AdminActionRequest request,
            Principal principal) {
        MessageResponse response = adminService.blockCandidate(candidateId, request, principal.getName());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/users/candidates/{candidateId}/unblock")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Unblock candidate", description = "Unblock candidate account")
    public ResponseEntity<MessageResponse> unblockCandidate(
            @PathVariable String candidateId,
            @RequestBody(required = false) AdminActionRequest request,
            Principal principal) {
        MessageResponse response = adminService.unblockCandidate(candidateId, request, principal.getName());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/users/employers/{employerId}/block")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Block employer", description = "Block employer account")
    public ResponseEntity<MessageResponse> blockEmployer(
            @PathVariable String employerId,
            @Valid @RequestBody AdminActionRequest request,
            Principal principal) {
        MessageResponse response = adminService.blockEmployer(employerId, request, principal.getName());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/users/employers/{employerId}/unblock")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Unblock employer", description = "Unblock employer account")
    public ResponseEntity<MessageResponse> unblockEmployer(
            @PathVariable String employerId,
            @RequestBody(required = false) AdminActionRequest request,
            Principal principal) {
        MessageResponse response = adminService.unblockEmployer(employerId, request, principal.getName());
        return ResponseEntity.ok(response);
    }

    // Admin Management

    @GetMapping("/admins")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all admins", description = "View all admin accounts (SUPER_ADMIN only)")
    public ResponseEntity<PagedResponse<AdminResponse>> getAllAdmins(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "desc") String sortDir,
            @Parameter(description = "Filter by role") @RequestParam(required = false) AdminRole role,
            @Parameter(description = "Filter by active status") @RequestParam(required = false) Boolean isActive,
            Principal principal) {
        
        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        
        PagedResponse<AdminResponse> response = adminService.getAllAdmins(
            pageable, role, isActive, principal.getName());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/admins")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create admin", description = "Create new admin account (SUPER_ADMIN only)")
    public ResponseEntity<AdminResponse> createAdmin(
            @Valid @RequestBody AdminCreateRequest request,
            Principal principal) {
        AdminResponse response = adminService.createAdmin(request, principal.getName());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/admins/{adminId}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Activate admin", description = "Activate admin account")
    public ResponseEntity<MessageResponse> activateAdmin(
            @PathVariable String adminId,
            Principal principal) {
        MessageResponse response = adminService.activateAdmin(adminId, principal.getName());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/admins/{adminId}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Deactivate admin", description = "Deactivate admin account")
    public ResponseEntity<MessageResponse> deactivateAdmin(
            @PathVariable String adminId,
            @Valid @RequestBody AdminActionRequest request,
            Principal principal) {
        MessageResponse response = adminService.deactivateAdmin(adminId, request, principal.getName());
        return ResponseEntity.ok(response);
    }

    // Analytics and Dashboard Endpoints

    @GetMapping("/dashboard/analytics")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get dashboard analytics", description = "Get comprehensive dashboard analytics and statistics")
    public ResponseEntity<DashboardAnalyticsResponse> getDashboardAnalytics(
            @Parameter(description = "Number of days for trends") @RequestParam(defaultValue = "30") int days,
            Principal principal) {
        DashboardAnalyticsResponse response = adminService.getDashboardAnalytics(days, principal.getName());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/analytics/users")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get user analytics", description = "Get detailed user statistics and metrics")
    public ResponseEntity<DashboardAnalyticsResponse.UserStatistics> getUserAnalytics(Principal principal) {
        DashboardAnalyticsResponse.UserStatistics response = adminService.getUserAnalytics(principal.getName());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/analytics/jobs")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get job analytics", description = "Get detailed job statistics and metrics")
    public ResponseEntity<DashboardAnalyticsResponse.JobStatistics> getJobAnalytics(Principal principal) {
        DashboardAnalyticsResponse.JobStatistics response = adminService.getJobAnalytics(principal.getName());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/analytics/applications")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get application analytics", description = "Get detailed application statistics and metrics")
    public ResponseEntity<DashboardAnalyticsResponse.ApplicationStatistics> getApplicationAnalytics(Principal principal) {
        DashboardAnalyticsResponse.ApplicationStatistics response = adminService.getApplicationAnalytics(principal.getName());
        return ResponseEntity.ok(response);
    }

    // Content Moderation Endpoints

    @GetMapping("/moderation/jobs")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get jobs for moderation", description = "Get jobs that need content moderation")
    public ResponseEntity<PagedResponse<JobModerationResponse>> getJobsForModeration(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "desc") String sortDir,
            @Parameter(description = "Filter by status") @RequestParam(required = false) String status,
            Principal principal) {
        
        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        
        PagedResponse<JobModerationResponse> response = adminService.getJobsForModeration(
            pageable, status, principal.getName());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/moderation/jobs/{jobId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Moderate job content", description = "Take moderation action on job posting")
    public ResponseEntity<MessageResponse> moderateJob(
            @PathVariable String jobId,
            @Valid @RequestBody ContentModerationRequest request,
            Principal principal) {
        MessageResponse response = adminService.moderateJob(jobId, request, principal.getName());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/moderation/applications")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get applications for moderation", description = "Get applications that need content moderation")
    public ResponseEntity<PagedResponse<ApplicationModerationResponse>> getApplicationsForModeration(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "desc") String sortDir,
            @Parameter(description = "Filter by status") @RequestParam(required = false) String status,
            Principal principal) {
        
        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        
        PagedResponse<ApplicationModerationResponse> response = adminService.getApplicationsForModeration(
            pageable, status, principal.getName());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/moderation/applications/{applicationId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Moderate application content", description = "Take moderation action on job application")
    public ResponseEntity<MessageResponse> moderateApplication(
            @PathVariable String applicationId,
            @Valid @RequestBody ContentModerationRequest request,
            Principal principal) {
        MessageResponse response = adminService.moderateApplication(applicationId, request, principal.getName());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/reports/summary")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get system reports summary", description = "Get summary of system reports and metrics")
    public ResponseEntity<SystemReportSummary> getSystemReportsSummary(
            @Parameter(description = "Report period in days") @RequestParam(defaultValue = "30") int days,
            Principal principal) {
        SystemReportSummary response = adminService.getSystemReportsSummary(days, principal.getName());
        return ResponseEntity.ok(response);
    }
}