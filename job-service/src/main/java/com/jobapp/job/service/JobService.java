package com.jobapp.job.service;

import com.jobapp.job.dto.*;
import com.jobapp.job.exception.ResourceNotFoundException;
import com.jobapp.job.exception.UnauthorizedException;
import com.jobapp.job.model.Job;
import com.jobapp.job.model.JobType;
import com.jobapp.job.model.ExperienceLevel;
import com.jobapp.job.repository.JobRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for job management operations
 * Requirements: 3.2, 3.3
 */
@Service
@Transactional
public class JobService {
    
    private final JobRepository jobRepository;
    
    @Autowired
    public JobService(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }
    
    /**
     * Create a new job posting
     * @param employerId the employer ID
     * @param request the job creation request
     * @return the created job response
     */
    @Caching(evict = {
        @CacheEvict(value = "jobs", allEntries = true),
        @CacheEvict(value = "jobSearch", allEntries = true),
        @CacheEvict(value = "jobStats", key = "#employerId")
    })
    public JobResponse createJob(String employerId, CreateJobRequest request) {
        Job job = new Job();
        job.setEmployerId(employerId);
        job.setTitle(request.getTitle());
        job.setDescription(request.getDescription());
        job.setSalary(request.getSalary());
        job.setLocation(request.getLocation());
        job.setJobType(request.getJobType());
        job.setRequiredSkills(request.getRequiredSkills());
        job.setPreferredSkills(request.getPreferredSkills());
        job.setExperienceLevel(request.getExperienceLevel());
        job.setEducationRequirement(request.getEducationRequirement());
        job.setBenefits(request.getBenefits());
        job.setApplicationDeadline(request.getApplicationDeadline());
        job.setIsFeatured(request.getIsFeatured());
        
        Job savedJob = jobRepository.save(job);
        return convertToJobResponse(savedJob);
    }
    
