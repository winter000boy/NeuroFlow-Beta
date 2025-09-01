package com.jobapp.user.service;

import com.jobapp.user.dto.CandidateRegistrationRequest;
import com.jobapp.user.dto.CandidateProfileUpdateRequest;
import com.jobapp.user.dto.CandidateResponse;
import com.jobapp.user.model.Candidate;
import com.jobapp.user.repository.CandidateRepository;
import com.jobapp.user.exception.ResourceNotFoundException;
import com.jobapp.user.exception.EmailAlreadyExistsException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for CandidateService
 * Requirements: 1.1, 1.2, 1.4, 1.5
 */
@ExtendWith(MockitoExtension.class)
class CandidateServiceTest {
    
    @Mock
    private CandidateRepository candidateRepository;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @InjectMocks
    private CandidateService candidateService;
    
    private CandidateRegistrationRequest registrationRequest;
    private Candidate candidate;
    
    @BeforeEach
    void setUp() {
        registrationRequest = new CandidateRegistrationRequest(
            "john.doe@example.com",
            "password123",
            "John Doe",
            "+1234567890",
            "Computer Science",
            2022
        );
        
        candidate = new Candidate(
            "john.doe@example.com",
            "encodedPassword",
            "John Doe",
            "+1234567890",
            "Computer Science",
            2022
        );
        candidate.setId("candidate123");
        candidate.setCreatedAt(LocalDateTime.now());
        candidate.setUpdatedAt(LocalDateTime.now());
    }
    
    @Test
    void registerCandidate_ValidRequest_ReturnsResponse() {
        // Given
        when(candidateRepository.findByEmail(registrationRequest.getEmail()))
            .thenReturn(Optional.empty());
        when(passwordEncoder.encode(registrationRequest.getPassword()))
            .thenReturn("encodedPassword");
        when(candidateRepository.save(any(Candidate.class)))
            .thenReturn(candidate);
        
        // When
        CandidateResponse response = candidateService.registerCandidate(registrationRequest);
        
        // Then
        assertNotNull(response);
        assertEquals("john.doe@example.com", response.getEmail());
        assertEquals("John Doe", response.getName());
        assertEquals("+1234567890", response.getPhone());
        assertEquals("Computer Science", response.getDegree());
        assertEquals(2022, response.getGraduationYear());
        assertTrue(response.getIsActive());
        
        verify(candidateRepository).findByEmail("john.doe@example.com");
        verify(passwordEncoder).encode("password123");
        verify(candidateRepository).save(any(Candidate.class));
    }
    
    @Test
    void registerCandidate_DuplicateEmail_ThrowsException() {
        // Given
        when(candidateRepository.findByEmail(registrationRequest.getEmail()))
            .thenReturn(Optional.of(candidate));
        
        // When & Then
        EmailAlreadyExistsException exception = assertThrows(
            EmailAlreadyExistsException.class,
            () -> candidateService.registerCandidate(registrationRequest)
        );
        
        assertEquals("Email is already registered", exception.getMessage());
        verify(candidateRepository).findByEmail("john.doe@example.com");
        verify(candidateRepository, never()).save(any(Candidate.class));
    }
    
    @Test
    void getCandidateProfile_ExistingCandidate_ReturnsResponse() {
        // Given
        when(candidateRepository.findById("candidate123"))
            .thenReturn(Optional.of(candidate));
        
        // When
        CandidateResponse response = candidateService.getCandidateProfile("candidate123");
        
        // Then
        assertNotNull(response);
        assertEquals("candidate123", response.getId());
        assertEquals("john.doe@example.com", response.getEmail());
        assertEquals("John Doe", response.getName());
        
        verify(candidateRepository).findById("candidate123");
    }
    
    @Test
    void getCandidateProfile_NonExistentCandidate_ThrowsException() {
        // Given
        when(candidateRepository.findById("nonexistent"))
            .thenReturn(Optional.empty());
        
        // When & Then
        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> candidateService.getCandidateProfile("nonexistent")
        );
        
