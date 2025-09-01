package com.jobapp.user.repository;

import com.jobapp.user.model.Employer;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for EmployerRepository
 * Requirements: 3.1, 3.4
 */
@DataMongoTest
@TestPropertySource(properties = {
    "spring.data.mongodb.uri=mongodb://localhost:27017/jobapp_test"
})
class EmployerRepositoryTest {
    
    @Autowired
    private EmployerRepository employerRepository;
    
    private Employer testEmployer1;
    private Employer testEmployer2;
    private Employer testEmployer3;
    
    @BeforeEach
    void setUp() {
        employerRepository.deleteAll();
        
        testEmployer1 = new Employer(
            "tech.corp@example.com",
            "hashedPassword123",
            "Tech Corp",
            "https://techcorp.com",
            "Leading technology company specializing in software development"
        );
        testEmployer1.setLogoUrl("https://example.com/logo1.png");
        testEmployer1.setAddress("123 Tech Street, San Francisco, CA");
        testEmployer1.setContactPerson("John Manager");
        testEmployer1.setContactPhone("+1234567890");
        testEmployer1.approve("admin123");
        
        testEmployer2 = new Employer(
            "startup.inc@example.com",
            "hashedPassword456",
            "Startup Inc",
            "https://startup.inc",
            "Innovative startup focused on AI solutions"
        );
        testEmployer2.setLogoUrl("https://example.com/logo2.png");
        testEmployer2.setAddress("456 Innovation Ave, Austin, TX");
        // This employer is pending approval (not approved)
        
        testEmployer3 = new Employer(
            "old.company@example.com",
            "hashedPassword789",
            "Old Company",
            "https://oldcompany.com",
            "Established company with traditional values"
        );
        testEmployer3.setIsActive(false);
        testEmployer3.reject("Company does not meet our standards");
        
        employerRepository.save(testEmployer1);
        employerRepository.save(testEmployer2);
        employerRepository.save(testEmployer3);
    }
    
