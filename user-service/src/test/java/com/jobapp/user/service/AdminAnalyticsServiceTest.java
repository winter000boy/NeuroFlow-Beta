package com.jobapp.user.service;

import com.jobapp.user.dto.DashboardAnalyticsResponse;
import com.jobapp.user.dto.SystemReportSummary;
import com.jobapp.user.model.*;
import com.jobapp.user.repository.AdminRepository;
import com.jobapp.user.repository.CandidateRepository;
import com.jobapp.user.repository.EmployerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AdminService analytics functionality
 * Requirements: 5.4
 */
@ExtendWith(MockitoExtension.class)
public class AdminAnalyticsServiceTest {

    @Mock
    private AdminRepository adminRepository;

    @Mock
    private CandidateRepository candidateRepository;

    @Mock
    private EmployerRepository employerRepository;

    @Mock
    private JwtTokenService jwtTokenService;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private AdminService adminService;

    private Admin testAdmin;

    @BeforeEach
    void setUp() {
        testAdmin = new Admin();
        testAdmin.setId("admin123");
        testAdmin.setEmail("admin@test.com");
        testAdmin.setName("Test Admin");
        testAdmin.setRole(AdminRole.SUPER_ADMIN);
        testAdmin.setPermissions(Arrays.asList(AdminPermission.getAllPermissions()));
        testAdmin.setIsActive(true);
    }

    @Test
    void testGetUserAnalytics_ValidAdmin_ReturnsStatistics() {
        // Given
        when(adminRepository.findByEmailAndIsActive("admin@test.com", true))
                .thenReturn(Optional.of(testAdmin));
        when(candidateRepository.count()).thenReturn(100L);
        when(candidateRepository.countByIsActive(true)).thenReturn(90L);
        when(candidateRepository.countByIsActive(false)).thenReturn(10L);
        when(employerRepository.count()).thenReturn(50L);
        when(employerRepository.countByIsApproved(true)).thenReturn(40L);
        when(employerRepository.countPendingApproval()).thenReturn(8L);
        when(employerRepository.countByIsApproved(false)).thenReturn(2L);
        when(employerRepository.countByIsApprovedAndIsActive(true, false)).thenReturn(5L);

        Page<Candidate> candidatePage = new PageImpl<>(Arrays.asList());
        Page<Employer> employerPage = new PageImpl<>(Arrays.asList());
        when(candidateRepository.findByCreatedAtBetween(any(LocalDateTime.class), any(LocalDateTime.class), any()))
                .thenReturn(candidatePage);
        when(employerRepository.findByCreatedAtBetween(any(LocalDateTime.class), any(LocalDateTime.class), any()))
                .thenReturn(employerPage);

        // When
        DashboardAnalyticsResponse.UserStatistics result = adminService.getUserAnalytics("admin@test.com");

        // Then
        assertNotNull(result);
        assertEquals(100L, result.getTotalCandidates());
        assertEquals(90L, result.getActiveCandidates());
        assertEquals(10L, result.getBlockedCandidates());
        assertEquals(50L, result.getTotalEmployers());
        assertEquals(40L, result.getApprovedEmployers());
        assertEquals(8L, result.getPendingEmployers());
        assertEquals(2L, result.getRejectedEmployers());
        assertEquals(5L, result.getBlockedEmployers());

        verify(adminRepository).findByEmailAndIsActive("admin@test.com", true);
        verify(candidateRepository).count();
        verify(employerRepository).count();
    }

    @Test
    void testGetSystemAnalytics_ValidAdmin_ReturnsStatistics() {
        // Given
        when(adminRepository.findByEmailAndIsActive("admin@test.com", true))
                .thenReturn(Optional.of(testAdmin));
        when(adminRepository.count()).thenReturn(5L);
        when(adminRepository.countByIsActive(true)).thenReturn(4L);
        when(adminRepository.getTotalLoginCount()).thenReturn(150L);
        when(adminRepository.getTotalActionsCount()).thenReturn(500L);

        // When
        DashboardAnalyticsResponse.SystemStatistics result = adminService.getSystemAnalytics("admin@test.com");

        // Then
        assertNotNull(result);
        assertEquals(5L, result.getTotalAdmins());
        assertEquals(4L, result.getActiveAdmins());
        assertEquals(150L, result.getTotalLogins());
        assertEquals(500L, result.getTotalAdminActions());
        assertNotNull(result.getLastSystemUpdate());

        verify(adminRepository).findByEmailAndIsActive("admin@test.com", true);
        verify(adminRepository).count();
        verify(adminRepository).countByIsActive(true);
        verify(adminRepository).getTotalLoginCount();
        verify(adminRepository).getTotalActionsCount();
    }

