package com.jobapp.user.repository;

import com.jobapp.user.model.Candidate;
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
 * Unit tests for CandidateRepository
 * Requirements: 1.1, 1.2, 1.5
 */
@DataMongoTest
@TestPropertySource(properties = {
    "spring.data.mongodb.uri=mongodb://localhost:27017/jobapp_test"
})
class CandidateRepositoryTest {
    
    @Autowired
    private CandidateRepository candidateRepository;
    
    private Candidate testCandidate1;
    private Candidate testCandidate2;
    private Candidate testCandidate3;
    
    @BeforeEach
    void setUp() {
        candidateRepository.deleteAll();
        
        testCandidate1 = new Candidate(
            "john.doe@example.com",
            "hashedPassword123",
            "John Doe",
            "+1234567890",
            "Computer Science",
            2022
        );
        testCandidate1.setResumeUrl("https://example.com/resume1.pdf");
        testCandidate1.setLinkedinProfile("https://linkedin.com/in/johndoe");
        testCandidate1.setPortfolioUrl("https://johndoe.dev");
        
        testCandidate2 = new Candidate(
            "jane.smith@example.com",
            "hashedPassword456",
            "Jane Smith",
            "+1987654321",
            "Software Engineering",
            2021
        );
        testCandidate2.setResumeUrl("https://example.com/resume2.pdf");
        testCandidate2.setLinkedinProfile("https://linkedin.com/in/janesmith");
        
        testCandidate3 = new Candidate(
            "bob.wilson@example.com",
            "hashedPassword789",
            "Bob Wilson",
            "+1555666777",
            "Information Technology",
            2023
        );
        testCandidate3.setIsActive(false);
        
        candidateRepository.save(testCandidate1);
        candidateRepository.save(testCandidate2);
        candidateRepository.save(testCandidate3);
    }
    
    @Test
    @DisplayName("Should find candidate by email")
    void testFindByEmail() {
        // When
        Optional<Candidate> found = candidateRepository.findByEmail("john.doe@example.com");
        
        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("John Doe");
        assertThat(found.get().getDegree()).isEqualTo("Computer Science");
    }
    
    @Test
    @DisplayName("Should return empty when candidate not found by email")
    void testFindByEmailNotFound() {
        // When
        Optional<Candidate> found = candidateRepository.findByEmail("nonexistent@example.com");
        
        // Then
        assertThat(found).isEmpty();
    }
    
    @Test
    @DisplayName("Should find candidate by email and active status")
    void testFindByEmailAndIsActive() {
        // When
        Optional<Candidate> activeCandidate = candidateRepository.findByEmailAndIsActive("john.doe@example.com", true);
        Optional<Candidate> inactiveCandidate = candidateRepository.findByEmailAndIsActive("bob.wilson@example.com", false);
        Optional<Candidate> wrongStatus = candidateRepository.findByEmailAndIsActive("john.doe@example.com", false);
        
        // Then
        assertThat(activeCandidate).isPresent();
        assertThat(activeCandidate.get().getName()).isEqualTo("John Doe");
        
        assertThat(inactiveCandidate).isPresent();
        assertThat(inactiveCandidate.get().getName()).isEqualTo("Bob Wilson");
        
        assertThat(wrongStatus).isEmpty();
    }
    
    @Test
    @DisplayName("Should check if candidate exists by email")
    void testExistsByEmail() {
        // When & Then
        assertThat(candidateRepository.existsByEmail("john.doe@example.com")).isTrue();
        assertThat(candidateRepository.existsByEmail("nonexistent@example.com")).isFalse();
    }
    
    @Test
    @DisplayName("Should find active candidates")
    void testFindByIsActive() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        
        // When
        Page<Candidate> activeCandidates = candidateRepository.findByIsActive(true, pageable);
        Page<Candidate> inactiveCandidates = candidateRepository.findByIsActive(false, pageable);
        
        // Then
        assertThat(activeCandidates.getContent()).hasSize(2);
        assertThat(activeCandidates.getContent())
            .extracting(Candidate::getName)
            .containsExactlyInAnyOrder("John Doe", "Jane Smith");
        
