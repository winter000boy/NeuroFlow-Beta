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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for managing job applications
 * Requirements: 2.3, 2.4, 2.5, 4.1, 4.2, 4.3
 */
@Service
@Transactional
public class ApplicationService {
    
    private static final Logger logger = LoggerFactory.getLogger(ApplicationService.class);
    
    @Autowired
    private ApplicationRepository applicationRepository;
    
    @Autowired
    private RestTemplate restTemplate;
    
    // External service URLs (should be configured via properties)
    private static final String JOB_SERVICE_URL = "http://job-service:8083";
    private static final String USER_SERVICE_URL = "http://user-service:8082";
    
    /**
     * Create a new job application
     * Requirements: 2.3, 2.5
     */
    public ApplicationResponse createApplication(String candidateId, CreateApplicationRequest request) {
        logger.info("Creating application for candidate {} and job {}", candidateId, request.getJobId());
        
        // Check if application already exists
        if (applicationRepository.existsByCandidateIdAndJobId(candidateId, request.getJobId())) {
            throw new DuplicateApplicationException(candidateId, request.getJobId());
        }
        
        // Validate job exists and is active
        validateJobExists(request.getJobId());
        
        // Get employer ID from job
        String employerId = getEmployerIdFromJob(request.getJobId());
        
        // Create application
        Application application = new Application(candidateId, request.getJobId(), employerId);
        application.setCoverLetter(request.getCoverLetter());
        application.setResumeUrl(request.getResumeUrl());
        application.setAdditionalDocuments(request.getAdditionalDocuments());
        
        Application savedApplication = applicationRepository.save(application);
        
        logger.info("Application created successfully with ID: {}", savedApplication.getId());
        
        return convertToResponse(savedApplication);
    }
    
    /**
     * Get application by ID
     * Requirements: 2.4, 4.1, 4.2
     */
    @Transactional(readOnly = true)
    public ApplicationResponse getApplicationById(String applicationId) {
        logger.debug("Fetching application with ID: {}", applicationId);
        
        Application application = applicationRepository.findById(applicationId)
            .orElseThrow(() -> new ResourceNotFoundException("Application", applicationId));
        
        return convertToResponse(application);
    }
    
