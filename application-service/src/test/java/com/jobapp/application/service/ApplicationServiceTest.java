package com.jobapp.application.service;

import com.jobapp.application.dto.ApplicationResponse;
import com.jobapp.application.dto.CreateApplicationRequest;
import com.jobapp.application.dto.PagedResponse;
import com.jobapp.application.dto.UpdateApplicationStatusRequest;
import com.jobapp.application.exception.DuplicateApplicationException;
import com.jobapp.application.exception.InvalidApplicationStatusException;
import com.jobapp.application.exception.ResourceNotFoundException;
import com.jobapp.application.model.Application;
import com.jobapp.application.model.ApplicationStatus;
import com.jobapp.application.repository.ApplicationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ApplicationService
 * Requirements: 2.3, 2.4, 2.5, 4.1, 4.2, 4.3
 */
@ExtendWith(MockitoExtension.class)
class ApplicationServiceTest {

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ApplicationService applicationService;

    private Application testApplication;
    private CreateApplicationRequest createRequest;
    private UpdateApplicationStatusRequest updateRequest;

    @BeforeEach
    void setUp() {
        testApplication = new Application("candidate123", "job123", "employer123");
        testApplication.setId("app123");
        testApplication.setCoverLetter("Test cover letter");
        testApplication.setResumeUrl("http://example.com/resume.pdf");

        createRequest = new CreateApplicationRequest();
        createRequest.setJobId("job123");
        createRequest.setCoverLetter("Test cover letter");
        createRequest.setResumeUrl("http://example.com/resume.pdf");

        updateRequest = new UpdateApplicationStatusRequest();
        updateRequest.setStatus(ApplicationStatus.IN_REVIEW);
        updateRequest.setNotes("Application looks promising");
    }

    @Test
    void createApplication_ValidRequest_ReturnsApplicationResponse() {
        // Given
        when(applicationRepository.existsByCandidateIdAndJobId("candidate123", "job123")).thenReturn(false);
        when(restTemplate.getForObject(anyString(), eq(Object.class))).thenReturn(new Object());
        when(applicationRepository.save(any(Application.class))).thenReturn(testApplication);

        // When
        ApplicationResponse result = applicationService.createApplication("candidate123", createRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("app123");
        assertThat(result.getCandidateId()).isEqualTo("candidate123");
        assertThat(result.getJobId()).isEqualTo("job123");
        assertThat(result.getStatus()).isEqualTo(ApplicationStatus.APPLIED);
        assertThat(result.getCoverLetter()).isEqualTo("Test cover letter");

        verify(applicationRepository).existsByCandidateIdAndJobId("candidate123", "job123");
        verify(applicationRepository).save(any(Application.class));
    }

    @Test
    void createApplication_DuplicateApplication_ThrowsException() {
        // Given
        when(applicationRepository.existsByCandidateIdAndJobId("candidate123", "job123")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> applicationService.createApplication("candidate123", createRequest))
                .isInstanceOf(DuplicateApplicationException.class)
                .hasMessageContaining("Application already exists");

        verify(applicationRepository).existsByCandidateIdAndJobId("candidate123", "job123");
        verify(applicationRepository, never()).save(any(Application.class));
    }

    @Test
    void createApplication_JobNotFound_ThrowsException() {
        // Given
        when(applicationRepository.existsByCandidateIdAndJobId("candidate123", "job123")).thenReturn(false);
        when(restTemplate.getForObject(anyString(), eq(Object.class))).thenThrow(new RuntimeException("Job not found"));

        // When & Then
        assertThatThrownBy(() -> applicationService.createApplication("candidate123", createRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Job not found");

        verify(applicationRepository, never()).save(any(Application.class));
    }

    @Test
    void getApplicationById_ValidId_ReturnsApplicationResponse() {
        // Given
        when(applicationRepository.findById("app123")).thenReturn(Optional.of(testApplication));

        // When
        ApplicationResponse result = applicationService.getApplicationById("app123");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("app123");
        assertThat(result.getCandidateId()).isEqualTo("candidate123");
        assertThat(result.getJobId()).isEqualTo("job123");

        verify(applicationRepository).findById("app123");
    }

    @Test
    void getApplicationById_NotFound_ThrowsException() {
        // Given
        when(applicationRepository.findById("nonexistent")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> applicationService.getApplicationById("nonexistent"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Application not found");

        verify(applicationRepository).findById("nonexistent");
    }

    @Test
    void getCandidateApplications_ValidRequest_ReturnsPagedResponse() {
        // Given
        List<Application> applications = Arrays.asList(testApplication);
        Page<Application> page = new PageImpl<>(applications);

        when(applicationRepository.findByCandidateId(eq("candidate123"), any(Pageable.class)))
                .thenReturn(page);

        // When
        PagedResponse<ApplicationResponse> result = applicationService.getCandidateApplications("candidate123", 0, 10,
                null);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getCandidateId()).isEqualTo("candidate123");
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getPage()).isEqualTo(0);
        assertThat(result.getSize()).isEqualTo(10);

        verify(applicationRepository).findByCandidateId(eq("candidate123"), any(Pageable.class));
    }

    @Test
    void getCandidateApplications_WithStatusFilter_ReturnsFilteredResults() {
        // Given
        List<Application> applications = Arrays.asList(testApplication);
        Page<Application> page = new PageImpl<>(applications);

        when(applicationRepository.findByCandidateIdAndStatus(eq("candidate123"),
                eq(ApplicationStatus.APPLIED), any(Pageable.class)))
                .thenReturn(page);

        // When
        PagedResponse<ApplicationResponse> result = applicationService.getCandidateApplications("candidate123", 0, 10,
                "APPLIED");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getStatus()).isEqualTo(ApplicationStatus.APPLIED);

        verify(applicationRepository).findByCandidateIdAndStatus(eq("candidate123"),
                eq(ApplicationStatus.APPLIED), any(Pageable.class));
    }

    @Test
    void getJobApplications_ValidRequest_ReturnsPagedResponse() {
        // Given
        List<Application> applications = Arrays.asList(testApplication);
        Page<Application> page = new PageImpl<>(applications);

        when(restTemplate.getForObject(anyString(), eq(Object.class))).thenReturn(new Object());
        when(applicationRepository.findByJobId(eq("job123"), any(Pageable.class)))
                .thenReturn(page);

        // When
        PagedResponse<ApplicationResponse> result = applicationService.getJobApplications("job123", "employer123", 0,
                10, null);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getJobId()).isEqualTo("job123");

        verify(applicationRepository).findByJobId(eq("job123"), any(Pageable.class));
    }

    @Test
    void getEmployerApplications_ValidRequest_ReturnsPagedResponse() {
        // Given
        List<Application> applications = Arrays.asList(testApplication);
        Page<Application> page = new PageImpl<>(applications);

        when(applicationRepository.findByEmployerId(eq("employer123"), any(Pageable.class)))
                .thenReturn(page);

        // When
        PagedResponse<ApplicationResponse> result = applicationService.getEmployerApplications("employer123", 0, 10,
                null);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getEmployerId()).isEqualTo("employer123");

        verify(applicationRepository).findByEmployerId(eq("employer123"), any(Pageable.class));
    }

