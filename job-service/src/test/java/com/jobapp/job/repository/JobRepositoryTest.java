package com.jobapp.job.repository;

import com.jobapp.job.model.Job;
import com.jobapp.job.model.JobType;
import com.jobapp.job.model.ExperienceLevel;
import com.jobapp.job.model.SalaryRange;
import com.jobapp.job.model.SalaryPeriod;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for JobRepository
 * Requirements: 2.1, 2.2, 4.1, 4.2
 */
@DataMongoTest
@TestPropertySource(properties = {
    "spring.data.mongodb.uri=mongodb://localhost:27017/jobapp_test"
})
class JobRepositoryTest {
    
    @Autowired
    private JobRepository jobRepository;
    
    private Job testJob1;
    private Job testJob2;
    private Job testJob3;
    
    @BeforeEach
    void setUp() {
        jobRepository.deleteAll();
        
        // Create test job 1 - Software Engineer
        testJob1 = new Job("employer1", "Senior Software Engineer", 
                          "We are looking for a senior software engineer with Java and Spring Boot experience", 
                          "San Francisco, CA", JobType.FULL_TIME);
        testJob1.setSalary(new SalaryRange(120000.0, 150000.0, "USD", SalaryPeriod.YEARLY));
        testJob1.setExperienceLevel(ExperienceLevel.SENIOR);
        testJob1.setRequiredSkills(Arrays.asList("Java", "Spring Boot", "MongoDB"));
        testJob1.setPreferredSkills(Arrays.asList("React", "Docker"));
        testJob1.setEducationRequirement("Bachelor's degree in Computer Science");
        testJob1.setBenefits(Arrays.asList("Health Insurance", "401k", "Remote Work"));
        testJob1.setIsFeatured(true);
        testJob1.setApplicationCount(15);
        testJob1.setViewCount(250);
        
        // Create test job 2 - Frontend Developer
        testJob2 = new Job("employer2", "Frontend Developer", 
                          "Join our team as a frontend developer working with React and TypeScript", 
                          "New York, NY", JobType.REMOTE);
        testJob2.setSalary(new SalaryRange(80000.0, 100000.0, "USD", SalaryPeriod.YEARLY));
        testJob2.setExperienceLevel(ExperienceLevel.MID_LEVEL);
        testJob2.setRequiredSkills(Arrays.asList("React", "TypeScript", "CSS"));
        testJob2.setPreferredSkills(Arrays.asList("Next.js", "Tailwind CSS"));
        testJob2.setApplicationCount(8);
        testJob2.setViewCount(120);
        
        // Create test job 3 - Inactive job
        testJob3 = new Job("employer1", "Data Scientist", 
                          "Data scientist position for machine learning projects", 
                          "Austin, TX", JobType.CONTRACT);
        testJob3.setSalary(new SalaryRange(90000.0, 110000.0, "USD", SalaryPeriod.YEARLY));
        testJob3.setExperienceLevel(ExperienceLevel.SENIOR);
        testJob3.setRequiredSkills(Arrays.asList("Python", "Machine Learning", "SQL"));
        testJob3.setIsActive(false);
        testJob3.setApplicationCount(3);
        testJob3.setViewCount(45);
        
        jobRepository.save(testJob1);
        jobRepository.save(testJob2);
        jobRepository.save(testJob3);
    }
    
    @Test
    @DisplayName("Should find jobs by employer ID")
    void testFindByEmployerId() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        
        // When
        Page<Job> employer1Jobs = jobRepository.findByEmployerId("employer1", pageable);
        Page<Job> employer2Jobs = jobRepository.findByEmployerId("employer2", pageable);
        
        // Then
        assertThat(employer1Jobs.getContent()).hasSize(2);
        assertThat(employer1Jobs.getContent())
            .extracting(Job::getTitle)
            .containsExactlyInAnyOrder("Senior Software Engineer", "Data Scientist");
        
