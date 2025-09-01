package com.jobapp.user.service;

import com.jobapp.user.dto.*;
import com.jobapp.user.exception.ResourceNotFoundException;
import com.jobapp.user.exception.UnauthorizedException;
import com.jobapp.user.exception.EmailAlreadyExistsException;
import com.jobapp.user.model.*;
import com.jobapp.user.repository.AdminRepository;
import com.jobapp.user.repository.CandidateRepository;
import com.jobapp.user.repository.EmployerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service class for admin authentication and user management
 * Requirements: 5.1, 5.2, 5.3
 */
@Service
@Transactional
public class AdminService {

    @Autowired
    private AdminRepository adminRepository;
    
    @Autowired
    private CandidateRepository candidateRepository;
    
    @Autowired
    private EmployerRepository employerRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtTokenService jwtTokenService;
    
    @Autowired
    private NotificationService notificationService;

    /**
     * Authenticate admin with SUPER_ADMIN role verification
     * Requirements: 5.1
     */
    public AdminLoginResponse authenticateAdmin(AdminLoginRequest request) {
        Admin admin = adminRepository.findByEmailAndIsActive(request.getEmail(), true)
                .orElseThrow(() -> new UnauthorizedException("Invalid credentials or account inactive"));
        
        if (!passwordEncoder.matches(request.getPassword(), admin.getPassword())) {
            throw new UnauthorizedException("Invalid credentials");
        }
        
        // Verify admin role (must be at least ADMIN role)
        if (admin.getRole() == null) {
            throw new UnauthorizedException("Admin role not assigned");
        }
        
        // Record login
        admin.recordLogin();
        admin.recordAction("Admin login");
        adminRepository.save(admin);
        
        // Generate JWT tokens
        String token = jwtTokenService.generateAdminToken(admin);
        String refreshToken = jwtTokenService.generateRefreshToken(admin.getEmail());
        LocalDateTime expiresAt = LocalDateTime.now().plusHours(24); // Token expires in 24 hours
        
        AdminLoginResponse response = new AdminLoginResponse(
                token, refreshToken, admin.getId(), admin.getEmail(), 
                admin.getName(), admin.getRole(), admin.getPermissions(), expiresAt);
        response.setLastLogin(admin.getLastLogin());
        response.setLoginCount(admin.getLoginCount());
        
        return response;
    }

    /**
     * Get admin profile information
     * Requirements: 5.1
     */
    public AdminResponse getAdminProfile(String email) {
        Admin admin = adminRepository.findByEmailAndIsActive(email, true)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found"));
        
        return convertToAdminResponse(admin);
    }

    /**
     * Update admin profile information
     * Requirements: 5.1
     */
    public AdminResponse updateAdminProfile(String email, AdminProfileUpdateRequest request) {
        Admin admin = adminRepository.findByEmailAndIsActive(email, true)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found"));
        
        // Update name
        admin.setName(request.getName());
        
        // Update password if requested
        if (request.isPasswordChangeRequested()) {
            if (request.getCurrentPassword() == null || request.getCurrentPassword().trim().isEmpty()) {
                throw new IllegalArgumentException("Current password is required for password change");
            }
            
            if (!passwordEncoder.matches(request.getCurrentPassword(), admin.getPassword())) {
                throw new UnauthorizedException("Current password is incorrect");
            }
            
            admin.setPassword(passwordEncoder.encode(request.getNewPassword()));
        }
        
        admin.recordAction("Profile updated");
        admin = adminRepository.save(admin);
        
        return convertToAdminResponse(admin);
    }

    /**
     * Get all candidates with pagination and filtering
     * Requirements: 5.2
     */
    public PagedResponse<CandidateResponse> getAllCandidates(Pageable pageable, String search, 
                                                           Boolean isActive, String adminEmail) {
        verifyAdminPermission(adminEmail, AdminPermission.VIEW_USERS);
        
        Page<Candidate> candidatePage;
        
        if (search != null && !search.trim().isEmpty()) {
            if (isActive != null) {
                candidatePage = candidateRepository.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseAndIsActive(
                        search, search, isActive, pageable);
            } else {
                candidatePage = candidateRepository.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                        search, search, pageable);
            }
        } else if (isActive != null) {
            candidatePage = candidateRepository.findByIsActive(isActive, pageable);
        } else {
            candidatePage = candidateRepository.findAll(pageable);
        }
        