    @Test
    void testGetDashboardAnalytics_ValidAdmin_ReturnsCompleteAnalytics() {
        // Given
        when(adminRepository.findByEmailAndIsActive("admin@test.com", true))
                .thenReturn(Optional.of(testAdmin));

        // Mock all the repository calls for user analytics
        when(candidateRepository.count()).thenReturn(100L);
        when(candidateRepository.countByIsActive(true)).thenReturn(90L);
        when(candidateRepository.countByIsActive(false)).thenReturn(10L);
        when(employerRepository.count()).thenReturn(50L);
        when(employerRepository.countByIsApproved(true)).thenReturn(40L);
        when(employerRepository.countPendingApproval()).thenReturn(8L);
        when(employerRepository.countByIsApproved(false)).thenReturn(2L);
        when(employerRepository.countByIsApprovedAndIsActive(true, false)).thenReturn(5L);

        // Mock system analytics
        when(adminRepository.count()).thenReturn(5L);
        when(adminRepository.countByIsActive(true)).thenReturn(4L);
        when(adminRepository.getTotalLoginCount()).thenReturn(150L);
        when(adminRepository.getTotalActionsCount()).thenReturn(500L);

        Page<Candidate> candidatePage = new PageImpl<>(Arrays.asList());
        Page<Employer> employerPage = new PageImpl<>(Arrays.asList());
        when(candidateRepository.findByCreatedAtBetween(any(LocalDateTime.class), any(LocalDateTime.class), any()))
                .thenReturn(candidatePage);
        when(employerRepository.findByCreatedAtBetween(any(LocalDateTime.class), any(LocalDateTime.class), any()))
                .thenReturn(employerPage);

        // When
        DashboardAnalyticsResponse result = adminService.getDashboardAnalytics(30, "admin@test.com");

        // Then
        assertNotNull(result);
        assertNotNull(result.getUserStatistics());
        assertNotNull(result.getJobStatistics());
        assertNotNull(result.getApplicationStatistics());
        assertNotNull(result.getSystemStatistics());
        assertNotNull(result.getUserRegistrationTrends());
        assertNotNull(result.getJobPostingTrends());
        assertNotNull(result.getApplicationTrends());
        assertNotNull(result.getJobsByLocation());
        assertNotNull(result.getCandidatesByLocation());
        assertNotNull(result.getGeneratedAt());

        // Verify user statistics
        assertEquals(100L, result.getUserStatistics().getTotalCandidates());
        assertEquals(50L, result.getUserStatistics().getTotalEmployers());

        // Verify system statistics
        assertEquals(5L, result.getSystemStatistics().getTotalAdmins());
        assertEquals(150L, result.getSystemStatistics().getTotalLogins());

        // Verify geographic data
        assertTrue(result.getJobsByLocation().containsKey("New York"));
        assertTrue(result.getCandidatesByLocation().containsKey("New York"));
    }

    @Test
    void testGetSystemReportsSummary_ValidAdmin_ReturnsReport() {
        // Given
        when(adminRepository.findByEmailAndIsActive("admin@test.com", true))
                .thenReturn(Optional.of(testAdmin));

        // When
        SystemReportSummary result = adminService.getSystemReportsSummary(30, "admin@test.com");

        // Then
        assertNotNull(result);
        assertEquals(30, result.getReportPeriodDays());
        assertNotNull(result.getGeneratedAt());
        assertNotNull(result.getPerformance());
        assertNotNull(result.getSecurity());
        assertNotNull(result.getContent());
        assertNotNull(result.getEngagement());
        assertNotNull(result.getHealthIndicators());
        assertNotNull(result.getAlerts());

        // Verify performance metrics
        assertEquals(150.0, result.getPerformance().getAverageResponseTime());
        assertEquals(10000L, result.getPerformance().getTotalRequests());
        assertEquals(0.02, result.getPerformance().getErrorRate());

        // Verify security metrics
        assertEquals(25L, result.getSecurity().getFailedLoginAttempts());
        assertEquals(5L, result.getSecurity().getBlockedIPs());

        // Verify content metrics
        assertEquals(1000L, result.getContent().getTotalContentItems());
        assertEquals(0.95, result.getContent().getModerationAccuracy());

        // Verify engagement metrics
        assertEquals(800L, result.getEngagement().getActiveUsers());
        assertEquals(0.75, result.getEngagement().getUserRetentionRate());

        // Verify health indicators
        assertFalse(result.getHealthIndicators().isEmpty());
        assertTrue(result.getHealthIndicators().stream()
                .anyMatch(h -> "Database".equals(h.getComponent())));

        // Verify alerts
        assertFalse(result.getAlerts().isEmpty());
        assertTrue(result.getAlerts().stream()
                .anyMatch(a -> "High Storage Usage".equals(a.getTitle())));
    }