        assertThat(employer2Jobs.getContent()).hasSize(1);
        assertThat(employer2Jobs.getContent().get(0).getTitle()).isEqualTo("Frontend Developer");
    }
    
    @Test
    @DisplayName("Should find active jobs by employer ID")
    void testFindByEmployerIdAndIsActive() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        
        // When
        Page<Job> activeJobs = jobRepository.findByEmployerIdAndIsActive("employer1", true, pageable);
        Page<Job> inactiveJobs = jobRepository.findByEmployerIdAndIsActive("employer1", false, pageable);
        
        // Then
        assertThat(activeJobs.getContent()).hasSize(1);
        assertThat(activeJobs.getContent().get(0).getTitle()).isEqualTo("Senior Software Engineer");
        
        assertThat(inactiveJobs.getContent()).hasSize(1);
        assertThat(inactiveJobs.getContent().get(0).getTitle()).isEqualTo("Data Scientist");
    }
    
    @Test
    @DisplayName("Should find jobs by active status")
    void testFindByIsActive() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        
        // When
        Page<Job> activeJobs = jobRepository.findByIsActive(true, pageable);
        Page<Job> inactiveJobs = jobRepository.findByIsActive(false, pageable);
        
        // Then
        assertThat(activeJobs.getContent()).hasSize(2);
        assertThat(activeJobs.getContent())
            .extracting(Job::getTitle)
            .containsExactlyInAnyOrder("Senior Software Engineer", "Frontend Developer");
        
        assertThat(inactiveJobs.getContent()).hasSize(1);
        assertThat(inactiveJobs.getContent().get(0).getTitle()).isEqualTo("Data Scientist");
    }
    
    @Test
    @DisplayName("Should find jobs by location containing text")
    void testFindByLocationContainingIgnoreCase() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        
        // When
        Page<Job> californiaJobs = jobRepository.findByLocationContainingIgnoreCase("california", pageable);
        Page<Job> nyJobs = jobRepository.findByLocationContainingIgnoreCase("new york", pageable);
        Page<Job> txJobs = jobRepository.findByLocationContainingIgnoreCase("tx", pageable);
        
        // Then
        assertThat(californiaJobs.getContent()).hasSize(1);
        assertThat(californiaJobs.getContent().get(0).getTitle()).isEqualTo("Senior Software Engineer");
        
        assertThat(nyJobs.getContent()).hasSize(1);
        assertThat(nyJobs.getContent().get(0).getTitle()).isEqualTo("Frontend Developer");
        
        assertThat(txJobs.getContent()).hasSize(1);
        assertThat(txJobs.getContent().get(0).getTitle()).isEqualTo("Data Scientist");
    }
    
    @Test
    @DisplayName("Should find jobs by job type")
    void testFindByJobType() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        
        // When
        Page<Job> fullTimeJobs = jobRepository.findByJobType(JobType.FULL_TIME, pageable);
        Page<Job> remoteJobs = jobRepository.findByJobType(JobType.REMOTE, pageable);
        Page<Job> contractJobs = jobRepository.findByJobType(JobType.CONTRACT, pageable);
        
        // Then
        assertThat(fullTimeJobs.getContent()).hasSize(1);
        assertThat(fullTimeJobs.getContent().get(0).getTitle()).isEqualTo("Senior Software Engineer");
        
        assertThat(remoteJobs.getContent()).hasSize(1);
        assertThat(remoteJobs.getContent().get(0).getTitle()).isEqualTo("Frontend Developer");
        
        assertThat(contractJobs.getContent()).hasSize(1);
        assertThat(contractJobs.getContent().get(0).getTitle()).isEqualTo("Data Scientist");
    }
    
    @Test
    @DisplayName("Should find jobs by experience level")
    void testFindByExperienceLevel() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        
        // When
        Page<Job> seniorJobs = jobRepository.findByExperienceLevel(ExperienceLevel.SENIOR, pageable);
        Page<Job> midLevelJobs = jobRepository.findByExperienceLevel(ExperienceLevel.MID_LEVEL, pageable);
        
        // Then
        assertThat(seniorJobs.getContent()).hasSize(2);
        assertThat(seniorJobs.getContent())
            .extracting(Job::getTitle)
            .containsExactlyInAnyOrder("Senior Software Engineer", "Data Scientist");
        
        assertThat(midLevelJobs.getContent()).hasSize(1);
        assertThat(midLevelJobs.getContent().get(0).getTitle()).isEqualTo("Frontend Developer");
    }
    
    @Test
    @DisplayName("Should find jobs by salary range")
    void testFindBySalaryRange() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        
        // When
        Page<Job> highSalaryJobs = jobRepository.findBySalaryRange(100000.0, 200000.0, pageable);
        Page<Job> midSalaryJobs = jobRepository.findBySalaryRange(70000.0, 120000.0, pageable);
        
        // Then
        assertThat(highSalaryJobs.getContent()).hasSize(2);
        assertThat(highSalaryJobs.getContent())
            .extracting(Job::getTitle)
            .containsExactlyInAnyOrder("Senior Software Engineer", "Frontend Developer");
        
        assertThat(midSalaryJobs.getContent()).hasSize(1);
        assertThat(midSalaryJobs.getContent().get(0).getTitle()).isEqualTo("Frontend Developer");
    }
    
    @Test
    @DisplayName("Should find jobs by required skills")
    void testFindByRequiredSkillsIn() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        List<String> javaSkills = Arrays.asList("Java", "Spring Boot");
        List<String> frontendSkills = Arrays.asList("React", "TypeScript");
        List<String> dataSkills = Arrays.asList("Python", "Machine Learning");
        
        // When
        Page<Job> javaJobs = jobRepository.findByRequiredSkillsIn(javaSkills, pageable);
        Page<Job> frontendJobs = jobRepository.findByRequiredSkillsIn(frontendSkills, pageable);
        Page<Job> dataJobs = jobRepository.findByRequiredSkillsIn(dataSkills, pageable);
        
        // Then
        assertThat(javaJobs.getContent()).hasSize(1);
        assertThat(javaJobs.getContent().get(0).getTitle()).isEqualTo("Senior Software Engineer");
        
        assertThat(frontendJobs.getContent()).hasSize(1);
        assertThat(frontendJobs.getContent().get(0).getTitle()).isEqualTo("Frontend Developer");
        
        assertThat(dataJobs.getContent()).hasSize(0); // Data Scientist job is inactive
    }
    
    @Test
    @DisplayName("Should find jobs accepting applications")
    void testFindJobsAcceptingApplications() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        Pageable pageable = PageRequest.of(0, 10);
        
        // When
        Page<Job> acceptingJobs = jobRepository.findJobsAcceptingApplications(now, pageable);
        
        // Then
        assertThat(acceptingJobs.getContent()).hasSize(2); // Only active jobs
        assertThat(acceptingJobs.getContent())
            .extracting(Job::getTitle)
            .containsExactlyInAnyOrder("Senior Software Engineer", "Frontend Developer");
    }
    
    @Test
    @DisplayName("Should find featured jobs")
    void testFindByIsFeaturedAndIsActive() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        
        // When
        Page<Job> featuredJobs = jobRepository.findByIsFeaturedAndIsActive(true, true, pageable);
        
        // Then
        assertThat(featuredJobs.getContent()).hasSize(1);
        assertThat(featuredJobs.getContent().get(0).getTitle()).isEqualTo("Senior Software Engineer");
        assertThat(featuredJobs.getContent().get(0).getIsFeatured()).isTrue();
    }
    
    @Test
    @DisplayName("Should find jobs by title containing text")
    void testFindByTitleContainingIgnoreCase() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        
        // When
        Page<Job> engineerJobs = jobRepository.findByTitleContainingIgnoreCase("engineer", pageable);
        Page<Job> developerJobs = jobRepository.findByTitleContainingIgnoreCase("developer", pageable);
        Page<Job> scientistJobs = jobRepository.findByTitleContainingIgnoreCase("scientist", pageable);
        
        // Then
        assertThat(engineerJobs.getContent()).hasSize(1);
        assertThat(engineerJobs.getContent().get(0).getTitle()).isEqualTo("Senior Software Engineer");
        
        assertThat(developerJobs.getContent()).hasSize(1);
        assertThat(developerJobs.getContent().get(0).getTitle()).isEqualTo("Frontend Developer");
        
        assertThat(scientistJobs.getContent()).hasSize(1);
        assertThat(scientistJobs.getContent().get(0).getTitle()).isEqualTo("Data Scientist");
    }
    
    @Test
    @DisplayName("Should count jobs by various criteria")
    void testCountMethods() {
        // When & Then
        assertThat(jobRepository.countByEmployerId("employer1")).isEqualTo(2);
        assertThat(jobRepository.countByEmployerId("employer2")).isEqualTo(1);
        
        assertThat(jobRepository.countByEmployerIdAndIsActive("employer1", true)).isEqualTo(1);
        assertThat(jobRepository.countByEmployerIdAndIsActive("employer1", false)).isEqualTo(1);
        
        assertThat(jobRepository.countByIsActive(true)).isEqualTo(2);
        assertThat(jobRepository.countByIsActive(false)).isEqualTo(1);
        
        assertThat(jobRepository.countByJobType(JobType.FULL_TIME)).isEqualTo(1);
        assertThat(jobRepository.countByJobType(JobType.REMOTE)).isEqualTo(1);
        assertThat(jobRepository.countByJobType(JobType.CONTRACT)).isEqualTo(1);
        
        assertThat(jobRepository.countByLocationContainingIgnoreCase("CA")).isEqualTo(1);
        assertThat(jobRepository.countByLocationContainingIgnoreCase("NY")).isEqualTo(1);
    }
    
    @Test
    @DisplayName("Should find recently posted jobs")
    void testFindRecentlyPosted() {
        // Given
        LocalDateTime twoDaysAgo = LocalDateTime.now().minusDays(2);
        Pageable pageable = PageRequest.of(0, 10);
        
        // When
        Page<Job> recentJobs = jobRepository.findRecentlyPosted(twoDaysAgo, true, pageable);
        
        // Then
        assertThat(recentJobs.getContent()).hasSize(2); // All active jobs are recent in test
    }
    
    @Test
    @DisplayName("Should find jobs created within date range")
    void testFindByCreatedAtBetween() {
        // Given
        LocalDateTime startDate = LocalDateTime.now().minusDays(1);
        LocalDateTime endDate = LocalDateTime.now().plusDays(1);
        Pageable pageable = PageRequest.of(0, 10);
        
        // When
        Page<Job> jobsInRange = jobRepository.findByCreatedAtBetween(startDate, endDate, pageable);
        
        // Then
        assertThat(jobsInRange.getContent()).hasSize(3); // All jobs created today
    }
    
    @Test
    @DisplayName("Should test job business methods")
    void testJobBusinessMethods() {
        // Test isAcceptingApplications
        assertThat(testJob1.isAcceptingApplications()).isTrue(); // Active job
        assertThat(testJob2.isAcceptingApplications()).isTrue(); // Active job
        assertThat(testJob3.isAcceptingApplications()).isFalse(); // Inactive job
        
        // Test incrementApplicationCount
        int initialCount = testJob1.getApplicationCount();
        testJob1.incrementApplicationCount();
        assertThat(testJob1.getApplicationCount()).isEqualTo(initialCount + 1);
        
        // Test incrementViewCount
        int initialViewCount = testJob1.getViewCount();
        testJob1.incrementViewCount();
        assertThat(testJob1.getViewCount()).isEqualTo(initialViewCount + 1);
        
        // Test isExpired (should be false for new jobs)
        assertThat(testJob1.isExpired()).isFalse();
        assertThat(testJob2.isExpired()).isFalse();
        assertThat(testJob3.isExpired()).isFalse();
    }
    
    @Test
    @DisplayName("Should test salary range methods")
    void testSalaryRangeMethods() {
        // Test salary range validation
        SalaryRange validRange = new SalaryRange(50000.0, 70000.0, "USD", SalaryPeriod.YEARLY);
        assertThat(validRange.isValid()).isTrue();
        
        SalaryRange invalidRange = new SalaryRange(70000.0, 50000.0, "USD", SalaryPeriod.YEARLY);
        assertThat(invalidRange.isValid()).isFalse();
        
        // Test formatted range
        SalaryRange range = new SalaryRange(80000.0, 100000.0, "USD", SalaryPeriod.YEARLY);
        range.setNegotiable(true);
        String formatted = range.getFormattedRange();
        assertThat(formatted).contains("80000 - 100000");
        assertThat(formatted).contains("USD");
        assertThat(formatted).contains("per year");
        assertThat(formatted).contains("Negotiable");
    }
    
    @Test
    @DisplayName("Should handle pagination correctly")
    void testPagination() {
        // Given
        Pageable firstPage = PageRequest.of(0, 2);
        Pageable secondPage = PageRequest.of(1, 2);
        
        // When
        Page<Job> page1 = jobRepository.findByIsActive(true, firstPage);
        Page<Job> page2 = jobRepository.findByIsActive(true, secondPage);
        
        // Then
        assertThat(page1.getContent()).hasSize(2);
        assertThat(page1.getTotalElements()).isEqualTo(2);
        assertThat(page1.getTotalPages()).isEqualTo(1);
        assertThat(page1.hasNext()).isFalse();
        
        assertThat(page2.getContent()).isEmpty();
    }
}