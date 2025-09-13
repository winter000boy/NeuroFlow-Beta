package com.jobapp.job.service;

import com.jobapp.job.dto.*;
import com.jobapp.job.exception.ResourceNotFoundException;
import com.jobapp.job.exception.UnauthorizedException;
import com.jobapp.job.model.Job;
import com.jobapp.job.model.JobType;
import com.jobapp.job.model.ExperienceLevel;
import com.jobapp.job.repository.JobRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JobServiceTest {

    @Mock
    private JobRepository jobRepository;

    @InjectMocks
    private JobService jobService;

    private CreateJobRequest createJobRequest;
    private Job testJob;
    private String employerId = "employer123";
    private String jobId = "job123";

    @BeforeEach
    void setUp() {
        createJobRequest = new CreateJobRequest();
        createJobRequest.setTitle("Software Engineer");
        createJobRequest.setDescription("Java developer position");
        createJobRequest.setLocation("San Francisco");
        createJobRequest.setJobType(JobType.FULL_TIME);
        createJobRequest.setExperienceLevel(ExperienceLevel.MID_LEVEL);
        createJobRequest.setRequiredSkills(Arrays.asList("Java", "Spring Boot"));

        testJob = new Job();
        testJob.setId(jobId);
        testJob.setEmployerId(employerId);
        testJob.setTitle("Software Engineer");
        testJob.setDescription("Java developer position");
        testJob.setLocation("San Francisco");
        testJob.setJobType(JobType.FULL_TIME);
        testJob.setExperienceLevel(ExperienceLevel.MID_LEVEL);
        testJob.setRequiredSkills(Arrays.asList("Java", "Spring Boot"));
        testJob.setIsActive(true);
        testJob.setCreatedAt(LocalDateTime.now());
        testJob.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void createJob_ValidRequest_ReturnsJobResponse() {
        // Given
        when(jobRepository.save(any(Job.class))).thenReturn(testJob);

        // When
        JobResponse response = jobService.createJob(employerId, createJobRequest);

        // Then
        assertNotNull(response);
        assertEquals(testJob.getId(), response.getId());
        assertEquals(testJob.getTitle(), response.getTitle());
        assertEquals(testJob.getEmployerId(), response.getEmployerId());
        verify(jobRepository).save(any(Job.class));
    }

    @Test
    void getJobById_ValidId_ReturnsJobResponse() {
        // Given
        when(jobRepository.findById(jobId)).thenReturn(Optional.of(testJob));
        when(jobRepository.save(any(Job.class))).thenReturn(testJob);

        // When
        JobResponse response = jobService.getJobById(jobId);

        // Then
        assertNotNull(response);
        assertEquals(testJob.getId(), response.getId());
        verify(jobRepository).findById(jobId);
        verify(jobRepository).save(any(Job.class));
    }

    @Test
    void getJobById_InvalidId_ThrowsException() {
        // Given
        when(jobRepository.findById(jobId)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
            () -> jobService.getJobById(jobId));
        assertEquals("Job not found with id: " + jobId, exception.getMessage());
        verify(jobRepository).findById(jobId);
    }
}