    @Test
    void testGetJobAnalytics_ValidAdmin_ReturnsPlaceholderStatistics() {
        // Given
        when(adminRepository.findByEmailAndIsActive("admin@test.com", true))
                .thenReturn(Optional.of(testAdmin));

        // When
        DashboardAnalyticsResponse.JobStatistics result = adminService.getJobAnalytics("admin@test.com");

        // Then
        assertNotNull(result);
        // Since this is a placeholder implementation, all values should be 0
        assertEquals(0L, result.getTotalJobs());
        assertEquals(0L, result.getActiveJobs());
        assertEquals(0L, result.getInactiveJobs());
        assertEquals(0L, result.getJobsThisMonth());

        verify(adminRepository).findByEmailAndIsActive("admin@test.com", true);
    }

    @Test
    void testGetApplicationAnalytics_ValidAdmin_ReturnsPlaceholderStatistics() {
        // Given
        when(adminRepository.findByEmailAndIsActive("admin@test.com", true))
                .thenReturn(Optional.of(testAdmin));

        // When
        DashboardAnalyticsResponse.ApplicationStatistics result = adminService
                .getApplicationAnalytics("admin@test.com");

        // Then
        assertNotNull(result);
        // Since this is a placeholder implementation, all values should be 0
        assertEquals(0L, result.getTotalApplications());
        assertEquals(0L, result.getApplicationsThisMonth());
        assertEquals(0.0, result.getAverageApplicationsPerJob());
        assertEquals(0.0, result.getApplicationSuccessRate());

        verify(adminRepository).findByEmailAndIsActive("admin@test.com", true);
    }

    @Test
    void testAnalytics_UnauthorizedAdmin_ThrowsException() {
        // Given
        Admin limitedAdmin = new Admin();
        limitedAdmin.setEmail("limited@test.com");
        limitedAdmin.setRole(AdminRole.SUPPORT);
        limitedAdmin.setPermissions(Arrays.asList(AdminPermission.VIEW_USERS)); // No analytics permission
        limitedAdmin.setIsActive(true);

        when(adminRepository.findByEmailAndIsActive("limited@test.com", true))
                .thenReturn(Optional.of(limitedAdmin));

        // When & Then
        assertThrows(Exception.class, () -> {
            adminService.getUserAnalytics("limited@test.com");
        });

        assertThrows(Exception.class, () -> {
            adminService.getSystemAnalytics("limited@test.com");
        });

        assertThrows(Exception.class, () -> {
            adminService.getDashboardAnalytics(30, "limited@test.com");
        });
    }

    @Test
    void testAnalytics_InactiveAdmin_ThrowsException() {
        // Given
        when(adminRepository.findByEmailAndIsActive("inactive@test.com", true))
                .thenReturn(Optional.empty());

        // When & Then
        assertThrows(Exception.class, () -> {
            adminService.getUserAnalytics("inactive@test.com");
        });
    }

    @Test
    void testUserRegistrationTrends_ReturnsTimeSeriesData() {
        // Given
        when(adminRepository.findByEmailAndIsActive("admin@test.com", true))
                .thenReturn(Optional.of(testAdmin));

        Page<Candidate> candidatePage = new PageImpl<>(Arrays.asList());
        when(candidateRepository.findByCreatedAtBetween(any(LocalDateTime.class), any(LocalDateTime.class), any()))
                .thenReturn(candidatePage);

        // Mock other required calls for dashboard analytics
        when(candidateRepository.count()).thenReturn(100L);
        when(candidateRepository.countByIsActive(true)).thenReturn(90L);
        when(candidateRepository.countByIsActive(false)).thenReturn(10L);
        when(employerRepository.count()).thenReturn(50L);
        when(employerRepository.countByIsApproved(true)).thenReturn(40L);
        when(employerRepository.countPendingApproval()).thenReturn(8L);
        when(employerRepository.countByIsApproved(false)).thenReturn(2L);
        when(employerRepository.countByIsApprovedAndIsActive(true, false)).thenReturn(5L);
        when(adminRepository.count()).thenReturn(5L);
        when(adminRepository.countByIsActive(true)).thenReturn(4L);
        when(adminRepository.getTotalLoginCount()).thenReturn(150L);
        when(adminRepository.getTotalActionsCount()).thenReturn(500L);

        Page<Employer> employerPage = new PageImpl<>(Arrays.asList());
        when(employerRepository.findByCreatedAtBetween(any(LocalDateTime.class), any(LocalDateTime.class), any()))
                .thenReturn(employerPage);

        // When
        DashboardAnalyticsResponse result = adminService.getDashboardAnalytics(7, "admin@test.com");

        // Then
        assertNotNull(result.getUserRegistrationTrends());
        assertTrue(result.getUserRegistrationTrends().size() >= 7); // At least 7 days of data

        // Verify each trend data point has date and count
        for (DashboardAnalyticsResponse.TimeSeriesData trend : result.getUserRegistrationTrends()) {
            assertNotNull(trend.getDate());
            assertTrue(trend.getCount() >= 0);
        }
    }
}