    /**
     * Update an existing job posting
     * @param jobId the job ID
     * @param employerId the employer ID
     * @param request the job update request
     * @return the updated job response
     */
    @Caching(evict = {
        @CacheEvict(value = "jobDetails", key = "#jobId"),
        @CacheEvict(value = "jobs", allEntries = true),
        @CacheEvict(value = "jobSearch", allEntries = true),
        @CacheEvict(value = "jobStats", key = "#employerId")
    })
    public JobResponse updateJob(String jobId, String employerId, UpdateJobRequest request) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + jobId));
        
        // Verify ownership
        if (!job.getEmployerId().equals(employerId)) {
            throw new UnauthorizedException("You are not authorized to update this job");
        }
        
        // Update fields if provided
        if (request.getTitle() != null) {
            job.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            job.setDescription(request.getDescription());
        }
        if (request.getSalary() != null) {
            job.setSalary(request.getSalary());
        }
        if (request.getLocation() != null) {
            job.setLocation(request.getLocation());
        }
        if (request.getJobType() != null) {
            job.setJobType(request.getJobType());
        }
        if (request.getRequiredSkills() != null) {
            job.setRequiredSkills(request.getRequiredSkills());
        }
        if (request.getPreferredSkills() != null) {
            job.setPreferredSkills(request.getPreferredSkills());
        }
        if (request.getExperienceLevel() != null) {
            job.setExperienceLevel(request.getExperienceLevel());
        }
        if (request.getEducationRequirement() != null) {
            job.setEducationRequirement(request.getEducationRequirement());
        }
        if (request.getBenefits() != null) {
            job.setBenefits(request.getBenefits());
        }
        if (request.getApplicationDeadline() != null) {
            job.setApplicationDeadline(request.getApplicationDeadline());
        }
        if (request.getIsFeatured() != null) {
            job.setIsFeatured(request.getIsFeatured());
        }
        
        Job updatedJob = jobRepository.save(job);
        return convertToJobResponse(updatedJob);
    }
    
    /**
     * Activate or deactivate a job posting
     * @param jobId the job ID
     * @param employerId the employer ID
     * @param isActive the active status
     * @return the updated job response
     */
    public JobResponse toggleJobStatus(String jobId, String employerId, boolean isActive) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + jobId));
        
        // Verify ownership
        if (!job.getEmployerId().equals(employerId)) {
            throw new UnauthorizedException("You are not authorized to modify this job");
        }
        
        job.setIsActive(isActive);
        Job updatedJob = jobRepository.save(job);
        return convertToJobResponse(updatedJob);
    }
    
    /**
     * Get jobs by employer with pagination
     * @param employerId the employer ID
     * @param page the page number
     * @param size the page size
     * @param sortBy the sort field
     * @param sortDir the sort direction
     * @return paginated job summary responses
     */
    public PagedResponse<JobSummaryResponse> getJobsByEmployer(String employerId, int page, int size, 
                                                              String sortBy, String sortDir) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Job> jobPage = jobRepository.findByEmployerId(employerId, pageable);
        
        List<JobSummaryResponse> jobSummaries = jobPage.getContent().stream()
                .map(this::convertToJobSummaryResponse)
                .collect(Collectors.toList());
        
        return new PagedResponse<>(
                jobSummaries,
                jobPage.getNumber(),
                jobPage.getSize(),
                jobPage.getTotalElements(),
                jobPage.getTotalPages()
        );
    }
    
    /**
     * Get active jobs by employer with pagination
     * @param employerId the employer ID
     * @param page the page number
     * @param size the page size
     * @param sortBy the sort field
     * @param sortDir the sort direction
     * @return paginated job summary responses
     */
    public PagedResponse<JobSummaryResponse> getActiveJobsByEmployer(String employerId, int page, int size, 
                                                                    String sortBy, String sortDir) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Job> jobPage = jobRepository.findByEmployerIdAndIsActive(employerId, true, pageable);
        
        List<JobSummaryResponse> jobSummaries = jobPage.getContent().stream()
                .map(this::convertToJobSummaryResponse)
                .collect(Collectors.toList());
        
        return new PagedResponse<>(
                jobSummaries,
                jobPage.getNumber(),
                jobPage.getSize(),
                jobPage.getTotalElements(),
                jobPage.getTotalPages()
        );
    }
    
    /**
     * Get job by ID
     * @param jobId the job ID
     * @return the job response
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "jobDetails", key = "#jobId")
    public JobResponse getJobById(String jobId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + jobId));
        
        // Increment view count
        job.incrementViewCount();
        jobRepository.save(job);
        
        return convertToJobResponse(job);
    }
    
    /**
     * Get job by ID for employer (with ownership verification)
     * @param jobId the job ID
     * @param employerId the employer ID
     * @return the job response
     */
    @Transactional(readOnly = true)
    public JobResponse getJobByIdForEmployer(String jobId, String employerId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + jobId));
        
        // Verify ownership
        if (!job.getEmployerId().equals(employerId)) {
            throw new UnauthorizedException("You are not authorized to view this job");
        }
        
        return convertToJobResponse(job);
    }
    
    /**
     * Delete a job posting
     * @param jobId the job ID
     * @param employerId the employer ID
     */
    public void deleteJob(String jobId, String employerId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + jobId));
        
        // Verify ownership
        if (!job.getEmployerId().equals(employerId)) {
            throw new UnauthorizedException("You are not authorized to delete this job");
        }
        
        jobRepository.delete(job);
    }
    
    /**
     * Get job statistics for employer
     * @param employerId the employer ID
     * @return job statistics
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "jobStats", key = "#employerId")
    public JobStatisticsResponse getJobStatistics(String employerId) {
        long totalJobs = jobRepository.countByEmployerId(employerId);
        long activeJobs = jobRepository.countByEmployerIdAndIsActive(employerId, true);
        long inactiveJobs = totalJobs - activeJobs;
        
        // Get total applications and views for employer's jobs
        Page<Job> allJobs = jobRepository.findByEmployerId(employerId, Pageable.unpaged());
        int totalApplications = allJobs.getContent().stream()
                .mapToInt(job -> job.getApplicationCount() != null ? job.getApplicationCount() : 0)
                .sum();
        int totalViews = allJobs.getContent().stream()
                .mapToInt(job -> job.getViewCount() != null ? job.getViewCount() : 0)
                .sum();
        
        JobStatisticsResponse stats = new JobStatisticsResponse();
        stats.setTotalJobs(totalJobs);
        stats.setActiveJobs(activeJobs);
        stats.setInactiveJobs(inactiveJobs);
        stats.setTotalApplications(totalApplications);
        stats.setTotalViews(totalViews);
        
        return stats;
    }
    
    /**
     * Convert Job entity to JobResponse DTO
     */
    private JobResponse convertToJobResponse(Job job) {
        JobResponse response = new JobResponse();
        response.setId(job.getId());
        response.setEmployerId(job.getEmployerId());
        response.setTitle(job.getTitle());
        response.setDescription(job.getDescription());
        response.setSalary(job.getSalary());
        response.setLocation(job.getLocation());
        response.setJobType(job.getJobType());
        response.setRequiredSkills(job.getRequiredSkills());
        response.setPreferredSkills(job.getPreferredSkills());
        response.setExperienceLevel(job.getExperienceLevel());
        response.setEducationRequirement(job.getEducationRequirement());
        response.setBenefits(job.getBenefits());
        response.setApplicationDeadline(job.getApplicationDeadline());
        response.setIsActive(job.getIsActive());
        response.setIsFeatured(job.getIsFeatured());
        response.setApplicationCount(job.getApplicationCount());
        response.setViewCount(job.getViewCount());
        response.setCreatedAt(job.getCreatedAt());
        response.setUpdatedAt(job.getUpdatedAt());
        response.setExpiresAt(job.getExpiresAt());
        return response;
    }
    
    /**
     * Search jobs with filters and full-text search
     * @param search search text for title and description
     * @param location location filter
     * @param jobType job type filter
     * @param experienceLevel experience level filter
     * @param minSalary minimum salary filter
     * @param maxSalary maximum salary filter
     * @param skills required skills filter (comma-separated)
     * @param page page number
     * @param size page size
     * @param sortBy sort field
     * @param sortDir sort direction
     * @return paginated job summary responses
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "jobSearch", key = "#search + '_' + #location + '_' + #jobType + '_' + #experienceLevel + '_' + #minSalary + '_' + #maxSalary + '_' + #skills + '_' + #page + '_' + #size + '_' + #sortBy + '_' + #sortDir")
    public PagedResponse<JobSummaryResponse> searchJobs(String search, String location, JobType jobType,
                                                       ExperienceLevel experienceLevel, Double minSalary, 
                                                       Double maxSalary, String skills, int page, int size, 
                                                       String sortBy, String sortDir) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Job> jobPage;
        
        // If we have multiple filters, use advanced search
        if (hasMultipleFilters(search, location, jobType, experienceLevel, minSalary, maxSalary)) {
            jobPage = jobRepository.findByAdvancedSearch(
                    search, location, jobType, minSalary, maxSalary, experienceLevel, pageable);
        } else if (search != null && !search.trim().isEmpty()) {
            // Simple text search
            jobPage = jobRepository.findByTextSearch(search.trim(), pageable);
        } else if (location != null && !location.trim().isEmpty()) {
            // Location-only search
            jobPage = jobRepository.findByLocationContainingIgnoreCase(location.trim(), pageable);
        } else if (jobType != null) {
            // Job type filter
            jobPage = jobRepository.findByJobType(jobType, pageable);
        } else if (experienceLevel != null) {
            // Experience level filter
            jobPage = jobRepository.findByExperienceLevel(experienceLevel, pageable);
        } else if (minSalary != null || maxSalary != null) {
            // Salary range filter
            double min = minSalary != null ? minSalary : 0.0;
            double max = maxSalary != null ? maxSalary : Double.MAX_VALUE;
            jobPage = jobRepository.findBySalaryRange(min, max, pageable);
        } else if (skills != null && !skills.trim().isEmpty()) {
            // Skills-based search
            List<String> skillList = Arrays.asList(skills.split(","))
                    .stream()
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());
            jobPage = jobRepository.findByRequiredSkillsIn(skillList, pageable);
        } else {
            // Default: get all active jobs
            jobPage = jobRepository.findByIsActive(true, pageable);
        }
        
        List<JobSummaryResponse> jobSummaries = jobPage.getContent().stream()
                .map(this::convertToJobSummaryResponse)
                .collect(Collectors.toList());
        
        return new PagedResponse<>(
                jobSummaries,
                jobPage.getNumber(),
                jobPage.getSize(),
                jobPage.getTotalElements(),
                jobPage.getTotalPages()
        );
    }
    
    /**
     * Get featured jobs
     * @param page page number
     * @param size page size
     * @return paginated featured job summary responses
     */
    @Transactional(readOnly = true)
    public PagedResponse<JobSummaryResponse> getFeaturedJobs(int page, int size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Job> jobPage = jobRepository.findByIsFeaturedAndIsActive(true, true, pageable);
        
        List<JobSummaryResponse> jobSummaries = jobPage.getContent().stream()
                .map(this::convertToJobSummaryResponse)
                .collect(Collectors.toList());
        
        return new PagedResponse<>(
                jobSummaries,
                jobPage.getNumber(),
                jobPage.getSize(),
                jobPage.getTotalElements(),
                jobPage.getTotalPages()
        );
    }
    
    /**
     * Get recent jobs
     * @param days number of days to look back
     * @param page page number
     * @param size page size
     * @return paginated recent job summary responses
     */
    @Transactional(readOnly = true)
    public PagedResponse<JobSummaryResponse> getRecentJobs(int days, int page, int size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable pageable = PageRequest.of(page, size, sort);
        
        LocalDateTime daysAgo = LocalDateTime.now().minusDays(days);
        Page<Job> jobPage = jobRepository.findRecentlyPosted(daysAgo, true, pageable);
        
        List<JobSummaryResponse> jobSummaries = jobPage.getContent().stream()
                .map(this::convertToJobSummaryResponse)
                .collect(Collectors.toList());
        
        return new PagedResponse<>(
                jobSummaries,
                jobPage.getNumber(),
                jobPage.getSize(),
                jobPage.getTotalElements(),
                jobPage.getTotalPages()
        );
    }
    
    /**
     * Get popular jobs based on application count
     * @param page page number
     * @param size page size
     * @return paginated popular job summary responses
     */
    @Transactional(readOnly = true)
    public PagedResponse<JobSummaryResponse> getPopularJobs(int page, int size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "applicationCount", "viewCount");
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Job> jobPage = jobRepository.findTopJobsByApplicationCount(true, pageable);
        
        List<JobSummaryResponse> jobSummaries = jobPage.getContent().stream()
                .map(this::convertToJobSummaryResponse)
                .collect(Collectors.toList());
        
        return new PagedResponse<>(
                jobSummaries,
                jobPage.getNumber(),
                jobPage.getSize(),
                jobPage.getTotalElements(),
                jobPage.getTotalPages()
        );
    }
    
    /**
     * Helper method to check if multiple filters are applied
     */
    private boolean hasMultipleFilters(String search, String location, JobType jobType,
                                     ExperienceLevel experienceLevel, Double minSalary, Double maxSalary) {
        int filterCount = 0;
        if (search != null && !search.trim().isEmpty()) filterCount++;
        if (location != null && !location.trim().isEmpty()) filterCount++;
        if (jobType != null) filterCount++;
        if (experienceLevel != null) filterCount++;
        if (minSalary != null || maxSalary != null) filterCount++;
        
        return filterCount > 1;
    }
    
    /**
     * Get job details with company information
     * @param jobId the job ID
     * @return job detail response with company info
     */
    @Transactional(readOnly = true)
    public JobDetailResponse getJobDetailsWithCompany(String jobId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + jobId));
        
        // Only show active jobs to public
        if (!Boolean.TRUE.equals(job.getIsActive())) {
            throw new ResourceNotFoundException("Job not found with id: " + jobId);
        }
        
        // Increment view count
        job.incrementViewCount();
        jobRepository.save(job);
        
        JobDetailResponse response = convertToJobDetailResponse(job);
        
        // Add company information (this would typically come from user-service)
        // For now, we'll create a placeholder
        JobDetailResponse.CompanyInfo companyInfo = new JobDetailResponse.CompanyInfo();
        companyInfo.setId(job.getEmployerId());
        companyInfo.setName("Company Name"); // This should come from user-service
        response.setCompany(companyInfo);
        
        // Add related jobs (same company, similar skills, or location)
        List<JobSummaryResponse> relatedJobs = getRelatedJobs(job);
        response.setRelatedJobs(relatedJobs);
        
        return response;
    }
    
    /**
     * Get active jobs by company (public endpoint)
     * @param employerId the employer ID
     * @param page page number
     * @param size page size
     * @param sortBy sort field
     * @param sortDir sort direction
     * @return paginated job summary responses
     */
    @Transactional(readOnly = true)
    public PagedResponse<JobSummaryResponse> getActiveJobsByCompany(String employerId, int page, 
                                                                   int size, String sortBy, String sortDir) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Job> jobPage = jobRepository.findByEmployerIdAndIsActive(employerId, true, pageable);
        
        List<JobSummaryResponse> jobSummaries = jobPage.getContent().stream()
                .map(this::convertToJobSummaryResponse)
                .collect(Collectors.toList());
        
        return new PagedResponse<>(
                jobSummaries,
                jobPage.getNumber(),
                jobPage.getSize(),
                jobPage.getTotalElements(),
                jobPage.getTotalPages()
        );
    }
    
    /**
     * Get company profile information
     * @param employerId the employer ID
     * @return company profile response
     */
    @Transactional(readOnly = true)
    public CompanyProfileResponse getCompanyProfile(String employerId) {
        // Check if company has any jobs (to verify it exists)
        long totalJobs = jobRepository.countByEmployerId(employerId);
        if (totalJobs == 0) {
            throw new ResourceNotFoundException("Company not found with id: " + employerId);
        }
        
        CompanyProfileResponse response = new CompanyProfileResponse();
        response.setId(employerId);
        
        // This information should come from user-service
        // For now, we'll set placeholder values
        response.setCompanyName("Company Name");
        response.setDescription("Company Description");
        response.setWebsite("https://company.com");
        response.setIsApproved(true);
        response.setIsActive(true);
        
        // Set job statistics
        long activeJobs = jobRepository.countByEmployerIdAndIsActive(employerId, true);
        response.setTotalJobs((int) totalJobs);
        response.setActiveJobs((int) activeJobs);
        
        // Calculate total applications and views
        Page<Job> allJobs = jobRepository.findByEmployerId(employerId, Pageable.unpaged());
        int totalApplications = allJobs.getContent().stream()
                .mapToInt(job -> job.getApplicationCount() != null ? job.getApplicationCount() : 0)
                .sum();
        int totalViews = allJobs.getContent().stream()
                .mapToInt(job -> job.getViewCount() != null ? job.getViewCount() : 0)
                .sum();
        
        response.setTotalApplications(totalApplications);
        response.setTotalViews(totalViews);
        
        // Add recent jobs
        PagedResponse<JobSummaryResponse> recentJobs = getActiveJobsByCompany(
                employerId, 0, 5, "createdAt", "desc");
        response.setRecentJobs(recentJobs);
        
        return response;
    }
    
    /**
     * Clean up expired jobs
     * @return cleanup response with statistics
     */
    @Transactional
    public ExpiredJobsCleanupResponse cleanupExpiredJobs() {
        LocalDateTime now = LocalDateTime.now();
        
        // Find expired jobs
        Page<Job> expiredJobs = jobRepository.findExpiredJobs(now, Pageable.unpaged());
        
        ExpiredJobsCleanupResponse response = new ExpiredJobsCleanupResponse();
        response.setTotalExpiredJobs((int) expiredJobs.getTotalElements());
        
        List<String> deactivatedJobIds = new ArrayList<>();
        List<String> deletedJobIds = new ArrayList<>();
        
        for (Job job : expiredJobs.getContent()) {
            // If job has applications, just deactivate it
            if (job.getApplicationCount() != null && job.getApplicationCount() > 0) {
                job.setIsActive(false);
                jobRepository.save(job);
                deactivatedJobIds.add(job.getId());
            } else {
                // If no applications, delete the job
                jobRepository.delete(job);
                deletedJobIds.add(job.getId());
            }
        }
        
        response.setDeactivatedJobs(deactivatedJobIds.size());
        response.setDeletedJobs(deletedJobIds.size());
        response.setDeactivatedJobIds(deactivatedJobIds);
        response.setDeletedJobIds(deletedJobIds);
        
        return response;
    }
    
    /**
     * Get related jobs based on company, skills, or location
     * @param job the reference job
     * @return list of related job summaries
     */
    private List<JobSummaryResponse> getRelatedJobs(Job job) {
        // Get jobs from same company (excluding current job)
        Pageable pageable = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Job> companyJobs = jobRepository.findByEmployerIdAndIsActive(job.getEmployerId(), true, pageable);
        
        List<Job> relatedJobs = companyJobs.getContent().stream()
                .filter(j -> !j.getId().equals(job.getId()))
                .collect(Collectors.toList());
        
        // If we need more jobs, find by similar skills or location
        if (relatedJobs.size() < 3) {
            if (job.getRequiredSkills() != null && !job.getRequiredSkills().isEmpty()) {
                Page<Job> skillBasedJobs = jobRepository.findByRequiredSkillsIn(
                        job.getRequiredSkills(), PageRequest.of(0, 5));
                
                skillBasedJobs.getContent().stream()
                        .filter(j -> !j.getId().equals(job.getId()) && 
                                    !j.getEmployerId().equals(job.getEmployerId()))
                        .limit(3 - relatedJobs.size())
                        .forEach(relatedJobs::add);
            }
        }
        
        return relatedJobs.stream()
                .map(this::convertToJobSummaryResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Convert Job entity to JobDetailResponse DTO
     */
    private JobDetailResponse convertToJobDetailResponse(Job job) {
        JobDetailResponse response = new JobDetailResponse();
        response.setId(job.getId());
        response.setTitle(job.getTitle());
        response.setDescription(job.getDescription());
        response.setSalary(job.getSalary());
        response.setLocation(job.getLocation());
        response.setJobType(job.getJobType());
        response.setRequiredSkills(job.getRequiredSkills());
        response.setPreferredSkills(job.getPreferredSkills());
        response.setExperienceLevel(job.getExperienceLevel());
        response.setEducationRequirement(job.getEducationRequirement());
        response.setBenefits(job.getBenefits());
        response.setApplicationDeadline(job.getApplicationDeadline());
        response.setIsActive(job.getIsActive());
        response.setIsFeatured(job.getIsFeatured());
        response.setApplicationCount(job.getApplicationCount());
        response.setViewCount(job.getViewCount());
        response.setCreatedAt(job.getCreatedAt());
        response.setUpdatedAt(job.getUpdatedAt());
        response.setExpiresAt(job.getExpiresAt());
        response.setIsAcceptingApplications(job.isAcceptingApplications());
        
        return response;
    }
    
    /**
     * Convert Job entity to JobSummaryResponse DTO
     */
    private JobSummaryResponse convertToJobSummaryResponse(Job job) {
        JobSummaryResponse response = new JobSummaryResponse();
        response.setId(job.getId());
        response.setEmployerId(job.getEmployerId());
        response.setTitle(job.getTitle());
        
        // Create short description (first 150 characters)
        String description = job.getDescription();
        if (description != null && description.length() > 150) {
            response.setShortDescription(description.substring(0, 150) + "...");
        } else {
            response.setShortDescription(description);
        }
        
        response.setSalary(job.getSalary());
        response.setLocation(job.getLocation());
        response.setJobType(job.getJobType());
        response.setExperienceLevel(job.getExperienceLevel());
        response.setIsActive(job.getIsActive());
        response.setIsFeatured(job.getIsFeatured());
        response.setApplicationCount(job.getApplicationCount());
        response.setViewCount(job.getViewCount());
        response.setCreatedAt(job.getCreatedAt());
        response.setExpiresAt(job.getExpiresAt());
        response.setIsAcceptingApplications(job.isAcceptingApplications());
        
        return response;
    }
}