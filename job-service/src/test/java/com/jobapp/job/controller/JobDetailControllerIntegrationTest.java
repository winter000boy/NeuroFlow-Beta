package com.jobapp.job.controller;

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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

/**
 * Integration tests for Job Detail and Company endpoints
 * Requirements: 2.2, 7.4
 */
@SpringBootTest
@AutoConfigureWebMvc
@TestPropertySource(locations = "classpath:application-test.yml")
class JobDetailControllerIntegrationTest {
    
    @Autowired
    private WebApplicationContext context;
    
    @Autowired
    private JobRepository jobRepository;
    
    private MockMvc mockMvc;
    
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .build();
        
        // Clean up database before each test
        jobRepository.deleteAll();
        
        // Create test data
        createTestJobs();
    }
    
    @Test
    void getJobDetails_ValidJobId_ReturnsJobDetails() throws Exception {
        // Given - Get a job ID from test data
        Job job = jobRepository.findAll().get(0);
        
        // When & Then
        mockMvc.perform(get("/api/jobs/{jobId}", job.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(job.getId())))
                .andExpect(jsonPath("$.title", is(job.getTitle())))
                .andExpect(jsonPath("$.description", is(job.getDescription())))
                .andExpect(jsonPath("$.location", is(job.getLocation())))
                .andExpect(jsonPath("$.jobType", is(job.getJobType().toString())))
                .andExpect(jsonPath("$.isActive", is(true)))
                .andExpect(jsonPath("$.viewCount", greaterThan(0))); // Should increment view count
    }
    
    @Test
    void getJobDetails_InactiveJob_ReturnsNotFound() throws Exception {
        // Given - Create an inactive job
        Job inactiveJob = createTestJob("employer1");
        inactiveJob.setIsActive(false);
        inactiveJob = jobRepository.save(inactiveJob);
        
        // When & Then
        mockMvc.perform(get("/api/jobs/{jobId}", inactiveJob.getId()))
                .andExpect(status().isNotFound());
    }
    
    @Test
    void getJobDetails_NonExistentJob_ReturnsNotFound() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/jobs/{jobId}", "nonexistent-id"))
                .andExpect(status().isNotFound());
    }
    
    @Test
    void getJobDetailsWithCompany_ValidJobId_ReturnsJobWithCompanyInfo() throws Exception {
        // Given - Get a job ID from test data
        Job job = jobRepository.findAll().get(0);
        
        // When & Then
        mockMvc.perform(get("/api/jobs/{jobId}/details", job.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(job.getId())))
                .andExpect(jsonPath("$.title", is(job.getTitle())))
                .andExpect(jsonPath("$.company", notNullValue()))
                .andExpect(jsonPath("$.company.id", is(job.getEmployerId())))
                .andExpect(jsonPath("$.company.name", notNullValue()))
                .andExpect(jsonPath("$.relatedJobs", notNullValue()))
                .andExpect(jsonPath("$.isAcceptingApplications", notNullValue()));
    }
    
    @Test
    void getJobsByCompany_ValidEmployerId_ReturnsCompanyJobs() throws Exception {
        // Given - Use employer1 who has multiple jobs
        String employerId = "employer1";
        
        // When & Then
        mockMvc.perform(get("/api/jobs/company/{employerId}", employerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(greaterThan(0))))
                .andExpect(jsonPath("$.content[0].employerId", is(employerId)))
                .andExpect(jsonPath("$.content[0].isActive", is(true)))
                .andExpect(jsonPath("$.totalElements", greaterThan(0)));
    }
    
    @Test
    void getJobsByCompany_WithPagination_ReturnsPaginatedResults() throws Exception {
        // Given - Use employer1 who has multiple jobs
        String employerId = "employer1";
        
        // When & Then
        mockMvc.perform(get("/api/jobs/company/{employerId}", employerId)
                .param("page", "0")
                .param("size", "1")
                .param("sortBy", "createdAt")
                .param("sortDir", "desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page", is(0)))
                .andExpect(jsonPath("$.size", is(1)))
                .andExpect(jsonPath("$.content", hasSize(lessThanOrEqualTo(1))));
    }
    
    @Test
    void getJobsByCompany_NonExistentCompany_ReturnsEmptyResults() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/jobs/company/{employerId}", "nonexistent-employer"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)))
                .andExpect(jsonPath("$.totalElements", is(0)));
    }
    
    @Test
    void getCompanyProfile_ValidEmployerId_ReturnsCompanyProfile() throws Exception {
        // Given - Use employer1 who has jobs
        String employerId = "employer1";
        
        // When & Then
        mockMvc.perform(get("/api/jobs/company/{employerId}/profile", employerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(employerId)))
                .andExpect(jsonPath("$.companyName", notNullValue()))
                .andExpect(jsonPath("$.totalJobs", greaterThan(0)))
                .andExpect(jsonPath("$.activeJobs", greaterThan(0)))
                .andExpect(jsonPath("$.recentJobs", notNullValue()))
                .andExpect(jsonPath("$.recentJobs.content", notNullValue()));
    }
    
    @Test
    void getCompanyProfile_NonExistentCompany_ReturnsNotFound() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/jobs/company/{employerId}/profile", "nonexistent-employer"))
                .andExpect(status().isNotFound());
    }
    
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void cleanupExpiredJobs_AsAdmin_ReturnsCleanupResults() throws Exception {
        // Given - Create an expired job
        Job expiredJob = createTestJob("employer1");
        expiredJob.setExpiresAt(LocalDateTime.now().minusDays(1)); // Expired yesterday
        expiredJob.setApplicationCount(0); // No applications, should be deleted
        jobRepository.save(expiredJob);
        
        // When & Then
        mockMvc.perform(delete("/api/jobs/expired"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalExpiredJobs", greaterThanOrEqualTo(0)))
                .andExpect(jsonPath("$.deletedJobs", greaterThanOrEqualTo(0)))
                .andExpect(jsonPath("$.deactivatedJobs", greaterThanOrEqualTo(0)))
                .andExpect(jsonPath("$.cleanupTimestamp", notNullValue()));
    }
    
    @Test
    void cleanupExpiredJobs_AsNonAdmin_ReturnsForbidden() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/jobs/expired"))
                .andExpect(status().isUnauthorized());
    }
    
    @Test
    @WithMockUser(username = "employer1", roles = {"EMPLOYER"})
    void cleanupExpiredJobs_AsEmployer_ReturnsForbidden() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/jobs/expired"))
                .andExpect(status().isForbidden());
    }
    
    @Test
    void getJobDetails_IncrementsViewCount() throws Exception {
        // Given - Get a job and check initial view count
        Job job = jobRepository.findAll().get(0);
        Integer initialViewCount = job.getViewCount();
        
        // When - Access job details
        mockMvc.perform(get("/api/jobs/{jobId}", job.getId()))
                .andExpect(status().isOk());
        
        // Then - View count should be incremented
        Job updatedJob = jobRepository.findById(job.getId()).orElseThrow();
        assert updatedJob.getViewCount() > initialViewCount;
    }
    
    @Test
    void getJobDetailsWithCompany_IncrementsViewCount() throws Exception {
        // Given - Get a job and check initial view count
        Job job = jobRepository.findAll().get(0);
        Integer initialViewCount = job.getViewCount();
        
        // When - Access job details with company info
        mockMvc.perform(get("/api/jobs/{jobId}/details", job.getId()))
                .andExpect(status().isOk());
        
        // Then - View count should be incremented
        Job updatedJob = jobRepository.findById(job.getId()).orElseThrow();
        assert updatedJob.getViewCount() > initialViewCount;
    }
    
    /**
     * Helper method to create test jobs
     */
    private void createTestJobs() {
        // Job 1: Senior Java Developer for employer1
        Job job1 = createTestJob("employer1");
        job1.setTitle("Senior Java Developer");
        job1.setDescription("We are looking for an experienced Java developer to join our team. Must have expertise in Spring Boot, microservices, and MongoDB.");
        job1.setRequiredSkills(Arrays.asList("Java", "Spring Boot", "MongoDB"));
        job1.setApplicationCount(5);
        job1.setViewCount(100);
        
        // Job 2: Frontend Developer for employer1
        Job job2 = createTestJob("employer1");
        job2.setTitle("Frontend Developer");
        job2.setDescription("Looking for a skilled frontend developer with React experience.");
        job2.setRequiredSkills(Arrays.asList("React", "JavaScript", "CSS"));
        job2.setApplicationCount(3);
        job2.setViewCount(75);
        
        // Job 3: Data Scientist for employer2
        Job job3 = createTestJob("employer2");
        job3.setTitle("Data Scientist");
        job3.setDescription("Seeking a data scientist with machine learning expertise.");
        job3.setRequiredSkills(Arrays.asList("Python", "Machine Learning", "SQL"));
        job3.setApplicationCount(8);
        job3.setViewCount(150);
        
        // Save all test jobs
        jobRepository.saveAll(Arrays.asList(job1, job2, job3));
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
</content>