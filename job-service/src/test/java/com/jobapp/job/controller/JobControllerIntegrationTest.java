package com.jobapp.job.controller;

import com.jobapp.job.dto.CreateJobRequest;
import com.jobapp.job.dto.UpdateJobRequest;
import com.jobapp.job.model.Job;
import com.jobapp.job.model.JobType;
import com.jobapp.job.model.ExperienceLevel;
import com.jobapp.job.repository.JobRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
class JobControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private CreateJobRequest createJobRequest;
    private UpdateJobRequest updateJobRequest;
    private Job testJob;
    private String employerId = "employer123";

    @BeforeEach
    void setUp() {
        createJobRequest = new CreateJobRequest();
        createJobRequest.setTitle("Software Engineer");
        createJobRequest.setDescription("Java developer position with Spring Boot experience");
        createJobRequest.setLocation("San Francisco, CA");
        createJobRequest.setJobType(JobType.FULL_TIME);
        createJobRequest.setExperienceLevel(ExperienceLevel.MID_LEVEL);
        createJobRequest.setRequiredSkills(Arrays.asList("Java", "Spring Boot", "MongoDB"));
        createJobRequest.setPreferredSkills(Arrays.asList("React", "Docker", "AWS"));

        updateJobRequest = new UpdateJobRequest();
        updateJobRequest.setTitle("Senior Software Engineer");
        updateJobRequest.setDescription("Updated job description");

        testJob = new Job();
        testJob.setEmployerId(employerId);
        testJob.setTitle("Test Job");
        testJob.setDescription("Test job description");
        testJob.setLocation("Test Location");
        testJob.setJobType(JobType.FULL_TIME);
        testJob.setExperienceLevel(ExperienceLevel.ENTRY_LEVEL);
        testJob.setRequiredSkills(Arrays.asList("Java", "Spring"));
        testJob.setIsActive(true);
        testJob.setCreatedAt(LocalDateTime.now());
        testJob.setUpdatedAt(LocalDateTime.now());
    }

    @AfterEach
    void tearDown() {
        jobRepository.deleteAll();
    }

    @Test
    @WithMockUser(roles = "EMPLOYER")
    void createJob_ValidRequest_ReturnsCreated() throws Exception {
        mockMvc.perform(post("/api/jobs")
                .with(csrf())
                .header("X-User-Id", employerId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createJobRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value(createJobRequest.getTitle()))
                .andExpect(jsonPath("$.description").value(createJobRequest.getDescription()))
                .andExpect(jsonPath("$.location").value(createJobRequest.getLocation()))
                .andExpect(jsonPath("$.jobType").value(createJobRequest.getJobType().toString()))
                .andExpect(jsonPath("$.employerId").value(employerId));
    }

    @Test
    @WithMockUser(roles = "EMPLOYER")
    void createJob_InvalidRequest_ReturnsBadRequest() throws Exception {
        CreateJobRequest invalidRequest = new CreateJobRequest();
        // Missing required fields

        mockMvc.perform(post("/api/jobs")
                .with(csrf())
                .header("X-User-Id", employerId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "EMPLOYER")
    void updateJob_ValidRequest_ReturnsUpdatedJob() throws Exception {
        // Create test job first
        Job savedJob = jobRepository.save(testJob);

        mockMvc.perform(put("/api/jobs/{jobId}", savedJob.getId())
                .with(csrf())
                .header("X-User-Id", employerId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateJobRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedJob.getId()))
                .andExpect(jsonPath("$.title").value(updateJobRequest.getTitle()))
                .andExpect(jsonPath("$.description").value(updateJobRequest.getDescription()));
    }

    @Test
    @WithMockUser(roles = "EMPLOYER")
    void updateJob_NonExistentJob_ReturnsNotFound() throws Exception {
        mockMvc.perform(put("/api/jobs/{jobId}", "nonexistent-id")
                .with(csrf())
                .header("X-User-Id", employerId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateJobRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "EMPLOYER")
    void updateJob_UnauthorizedEmployer_ReturnsForbidden() throws Exception {
        Job savedJob = jobRepository.save(testJob);
        String wrongEmployerId = "wrong-employer";

        mockMvc.perform(put("/api/jobs/{jobId}", savedJob.getId())
                .with(csrf())
                .header("X-User-Id", wrongEmployerId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateJobRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "EMPLOYER")
    void getJobsByEmployer_ValidRequest_ReturnsPagedJobs() throws Exception {
        // Create multiple test jobs
        Job job1 = new Job(testJob);
        job1.setTitle("Job 1");
        Job job2 = new Job(testJob);
        job2.setTitle("Job 2");
        
        jobRepository.saveAll(Arrays.asList(job1, job2));

        mockMvc.perform(get("/api/jobs/employer")
                .with(csrf())
                .header("X-User-Id", employerId)
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(10));
    }

    @Test
    void getJobById_ExistingJob_ReturnsJob() throws Exception {
        Job savedJob = jobRepository.save(testJob);

        mockMvc.perform(get("/api/jobs/{jobId}", savedJob.getId())
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedJob.getId()))
                .andExpect(jsonPath("$.title").value(savedJob.getTitle()))
                .andExpect(jsonPath("$.description").value(savedJob.getDescription()));
    }

    @Test
    void getJobById_NonExistentJob_ReturnsNotFound() throws Exception {
        mockMvc.perform(get("/api/jobs/{jobId}", "nonexistent-id")
                .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "EMPLOYER")
    void toggleJobStatus_ValidRequest_ReturnsUpdatedJob() throws Exception {
        Job savedJob = jobRepository.save(testJob);

        mockMvc.perform(patch("/api/jobs/{jobId}/status", savedJob.getId())
                .with(csrf())
                .header("X-User-Id", employerId)
                .param("isActive", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedJob.getId()))
                .andExpect(jsonPath("$.isActive").value(false));
    }

    @Test
    @WithMockUser(roles = "EMPLOYER")
    void deleteJob_ValidRequest_ReturnsNoContent() throws Exception {
        Job savedJob = jobRepository.save(testJob);

        mockMvc.perform(delete("/api/jobs/{jobId}", savedJob.getId())
                .with(csrf())
                .header("X-User-Id", employerId))
                .andExpect(status().isNoContent());
    }

    @Test
    void searchJobs_WithTextSearch_ReturnsMatchingJobs() throws Exception {
        // Create jobs with different titles
        Job javaJob = new Job(testJob);
        javaJob.setTitle("Java Developer");
        javaJob.setDescription("Java development position");
        
        Job pythonJob = new Job(testJob);
        pythonJob.setTitle("Python Developer");
        pythonJob.setDescription("Python development position");
        
        jobRepository.saveAll(Arrays.asList(javaJob, pythonJob));

        mockMvc.perform(get("/api/jobs/search")
                .with(csrf())
                .param("search", "Java")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].title").value("Java Developer"));
    }

    @Test
    void searchJobs_WithLocationFilter_ReturnsMatchingJobs() throws Exception {
        Job sfJob = new Job(testJob);
        sfJob.setLocation("San Francisco, CA");
        
        Job nyJob = new Job(testJob);
        nyJob.setLocation("New York, NY");
        
        jobRepository.saveAll(Arrays.asList(sfJob, nyJob));

        mockMvc.perform(get("/api/jobs/search")
                .with(csrf())
                .param("location", "San Francisco")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].location").value("San Francisco, CA"));
    }

    @Test
    void searchJobs_WithJobTypeFilter_ReturnsMatchingJobs() throws Exception {
        Job fullTimeJob = new Job(testJob);
        fullTimeJob.setJobType(JobType.FULL_TIME);
        
        Job partTimeJob = new Job(testJob);
        partTimeJob.setJobType(JobType.PART_TIME);
        
        jobRepository.saveAll(Arrays.asList(fullTimeJob, partTimeJob));

        mockMvc.perform(get("/api/jobs/search")
                .with(csrf())
                .param("jobType", "FULL_TIME")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].jobType").value("FULL_TIME"));
    }

    @Test
    @WithMockUser(roles = "EMPLOYER")
    void getJobStatistics_ValidEmployer_ReturnsStatistics() throws Exception {
        // Create multiple jobs for the employer
        Job activeJob1 = new Job(testJob);
        activeJob1.setIsActive(true);
        
        Job activeJob2 = new Job(testJob);
        activeJob2.setIsActive(true);
        
        Job inactiveJob = new Job(testJob);
        inactiveJob.setIsActive(false);
        
        jobRepository.saveAll(Arrays.asList(activeJob1, activeJob2, inactiveJob));

        mockMvc.perform(get("/api/jobs/employer/statistics")
                .with(csrf())
                .header("X-User-Id", employerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalJobs").value(3))
                .andExpect(jsonPath("$.activeJobs").value(2))
                .andExpect(jsonPath("$.inactiveJobs").value(1));
    }
}