    /**
     * Get applications for a candidate
     * Requirements: 2.4
     */
    @Transactional(readOnly = true)
    public PagedResponse<ApplicationResponse> getCandidateApplications(String candidateId, int page, int size, String status) {
        logger.debug("Fetching applications for candidate: {}, page: {}, size: {}, status: {}", 
                    candidateId, page, size, status);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "appliedAt"));
        Page<Application> applicationPage;
        
        if (status != null && !status.isEmpty()) {
            ApplicationStatus applicationStatus = ApplicationStatus.valueOf(status.toUpperCase());
            applicationPage = applicationRepository.findByCandidateIdAndStatus(candidateId, applicationStatus, pageable);
        } else {
            applicationPage = applicationRepository.findByCandidateId(candidateId, pageable);
        }
        
        List<ApplicationResponse> responses = applicationPage.getContent().stream()
            .map(this::convertToResponseWithJobInfo)
            .collect(Collectors.toList());
        
        return new PagedResponse<>(responses, page, size, applicationPage.getTotalElements());
    }
    
    /**
     * Get applications for a job (for employers)
     * Requirements: 4.1, 4.2
     */
    @Transactional(readOnly = true)
    public PagedResponse<ApplicationResponse> getJobApplications(String jobId, String employerId, int page, int size, String status) {
        logger.debug("Fetching applications for job: {}, employer: {}, page: {}, size: {}, status: {}", 
                    jobId, employerId, page, size, status);
        
        // Validate employer owns the job
        validateEmployerOwnsJob(employerId, jobId);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "appliedAt"));
        Page<Application> applicationPage;
        
        if (status != null && !status.isEmpty()) {
            ApplicationStatus applicationStatus = ApplicationStatus.valueOf(status.toUpperCase());
            applicationPage = applicationRepository.findByJobIdAndStatus(jobId, applicationStatus, pageable);
        } else {
            applicationPage = applicationRepository.findByJobId(jobId, pageable);
        }
        
        List<ApplicationResponse> responses = applicationPage.getContent().stream()
            .map(this::convertToResponseWithCandidateInfo)
            .collect(Collectors.toList());
        
        return new PagedResponse<>(responses, page, size, applicationPage.getTotalElements());
    }
    
    /**
     * Get applications for an employer (all jobs)
     * Requirements: 4.1, 4.2
     */
    @Transactional(readOnly = true)
    public PagedResponse<ApplicationResponse> getEmployerApplications(String employerId, int page, int size, String status) {
        logger.debug("Fetching applications for employer: {}, page: {}, size: {}, status: {}", 
                    employerId, page, size, status);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "appliedAt"));
        Page<Application> applicationPage;
        
        if (status != null && !status.isEmpty()) {
            ApplicationStatus applicationStatus = ApplicationStatus.valueOf(status.toUpperCase());
            applicationPage = applicationRepository.findByEmployerIdAndStatus(employerId, applicationStatus, pageable);
        } else {
            applicationPage = applicationRepository.findByEmployerId(employerId, pageable);
        }
        
        List<ApplicationResponse> responses = applicationPage.getContent().stream()
            .map(this::convertToResponseWithJobAndCandidateInfo)
            .collect(Collectors.toList());
        
        return new PagedResponse<>(responses, page, size, applicationPage.getTotalElements());
    }
    
    /**
     * Update application status
     * Requirements: 4.1, 4.2, 4.3
     */
    public ApplicationResponse updateApplicationStatus(String applicationId, String employerId, UpdateApplicationStatusRequest request) {
        logger.info("Updating application {} status to {} by employer {}", applicationId, request.getStatus(), employerId);
        
        Application application = applicationRepository.findById(applicationId)
            .orElseThrow(() -> new ResourceNotFoundException("Application", applicationId));
        
        // Validate employer owns the application
        if (!application.getEmployerId().equals(employerId)) {
            throw new IllegalArgumentException("Employer does not have permission to update this application");
        }
        
        // Validate status transition
        if (!application.getStatus().canTransitionTo(request.getStatus())) {
            throw new InvalidApplicationStatusException(application.getStatus(), request.getStatus());
        }
        
        // Update application based on new status
        updateApplicationFields(application, request, employerId);
        
        Application updatedApplication = applicationRepository.save(application);
        
        logger.info("Application {} status updated successfully to {}", applicationId, request.getStatus());
        
        return convertToResponse(updatedApplication);
    }
    
    /**
     * Withdraw application (by candidate)
     * Requirements: 2.4
     */
    public ApplicationResponse withdrawApplication(String applicationId, String candidateId) {
        logger.info("Withdrawing application {} by candidate {}", applicationId, candidateId);
        
        Application application = applicationRepository.findById(applicationId)
            .orElseThrow(() -> new ResourceNotFoundException("Application", applicationId));
        
        // Validate candidate owns the application
        if (!application.getCandidateId().equals(candidateId)) {
            throw new IllegalArgumentException("Candidate does not have permission to withdraw this application");
        }
        
        // Check if application can be withdrawn
        if (application.getStatus().isFinal()) {
            throw new InvalidApplicationStatusException("Cannot withdraw application in final status: " + application.getStatus());
        }
        
        application.updateStatus(ApplicationStatus.WITHDRAWN, candidateId, "Withdrawn by candidate");
        
        Application updatedApplication = applicationRepository.save(application);
        
        logger.info("Application {} withdrawn successfully", applicationId);
        
        return convertToResponse(updatedApplication);
    }
    
    /**
     * Get application statistics for a candidate
     * Requirements: 2.4
     */
    @Transactional(readOnly = true)
    public ApplicationStatistics getCandidateApplicationStatistics(String candidateId) {
        logger.debug("Fetching application statistics for candidate: {}", candidateId);
        
        long totalApplications = applicationRepository.countByCandidateId(candidateId);
        long appliedCount = applicationRepository.countByCandidateIdAndStatus(candidateId, ApplicationStatus.APPLIED);
        long inReviewCount = applicationRepository.countByCandidateIdAndStatus(candidateId, ApplicationStatus.IN_REVIEW);
        long interviewCount = applicationRepository.countByCandidateIdAndStatus(candidateId, ApplicationStatus.INTERVIEW_SCHEDULED) +
                             applicationRepository.countByCandidateIdAndStatus(candidateId, ApplicationStatus.INTERVIEWED);
        long hiredCount = applicationRepository.countByCandidateIdAndStatus(candidateId, ApplicationStatus.HIRED);
        long rejectedCount = applicationRepository.countByCandidateIdAndStatus(candidateId, ApplicationStatus.REJECTED);
        
        return new ApplicationStatistics(totalApplications, appliedCount, inReviewCount, interviewCount, hiredCount, rejectedCount);
    }
    
    /**
     * Get application statistics for an employer
     * Requirements: 4.1, 4.2
     */
    @Transactional(readOnly = true)
    public ApplicationStatistics getEmployerApplicationStatistics(String employerId) {
        logger.debug("Fetching application statistics for employer: {}", employerId);
        
        long totalApplications = applicationRepository.countByEmployerId(employerId);
        long appliedCount = applicationRepository.countByEmployerIdAndStatus(employerId, ApplicationStatus.APPLIED);
        long inReviewCount = applicationRepository.countByEmployerIdAndStatus(employerId, ApplicationStatus.IN_REVIEW);
        long interviewCount = applicationRepository.countByEmployerIdAndStatus(employerId, ApplicationStatus.INTERVIEW_SCHEDULED) +
                             applicationRepository.countByEmployerIdAndStatus(employerId, ApplicationStatus.INTERVIEWED);
        long hiredCount = applicationRepository.countByEmployerIdAndStatus(employerId, ApplicationStatus.HIRED);
        long rejectedCount = applicationRepository.countByEmployerIdAndStatus(employerId, ApplicationStatus.REJECTED);
        
        return new ApplicationStatistics(totalApplications, appliedCount, inReviewCount, interviewCount, hiredCount, rejectedCount);
    }
    
    // Private helper methods
    
    private void validateJobExists(String jobId) {
        try {
            String url = JOB_SERVICE_URL + "/api/jobs/" + jobId;
            restTemplate.getForObject(url, Object.class);
        } catch (Exception e) {
            logger.error("Failed to validate job existence: {}", jobId, e);
            throw new ResourceNotFoundException("Job", jobId);
        }
    }
    
    private String getEmployerIdFromJob(String jobId) {
        try {
            String url = JOB_SERVICE_URL + "/api/jobs/" + jobId;
            // This would return job details including employerId
            // For now, we'll assume the response has an employerId field
            Object jobResponse = restTemplate.getForObject(url, Object.class);
            // Extract employerId from response (implementation depends on actual job service response)
            // This is a placeholder - actual implementation would parse the response
            return "employer-id-from-job-service";
        } catch (Exception e) {
            logger.error("Failed to get employer ID from job: {}", jobId, e);
            throw new ResourceNotFoundException("Job", jobId);
        }
    }
    
    private void validateEmployerOwnsJob(String employerId, String jobId) {
        String jobEmployerId = getEmployerIdFromJob(jobId);
        if (!employerId.equals(jobEmployerId)) {
            throw new IllegalArgumentException("Employer does not own this job");
        }
    }
    
    private void updateApplicationFields(Application application, UpdateApplicationStatusRequest request, String employerId) {
        application.updateStatus(request.getStatus(), employerId, request.getReason());
        
        if (request.getNotes() != null) {
            application.setNotes(request.getNotes());
        }
        
        switch (request.getStatus()) {
            case INTERVIEW_SCHEDULED:
                if (request.getInterviewScheduled() != null) {
                    application.setInterviewScheduled(request.getInterviewScheduled());
                }
                if (request.getInterviewNotes() != null) {
                    application.setInterviewNotes(request.getInterviewNotes());
                }
                break;
            case OFFER_MADE:
                if (request.getSalaryOffered() != null) {
                    application.setSalaryOffered(request.getSalaryOffered());
                }
                if (request.getOfferDetails() != null) {
                    application.setOfferDetails(request.getOfferDetails());
                }
                if (request.getOfferExpiresAt() != null) {
                    application.setOfferExpiresAt(request.getOfferExpiresAt());
                }
                break;
            case REJECTED:
                if (request.getRejectionReason() != null) {
                    application.setRejectionReason(request.getRejectionReason());
                }
                break;
        }
    }
    
    private ApplicationResponse convertToResponse(Application application) {
        ApplicationResponse response = new ApplicationResponse();
        response.setId(application.getId());
        response.setCandidateId(application.getCandidateId());
        response.setJobId(application.getJobId());
        response.setEmployerId(application.getEmployerId());
        response.setStatus(application.getStatus());
        response.setCoverLetter(application.getCoverLetter());
        response.setResumeUrl(application.getResumeUrl());
        response.setAdditionalDocuments(application.getAdditionalDocuments());
        response.setNotes(application.getNotes());
        response.setInterviewScheduled(application.getInterviewScheduled());
        response.setInterviewNotes(application.getInterviewNotes());
        response.setRejectionReason(application.getRejectionReason());
        response.setSalaryOffered(application.getSalaryOffered());
        response.setOfferDetails(application.getOfferDetails());
        response.setOfferExpiresAt(application.getOfferExpiresAt());
        response.setAppliedAt(application.getAppliedAt());
        response.setUpdatedAt(application.getUpdatedAt());
        response.setStatusHistory(application.getStatusHistory());
        
        return response;
    }
    
    private ApplicationResponse convertToResponseWithJobInfo(Application application) {
        ApplicationResponse response = convertToResponse(application);
        
        // Fetch job information
        try {
            String url = JOB_SERVICE_URL + "/api/jobs/" + application.getJobId();
            // This would fetch job details and set jobTitle, companyName
            // For now, we'll set placeholder values
            response.setJobTitle("Job Title from Service");
            response.setCompanyName("Company Name from Service");
        } catch (Exception e) {
            logger.warn("Failed to fetch job information for application: {}", application.getId(), e);
        }
        
        return response;
    }
    
    private ApplicationResponse convertToResponseWithCandidateInfo(Application application) {
        ApplicationResponse response = convertToResponse(application);
        
        // Fetch candidate information
        try {
            String url = USER_SERVICE_URL + "/api/candidates/" + application.getCandidateId();
            // This would fetch candidate details and set candidateName, candidateEmail
            // For now, we'll set placeholder values
            response.setCandidateName("Candidate Name from Service");
            response.setCandidateEmail("candidate@example.com");
        } catch (Exception e) {
            logger.warn("Failed to fetch candidate information for application: {}", application.getId(), e);
        }
        
        return response;
    }
    
    private ApplicationResponse convertToResponseWithJobAndCandidateInfo(Application application) {
        ApplicationResponse response = convertToResponseWithJobInfo(application);
        
        // Fetch candidate information
        try {
            String url = USER_SERVICE_URL + "/api/candidates/" + application.getCandidateId();
            // This would fetch candidate details and set candidateName, candidateEmail
            // For now, we'll set placeholder values
            response.setCandidateName("Candidate Name from Service");
            response.setCandidateEmail("candidate@example.com");
        } catch (Exception e) {
            logger.warn("Failed to fetch candidate information for application: {}", application.getId(), e);
        }
        
        return response;
    }
    
    /**
     * Inner class for application statistics
     */
    public static class ApplicationStatistics {
        private final long totalApplications;
        private final long appliedCount;
        private final long inReviewCount;
        private final long interviewCount;
        private final long hiredCount;
        private final long rejectedCount;
        
        public ApplicationStatistics(long totalApplications, long appliedCount, long inReviewCount, 
                                   long interviewCount, long hiredCount, long rejectedCount) {
            this.totalApplications = totalApplications;
            this.appliedCount = appliedCount;
            this.inReviewCount = inReviewCount;
            this.interviewCount = interviewCount;
            this.hiredCount = hiredCount;
            this.rejectedCount = rejectedCount;
        }
        
        // Getters
        public long getTotalApplications() { return totalApplications; }
        public long getAppliedCount() { return appliedCount; }
        public long getInReviewCount() { return inReviewCount; }
        public long getInterviewCount() { return interviewCount; }
        public long getHiredCount() { return hiredCount; }
        public long getRejectedCount() { return rejectedCount; }
    }
}