    @Test
    @DisplayName("Should find employer by email")
    void testFindByEmail() {
        // When
        Optional<Employer> found = employerRepository.findByEmail("tech.corp@example.com");
        
        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getCompanyName()).isEqualTo("Tech Corp");
        assertThat(found.get().getWebsite()).isEqualTo("https://techcorp.com");
    }
    
    @Test
    @DisplayName("Should return empty when employer not found by email")
    void testFindByEmailNotFound() {
        // When
        Optional<Employer> found = employerRepository.findByEmail("nonexistent@example.com");
        
        // Then
        assertThat(found).isEmpty();
    }
    
    @Test
    @DisplayName("Should find employer by email and active status")
    void testFindByEmailAndIsActive() {
        // When
        Optional<Employer> activeEmployer = employerRepository.findByEmailAndIsActive("tech.corp@example.com", true);
        Optional<Employer> inactiveEmployer = employerRepository.findByEmailAndIsActive("old.company@example.com", false);
        Optional<Employer> wrongStatus = employerRepository.findByEmailAndIsActive("tech.corp@example.com", false);
        
        // Then
        assertThat(activeEmployer).isPresent();
        assertThat(activeEmployer.get().getCompanyName()).isEqualTo("Tech Corp");
        
        assertThat(inactiveEmployer).isPresent();
        assertThat(inactiveEmployer.get().getCompanyName()).isEqualTo("Old Company");
        
        assertThat(wrongStatus).isEmpty();
    }
    
    @Test
    @DisplayName("Should check if employer exists by email")
    void testExistsByEmail() {
        // When & Then
        assertThat(employerRepository.existsByEmail("tech.corp@example.com")).isTrue();
        assertThat(employerRepository.existsByEmail("nonexistent@example.com")).isFalse();
    }
    
    @Test
    @DisplayName("Should find employers by approval status")
    void testFindByIsApproved() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        
        // When
        Page<Employer> approvedEmployers = employerRepository.findByIsApproved(true, pageable);
        Page<Employer> notApprovedEmployers = employerRepository.findByIsApproved(false, pageable);
        
        // Then
        assertThat(approvedEmployers.getContent()).hasSize(1);
        assertThat(approvedEmployers.getContent().get(0).getCompanyName()).isEqualTo("Tech Corp");
        
        assertThat(notApprovedEmployers.getContent()).hasSize(2);
        assertThat(notApprovedEmployers.getContent())
            .extracting(Employer::getCompanyName)
            .containsExactlyInAnyOrder("Startup Inc", "Old Company");
    }
    
    @Test
    @DisplayName("Should find employers by approval and active status")
    void testFindByIsApprovedAndIsActive() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        
        // When
        Page<Employer> approvedActive = employerRepository.findByIsApprovedAndIsActive(true, true, pageable);
        Page<Employer> notApprovedActive = employerRepository.findByIsApprovedAndIsActive(false, true, pageable);
        Page<Employer> notApprovedInactive = employerRepository.findByIsApprovedAndIsActive(false, false, pageable);
        
        // Then
        assertThat(approvedActive.getContent()).hasSize(1);
        assertThat(approvedActive.getContent().get(0).getCompanyName()).isEqualTo("Tech Corp");
        
        assertThat(notApprovedActive.getContent()).hasSize(1);
        assertThat(notApprovedActive.getContent().get(0).getCompanyName()).isEqualTo("Startup Inc");
        
        assertThat(notApprovedInactive.getContent()).hasSize(1);
        assertThat(notApprovedInactive.getContent().get(0).getCompanyName()).isEqualTo("Old Company");
    }
    
    @Test
    @DisplayName("Should find employers pending approval")
    void testFindPendingApproval() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        
        // When
        Page<Employer> pendingEmployers = employerRepository.findPendingApproval(pageable);
        
        // Then
        assertThat(pendingEmployers.getContent()).hasSize(1);
        assertThat(pendingEmployers.getContent().get(0).getCompanyName()).isEqualTo("Startup Inc");
        assertThat(pendingEmployers.getContent().get(0).getIsApproved()).isFalse();
        assertThat(pendingEmployers.getContent().get(0).getIsActive()).isTrue();
    }
    
    @Test
    @DisplayName("Should find active approved employers")
    void testFindActiveApprovedEmployers() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        
        // When
        Page<Employer> activeApproved = employerRepository.findActiveApprovedEmployers(pageable);
        
        // Then
        assertThat(activeApproved.getContent()).hasSize(1);
        assertThat(activeApproved.getContent().get(0).getCompanyName()).isEqualTo("Tech Corp");
        assertThat(activeApproved.getContent().get(0).canPostJobs()).isTrue();
    }
    
    @Test
    @DisplayName("Should find employers by company name containing text")
    void testFindByCompanyNameContainingIgnoreCase() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        
        // When
        Page<Employer> techEmployers = employerRepository.findByCompanyNameContainingIgnoreCase("tech", pageable);
        Page<Employer> startupEmployers = employerRepository.findByCompanyNameContainingIgnoreCase("startup", pageable);
        
        // Then
        assertThat(techEmployers.getContent()).hasSize(1);
        assertThat(techEmployers.getContent().get(0).getCompanyName()).isEqualTo("Tech Corp");
        
        assertThat(startupEmployers.getContent()).hasSize(1);
        assertThat(startupEmployers.getContent().get(0).getCompanyName()).isEqualTo("Startup Inc");
    }
    
    @Test
    @DisplayName("Should find employers approved by specific admin")
    void testFindByApprovedBy() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        
        // When
        Page<Employer> approvedByAdmin = employerRepository.findByApprovedBy("admin123", pageable);
        Page<Employer> approvedByOther = employerRepository.findByApprovedBy("admin456", pageable);
        
        // Then
        assertThat(approvedByAdmin.getContent()).hasSize(1);
        assertThat(approvedByAdmin.getContent().get(0).getCompanyName()).isEqualTo("Tech Corp");
        
        assertThat(approvedByOther.getContent()).isEmpty();
    }
    
    @Test
    @DisplayName("Should find employers with logos")
    void testFindEmployersWithLogo() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        
        // When
        Page<Employer> employersWithLogo = employerRepository.findEmployersWithLogo(pageable);
        
        // Then
        assertThat(employersWithLogo.getContent()).hasSize(2);
        assertThat(employersWithLogo.getContent())
            .extracting(Employer::getCompanyName)
            .containsExactlyInAnyOrder("Tech Corp", "Startup Inc");
    }
    
    @Test
    @DisplayName("Should find employers by multiple criteria")
    void testFindByCriteria() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        
        // When
        Page<Employer> techApproved = employerRepository.findByCriteria("Tech", true, true, pageable);
        Page<Employer> anyNotApproved = employerRepository.findByCriteria(null, false, true, pageable);
        Page<Employer> startupAnyApproval = employerRepository.findByCriteria("Startup", null, true, pageable);
        
        // Then
        assertThat(techApproved.getContent()).hasSize(1);
        assertThat(techApproved.getContent().get(0).getCompanyName()).isEqualTo("Tech Corp");
        
        assertThat(anyNotApproved.getContent()).hasSize(1);
        assertThat(anyNotApproved.getContent().get(0).getCompanyName()).isEqualTo("Startup Inc");
        
        assertThat(startupAnyApproval.getContent()).hasSize(1);
        assertThat(startupAnyApproval.getContent().get(0).getCompanyName()).isEqualTo("Startup Inc");
    }
    
    @Test
    @DisplayName("Should count employers by approval status")
    void testCountByIsApproved() {
        // When
        long approvedCount = employerRepository.countByIsApproved(true);
        long notApprovedCount = employerRepository.countByIsApproved(false);
        
        // Then
        assertThat(approvedCount).isEqualTo(1);
        assertThat(notApprovedCount).isEqualTo(2);
    }
    
    @Test
    @DisplayName("Should count employers by approval and active status")
    void testCountByIsApprovedAndIsActive() {
        // When
        long approvedActiveCount = employerRepository.countByIsApprovedAndIsActive(true, true);
        long notApprovedActiveCount = employerRepository.countByIsApprovedAndIsActive(false, true);
        long notApprovedInactiveCount = employerRepository.countByIsApprovedAndIsActive(false, false);
        
        // Then
        assertThat(approvedActiveCount).isEqualTo(1);
        assertThat(notApprovedActiveCount).isEqualTo(1);
        assertThat(notApprovedInactiveCount).isEqualTo(1);
    }
    
    @Test
    @DisplayName("Should count employers pending approval")
    void testCountPendingApproval() {
        // When
        long pendingCount = employerRepository.countPendingApproval();
        
        // Then
        assertThat(pendingCount).isEqualTo(1);
    }
    
    @Test
    @DisplayName("Should count employers with logos")
    void testCountEmployersWithLogo() {
        // When
        long countWithLogo = employerRepository.countEmployersWithLogo();
        
        // Then
        assertThat(countWithLogo).isEqualTo(2);
    }
    
    @Test
    @DisplayName("Should find recently registered employers")
    void testFindRecentlyRegistered() {
        // Given
        LocalDateTime twoDaysAgo = LocalDateTime.now().minusDays(2);
        Pageable pageable = PageRequest.of(0, 10);
        
        // When
        Page<Employer> recentEmployers = employerRepository.findRecentlyRegistered(twoDaysAgo, pageable);
        
        // Then
        assertThat(recentEmployers.getContent()).hasSize(3); // All employers are recent in test
    }
    
    @Test
    @DisplayName("Should find recently approved employers")
    void testFindRecentlyApproved() {
        // Given
        LocalDateTime twoDaysAgo = LocalDateTime.now().minusDays(2);
        Pageable pageable = PageRequest.of(0, 10);
        
        // When
        Page<Employer> recentlyApproved = employerRepository.findRecentlyApproved(twoDaysAgo, pageable);
        
        // Then
        assertThat(recentlyApproved.getContent()).hasSize(1);
        assertThat(recentlyApproved.getContent().get(0).getCompanyName()).isEqualTo("Tech Corp");
    }
    
    @Test
    @DisplayName("Should find employers by rejection reason")
    void testFindByRejectionReasonContainingIgnoreCase() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        
        // When
        Page<Employer> rejectedEmployers = employerRepository.findByRejectionReasonContainingIgnoreCase("standards", pageable);
        
        // Then
        assertThat(rejectedEmployers.getContent()).hasSize(1);
        assertThat(rejectedEmployers.getContent().get(0).getCompanyName()).isEqualTo("Old Company");
    }
    
    @Test
    @DisplayName("Should find employers created within date range")
    void testFindByCreatedAtBetween() {
        // Given
        LocalDateTime startDate = LocalDateTime.now().minusDays(1);
        LocalDateTime endDate = LocalDateTime.now().plusDays(1);
        Pageable pageable = PageRequest.of(0, 10);
        
        // When
        Page<Employer> employersInRange = employerRepository.findByCreatedAtBetween(startDate, endDate, pageable);
        
        // Then
        assertThat(employersInRange.getContent()).hasSize(3); // All employers created today
    }
    
    @Test
    @DisplayName("Should find employers approved within date range")
    void testFindByApprovalDateBetween() {
        // Given
        LocalDateTime startDate = LocalDateTime.now().minusDays(1);
        LocalDateTime endDate = LocalDateTime.now().plusDays(1);
        Pageable pageable = PageRequest.of(0, 10);
        
        // When
        Page<Employer> approvedInRange = employerRepository.findByApprovalDateBetween(startDate, endDate, pageable);
        
        // Then
        assertThat(approvedInRange.getContent()).hasSize(1);
        assertThat(approvedInRange.getContent().get(0).getCompanyName()).isEqualTo("Tech Corp");
    }
    
    @Test
    @DisplayName("Should test employer business methods")
    void testEmployerBusinessMethods() {
        // Test canPostJobs method
        assertThat(testEmployer1.canPostJobs()).isTrue(); // approved and active
        assertThat(testEmployer2.canPostJobs()).isFalse(); // not approved
        assertThat(testEmployer3.canPostJobs()).isFalse(); // not active
        
        // Test approve method
        testEmployer2.approve("admin456");
        assertThat(testEmployer2.getIsApproved()).isTrue();
        assertThat(testEmployer2.getApprovedBy()).isEqualTo("admin456");
        assertThat(testEmployer2.getApprovalDate()).isNotNull();
        assertThat(testEmployer2.getRejectionReason()).isNull();
        
        // Test reject method
        testEmployer2.reject("New rejection reason");
        assertThat(testEmployer2.getIsApproved()).isFalse();
        assertThat(testEmployer2.getRejectionReason()).isEqualTo("New rejection reason");
        assertThat(testEmployer2.getApprovalDate()).isNull();
        assertThat(testEmployer2.getApprovedBy()).isNull();
    }
    
    @Test
    @DisplayName("Should handle pagination correctly")
    void testPagination() {
        // Given
        Pageable firstPage = PageRequest.of(0, 2);
        Pageable secondPage = PageRequest.of(1, 2);
        
        // When
        Page<Employer> page1 = employerRepository.findByIsActive(true, firstPage);
        Page<Employer> page2 = employerRepository.findByIsActive(true, secondPage);
        
        // Then
        assertThat(page1.getContent()).hasSize(2);
        assertThat(page1.getTotalElements()).isEqualTo(2);
        assertThat(page1.getTotalPages()).isEqualTo(1);
        assertThat(page1.hasNext()).isFalse();
        
        assertThat(page2.getContent()).isEmpty();
    }
}