        assertThat(inactiveCandidates.getContent()).hasSize(1);
        assertThat(inactiveCandidates.getContent().get(0).getName()).isEqualTo("Bob Wilson");
    }
    
    @Test
    @DisplayName("Should find candidates by degree containing text")
    void testFindByDegreeContainingIgnoreCase() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        
        // When
        Page<Candidate> computerCandidates = candidateRepository.findByDegreeContainingIgnoreCase("computer", pageable);
        Page<Candidate> engineeringCandidates = candidateRepository.findByDegreeContainingIgnoreCase("engineering", pageable);
        
        // Then
        assertThat(computerCandidates.getContent()).hasSize(1);
        assertThat(computerCandidates.getContent().get(0).getName()).isEqualTo("John Doe");
        
        assertThat(engineeringCandidates.getContent()).hasSize(1);
        assertThat(engineeringCandidates.getContent().get(0).getName()).isEqualTo("Jane Smith");
    }
    
    @Test
    @DisplayName("Should find candidates by graduation year range")
    void testFindByGraduationYearBetween() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        
        // When
        Page<Candidate> recentGrads = candidateRepository.findByGraduationYearBetween(2021, 2022, pageable);
        Page<Candidate> allGrads = candidateRepository.findByGraduationYearBetween(2020, 2025, pageable);
        
        // Then
        assertThat(recentGrads.getContent()).hasSize(2);
        assertThat(recentGrads.getContent())
            .extracting(Candidate::getName)
            .containsExactlyInAnyOrder("John Doe", "Jane Smith");
        
        assertThat(allGrads.getContent()).hasSize(3);
    }
    
    @Test
    @DisplayName("Should find candidates with resume")
    void testFindCandidatesWithResume() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        
        // When
        Page<Candidate> candidatesWithResume = candidateRepository.findCandidatesWithResume(pageable);
        
        // Then
        assertThat(candidatesWithResume.getContent()).hasSize(2);
        assertThat(candidatesWithResume.getContent())
            .extracting(Candidate::getName)
            .containsExactlyInAnyOrder("John Doe", "Jane Smith");
    }
    
    @Test
    @DisplayName("Should find candidates with LinkedIn profiles")
    void testFindCandidatesWithLinkedIn() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        
        // When
        Page<Candidate> candidatesWithLinkedIn = candidateRepository.findCandidatesWithLinkedIn(pageable);
        
        // Then
        assertThat(candidatesWithLinkedIn.getContent()).hasSize(2);
        assertThat(candidatesWithLinkedIn.getContent())
            .extracting(Candidate::getName)
            .containsExactlyInAnyOrder("John Doe", "Jane Smith");
    }
    
    @Test
    @DisplayName("Should find candidates by name containing text")
    void testFindByNameContainingIgnoreCase() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        
        // When
        Page<Candidate> johnCandidates = candidateRepository.findByNameContainingIgnoreCase("john", pageable);
        Page<Candidate> smithCandidates = candidateRepository.findByNameContainingIgnoreCase("smith", pageable);
        
        // Then
        assertThat(johnCandidates.getContent()).hasSize(1);
        assertThat(johnCandidates.getContent().get(0).getName()).isEqualTo("John Doe");
        
        assertThat(smithCandidates.getContent()).hasSize(1);
        assertThat(smithCandidates.getContent().get(0).getName()).isEqualTo("Jane Smith");
    }
    
    @Test
    @DisplayName("Should find candidates by multiple criteria")
    void testFindByCriteria() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        
        // When
        Page<Candidate> computerScience2022 = candidateRepository.findByCriteria("Computer", 2022, true, pageable);
        Page<Candidate> anyDegree2021 = candidateRepository.findByCriteria(null, 2021, true, pageable);
        Page<Candidate> engineeringAnyYear = candidateRepository.findByCriteria("Engineering", null, true, pageable);
        
        // Then
        assertThat(computerScience2022.getContent()).hasSize(1);
        assertThat(computerScience2022.getContent().get(0).getName()).isEqualTo("John Doe");
        
        assertThat(anyDegree2021.getContent()).hasSize(1);
        assertThat(anyDegree2021.getContent().get(0).getName()).isEqualTo("Jane Smith");
        
        assertThat(engineeringAnyYear.getContent()).hasSize(1);
        assertThat(engineeringAnyYear.getContent().get(0).getName()).isEqualTo("Jane Smith");
    }
    
    @Test
    @DisplayName("Should count candidates by active status")
    void testCountByIsActive() {
        // When
        long activeCount = candidateRepository.countByIsActive(true);
        long inactiveCount = candidateRepository.countByIsActive(false);
        
        // Then
        assertThat(activeCount).isEqualTo(2);
        assertThat(inactiveCount).isEqualTo(1);
    }
    
    @Test
    @DisplayName("Should count candidates with resume")
    void testCountCandidatesWithResume() {
        // When
        long countWithResume = candidateRepository.countCandidatesWithResume();
        
        // Then
        assertThat(countWithResume).isEqualTo(2);
    }
    
    @Test
    @DisplayName("Should find recently registered candidates")
    void testFindRecentlyRegistered() {
        // Given
        LocalDateTime twoDaysAgo = LocalDateTime.now().minusDays(2);
        Pageable pageable = PageRequest.of(0, 10);
        
        // When
        Page<Candidate> recentCandidates = candidateRepository.findRecentlyRegistered(twoDaysAgo, pageable);
        
        // Then
        assertThat(recentCandidates.getContent()).hasSize(3); // All candidates are recent in test
    }
    
    @Test
    @DisplayName("Should find candidates created within date range")
    void testFindByCreatedAtBetween() {
        // Given
        LocalDateTime startDate = LocalDateTime.now().minusDays(1);
        LocalDateTime endDate = LocalDateTime.now().plusDays(1);
        Pageable pageable = PageRequest.of(0, 10);
        
        // When
        Page<Candidate> candidatesInRange = candidateRepository.findByCreatedAtBetween(startDate, endDate, pageable);
        
        // Then
        assertThat(candidatesInRange.getContent()).hasSize(3); // All candidates created today
    }
    
    @Test
    @DisplayName("Should handle pagination correctly")
    void testPagination() {
        // Given
        Pageable firstPage = PageRequest.of(0, 2);
        Pageable secondPage = PageRequest.of(1, 2);
        
        // When
        Page<Candidate> page1 = candidateRepository.findByIsActive(true, firstPage);
        Page<Candidate> page2 = candidateRepository.findByIsActive(true, secondPage);
        
        // Then
        assertThat(page1.getContent()).hasSize(2);
        assertThat(page1.getTotalElements()).isEqualTo(2);
        assertThat(page1.getTotalPages()).isEqualTo(1);
        assertThat(page1.hasNext()).isFalse();
        
        assertThat(page2.getContent()).isEmpty();
    }
}