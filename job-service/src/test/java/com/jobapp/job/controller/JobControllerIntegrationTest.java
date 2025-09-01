package com.jobapp.job.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobapp.job.dto.CreateJobRequest;
import com.jobapp.job.dto.UpdateJobRequest;
import com.jobapp.job.model.Job;
import com.jobapp.job.model.JobType;
import com.jobapp.job.model.ExperienceLevel;
import com.jobapp.job.model.SalaryRange;
import com.jobapp.job.repository.JobRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.hamcrest.Matchers.*;

/**
 * Integration tests for JobController
 * Requirements: 3.2, 3.3
 */
@SpringBootTest
@AutoConfigureWebMvc
@TestPropertySource(locations = "classpath:application-test.yml")
class JobControllerIntegrationTest {
    
    @Autowired
    private WebApplicationContext context;
    
    @Autowired
    private JobRepository jobRepository;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private MockMvc mockMvc;
    
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
        
        // Clean up database before each test
        jobRepository.deleteAll();
    }
    
    @Test
    @WithMockUser(username = "employer1", roles = {"EMPLOYER"})
    void createJob_ValidRequest_ReturnsCreatedJob() throws Exception {
        // Given
        CreateJobRequest request = new CreateJobRequest();
        request.setTitle("Senior Software Engineer");
        request.setDescription("We are looking for a senior software engineer with 5+ years of experience in Java and Spring Boot development.");
        request.setLocation("San Francisco, CA");
        request.setJobType(JobType.FULL_TIME);
        request.setExperienceLevel(ExperienceLevel.SENIOR);
        request.setRequiredSkills(Arrays.asList("Java", "Spring Boot", "MongoDB"));
        request.setPreferredSkills(Arrays.asList("React", "Docker"));
        
        SalaryRange salary = new SalaryRange();
        salary.setMin(120000.0);
        salary.setMax(180000.0);
        salary.setCurrency("USD");
        request.setSalary(salary);
        
        // When & Then
        mockMvc.perform(post("/api/jobs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title", is("Senior Software Engineer")))
                .andExpect(jsonPath("$.description", containsString("senior software engineer")))
                .andExpect(jsonPath("$.location", is("San Francisco, CA")))
                .andExpect(jsonPath("$.jobType", is("FULL_TIME")))
                .andExpect(jsonPath("$.experienceLevel", is("SENIOR")))
                .andExpect(jsonPath("$.salary.min", is(120000.0)))
                .andExpect(jsonPath("$.salary.max", is(180000.0)))
                .andExpect(jsonPath("$.salary.currency", is("USD")))
                .andExpect(jsonPath("$.requiredSkills", hasSize(3)))
                .andExpect(jsonPath("$.requiredSkills", hasItem("Java")))
                .andExpect(jsonPath("$.isActive", is(true)))
                .andExpect(jsonPath("$.employerId", is("employer1")))
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.createdAt", notNullValue()));
    }
    
    @Test
    @WithMockUser(username = "employer1", roles = {"EMPLOYER"})
    void createJob_InvalidRequest_ReturnsBadRequest() throws Exception {
        // Given - Invalid request with missing required fields
        CreateJobRequest request = new CreateJobRequest();
        request.setTitle(""); // Invalid - too short
        request.setDescription("Short"); // Invalid - too short
        
        // When & Then
        mockMvc.perform(post("/api/jobs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code", is("VALIDATION_ERROR")))
                .andExpect(jsonPath("$.validationErrors", notNullValue()));
    }
    
    @Test
    void createJob_Unauthorized_ReturnsUnauthorized() throws Exception {
        // Given
        CreateJobRequest request = new CreateJobRequest();
        request.setTitle("Software Engineer");
        request.setDescription("A great opportunity for a software engineer to join our team.");
        request.setLocation("New York, NY");
        request.setJobType(JobType.FULL_TIME);
        
        // When & Then
        mockMvc.perform(post("/api/jobs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }
    
    @Test
    @WithMockUser(username = "candidate1", roles = {"CANDIDATE"})
    void createJob_WrongRole_ReturnsForbidden() throws Exception {
        // Given
        CreateJobRequest request = new CreateJobRequest();
        request.setTitle("Software Engineer");
        request.setDescription("A great opportunity for a software engineer to join our team.");
        request.setLocation("New York, NY");
        request.setJobType(JobType.FULL_TIME);
        
        // When & Then
        mockMvc.perform(post("/api/jobs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }
    
    @Test
    @WithMockUser(username = "employer1", roles = {"EMPLOYER"})
    void updateJob_ValidRequest_ReturnsUpdatedJob() throws Exception {
        // Given - Create a job first
        Job job = createTestJob("employer1");
        job = jobRepository.save(job);
        
        UpdateJobRequest request = new UpdateJobRequest();
        request.setTitle("Updated Senior Software Engineer");
        request.setDescription("Updated description for the senior software engineer position.");
        request.setLocation("Seattle, WA");
        
        // When & Then
        mockMvc.perform(put("/api/jobs/{jobId}", job.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Updated Senior Software Engineer")))
                .andExpect(jsonPath("$.description", containsString("Updated description")))
                .andExpect(jsonPath("$.location", is("Seattle, WA")))
                .andExpect(jsonPath("$.id", is(job.getId())));
    }
    
    @Test
    @WithMockUser(username = "employer2", roles = {"EMPLOYER"})
    void updateJob_NotOwner_ReturnsForbidden() throws Exception {
        // Given - Create a job for employer1
        Job job = createTestJob("employer1");
        job = jobRepository.save(job);
        
        UpdateJobRequest request = new UpdateJobRequest();
        request.setTitle("Updated Title");
        
        // When & Then - Try to update as employer2
        mockMvc.perform(put("/api/jobs/{jobId}", job.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code", is("UNAUTHORIZED")));
    }
    
    @Test
    @WithMockUser(username = "employer1", roles = {"EMPLOYER"})
    void activateJob_ValidRequest_ReturnsActivatedJob() throws Exception {
        // Given - Create an inactive job
        Job job = createTestJob("employer1");
        job.setIsActive(false);
        job = jobRepository.save(job);
        
        // When & Then
        mockMvc.perform(put("/api/jobs/{jobId}/activate", job.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isActive", is(true)))
                .andExpect(jsonPath("$.id", is(job.getId())));
    }
    
    @Test
    @WithMockUser(username = "employer1", roles = {"EMPLOYER"})
    void deactivateJob_ValidRequest_ReturnsDeactivatedJob() throws Exception {
        // Given - Create an active job
        Job job = createTestJob("employer1");
        job.setIsActive(true);
        job = jobRepository.save(job);
        
        // When & Then
        mockMvc.perform(put("/api/jobs/{jobId}/deactivate", job.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isActive", is(false)))
                .andExpect(jsonPath("$.id", is(job.getId())));
    }
    
    @Test
    @WithMockUser(username = "employer1", roles = {"EMPLOYER"})
    void getJobsByEmployer_ValidRequest_ReturnsPagedJobs() throws Exception {
        // Given - Create multiple jobs for employer1
        Job job1 = createTestJob("employer1");
        job1.setTitle("Job 1");
        Job job2 = createTestJob("employer1");
        job2.setTitle("Job 2");
        Job job3 = createTestJob("employer2"); // Different employer
        job3.setTitle("Job 3");
        
        jobRepository.saveAll(Arrays.asList(job1, job2, job3));
        
        // When & Then
        mockMvc.perform(get("/api/jobs/employer")
                .param("page", "0")
                .param("size", "10")
                .param("sortBy", "createdAt")
                .param("sortDir", "desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2))) // Only employer1's jobs
                .andExpect(jsonPath("$.totalElements", is(2)))
                .andExpect(jsonPath("$.page", is(0)))
                .andExpect(jsonPath("$.size", is(10)));
    }
    
    @Test
    @WithMockUser(username = "employer1", roles = {"EMPLOYER"})
    void getActiveJobsByEmployer_ValidRequest_ReturnsActiveJobs() throws Exception {
        // Given - Create active and inactive jobs
        Job activeJob = createTestJob("employer1");
        activeJob.setTitle("Active Job");
        activeJob.setIsActive(true);
        
        Job inactiveJob = createTestJob("employer1");
        inactiveJob.setTitle("Inactive Job");
        inactiveJob.setIsActive(false);
        
        jobRepository.saveAll(Arrays.asList(activeJob, inactiveJob));
        
        // When & Then
        mockMvc.perform(get("/api/jobs/employer/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1))) // Only active job
                .andExpect(jsonPath("$.content[0].title", is("Active Job")))
                .andExpect(jsonPath("$.content[0].isActive", is(true)));
    }
    
    @Test
    @WithMockUser(username = "employer1", roles = {"EMPLOYER"})
    void getJobByIdForEmployer_ValidRequest_ReturnsJob() throws Exception {
        // Given
        Job job = createTestJob("employer1");
        job = jobRepository.save(job);
        
        // When & Then
        mockMvc.perform(get("/api/jobs/employer/{jobId}", job.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(job.getId())))
                .andExpect(jsonPath("$.title", is(job.getTitle())))
                .andExpect(jsonPath("$.employerId", is("employer1")));
    }
    
    @Test
    @WithMockUser(username = "employer1", roles = {"EMPLOYER"})
    void deleteJob_ValidRequest_ReturnsNoContent() throws Exception {
        // Given
        Job job = createTestJob("employer1");
        job = jobRepository.save(job);
        
        // When & Then
        mockMvc.perform(delete("/api/jobs/{jobId}", job.getId()))
                .andExpect(status().isNoContent());
        
        // Verify job is deleted
        mockMvc.perform(get("/api/jobs/employer/{jobId}", job.getId()))
                .andExpect(status().isNotFound());
    }
    
    @Test
    @WithMockUser(username = "employer1", roles = {"EMPLOYER"})
    void getJobStatistics_ValidRequest_ReturnsStatistics() throws Exception {
        // Given - Create jobs with different statuses
        Job activeJob1 = createTestJob("employer1");
        activeJob1.setIsActive(true);
        activeJob1.setApplicationCount(5);
        activeJob1.setViewCount(100);
        
        Job activeJob2 = createTestJob("employer1");
        activeJob2.setIsActive(true);
        activeJob2.setApplicationCount(3);
        activeJob2.setViewCount(75);
        
        Job inactiveJob = createTestJob("employer1");
        inactiveJob.setIsActive(false);
        inactiveJob.setApplicationCount(2);
        inactiveJob.setViewCount(50);
        
        jobRepository.saveAll(Arrays.asList(activeJob1, activeJob2, inactiveJob));
        
        // When & Then
        mockMvc.perform(get("/api/jobs/employer/statistics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalJobs", is(3)))
                .andExpect(jsonPath("$.activeJobs", is(2)))
                .andExpect(jsonPath("$.inactiveJobs", is(1)))
                .andExpect(jsonPath("$.totalApplications", is(10))) // 5 + 3 + 2
                .andExpect(jsonPath("$.totalViews", is(225))); // 100 + 75 + 50
    }
    
    /**
     * Helper method to create a test job
     */
    private Job createTestJob(String employerId) {
        Job job = new Job();
        job.setEmployerId(employerId);
        job.setTitle("Software Engineer");
        job.setDescription("We are looking for a talented software engineer to join our team and help build amazing products.");
        job.setLocation("San Francisco, CA");
        job.setJobType(JobType.FULL_TIME);
        job.setExperienceLevel(ExperienceLevel.MID_LEVEL);
        job.setRequiredSkills(Arrays.asList("Java", "Spring Boot"));
        job.setIsActive(true);
        job.setApplicationCount(0);
        job.setViewCount(0);
        
        SalaryRange salary = new SalaryRange();
        salary.setMin(80000.0);
        salary.setMax(120000.0);
        salary.setCurrency("USD");
        job.setSalary(salary);
        
        return job;
    }
}