        List<CandidateResponse> candidates = candidatePage.getContent().stream()
                .map(this::convertToCandidateResponse)
                .collect(Collectors.toList());
        
        recordAdminAction(adminEmail, "Viewed candidates list");
        
        return new PagedResponse<>(candidates, candidatePage.getNumber(), candidatePage.getSize(),
                candidatePage.getTotalElements(), candidatePage.getTotalPages());
    }

    /**
     * Get all employers with pagination and filtering
     * Requirements: 5.2
     */
    public PagedResponse<EmployerResponse> getAllEmployers(Pageable pageable, String search, 
                                                         Boolean isApproved, Boolean isActive, String adminEmail) {
        verifyAdminPermission(adminEmail, AdminPermission.VIEW_USERS);
        
        Page<Employer> employerPage;
        
        if (search != null && !search.trim().isEmpty()) {
            if (isApproved != null && isActive != null) {
                employerPage = employerRepository.findByCompanyNameContainingIgnoreCaseOrEmailContainingIgnoreCaseAndIsApprovedAndIsActive(
                        search, search, isApproved, isActive, pageable);
            } else if (isApproved != null) {
                employerPage = employerRepository.findByCompanyNameContainingIgnoreCaseOrEmailContainingIgnoreCaseAndIsApproved(
                        search, search, isApproved, pageable);
            } else if (isActive != null) {
                employerPage = employerRepository.findByCompanyNameContainingIgnoreCaseOrEmailContainingIgnoreCaseAndIsActive(
                        search, search, isActive, pageable);
            } else {
                employerPage = employerRepository.findByCompanyNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                        search, search, pageable);
            }
        } else if (isApproved != null && isActive != null) {
            employerPage = employerRepository.findByIsApprovedAndIsActive(isApproved, isActive, pageable);
        } else if (isApproved != null) {
            employerPage = employerRepository.findByIsApproved(isApproved, pageable);
        } else if (isActive != null) {
            employerPage = employerRepository.findByIsActive(isActive, pageable);
        } else {
            employerPage = employerRepository.findAll(pageable);
        }
        
        List<EmployerResponse> employers = employerPage.getContent().stream()
                .map(this::convertToEmployerResponse)
                .collect(Collectors.toList());
        
        recordAdminAction(adminEmail, "Viewed employers list");
        
        return new PagedResponse<>(employers, employerPage.getNumber(), employerPage.getSize(),
                employerPage.getTotalElements(), employerPage.getTotalPages());
    }

    /**
     * Get candidate details
     * Requirements: 5.2
     */
    public CandidateResponse getCandidateDetails(String candidateId, String adminEmail) {
        verifyAdminPermission(adminEmail, AdminPermission.VIEW_USERS);
        
        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate not found"));
        
        recordAdminAction(adminEmail, "Viewed candidate details: " + candidate.getEmail());
        
        return convertToCandidateResponse(candidate);
    }

    /**
     * Get employer details
     * Requirements: 5.2
     */
    public EmployerResponse getEmployerDetails(String employerId, String adminEmail) {
        verifyAdminPermission(adminEmail, AdminPermission.VIEW_USERS);
        
        Employer employer = employerRepository.findById(employerId)
                .orElseThrow(() -> new ResourceNotFoundException("Employer not found"));
        
        recordAdminAction(adminEmail, "Viewed employer details: " + employer.getEmail());
        
        return convertToEmployerResponse(employer);
    }

    /**
     * Approve employer registration
     * Requirements: 5.3
     */
    public MessageResponse approveEmployer(String employerId, AdminActionRequest request, String adminEmail) {
        verifyAdminPermission(adminEmail, AdminPermission.APPROVE_EMPLOYERS);
        
        Employer employer = employerRepository.findById(employerId)
                .orElseThrow(() -> new ResourceNotFoundException("Employer not found"));
        
        if (Boolean.TRUE.equals(employer.getIsApproved())) {
            throw new IllegalStateException("Employer is already approved");
        }
        
        employer.setIsApproved(true);
        employer.setApprovedAt(LocalDateTime.now());
        employer.setApprovedBy(adminEmail);
        
        if (request != null && request.getNotes() != null) {
            employer.setApprovalNotes(request.getNotes());
        }
        
        employerRepository.save(employer);
        
        // Send notification if requested
        if (request == null || Boolean.TRUE.equals(request.getSendNotification())) {
            notificationService.sendEmployerApprovalNotification(employer);
        }
        
        recordAdminAction(adminEmail, "Approved employer: " + employer.getEmail());
        
        return new MessageResponse("Employer approved successfully");
    }

    /**
     * Reject employer registration
     * Requirements: 5.3
     */
    public MessageResponse rejectEmployer(String employerId, AdminActionRequest request, String adminEmail) {
        verifyAdminPermission(adminEmail, AdminPermission.APPROVE_EMPLOYERS);
        
        Employer employer = employerRepository.findById(employerId)
                .orElseThrow(() -> new ResourceNotFoundException("Employer not found"));
        
        if (Boolean.TRUE.equals(employer.getIsApproved())) {
            throw new IllegalStateException("Cannot reject an already approved employer");
        }
        
        employer.setIsApproved(false);
        employer.setRejectedAt(LocalDateTime.now());
        employer.setRejectedBy(adminEmail);
        employer.setRejectionReason(request.getReason());
        
        if (request.getNotes() != null) {
            employer.setRejectionNotes(request.getNotes());
        }
        
        employerRepository.save(employer);
        
        // Send notification if requested
        if (Boolean.TRUE.equals(request.getSendNotification())) {
            notificationService.sendEmployerRejectionNotification(employer, request.getReason());
        }
        
        recordAdminAction(adminEmail, "Rejected employer: " + employer.getEmail());
        
        return new MessageResponse("Employer rejected successfully");
    }

    /**
     * Block candidate account
     * Requirements: 5.3
     */
    public MessageResponse blockCandidate(String candidateId, AdminActionRequest request, String adminEmail) {
        verifyAdminPermission(adminEmail, AdminPermission.BLOCK_USERS);
        
        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate not found"));
        
        if (Boolean.FALSE.equals(candidate.getIsActive())) {
            throw new IllegalStateException("Candidate is already blocked");
        }
        
        candidate.setIsActive(false);
        candidate.setBlockedAt(LocalDateTime.now());
        candidate.setBlockedBy(adminEmail);
        candidate.setBlockReason(request.getReason());
        
        if (request.getNotes() != null) {
            candidate.setBlockNotes(request.getNotes());
        }
        
        candidateRepository.save(candidate);
        
        // Send notification if requested
        if (Boolean.TRUE.equals(request.getSendNotification())) {
            notificationService.sendCandidateBlockNotification(candidate, request.getReason());
        }
        
        recordAdminAction(adminEmail, "Blocked candidate: " + candidate.getEmail());
        
        return new MessageResponse("Candidate blocked successfully");
    }

    /**
     * Unblock candidate account
     * Requirements: 5.3
     */
    public MessageResponse unblockCandidate(String candidateId, AdminActionRequest request, String adminEmail) {
        verifyAdminPermission(adminEmail, AdminPermission.BLOCK_USERS);
        
        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate not found"));
        
        if (Boolean.TRUE.equals(candidate.getIsActive())) {
            throw new IllegalStateException("Candidate is not blocked");
        }
        
        candidate.setIsActive(true);
        candidate.setUnblockedAt(LocalDateTime.now());
        candidate.setUnblockedBy(adminEmail);
        
        // Clear block information
        candidate.setBlockedAt(null);
        candidate.setBlockedBy(null);
        candidate.setBlockReason(null);
        candidate.setBlockNotes(null);
        
        candidateRepository.save(candidate);
        
        // Send notification if requested
        if (request == null || Boolean.TRUE.equals(request.getSendNotification())) {
            notificationService.sendCandidateUnblockNotification(candidate);
        }
        
        recordAdminAction(adminEmail, "Unblocked candidate: " + candidate.getEmail());
        
        return new MessageResponse("Candidate unblocked successfully");
    }

    /**
     * Block employer account
     * Requirements: 5.3
     */
    public MessageResponse blockEmployer(String employerId, AdminActionRequest request, String adminEmail) {
        verifyAdminPermission(adminEmail, AdminPermission.BLOCK_USERS);
        
        Employer employer = employerRepository.findById(employerId)
                .orElseThrow(() -> new ResourceNotFoundException("Employer not found"));
        
        if (Boolean.FALSE.equals(employer.getIsActive())) {
            throw new IllegalStateException("Employer is already blocked");
        }
        
        employer.setIsActive(false);
        employer.setBlockedAt(LocalDateTime.now());
        employer.setBlockedBy(adminEmail);
        employer.setBlockReason(request.getReason());
        
        if (request.getNotes() != null) {
            employer.setBlockNotes(request.getNotes());
        }
        
        employerRepository.save(employer);
        
        // Send notification if requested
        if (Boolean.TRUE.equals(request.getSendNotification())) {
            notificationService.sendEmployerBlockNotification(employer, request.getReason());
        }
        
        recordAdminAction(adminEmail, "Blocked employer: " + employer.getEmail());
        
        return new MessageResponse("Employer blocked successfully");
    }

    /**
     * Unblock employer account
     * Requirements: 5.3
     */
    public MessageResponse unblockEmployer(String employerId, AdminActionRequest request, String adminEmail) {
        verifyAdminPermission(adminEmail, AdminPermission.BLOCK_USERS);
        
        Employer employer = employerRepository.findById(employerId)
                .orElseThrow(() -> new ResourceNotFoundException("Employer not found"));
        
        if (Boolean.TRUE.equals(employer.getIsActive())) {
            throw new IllegalStateException("Employer is not blocked");
        }
        
        employer.setIsActive(true);
        employer.setUnblockedAt(LocalDateTime.now());
        employer.setUnblockedBy(adminEmail);
        
        // Clear block information
        employer.setBlockedAt(null);
        employer.setBlockedBy(null);
        employer.setBlockReason(null);
        employer.setBlockNotes(null);
        
        employerRepository.save(employer);
        
        // Send notification if requested
        if (request == null || Boolean.TRUE.equals(request.getSendNotification())) {
            notificationService.sendEmployerUnblockNotification(employer);
        }
        
        recordAdminAction(adminEmail, "Unblocked employer: " + employer.getEmail());
        
        return new MessageResponse("Employer unblocked successfully");
    }

    /**
     * Get all admins (SUPER_ADMIN only)
     * Requirements: 5.1
     */
    public PagedResponse<AdminResponse> getAllAdmins(Pageable pageable, AdminRole role, 
                                                   Boolean isActive, String adminEmail) {
        verifyAdminPermission(adminEmail, AdminPermission.MANAGE_ADMINS);
        
        Page<Admin> adminPage;
        
        if (role != null && isActive != null) {
            adminPage = adminRepository.findByRoleAndIsActive(role, isActive, pageable);
        } else if (role != null) {
            adminPage = adminRepository.findByRole(role, pageable);
        } else if (isActive != null) {
            adminPage = adminRepository.findByIsActive(isActive, pageable);
        } else {
            adminPage = adminRepository.findAll(pageable);
        }
        
        List<AdminResponse> admins = adminPage.getContent().stream()
                .map(this::convertToAdminResponse)
                .collect(Collectors.toList());
        
        recordAdminAction(adminEmail, "Viewed admins list");
        
        return new PagedResponse<>(admins, adminPage.getNumber(), adminPage.getSize(),
                adminPage.getTotalElements(), adminPage.getTotalPages());
    }

    /**
     * Create new admin account (SUPER_ADMIN only)
     * Requirements: 5.1
     */
    public AdminResponse createAdmin(AdminCreateRequest request, String adminEmail) {
        verifyAdminPermission(adminEmail, AdminPermission.MANAGE_ADMINS);
        
        if (adminRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("Admin with this email already exists");
        }
        
        Admin admin = new Admin();
        admin.setEmail(request.getEmail());
        admin.setPassword(passwordEncoder.encode(request.getPassword()));
        admin.setName(request.getName());
        admin.setRole(request.getRole());
        admin.setPermissions(request.getPermissions());
        admin.setIsActive(request.getIsActive());
        admin.setCreatedBy(adminEmail);
        
        admin = adminRepository.save(admin);
        
        recordAdminAction(adminEmail, "Created admin: " + admin.getEmail());
        
        return convertToAdminResponse(admin);
    }

    /**
     * Activate admin account
     * Requirements: 5.1
     */
    public MessageResponse activateAdmin(String adminId, String adminEmail) {
        verifyAdminPermission(adminEmail, AdminPermission.MANAGE_ADMINS);
        
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found"));
        
        if (Boolean.TRUE.equals(admin.getIsActive())) {
            throw new IllegalStateException("Admin is already active");
        }
        
        admin.setIsActive(true);
        adminRepository.save(admin);
        
        recordAdminAction(adminEmail, "Activated admin: " + admin.getEmail());
        
        return new MessageResponse("Admin activated successfully");
    }

    /**
     * Deactivate admin account
     * Requirements: 5.1
     */
    public MessageResponse deactivateAdmin(String adminId, AdminActionRequest request, String adminEmail) {
        verifyAdminPermission(adminEmail, AdminPermission.MANAGE_ADMINS);
        
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found"));
        
        if (Boolean.FALSE.equals(admin.getIsActive())) {
            throw new IllegalStateException("Admin is already inactive");
        }
        
        // Prevent self-deactivation
        Admin currentAdmin = adminRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Current admin not found"));
        
        if (admin.getId().equals(currentAdmin.getId())) {
            throw new IllegalStateException("Cannot deactivate your own account");
        }
        
        admin.setIsActive(false);
        adminRepository.save(admin);
        
        recordAdminAction(adminEmail, "Deactivated admin: " + admin.getEmail() + " - Reason: " + request.getReason());
        
        return new MessageResponse("Admin deactivated successfully");
    }

    // Helper methods

    private void verifyAdminPermission(String adminEmail, AdminPermission permission) {
        Admin admin = adminRepository.findByEmailAndIsActive(adminEmail, true)
                .orElseThrow(() -> new UnauthorizedException("Admin not found or inactive"));
        
        if (!admin.hasPermission(permission)) {
            throw new UnauthorizedException("Insufficient permissions for this action");
        }
    }

    private void recordAdminAction(String adminEmail, String action) {
        adminRepository.findByEmail(adminEmail).ifPresent(admin -> {
            admin.recordAction(action);
            adminRepository.save(admin);
        });
    }

    private AdminResponse convertToAdminResponse(Admin admin) {
        AdminResponse response = new AdminResponse();
        response.setId(admin.getId());
        response.setEmail(admin.getEmail());
        response.setName(admin.getName());
        response.setRole(admin.getRole());
        response.setPermissions(admin.getPermissions());
        response.setIsActive(admin.getIsActive());
        response.setLastLogin(admin.getLastLogin());
        response.setLoginCount(admin.getLoginCount());
        response.setCreatedBy(admin.getCreatedBy());
        response.setCreatedAt(admin.getCreatedAt());
        response.setUpdatedAt(admin.getUpdatedAt());
        response.setLastAction(admin.getLastAction());
        response.setLastActionAt(admin.getLastActionAt());
        response.setActionsPerformed(admin.getActionsPerformed());
        return response;
    }

    // Analytics Methods

    /**
     * Get comprehensive dashboard analytics
     * Requirements: 5.4
     */
    public DashboardAnalyticsResponse getDashboardAnalytics(int days, String adminEmail) {
        verifyAdminPermission(adminEmail, AdminPermission.VIEW_ANALYTICS);
        
        DashboardAnalyticsResponse response = new DashboardAnalyticsResponse();
        
        // Get all analytics components
        response.setUserStatistics(getUserAnalytics(adminEmail));
        response.setJobStatistics(getJobAnalytics(adminEmail));
        response.setApplicationStatistics(getApplicationAnalytics(adminEmail));
        response.setSystemStatistics(getSystemAnalytics(adminEmail));
        
        // Get time series data
        LocalDateTime startDate = LocalDateTime.now().minusDays(days);
        response.setUserRegistrationTrends(getUserRegistrationTrends(startDate));
        response.setJobPostingTrends(getJobPostingTrends(startDate));
        response.setApplicationTrends(getApplicationTrends(startDate));
        
        // Get geographic data (placeholder implementation)
        response.setJobsByLocation(getJobsByLocation());
        response.setCandidatesByLocation(getCandidatesByLocation());
        
        recordAdminAction(adminEmail, "Generated dashboard analytics");
        
        return response;
    }

    /**
     * Get user analytics
     * Requirements: 5.4
     */
    public DashboardAnalyticsResponse.UserStatistics getUserAnalytics(String adminEmail) {
        verifyAdminPermission(adminEmail, AdminPermission.VIEW_ANALYTICS);
        
        DashboardAnalyticsResponse.UserStatistics stats = new DashboardAnalyticsResponse.UserStatistics();
        
        // Candidate statistics
        stats.setTotalCandidates(candidateRepository.count());
        stats.setActiveCandidates(candidateRepository.countByIsActive(true));
        stats.setBlockedCandidates(candidateRepository.countByIsActive(false));
        
        // Employer statistics
        stats.setTotalEmployers(employerRepository.count());
        stats.setApprovedEmployers(employerRepository.countByIsApproved(true));
        stats.setPendingEmployers(employerRepository.countPendingApproval());
        stats.setRejectedEmployers(employerRepository.countByIsApproved(false));
        // Note: Using countByIsApprovedAndIsActive for blocked employers
        stats.setBlockedEmployers(employerRepository.countByIsApprovedAndIsActive(true, false));
        
        // Monthly statistics
        LocalDateTime monthStart = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        stats.setNewCandidatesThisMonth(candidateRepository.findByCreatedAtBetween(monthStart, LocalDateTime.now(), null).getTotalElements());
        stats.setNewEmployersThisMonth(employerRepository.findByCreatedAtBetween(monthStart, LocalDateTime.now(), null).getTotalElements());
        
        recordAdminAction(adminEmail, "Generated user analytics");
        
        return stats;
    }

    /**
     * Get job analytics (placeholder - would need job service integration)
     * Requirements: 5.4
     */
    public DashboardAnalyticsResponse.JobStatistics getJobAnalytics(String adminEmail) {
        verifyAdminPermission(adminEmail, AdminPermission.VIEW_ANALYTICS);
        
        DashboardAnalyticsResponse.JobStatistics stats = new DashboardAnalyticsResponse.JobStatistics();
        
        // Placeholder implementation - in real system would call job service
        stats.setTotalJobs(0L);
        stats.setActiveJobs(0L);
        stats.setInactiveJobs(0L);
        stats.setJobsThisMonth(0L);
        
        recordAdminAction(adminEmail, "Generated job analytics");
        
        return stats;
    }

    /**
     * Get application analytics (placeholder - would need application service integration)
     * Requirements: 5.4
     */
    public DashboardAnalyticsResponse.ApplicationStatistics getApplicationAnalytics(String adminEmail) {
        verifyAdminPermission(adminEmail, AdminPermission.VIEW_ANALYTICS);
        
        DashboardAnalyticsResponse.ApplicationStatistics stats = new DashboardAnalyticsResponse.ApplicationStatistics();
        
        // Placeholder implementation - in real system would call application service
        stats.setTotalApplications(0L);
        stats.setApplicationsThisMonth(0L);
        stats.setAverageApplicationsPerJob(0.0);
        stats.setApplicationSuccessRate(0.0);
        
        recordAdminAction(adminEmail, "Generated application analytics");
        
        return stats;
    }

    /**
     * Get system analytics
     * Requirements: 5.4
     */
    public DashboardAnalyticsResponse.SystemStatistics getSystemAnalytics(String adminEmail) {
        verifyAdminPermission(adminEmail, AdminPermission.VIEW_ANALYTICS);
        
        DashboardAnalyticsResponse.SystemStatistics stats = new DashboardAnalyticsResponse.SystemStatistics();
        
        stats.setTotalAdmins(adminRepository.count());
        stats.setActiveAdmins(adminRepository.countByIsActive(true));
        
        // Get aggregated statistics from repository
        Long totalLogins = adminRepository.getTotalLoginCount();
        Long totalActions = adminRepository.getTotalActionsCount();
        
        stats.setTotalLogins(totalLogins != null ? totalLogins : 0L);
        stats.setTotalAdminActions(totalActions != null ? totalActions : 0L);
        stats.setLastSystemUpdate(LocalDateTime.now());
        
        recordAdminAction(adminEmail, "Generated system analytics");
        
        return stats;
    }

    // Content Moderation Methods

    /**
     * Get jobs for moderation (placeholder - would need job service integration)
     * Requirements: 5.5
     */
    public PagedResponse<JobModerationResponse> getJobsForModeration(Pageable pageable, String status, String adminEmail) {
        verifyAdminPermission(adminEmail, AdminPermission.MODERATE_CONTENT);
        
        // Placeholder implementation - in real system would call job service
        List<JobModerationResponse> jobs = new ArrayList<>();
        
        recordAdminAction(adminEmail, "Viewed jobs for moderation");
        
        return new PagedResponse<>(jobs, pageable.getPageNumber(), pageable.getPageSize(), 0L, 0);
    }

    /**
     * Moderate job content (placeholder - would need job service integration)
     * Requirements: 5.5
     */
    public MessageResponse moderateJob(String jobId, ContentModerationRequest request, String adminEmail) {
        verifyAdminPermission(adminEmail, AdminPermission.MODERATE_CONTENT);
        
        // Placeholder implementation - in real system would call job service
        recordAdminAction(adminEmail, "Moderated job: " + jobId + " - Action: " + request.getAction());
        
        return new MessageResponse("Job moderation action completed successfully");
    }

    /**
     * Get applications for moderation (placeholder - would need application service integration)
     * Requirements: 5.5
     */
    public PagedResponse<ApplicationModerationResponse> getApplicationsForModeration(Pageable pageable, String status, String adminEmail) {
        verifyAdminPermission(adminEmail, AdminPermission.MODERATE_CONTENT);
        
        // Placeholder implementation - in real system would call application service
        List<ApplicationModerationResponse> applications = new ArrayList<>();
        
        recordAdminAction(adminEmail, "Viewed applications for moderation");
        
        return new PagedResponse<>(applications, pageable.getPageNumber(), pageable.getPageSize(), 0L, 0);
    }

    /**
     * Moderate application content (placeholder - would need application service integration)
     * Requirements: 5.5
     */
    public MessageResponse moderateApplication(String applicationId, ContentModerationRequest request, String adminEmail) {
        verifyAdminPermission(adminEmail, AdminPermission.MODERATE_CONTENT);
        
        // Placeholder implementation - in real system would call application service
        recordAdminAction(adminEmail, "Moderated application: " + applicationId + " - Action: " + request.getAction());
        
        return new MessageResponse("Application moderation action completed successfully");
    }

    /**
     * Get system reports summary
     * Requirements: 5.4
     */
    public SystemReportSummary getSystemReportsSummary(int days, String adminEmail) {
        verifyAdminPermission(adminEmail, AdminPermission.VIEW_ANALYTICS);
        
        SystemReportSummary summary = new SystemReportSummary(days);
        
        // Generate performance metrics (placeholder)
        SystemReportSummary.PerformanceMetrics performance = new SystemReportSummary.PerformanceMetrics();
        performance.setAverageResponseTime(150.0); // ms
        performance.setTotalRequests(10000L);
        performance.setErrorRate(0.02); // 2%
        performance.setPeakConcurrentUsers(500L);
        summary.setPerformance(performance);
        
        // Generate security metrics (placeholder)
        SystemReportSummary.SecurityMetrics security = new SystemReportSummary.SecurityMetrics();
        security.setFailedLoginAttempts(25L);
        security.setBlockedIPs(5L);
        security.setSuspiciousActivities(3L);
        security.setDataBreachAttempts(0L);
        summary.setSecurity(security);
        
        // Generate content metrics (placeholder)
        SystemReportSummary.ContentMetrics content = new SystemReportSummary.ContentMetrics();
        content.setTotalContentItems(1000L);
        content.setModeratedContent(50L);
        content.setFlaggedContent(10L);
        content.setRemovedContent(5L);
        content.setModerationAccuracy(0.95); // 95%
        summary.setContent(content);
        
        // Generate engagement metrics (placeholder)
        SystemReportSummary.EngagementMetrics engagement = new SystemReportSummary.EngagementMetrics();
        engagement.setActiveUsers(800L);
        engagement.setUserRetentionRate(0.75); // 75%
        engagement.setJobApplicationRate(0.15); // 15%
        engagement.setEmployerSatisfactionScore(4.2);
        engagement.setCandidateSatisfactionScore(4.0);
        summary.setEngagement(engagement);
        
        // Generate health indicators
        List<SystemReportSummary.HealthIndicator> healthIndicators = new ArrayList<>();
        healthIndicators.add(new SystemReportSummary.HealthIndicator("Database", "HEALTHY", "All connections active"));
        healthIndicators.add(new SystemReportSummary.HealthIndicator("Authentication", "HEALTHY", "JWT service operational"));
        healthIndicators.add(new SystemReportSummary.HealthIndicator("File Storage", "WARNING", "Storage 80% full"));
        summary.setHealthIndicators(healthIndicators);
        
        // Generate alerts
        List<SystemReportSummary.SystemAlert> alerts = new ArrayList<>();
        alerts.add(new SystemReportSummary.SystemAlert("WARNING", "High Storage Usage", "File storage is 80% full"));
        alerts.add(new SystemReportSummary.SystemAlert("INFO", "System Update", "Scheduled maintenance in 3 days"));
        summary.setAlerts(alerts);
        
        recordAdminAction(adminEmail, "Generated system reports summary");
        
        return summary;
    }

    // Helper methods for analytics

    private List<DashboardAnalyticsResponse.TimeSeriesData> getUserRegistrationTrends(LocalDateTime startDate) {
        // Placeholder implementation - would use aggregation queries
        List<DashboardAnalyticsResponse.TimeSeriesData> trends = new ArrayList<>();
        
        LocalDateTime current = startDate;
        while (current.isBefore(LocalDateTime.now())) {
            String dateStr = current.toLocalDate().toString();
            long count = candidateRepository.findByCreatedAtBetween(current, current.plusDays(1), null).getTotalElements();
            trends.add(new DashboardAnalyticsResponse.TimeSeriesData(dateStr, count));
            current = current.plusDays(1);
        }
        
        return trends;
    }

    private List<DashboardAnalyticsResponse.TimeSeriesData> getJobPostingTrends(LocalDateTime startDate) {
        // Placeholder implementation - would call job service
        return new ArrayList<>();
    }

    private List<DashboardAnalyticsResponse.TimeSeriesData> getApplicationTrends(LocalDateTime startDate) {
        // Placeholder implementation - would call application service
        return new ArrayList<>();
    }

    private Map<String, Long> getJobsByLocation() {
        // Placeholder implementation - would call job service
        Map<String, Long> locationMap = new HashMap<>();
        locationMap.put("New York", 150L);
        locationMap.put("San Francisco", 120L);
        locationMap.put("Los Angeles", 100L);
        locationMap.put("Chicago", 80L);
        locationMap.put("Boston", 70L);
        return locationMap;
    }

    private Map<String, Long> getCandidatesByLocation() {
        // Placeholder implementation - would need location field in candidate model
        Map<String, Long> locationMap = new HashMap<>();
        locationMap.put("New York", 200L);
        locationMap.put("San Francisco", 180L);
        locationMap.put("Los Angeles", 150L);
        locationMap.put("Chicago", 120L);
        locationMap.put("Boston", 100L);
        return locationMap;
    }

    private CandidateResponse convertToCandidateResponse(Candidate candidate) {
        // This method should be implemented based on existing CandidateResponse structure
        // For now, creating a basic implementation
        CandidateResponse response = new CandidateResponse();
        response.setId(candidate.getId());
        response.setEmail(candidate.getEmail());
        response.setName(candidate.getName());
        response.setPhone(candidate.getPhone());
        response.setDegree(candidate.getDegree());
        response.setGraduationYear(candidate.getGraduationYear());
        response.setResumeUrl(candidate.getResumeUrl());
        response.setLinkedinProfile(candidate.getLinkedinProfile());
        response.setPortfolioUrl(candidate.getPortfolioUrl());
        response.setIsActive(candidate.getIsActive());
        response.setCreatedAt(candidate.getCreatedAt());
        response.setUpdatedAt(candidate.getUpdatedAt());
        return response;
    }

    private EmployerResponse convertToEmployerResponse(Employer employer) {
        // This method should be implemented based on existing EmployerResponse structure
        // For now, creating a basic implementation
        EmployerResponse response = new EmployerResponse();
        response.setId(employer.getId());
        response.setEmail(employer.getEmail());
        response.setCompanyName(employer.getCompanyName());
        response.setWebsite(employer.getWebsite());
        response.setDescription(employer.getDescription());
        response.setLogoUrl(employer.getLogoUrl());
        response.setAddress(employer.getAddress());
        response.setIsApproved(employer.getIsApproved());
        response.setIsActive(employer.getIsActive());
        response.setCreatedAt(employer.getCreatedAt());
        response.setUpdatedAt(employer.getUpdatedAt());
        return response;
    }
}