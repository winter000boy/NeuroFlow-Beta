package com.jobapp.application.repository;

import com.jobapp.application.model.Application;
import com.jobapp.application.model.ApplicationStatus;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for ApplicationRepository
 * Requirements: 2.3, 2.5, 4.1, 4.2, 4.3
 */
@DataMongoTest
@TestPropertySource(properties = {
    "spring.data.mongodb.uri=mongodb://localhost:27017/jobapp_test"
})
class ApplicationRepositoryTest {
    
    @Autowired
    private ApplicationRepository applicationRepository;
    
    private Application testApplication1;
    private Application testApplication2;
    private Application testApplication3;
    
    @BeforeEach
    void setUp() {
        applicationRepository.deleteAll();
        
        // Create test application 1 - Applied status
        testApplication1 = new Application("candidate1", "job1", "employer1");
        testApplication1.setCoverLetter("I am very interested in this position and believe my skills align well.");
        testApplication1.setResumeUrl("https://example.com/resume1.pdf");
        testApplication1.setAdditionalDocuments(Arrays.asList("https://example.com/portfolio1.pdf"));
        
        // Create test application 2 - In Review status
        testApplication2 = new Application("candidate2", "job2", "employer2");
        testApplication2.setCoverLetter("Looking forward to contributing to your team.");
        testApplication2.setResumeUrl("https://example.com/resume2.pdf");
        testApplication2.updateStatus(ApplicationStatus.IN_REVIEW, "employer2", "Application looks promising");
        testApplication2.setNotes("Strong technical background");
        
        // Create test application 3 - Hired status
        testApplication3 = new Application("candidate1", "job3", "employer1");
        testApplication3.setCoverLetter("Excited about this opportunity.");
        testApplication3.setResumeUrl("https://example.com/resume1.pdf");
        testApplication3.updateStatus(ApplicationStatus.IN_REVIEW, "employer1", "Good candidate");
        testApplication3.updateStatus(ApplicationStatus.INTERVIEW_SCHEDULED, "employer1", "Interview scheduled");
        testApplication3.setInterviewScheduled(LocalDateTime.now().plusDays(2));
        testApplication3.updateStatus(ApplicationStatus.INTERVIEWED, "employer1", "Interview completed");
        testApplication3.updateStatus(ApplicationStatus.OFFER_MADE, "employer1", "Job offer extended");
        testApplication3.makeOffer(95000.0, "Full-time position with benefits", LocalDateTime.now().plusDays(7), "employer1");
        testApplication3.updateStatus(ApplicationStatus.OFFER_ACCEPTED, "candidate1", "Offer accepted");
        testApplication3.updateStatus(ApplicationStatus.HIRED, "employer1", "Welcome to the team");
        
        applicationRepository.save(testApplication1);
        applicationRepository.save(testApplication2);
        applicationRepository.save(testApplication3);
    }
    
    @Test
    @DisplayName("Should find application by candidate and job ID")
    void testFindByCandidateIdAndJobId() {
        // When
        Optional<Application> found = applicationRepository.findByCandidateIdAndJobId("candidate1", "job1");
        Optional<Application> notFound = applicationRepository.findByCandidateIdAndJobId("candidate1", "nonexistent");
        
        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getCoverLetter()).contains("very interested");
        
