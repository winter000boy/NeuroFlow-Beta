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

@ExtendWith(MockitoExtension.class)
class CandidateServiceTest {

    @Mock
    private CandidateRepository candidateRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private CandidateService candidateService;

    private CandidateRegistrationRequest registrationRequest;
    private CandidateProfileUpdateRequest updateRequest;
    private Candidate testCandidate;

    @BeforeEach
    void setUp() {
        registrationRequest = new CandidateRegistrationRequest();
        registrationRequest.setEmail("john.doe@example.com");
        registrationRequest.setPassword("password123");
        registrationRequest.setName("John Doe");
        registrationRequest.setPhone("1234567890");
        registrationRequest.setDegree("Computer Science");
        registrationRequest.setGraduationYear(2022);

        updateRequest = new CandidateProfileUpdateRequest();
        updateRequest.setName("John Updated");
        updateRequest.setPhone("0987654321");
        updateRequest.setDegree("Software Engineering");
        updateRequest.setGraduationYear(2023);
        updateRequest.setLinkedinProfile("https://linkedin.com/in/johndoe");
        updateRequest.setPortfolioUrl("https://johndoe.dev");

        testCandidate = new Candidate();
        testCandidate.setId("candidate123");
        testCandidate.setEmail("john.doe@example.com");
        testCandidate.setPassword("encodedPassword");
        testCandidate.setName("John Doe");
        testCandidate.setPhone("1234567890");
        testCandidate.setDegree("Computer Science");
        testCandidate.setGraduationYear(2022);
        testCandidate.setIsActive(true);
        testCandidate.setCreatedAt(LocalDateTime.now());
        testCandidate.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void registerCandidate_ValidRequest_ReturnsResponse() {
        // Given
        when(candidateRepository.findByEmail(registrationRequest.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(registrationRequest.getPassword())).thenReturn("encodedPassword");
        when(candidateRepository.save(any(Candidate.class))).thenReturn(testCandidate);

        // When
        CandidateResponse response = candidateService.registerCandidate(registrationRequest);

        // Then
        assertNotNull(response);
        assertEquals(testCandidate.getId(), response.getId());
        assertEquals(testCandidate.getEmail(), response.getEmail());
        assertEquals(testCandidate.getName(), response.getName());
        assertEquals(testCandidate.getPhone(), response.getPhone());
        assertEquals(testCandidate.getDegree(), response.getDegree());
        assertEquals(testCandidate.getGraduationYear(), response.getGraduationYear());
        assertTrue(response.getIsActive());

        verify(candidateRepository).findByEmail(registrationRequest.getEmail());
        verify(passwordEncoder).encode(registrationRequest.getPassword());
        verify(candidateRepository).save(any(Candidate.class));
    }

    @Test
    void registerCandidate_EmailAlreadyExists_ThrowsException() {
        // Given
        when(candidateRepository.findByEmail(registrationRequest.getEmail())).thenReturn(Optional.of(testCandidate));

        // When & Then
        EmailAlreadyExistsException exception = assertThrows(EmailAlreadyExistsException.class,
            () -> candidateService.registerCandidate(registrationRequest));
        assertEquals("Email is already registered", exception.getMessage());

        verify(candidateRepository).findByEmail(registrationRequest.getEmail());
        verify(candidateRepository, never()).save(any(Candidate.class));
    }

    @Test
    void getCandidateProfile_ValidId_ReturnsResponse() {
        // Given
        String candidateId = "candidate123";
        when(candidateRepository.findById(candidateId)).thenReturn(Optional.of(testCandidate));

        // When
        CandidateResponse response = candidateService.getCandidateProfile(candidateId);

        // Then
        assertNotNull(response);
        assertEquals(testCandidate.getId(), response.getId());
        assertEquals(testCandidate.getEmail(), response.getEmail());
        assertEquals(testCandidate.getName(), response.getName());
        verify(candidateRepository).findById(candidateId);
    }

    @Test
    void getCandidateProfile_InvalidId_ThrowsException() {
        // Given
        String candidateId = "invalid123";
        when(candidateRepository.findById(candidateId)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
            () -> candidateService.getCandidateProfile(candidateId));
        assertEquals("Candidate not found with id: " + candidateId, exception.getMessage());
        verify(candidateRepository).findById(candidateId);
    }

    @Test
    void getCandidateByEmail_ValidEmail_ReturnsResponse() {
        // Given
        String email = "john.doe@example.com";
        when(candidateRepository.findByEmail(email)).thenReturn(Optional.of(testCandidate));

        // When
        CandidateResponse response = candidateService.getCandidateByEmail(email);

        // Then
        assertNotNull(response);
        assertEquals(testCandidate.getId(), response.getId());
        assertEquals(testCandidate.getEmail(), response.getEmail());
        verify(candidateRepository).findByEmail(email);
    }

    @Test
    void getCandidateByEmail_InvalidEmail_ThrowsException() {
        // Given
        String email = "invalid@example.com";
        when(candidateRepository.findByEmail(email)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
            () -> candidateService.getCandidateByEmail(email));
        assertEquals("Candidate not found with email: " + email, exception.getMessage());
        verify(candidateRepository).findByEmail(email);
    }

    @Test
    void updateCandidateProfile_ValidRequest_ReturnsUpdatedResponse() {
        // Given
        String candidateId = "candidate123";
        when(candidateRepository.findById(candidateId)).thenReturn(Optional.of(testCandidate));
        when(candidateRepository.save(any(Candidate.class))).thenReturn(testCandidate);

        // When
        CandidateResponse response = candidateService.updateCandidateProfile(candidateId, updateRequest);

        // Then
        assertNotNull(response);
        verify(candidateRepository).findById(candidateId);
        verify(candidateRepository).save(any(Candidate.class));
    }

    @Test
    void updateCandidateProfile_InvalidId_ThrowsException() {
        // Given
        String candidateId = "invalid123";
        when(candidateRepository.findById(candidateId)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
            () -> candidateService.updateCandidateProfile(candidateId, updateRequest));
        assertEquals("Candidate not found with id: " + candidateId, exception.getMessage());
        verify(candidateRepository).findById(candidateId);
        verify(candidateRepository, never()).save(any(Candidate.class));
    }

    @Test
    void updateCandidateProfile_PartialUpdate_UpdatesOnlyProvidedFields() {
        // Given
        String candidateId = "candidate123";
        CandidateProfileUpdateRequest partialRequest = new CandidateProfileUpdateRequest();
        partialRequest.setName("Updated Name");
        // Other fields are null

        when(candidateRepository.findById(candidateId)).thenReturn(Optional.of(testCandidate));
        when(candidateRepository.save(any(Candidate.class))).thenReturn(testCandidate);

        // When
        CandidateResponse response = candidateService.updateCandidateProfile(candidateId, partialRequest);

        // Then
        assertNotNull(response);
        verify(candidateRepository).findById(candidateId);
        verify(candidateRepository).save(any(Candidate.class));
    }

    @Test
    void updateResumeUrl_ValidRequest_ReturnsUpdatedResponse() {
        // Given
        String candidateId = "candidate123";
        String resumeUrl = "https://example.com/resume.pdf";
        when(candidateRepository.findById(candidateId)).thenReturn(Optional.of(testCandidate));
        when(candidateRepository.save(any(Candidate.class))).thenReturn(testCandidate);

        // When
        CandidateResponse response = candidateService.updateResumeUrl(candidateId, resumeUrl);

        // Then
        assertNotNull(response);
        verify(candidateRepository).findById(candidateId);
        verify(candidateRepository).save(any(Candidate.class));
    }

    @Test
    void updateResumeUrl_InvalidId_ThrowsException() {
        // Given
        String candidateId = "invalid123";
        String resumeUrl = "https://example.com/resume.pdf";
        when(candidateRepository.findById(candidateId)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
            () -> candidateService.updateResumeUrl(candidateId, resumeUrl));
        assertEquals("Candidate not found with id: " + candidateId, exception.getMessage());
        verify(candidateRepository).findById(candidateId);
        verify(candidateRepository, never()).save(any(Candidate.class));
    }

    @Test
    void existsByEmail_EmailExists_ReturnsTrue() {
        // Given
        String email = "existing@example.com";
        when(candidateRepository.findByEmail(email)).thenReturn(Optional.of(testCandidate));

        // When
        boolean result = candidateService.existsByEmail(email);

        // Then
        assertTrue(result);
        verify(candidateRepository).findByEmail(email);
    }

    @Test
    void existsByEmail_EmailNotExists_ReturnsFalse() {
        // Given
        String email = "nonexistent@example.com";
        when(candidateRepository.findByEmail(email)).thenReturn(Optional.empty());

        // When
        boolean result = candidateService.existsByEmail(email);

        // Then
        assertFalse(result);
        verify(candidateRepository).findByEmail(email);
    }
}