    @Test
    void updateApplicationStatus_ValidRequest_ReturnsUpdatedApplication() {
        // Given
        Application updatedApplication = new Application("candidate123", "job123", "employer123");
        updatedApplication.setId("app123");
        updatedApplication.setStatus(ApplicationStatus.IN_REVIEW);

        when(applicationRepository.findById("app123")).thenReturn(Optional.of(testApplication));
        when(applicationRepository.save(any(Application.class))).thenReturn(updatedApplication);

        // When
        ApplicationResponse result = applicationService.updateApplicationStatus("app123", "employer123", updateRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("app123");
        assertThat(result.getStatus()).isEqualTo(ApplicationStatus.IN_REVIEW);

        verify(applicationRepository).findById("app123");
        verify(applicationRepository).save(any(Application.class));
    }

    @Test
    void updateApplicationStatus_WrongEmployer_ThrowsException() {
        // Given
        when(applicationRepository.findById("app123")).thenReturn(Optional.of(testApplication));

        // When & Then
        assertThatThrownBy(() -> applicationService.updateApplicationStatus("app123", "wrongEmployer", updateRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Employer does not have permission");

        verify(applicationRepository).findById("app123");
        verify(applicationRepository, never()).save(any(Application.class));
    }

    @Test
    void updateApplicationStatus_InvalidTransition_ThrowsException() {
        // Given
        testApplication.setStatus(ApplicationStatus.HIRED); // Final status
        when(applicationRepository.findById("app123")).thenReturn(Optional.of(testApplication));

        // When & Then
        assertThatThrownBy(() -> applicationService.updateApplicationStatus("app123", "employer123", updateRequest))
                .isInstanceOf(InvalidApplicationStatusException.class)
                .hasMessageContaining("Invalid status transition");

        verify(applicationRepository).findById("app123");
        verify(applicationRepository, never()).save(any(Application.class));
    }

    @Test
    void withdrawApplication_ValidRequest_ReturnsUpdatedApplication() {
        // Given
        Application withdrawnApplication = new Application("candidate123", "job123", "employer123");
        withdrawnApplication.setId("app123");
        withdrawnApplication.setStatus(ApplicationStatus.WITHDRAWN);

        when(applicationRepository.findById("app123")).thenReturn(Optional.of(testApplication));
        when(applicationRepository.save(any(Application.class))).thenReturn(withdrawnApplication);

        // When
        ApplicationResponse result = applicationService.withdrawApplication("app123", "candidate123");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("app123");
        assertThat(result.getStatus()).isEqualTo(ApplicationStatus.WITHDRAWN);

        verify(applicationRepository).findById("app123");
        verify(applicationRepository).save(any(Application.class));
    }

    @Test
    void withdrawApplication_WrongCandidate_ThrowsException() {
        // Given
        when(applicationRepository.findById("app123")).thenReturn(Optional.of(testApplication));

        // When & Then
        assertThatThrownBy(() -> applicationService.withdrawApplication("app123", "wrongCandidate"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Candidate does not have permission");

        verify(applicationRepository).findById("app123");
        verify(applicationRepository, never()).save(any(Application.class));
    }

    @Test
    void withdrawApplication_FinalStatus_ThrowsException() {
        // Given
        testApplication.setStatus(ApplicationStatus.HIRED); // Final status
        when(applicationRepository.findById("app123")).thenReturn(Optional.of(testApplication));

        // When & Then
        assertThatThrownBy(() -> applicationService.withdrawApplication("app123", "candidate123"))
                .isInstanceOf(InvalidApplicationStatusException.class)
                .hasMessageContaining("Cannot withdraw application in final status");

        verify(applicationRepository).findById("app123");
        verify(applicationRepository, never()).save(any(Application.class));
    }

    @Test
    void getCandidateApplicationStatistics_ValidRequest_ReturnsStatistics() {
        // Given
        when(applicationRepository.countByCandidateId("candidate123")).thenReturn(5L);
        when(applicationRepository.countByCandidateIdAndStatus("candidate123", ApplicationStatus.APPLIED))
                .thenReturn(2L);
        when(applicationRepository.countByCandidateIdAndStatus("candidate123", ApplicationStatus.IN_REVIEW))
                .thenReturn(1L);
        when(applicationRepository.countByCandidateIdAndStatus("candidate123", ApplicationStatus.INTERVIEW_SCHEDULED))
                .thenReturn(1L);
        when(applicationRepository.countByCandidateIdAndStatus("candidate123", ApplicationStatus.INTERVIEWED))
                .thenReturn(0L);
        when(applicationRepository.countByCandidateIdAndStatus("candidate123", ApplicationStatus.HIRED)).thenReturn(1L);
        when(applicationRepository.countByCandidateIdAndStatus("candidate123", ApplicationStatus.REJECTED))
                .thenReturn(0L);

        // When
        ApplicationService.ApplicationStatistics result = applicationService
                .getCandidateApplicationStatistics("candidate123");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTotalApplications()).isEqualTo(5L);
        assertThat(result.getAppliedCount()).isEqualTo(2L);
        assertThat(result.getInReviewCount()).isEqualTo(1L);
        assertThat(result.getInterviewCount()).isEqualTo(1L);
        assertThat(result.getHiredCount()).isEqualTo(1L);
        assertThat(result.getRejectedCount()).isEqualTo(0L);

        verify(applicationRepository).countByCandidateId("candidate123");
        verify(applicationRepository, times(6)).countByCandidateIdAndStatus(eq("candidate123"),
                any(ApplicationStatus.class));
    }

    @Test
    void getEmployerApplicationStatistics_ValidRequest_ReturnsStatistics() {
        // Given
        when(applicationRepository.countByEmployerId("employer123")).thenReturn(10L);
        when(applicationRepository.countByEmployerIdAndStatus("employer123", ApplicationStatus.APPLIED)).thenReturn(4L);
        when(applicationRepository.countByEmployerIdAndStatus("employer123", ApplicationStatus.IN_REVIEW))
                .thenReturn(3L);
        when(applicationRepository.countByEmployerIdAndStatus("employer123", ApplicationStatus.INTERVIEW_SCHEDULED))
                .thenReturn(1L);
        when(applicationRepository.countByEmployerIdAndStatus("employer123", ApplicationStatus.INTERVIEWED))
                .thenReturn(1L);
        when(applicationRepository.countByEmployerIdAndStatus("employer123", ApplicationStatus.HIRED)).thenReturn(1L);
        when(applicationRepository.countByEmployerIdAndStatus("employer123", ApplicationStatus.REJECTED))
                .thenReturn(0L);

        // When
        ApplicationService.ApplicationStatistics result = applicationService
                .getEmployerApplicationStatistics("employer123");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTotalApplications()).isEqualTo(10L);
        assertThat(result.getAppliedCount()).isEqualTo(4L);
        assertThat(result.getInReviewCount()).isEqualTo(3L);
        assertThat(result.getInterviewCount()).isEqualTo(2L);
        assertThat(result.getHiredCount()).isEqualTo(1L);
        assertThat(result.getRejectedCount()).isEqualTo(0L);

        verify(applicationRepository).countByEmployerId("employer123");
        verify(applicationRepository, times(6)).countByEmployerIdAndStatus(eq("employer123"),
                any(ApplicationStatus.class));
    }
}