        assertEquals("Candidate not found with id: nonexistent", exception.getMessage());
        verify(candidateRepository).findById("nonexistent");
    }
    
    @Test
    void getCandidateByEmail_ExistingCandidate_ReturnsResponse() {
        // Given
        when(candidateRepository.findByEmail("john.doe@example.com"))
            .thenReturn(Optional.of(candidate));
        
        // When
        CandidateResponse response = candidateService.getCandidateByEmail("john.doe@example.com");
        
        // Then
        assertNotNull(response);
        assertEquals("john.doe@example.com", response.getEmail());
        assertEquals("John Doe", response.getName());
        
        verify(candidateRepository).findByEmail("john.doe@example.com");
    }
    
    @Test
    void updateCandidateProfile_ValidRequest_ReturnsUpdatedResponse() {
        // Given
        CandidateProfileUpdateRequest updateRequest = new CandidateProfileUpdateRequest();
        updateRequest.setName("John Updated Doe");
        updateRequest.setPhone("+1987654321");
        updateRequest.setLinkedinProfile("https://linkedin.com/in/johnupdated");
        updateRequest.setPortfolioUrl("https://johnupdated.dev");
        
        Candidate updatedCandidate = new Candidate(candidate.getEmail(), candidate.getPassword(),
            "John Updated Doe", "+1987654321", candidate.getDegree(), candidate.getGraduationYear());
        updatedCandidate.setId(candidate.getId());
        updatedCandidate.setLinkedinProfile("https://linkedin.com/in/johnupdated");
        updatedCandidate.setPortfolioUrl("https://johnupdated.dev");
        updatedCandidate.setCreatedAt(candidate.getCreatedAt());
        updatedCandidate.setUpdatedAt(LocalDateTime.now());
        
        when(candidateRepository.findById("candidate123"))
            .thenReturn(Optional.of(candidate));
        when(candidateRepository.save(any(Candidate.class)))
            .thenReturn(updatedCandidate);
        
        // When
        CandidateResponse response = candidateService.updateCandidateProfile("candidate123", updateRequest);
        
        // Then
        assertNotNull(response);
        assertEquals("John Updated Doe", response.getName());
        assertEquals("+1987654321", response.getPhone());
        assertEquals("https://linkedin.com/in/johnupdated", response.getLinkedinProfile());
        assertEquals("https://johnupdated.dev", response.getPortfolioUrl());
        
        verify(candidateRepository).findById("candidate123");
        verify(candidateRepository).save(any(Candidate.class));
    }
    
    @Test
    void updateResumeUrl_ValidRequest_ReturnsUpdatedResponse() {
        // Given
        String resumeUrl = "https://s3.amazonaws.com/resumes/john-doe-resume.pdf";
        Candidate updatedCandidate = new Candidate(candidate.getEmail(), candidate.getPassword(),
            candidate.getName(), candidate.getPhone(), candidate.getDegree(), candidate.getGraduationYear());
        updatedCandidate.setId(candidate.getId());
        updatedCandidate.setResumeUrl(resumeUrl);
        updatedCandidate.setCreatedAt(candidate.getCreatedAt());
        updatedCandidate.setUpdatedAt(LocalDateTime.now());
        
        when(candidateRepository.findById("candidate123"))
            .thenReturn(Optional.of(candidate));
        when(candidateRepository.save(any(Candidate.class)))
            .thenReturn(updatedCandidate);
        
        // When
        CandidateResponse response = candidateService.updateResumeUrl("candidate123", resumeUrl);
        
        // Then
        assertNotNull(response);
        assertEquals(resumeUrl, response.getResumeUrl());
        
        verify(candidateRepository).findById("candidate123");
        verify(candidateRepository).save(any(Candidate.class));
    }
    
    @Test
    void existsByEmail_ExistingEmail_ReturnsTrue() {
        // Given
        when(candidateRepository.findByEmail("john.doe@example.com"))
            .thenReturn(Optional.of(candidate));
        
        // When
        boolean exists = candidateService.existsByEmail("john.doe@example.com");
        
        // Then
        assertTrue(exists);
        verify(candidateRepository).findByEmail("john.doe@example.com");
    }
    
    @Test
    void existsByEmail_NonExistentEmail_ReturnsFalse() {
        // Given
        when(candidateRepository.findByEmail("nonexistent@example.com"))
            .thenReturn(Optional.empty());
        
        // When
        boolean exists = candidateService.existsByEmail("nonexistent@example.com");
        
        // Then
        assertFalse(exists);
        verify(candidateRepository).findByEmail("nonexistent@example.com");
    }
}