        assertThat(notFound).isEmpty();
    }
    
    @Test
    @DisplayName("Should check if application exists by candidate and job ID")
    void testExistsByCandidateIdAndJobId() {
        // When & Then
        assertThat(applicationRepository.existsByCandidateIdAndJobId("candidate1", "job1")).isTrue();
        assertThat(applicationRepository.existsByCandidateIdAndJobId("candidate1", "job3")).isTrue();
        assertThat(applicationRepository.existsByCandidateIdAndJobId("candidate1", "nonexistent")).isFalse();
    }
    
    @Test
    @DisplayName("Should find applications by candidate ID")
    void testFindByCandidateId() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        
        // When
        Page<Application> candidate1Apps = applicationRepository.findByCandidateId("candidate1", pageable);
        Page<Application> candidate2Apps = applicationRepository.findByCandidateId("candidate2", pageable);
        
        // Then
        assertThat(candidate1Apps.getContent()).hasSize(2);
        assertThat(candidate1Apps.getContent())
            .extracting(Application::getJobId)
            .containsExactlyInAnyOrder("job1", "job3");
        
        assertThat(candidate2Apps.getContent()).hasSize(1);
        assertThat(candidate2Apps.getContent().get(0).getJobId()).isEqualTo("job2");
    }
    
    @Test
    @DisplayName("Should find applications by candidate ID and status")
    void testFindByCandidateIdAndStatus() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        
        // When
        Page<Application> appliedApps = applicationRepository.findByCandidateIdAndStatus("candidate1", ApplicationStatus.APPLIED, pageable);
        Page<Application> hiredApps = applicationRepository.findByCandidateIdAndStatus("candidate1", ApplicationStatus.HIRED, pageable);
        
        // Then
        assertThat(appliedApps.getContent()).hasSize(1);
        assertThat(appliedApps.getContent().get(0).getJobId()).isEqualTo("job1");
        
        assertThat(hiredApps.getContent()).hasSize(1);
        assertThat(hiredApps.getContent().get(0).getJobId()).isEqualTo("job3");
    }
    
    @Test
    @DisplayName("Should find applications by job ID")
    void testFindByJobId() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        
        // When
        Page<Application> job1Apps = applicationRepository.findByJobId("job1", pageable);
        Page<Application> job2Apps = applicationRepository.findByJobId("job2", pageable);
        Page<Application> job3Apps = applicationRepository.findByJobId("job3", pageable);
        
        // Then
        assertThat(job1Apps.getContent()).hasSize(1);
        assertThat(job1Apps.getContent().get(0).getCandidateId()).isEqualTo("candidate1");
        
        assertThat(job2Apps.getContent()).hasSize(1);
        assertThat(job2Apps.getContent().get(0).getCandidateId()).isEqualTo("candidate2");
        
        assertThat(job3Apps.getContent()).hasSize(1);
        assertThat(job3Apps.getContent().get(0).getCandidateId()).isEqualTo("candidate1");
    }
    
    @Test
    @DisplayName("Should find applications by job ID and status")
    void testFindByJobIdAndStatus() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        
        // When
        Page<Application> job1Applied = applicationRepository.findByJobIdAndStatus("job1", ApplicationStatus.APPLIED, pageable);
        Page<Application> job3Hired = applicationRepository.findByJobIdAndStatus("job3", ApplicationStatus.HIRED, pageable);
        
        // Then
        assertThat(job1Applied.getContent()).hasSize(1);
        assertThat(job1Applied.getContent().get(0).getCandidateId()).isEqualTo("candidate1");
        
        assertThat(job3Hired.getContent()).hasSize(1);
        assertThat(job3Hired.getContent().get(0).getCandidateId()).isEqualTo("candidate1");
    }
    
    @Test
    @DisplayName("Should find applications by employer ID")
    void testFindByEmployerId() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        
        // When
        Page<Application> employer1Apps = applicationRepository.findByEmployerId("employer1", pageable);
        Page<Application> employer2Apps = applicationRepository.findByEmployerId("employer2", pageable);
        
        // Then
        assertThat(employer1Apps.getContent()).hasSize(2);
        assertThat(employer1Apps.getContent())
            .extracting(Application::getJobId)
            .containsExactlyInAnyOrder("job1", "job3");
        
        assertThat(employer2Apps.getContent()).hasSize(1);
        assertThat(employer2Apps.getContent().get(0).getJobId()).isEqualTo("job2");
    }
    
    @Test
    @DisplayName("Should find applications by employer ID and status")
    void testFindByEmployerIdAndStatus() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        
        // When
        Page<Application> employer1Applied = applicationRepository.findByEmployerIdAndStatus("employer1", ApplicationStatus.APPLIED, pageable);
        Page<Application> employer1Hired = applicationRepository.findByEmployerIdAndStatus("employer1", ApplicationStatus.HIRED, pageable);
        Page<Application> employer2InReview = applicationRepository.findByEmployerIdAndStatus("employer2", ApplicationStatus.IN_REVIEW, pageable);
        
        // Then
        assertThat(employer1Applied.getContent()).hasSize(1);
        assertThat(employer1Applied.getContent().get(0).getJobId()).isEqualTo("job1");
        
        assertThat(employer1Hired.getContent()).hasSize(1);
        assertThat(employer1Hired.getContent().get(0).getJobId()).isEqualTo("job3");
        
        assertThat(employer2InReview.getContent()).hasSize(1);
        assertThat(employer2InReview.getContent().get(0).getJobId()).isEqualTo("job2");
    }
    
    @Test
    @DisplayName("Should find applications by status")
    void testFindByStatus() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        
        // When
        Page<Application> appliedApps = applicationRepository.findByStatus(ApplicationStatus.APPLIED, pageable);
        Page<Application> inReviewApps = applicationRepository.findByStatus(ApplicationStatus.IN_REVIEW, pageable);
        Page<Application> hiredApps = applicationRepository.findByStatus(ApplicationStatus.HIRED, pageable);
        
        // Then
        assertThat(appliedApps.getContent()).hasSize(1);
        assertThat(appliedApps.getContent().get(0).getJobId()).isEqualTo("job1");
        
        assertThat(inReviewApps.getContent()).hasSize(1);
        assertThat(inReviewApps.getContent().get(0).getJobId()).isEqualTo("job2");
        
        assertThat(hiredApps.getContent()).hasSize(1);
        assertThat(hiredApps.getContent().get(0).getJobId()).isEqualTo("job3");
    }
    
    @Test
    @DisplayName("Should find applications with scheduled interviews")
    void testFindApplicationsWithScheduledInterviews() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        
        // When
        Page<Application> appsWithInterviews = applicationRepository.findApplicationsWithScheduledInterviews(pageable);
        
        // Then
        assertThat(appsWithInterviews.getContent()).hasSize(1);
        assertThat(appsWithInterviews.getContent().get(0).getJobId()).isEqualTo("job3");
        assertThat(appsWithInterviews.getContent().get(0).getInterviewScheduled()).isNotNull();
    }
    
    @Test
    @DisplayName("Should find applications with job offers")
    void testFindApplicationsWithOffers() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        
        // When
        Page<Application> appsWithOffers = applicationRepository.findApplicationsWithOffers(pageable);
        
        // Then
        assertThat(appsWithOffers.getContent()).hasSize(1);
        assertThat(appsWithOffers.getContent().get(0).getJobId()).isEqualTo("job3");
        assertThat(appsWithOffers.getContent().get(0).getSalaryOffered()).isEqualTo(95000.0);
    }
    
    @Test
    @DisplayName("Should find applications requiring action")
    void testFindApplicationsRequiringAction() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        
        // When
        Page<Application> employer1Actions = applicationRepository.findApplicationsRequiringAction("employer1", pageable);
        Page<Application> employer2Actions = applicationRepository.findApplicationsRequiringAction("employer2", pageable);
        
        // Then
        assertThat(employer1Actions.getContent()).isEmpty(); // job3 is hired (final status)
        assertThat(employer2Actions.getContent()).hasSize(1); // job2 is in review
        assertThat(employer2Actions.getContent().get(0).getStatus()).isEqualTo(ApplicationStatus.IN_REVIEW);
    }
    
    @Test
    @DisplayName("Should find applications with cover letters")
    void testFindApplicationsWithCoverLetters() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        
        // When
        Page<Application> appsWithCoverLetters = applicationRepository.findApplicationsWithCoverLetters(pageable);
        
        // Then
        assertThat(appsWithCoverLetters.getContent()).hasSize(3); // All test applications have cover letters
    }
    
    @Test
    @DisplayName("Should find applications with additional documents")
    void testFindApplicationsWithAdditionalDocuments() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        
        // When
        Page<Application> appsWithDocs = applicationRepository.findApplicationsWithAdditionalDocuments(pageable);
        
        // Then
        assertThat(appsWithDocs.getContent()).hasSize(1); // Only testApplication1 has additional documents
        assertThat(appsWithDocs.getContent().get(0).getJobId()).isEqualTo("job1");
    }
    
    @Test
    @DisplayName("Should count applications by various criteria")
    void testCountMethods() {
        // When & Then
        assertThat(applicationRepository.countByCandidateId("candidate1")).isEqualTo(2);
        assertThat(applicationRepository.countByCandidateId("candidate2")).isEqualTo(1);
        
        assertThat(applicationRepository.countByCandidateIdAndStatus("candidate1", ApplicationStatus.APPLIED)).isEqualTo(1);
        assertThat(applicationRepository.countByCandidateIdAndStatus("candidate1", ApplicationStatus.HIRED)).isEqualTo(1);
        
        assertThat(applicationRepository.countByJobId("job1")).isEqualTo(1);
        assertThat(applicationRepository.countByJobId("job2")).isEqualTo(1);
        assertThat(applicationRepository.countByJobId("job3")).isEqualTo(1);
        
        assertThat(applicationRepository.countByEmployerId("employer1")).isEqualTo(2);
        assertThat(applicationRepository.countByEmployerId("employer2")).isEqualTo(1);
        
        assertThat(applicationRepository.countByStatus(ApplicationStatus.APPLIED)).isEqualTo(1);
        assertThat(applicationRepository.countByStatus(ApplicationStatus.IN_REVIEW)).isEqualTo(1);
        assertThat(applicationRepository.countByStatus(ApplicationStatus.HIRED)).isEqualTo(1);
    }
    
    @Test
    @DisplayName("Should find recent applications")
    void testFindRecentApplications() {
        // Given
        LocalDateTime twoDaysAgo = LocalDateTime.now().minusDays(2);
        Pageable pageable = PageRequest.of(0, 10);
        
        // When
        Page<Application> recentCandidate1 = applicationRepository.findRecentApplicationsByCandidate("candidate1", twoDaysAgo, pageable);
        Page<Application> recentEmployer1 = applicationRepository.findRecentApplicationsByEmployer("employer1", twoDaysAgo, pageable);
        
        // Then
        assertThat(recentCandidate1.getContent()).hasSize(2); // All applications are recent in test
        assertThat(recentEmployer1.getContent()).hasSize(2); // All applications are recent in test
    }
    
    @Test
    @DisplayName("Should find applications within date range")
    void testFindByAppliedAtBetween() {
        // Given
        LocalDateTime startDate = LocalDateTime.now().minusDays(1);
        LocalDateTime endDate = LocalDateTime.now().plusDays(1);
        Pageable pageable = PageRequest.of(0, 10);
        
        // When
        Page<Application> appsInRange = applicationRepository.findByAppliedAtBetween(startDate, endDate, pageable);
        
        // Then
        assertThat(appsInRange.getContent()).hasSize(3); // All applications applied today
    }
    
    @Test
    @DisplayName("Should test application business methods")
    void testApplicationBusinessMethods() {
        // Test status transitions
        assertThat(testApplication1.getStatus()).isEqualTo(ApplicationStatus.APPLIED);
        assertThat(testApplication1.isFinalStatus()).isFalse();
        assertThat(testApplication1.isActive()).isTrue();
        
        assertThat(testApplication3.getStatus()).isEqualTo(ApplicationStatus.HIRED);
        assertThat(testApplication3.isFinalStatus()).isTrue();
        assertThat(testApplication3.isActive()).isFalse();
        
        // Test status history
        assertThat(testApplication3.getStatusHistory()).isNotEmpty();
        assertThat(testApplication3.getStatusHistory()).hasSizeGreaterThan(3);
        
        // Test offer methods
        assertThat(testApplication3.getSalaryOffered()).isEqualTo(95000.0);
        assertThat(testApplication3.getOfferDetails()).contains("Full-time position");
        assertThat(testApplication3.isOfferExpired()).isFalse(); // Offer expires in 7 days
        
        // Test interview scheduling
        assertThat(testApplication3.getInterviewScheduled()).isNotNull();
        assertThat(testApplication3.getInterviewScheduled()).isAfter(LocalDateTime.now());
    }
    
    @Test
    @DisplayName("Should test application status enum methods")
    void testApplicationStatusMethods() {
        // Test final status check
        assertThat(ApplicationStatus.APPLIED.isFinal()).isFalse();
        assertThat(ApplicationStatus.IN_REVIEW.isFinal()).isFalse();
        assertThat(ApplicationStatus.HIRED.isFinal()).isTrue();
        assertThat(ApplicationStatus.REJECTED.isFinal()).isTrue();
        
        // Test positive/negative status
        assertThat(ApplicationStatus.HIRED.isPositive()).isTrue();
        assertThat(ApplicationStatus.OFFER_MADE.isPositive()).isTrue();
        assertThat(ApplicationStatus.REJECTED.isNegative()).isTrue();
        assertThat(ApplicationStatus.WITHDRAWN.isNegative()).isTrue();
        
        // Test status transitions
        assertThat(ApplicationStatus.APPLIED.canTransitionTo(ApplicationStatus.IN_REVIEW)).isTrue();
        assertThat(ApplicationStatus.APPLIED.canTransitionTo(ApplicationStatus.HIRED)).isFalse();
        assertThat(ApplicationStatus.HIRED.canTransitionTo(ApplicationStatus.REJECTED)).isFalse();
        
        // Test next possible statuses
        ApplicationStatus[] nextFromApplied = ApplicationStatus.APPLIED.getNextPossibleStatuses();
        assertThat(nextFromApplied).contains(ApplicationStatus.IN_REVIEW, ApplicationStatus.REJECTED, ApplicationStatus.WITHDRAWN);
        
        ApplicationStatus[] nextFromHired = ApplicationStatus.HIRED.getNextPossibleStatuses();
        assertThat(nextFromHired).isEmpty(); // Final status
    }
    
    @Test
    @DisplayName("Should handle pagination correctly")
    void testPagination() {
        // Given
        Pageable firstPage = PageRequest.of(0, 2);
        Pageable secondPage = PageRequest.of(1, 2);
        
        // When
        Page<Application> page1 = applicationRepository.findByEmployerId("employer1", firstPage);
        Page<Application> page2 = applicationRepository.findByEmployerId("employer1", secondPage);
        
        // Then
        assertThat(page1.getContent()).hasSize(2);
        assertThat(page1.getTotalElements()).isEqualTo(2);
        assertThat(page1.getTotalPages()).isEqualTo(1);
        assertThat(page1.hasNext()).isFalse();
        
        assertThat(page2.getContent()).isEmpty();
    }
}