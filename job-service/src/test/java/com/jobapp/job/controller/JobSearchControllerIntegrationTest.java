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
 * Integration tests for Job Search functionality
 * Requirements: 2.1, 2.2, 8.2
 */
@SpringBootTest
@AutoConfigureWebMvc
@TestPropertySource(locations = "classpath:application-test.yml")
class JobSearchControllerIntegrationTest {
    
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
    void searchJobs_TextSearch_ReturnsMatchingJobs() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/jobs/search")
                .param("search", "Java developer"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(greaterThan(0))))
                .andExpect(jsonPath("$.content[0].title", containsStringIgnoringCase("Java")))
                .andExpect(jsonPath("$.totalElements", greaterThan(0)));
    }
    
    @Test
    void searchJobs_LocationFilter_ReturnsJobsInLocation() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/jobs/search")
                .param("location", "San Francisco"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(greaterThan(0))))
                .andExpect(jsonPath("$.content[0].location", containsStringIgnoringCase("San Francisco")));
    }
    
    @Test
    void searchJobs_JobTypeFilter_ReturnsJobsOfType() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/jobs/search")
                .param("jobType", "FULL_TIME"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(greaterThan(0))))
                .andExpect(jsonPath("$.content[0].jobType", is("FULL_TIME")));
    }
    
    @Test
    void searchJobs_ExperienceLevelFilter_ReturnsJobsForLevel() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/jobs/search")
                .param("experienceLevel", "SENIOR"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(greaterThan(0))))
                .andExpect(jsonPath("$.content[0].experienceLevel", is("SENIOR")));
    }
    
    @Test
    void searchJobs_SalaryRangeFilter_ReturnsJobsInRange() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/jobs/search")
                .param("minSalary", "100000")
                .param("maxSalary", "200000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(greaterThan(0))));
    }
    
    @Test
    void searchJobs_SkillsFilter_ReturnsJobsWithSkills() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/jobs/search")
                .param("skills", "Java,Spring Boot"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(greaterThan(0))));
    }
    
    @Test
    void searchJobs_MultipleFilters_ReturnsFilteredJobs() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/jobs/search")
                .param("search", "developer")
                .param("location", "San Francisco")
                .param("jobType", "FULL_TIME")
                .param("experienceLevel", "SENIOR"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", notNullValue()));
    }
    
    @Test
    void searchJobs_WithPagination_ReturnsPaginatedResults() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/jobs/search")
                .param("page", "0")
                .param("size", "2")
                .param("sortBy", "createdAt")
                .param("sortDir", "desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page", is(0)))
                .andExpect(jsonPath("$.size", is(2)))
                .andExpect(jsonPath("$.content", hasSize(lessThanOrEqualTo(2))));
    }
    
    @Test
    void searchJobs_WithSorting_ReturnsSortedResults() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/jobs/search")
                .param("sortBy", "title")
                .param("sortDir", "asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(greaterThan(0))));
    }
    
    @Test
    void searchJobs_NoFilters_ReturnsAllActiveJobs() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/jobs/search"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(greaterThan(0))))
                .andExpect(jsonPath("$.totalElements", greaterThan(0)));
    }
    
    @Test
    void getFeaturedJobs_ReturnsOnlyFeaturedJobs() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/jobs/featured"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(greaterThan(0))))
                .andExpect(jsonPath("$.content[0].isFeatured", is(true)));
    }
    
    @Test
    void getFeaturedJobs_WithPagination_ReturnsPaginatedFeaturedJobs() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/jobs/featured")
                .param("page", "0")
                .param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page", is(0)))
                .andExpect(jsonPath("$.size", is(1)))
                .andExpect(jsonPath("$.content", hasSize(lessThanOrEqualTo(1))));
    }
    
    @Test
    void getRecentJobs_ReturnsRecentJobs() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/jobs/recent")
                .param("days", "7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(greaterThan(0))));
    }
    
    @Test
    void getRecentJobs_WithCustomDays_ReturnsJobsFromPeriod() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/jobs/recent")
                .param("days", "30")
                .param("page", "0")
                .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page", is(0)))
                .andExpect(jsonPath("$.size", is(5)));
    }
    
    @Test
    void getPopularJobs_ReturnsJobsByApplicationCount() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/jobs/popular"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(greaterThan(0))));
    }
    
    @Test
    void getPopularJobs_WithPagination_ReturnsPaginatedPopularJobs() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/jobs/popular")
                .param("page", "0")
                .param("size", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page", is(0)))
                .andExpect(jsonPath("$.size", is(3)))
                .andExpect(jsonPath("$.content", hasSize(lessThanOrEqualTo(3))));
    }
    
    @Test
    void searchJobs_InvalidParameters_ReturnsBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/jobs/search")
                .param("page", "-1")) // Invalid page number
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void searchJobs_EmptySearchText_ReturnsAllActiveJobs() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/jobs/search")
                .param("search", ""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(greaterThan(0))));
    }
    
    @Test
    void searchJobs_NonExistentLocation_ReturnsEmptyResults() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/jobs/search")
                .param("location", "NonExistentCity"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)))
                .andExpect(jsonPath("$.totalElements", is(0)));
    }
    
    /**
     * Helper method to create test jobs
     */
    private void createTestJobs() {
        // Job 1: Senior Java Developer in San Francisco
        Job job1 = new Job();
        job1.setEmployerId("employer1");
        job1.setTitle("Senior Java Developer");
        job1.setDescription("We are looking for an experienced Java developer to join our team. Must have expertise in Spring Boot, microservices, and MongoDB.");
        job1.setLocation("San Francisco, CA");
        job1.setJobType(JobType.FULL_TIME);
        job1.setExperienceLevel(ExperienceLevel.SENIOR);
        job1.setRequiredSkills(Arrays.asList("Java", "Spring Boot", "MongoDB", "Microservices"));
        job1.setPreferredSkills(Arrays.asList("Docker", "Kubernetes"));
        job1.setIsActive(true);
        job1.setIsFeatured(true);
        job1.setApplicationCount(15);
        job1.setViewCount(250);
        
        SalaryRange salary1 = new SalaryRange();
        salary1.setMin(120000.0);
        salary1.setMax(180000.0);
        salary1.setCurrency("USD");
        job1.setSalary(salary1);
        
        // Job 2: Frontend Developer in New York
        Job job2 = new Job();
        job2.setEmployerId("employer2");
        job2.setTitle("Frontend Developer");
        job2.setDescription("Looking for a skilled frontend developer with React and TypeScript experience. Join our innovative team building cutting-edge web applications.");
        job2.setLocation("New York, NY");
        job2.setJobType(JobType.FULL_TIME);
        job2.setExperienceLevel(ExperienceLevel.MID_LEVEL);
        job2.setRequiredSkills(Arrays.asList("React", "TypeScript", "CSS", "HTML"));
        job2.setPreferredSkills(Arrays.asList("Next.js", "Tailwind CSS"));
        job2.setIsActive(true);
        job2.setIsFeatured(false);
        job2.setApplicationCount(8);
        job2.setViewCount(120);
        
        SalaryRange salary2 = new SalaryRange();
        salary2.setMin(80000.0);
        salary2.setMax(120000.0);
        salary2.setCurrency("USD");
        job2.setSalary(salary2);
        
        // Job 3: Data Scientist in Seattle
        Job job3 = new Job();
        job3.setEmployerId("employer3");
        job3.setTitle("Data Scientist");
        job3.setDescription("Seeking a data scientist with machine learning expertise. Work with large datasets and build predictive models.");
        job3.setLocation("Seattle, WA");
        job3.setJobType(JobType.FULL_TIME);
        job3.setExperienceLevel(ExperienceLevel.SENIOR);
        job3.setRequiredSkills(Arrays.asList("Python", "Machine Learning", "SQL", "Statistics"));
        job3.setPreferredSkills(Arrays.asList("TensorFlow", "PyTorch", "AWS"));
        job3.setIsActive(true);
        job3.setIsFeatured(true);
        job3.setApplicationCount(12);
        job3.setViewCount(180);
        
        SalaryRange salary3 = new SalaryRange();
        salary3.setMin(130000.0);
        salary3.setMax(200000.0);
        salary3.setCurrency("USD");
        job3.setSalary(salary3);
        
        // Job 4: Junior Developer (Remote)
        Job job4 = new Job();
        job4.setEmployerId("employer1");
        job4.setTitle("Junior Software Developer");
        job4.setDescription("Entry-level position for a junior developer. Great opportunity to learn and grow with our team.");
        job4.setLocation("Remote");
        job4.setJobType(JobType.REMOTE);
        job4.setExperienceLevel(ExperienceLevel.ENTRY_LEVEL);
        job4.setRequiredSkills(Arrays.asList("JavaScript", "HTML", "CSS"));
        job4.setPreferredSkills(Arrays.asList("React", "Node.js"));
        job4.setIsActive(true);
        job4.setIsFeatured(false);
        job4.setApplicationCount(25);
        job4.setViewCount(300);
        
        SalaryRange salary4 = new SalaryRange();
        salary4.setMin(60000.0);
        salary4.setMax(80000.0);
        salary4.setCurrency("USD");
        job4.setSalary(salary4);
        
        // Job 5: Inactive job (should not appear in search results)
        Job job5 = new Job();
        job5.setEmployerId("employer2");
        job5.setTitle("DevOps Engineer");
        job5.setDescription("DevOps engineer position with cloud infrastructure experience.");
        job5.setLocation("Austin, TX");
        job5.setJobType(JobType.FULL_TIME);
        job5.setExperienceLevel(ExperienceLevel.MID_LEVEL);
        job5.setRequiredSkills(Arrays.asList("AWS", "Docker", "Kubernetes"));
        job5.setIsActive(false); // Inactive job
        job5.setIsFeatured(false);
        job5.setApplicationCount(5);
        job5.setViewCount(75);
        
        // Save all test jobs
        jobRepository.saveAll(Arrays.asList(job1, job2, job3, job4, job5));
    }
}