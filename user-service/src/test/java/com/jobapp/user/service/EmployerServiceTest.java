package com.jobapp.user.service;

import com.jobapp.user.dto.EmployerRegistrationRequest;
import com.jobapp.user.dto.EmployerProfileUpdateRequest;
import com.jobapp.user.dto.EmployerResponse;
import com.jobapp.user.model.Employer;
import com.jobapp.user.repository.EmployerRepository;
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
class EmployerServiceTest {

    @Mock
    private EmployerRepository employerRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private EmployerService employerService;

    private EmployerRegistrationRequest registrationRequest;
    private EmployerProfileUpdateRequest updateRequest;
    private Employer testEmployer;

    @BeforeEach
    void setUp() {
        registrationRequest = new EmployerRegistrationRequest();
        registrationRequest.setEmail("company@example.com");
        registrationRequest.setPassword("password123");
        registrationRequest.setCompanyName("Tech Corp");
        registrationRequest.setWebsite("https://techcorp.com");
        registrationRequest.setDescription("Leading technology company");
        registrationRequest.setAddress("123 Tech Street, San Francisco, CA");

        updateRequest = new EmployerProfileUpdateRequest();
        updateRequest.setCompanyName("Updated Tech Corp");
        updateRequest.setWebsite("https://updated-techcorp.com");
        updateRequest.setDescription("Updated description");
        updateRequest.setAddress("456 Updated Street, San Francisco, CA");

        testEmployer = new Employer();
        testEmployer.setId("employer123");
        testEmployer.setEmail("company@example.com");
        testEmployer.setPassword("encodedPassword");
        testEmployer.setCompanyName("Tech Corp");
        testEmployer.setWebsite("https://techcorp.com");
        testEmployer.setDescription("Leading technology company");
        testEmployer.setAddress("123 Tech Street, San Francisco, CA");
        testEmployer.setIsApproved(false);
        testEmployer.setIsActive(true);
        testEmployer.setCreatedAt(LocalDateTime.now());
        testEmployer.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void registerEmployer_ValidRequest_ReturnsResponse() {
        // Given
        when(employerRepository.findByEmail(registrationRequest.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(registrationRequest.getPassword())).thenReturn("encodedPassword");
        when(employerRepository.save(any(Employer.class))).thenReturn(testEmployer);

        // When
        EmployerResponse response = employerService.registerEmployer(registrationRequest);

        // Then
        assertNotNull(response);
        assertEquals(testEmployer.getId(), response.getId());
        assertEquals(testEmployer.getEmail(), response.getEmail());
        assertEquals(testEmployer.getCompanyName(), response.getCompanyName());
        assertEquals(testEmployer.getWebsite(), response.getWebsite());
        assertFalse(response.getIsApproved()); // New employers should not be approved by default
        assertTrue(response.getIsActive());

        verify(employerRepository).findByEmail(registrationRequest.getEmail());
        verify(passwordEncoder).encode(registrationRequest.getPassword());
        verify(employerRepository).save(any(Employer.class));
    }

    @Test
    void registerEmployer_EmailAlreadyExists_ThrowsException() {
        // Given
        when(employerRepository.findByEmail(registrationRequest.getEmail())).thenReturn(Optional.of(testEmployer));

        // When & Then
        EmailAlreadyExistsException exception = assertThrows(EmailAlreadyExistsException.class,
            () -> employerService.registerEmployer(registrationRequest));
        assertEquals("Email is already registered", exception.getMessage());

        verify(employerRepository).findByEmail(registrationRequest.getEmail());
        verify(employerRepository, never()).save(any(Employer.class));
    }

    @Test
    void getEmployerProfile_ValidId_ReturnsResponse() {
        // Given
        String employerId = "employer123";
        when(employerRepository.findById(employerId)).thenReturn(Optional.of(testEmployer));

        // When
        EmployerResponse response = employerService.getEmployerProfile(employerId);

        // Then
        assertNotNull(response);
        assertEquals(testEmployer.getId(), response.getId());
        assertEquals(testEmployer.getEmail(), response.getEmail());
        assertEquals(testEmployer.getCompanyName(), response.getCompanyName());
        verify(employerRepository).findById(employerId);
    }

    @Test
    void getEmployerProfile_InvalidId_ThrowsException() {
        // Given
        String employerId = "invalid123";
        when(employerRepository.findById(employerId)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
            () -> employerService.getEmployerProfile(employerId));
        assertEquals("Employer not found with id: " + employerId, exception.getMessage());
        verify(employerRepository).findById(employerId);
    }

    @Test
    void updateEmployerProfile_ValidRequest_ReturnsUpdatedResponse() {
        // Given
        String employerId = "employer123";
        when(employerRepository.findById(employerId)).thenReturn(Optional.of(testEmployer));
        when(employerRepository.save(any(Employer.class))).thenReturn(testEmployer);

        // When
        EmployerResponse response = employerService.updateEmployerProfile(employerId, updateRequest);

        // Then
        assertNotNull(response);
        verify(employerRepository).findById(employerId);
        verify(employerRepository).save(any(Employer.class));
    }

    @Test
    void updateEmployerProfile_InvalidId_ThrowsException() {
        // Given
        String employerId = "invalid123";
        when(employerRepository.findById(employerId)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
            () -> employerService.updateEmployerProfile(employerId, updateRequest));
        assertEquals("Employer not found with id: " + employerId, exception.getMessage());
        verify(employerRepository).findById(employerId);
        verify(employerRepository, never()).save(any(Employer.class));
    }

    @Test
    void approveEmployer_ValidId_ReturnsApprovedEmployer() {
        // Given
        String employerId = "employer123";
        testEmployer.setIsApproved(false);
        when(employerRepository.findById(employerId)).thenReturn(Optional.of(testEmployer));
        when(employerRepository.save(any(Employer.class))).thenReturn(testEmployer);

        // When
        EmployerResponse response = employerService.approveEmployer(employerId);

        // Then
        assertNotNull(response);
        verify(employerRepository).findById(employerId);
        verify(employerRepository).save(any(Employer.class));
    }

    @Test
    void rejectEmployer_ValidId_ReturnsRejectedEmployer() {
        // Given
        String employerId = "employer123";
        testEmployer.setIsApproved(true);
        when(employerRepository.findById(employerId)).thenReturn(Optional.of(testEmployer));
        when(employerRepository.save(any(Employer.class))).thenReturn(testEmployer);

        // When
        EmployerResponse response = employerService.rejectEmployer(employerId);

        // Then
        assertNotNull(response);
        verify(employerRepository).findById(employerId);
        verify(employerRepository).save(any(Employer.class));
    }

    @Test
    void existsByEmail_EmailExists_ReturnsTrue() {
        // Given
        String email = "existing@example.com";
        when(employerRepository.findByEmail(email)).thenReturn(Optional.of(testEmployer));

        // When
        boolean result = employerService.existsByEmail(email);

        // Then
        assertTrue(result);
        verify(employerRepository).findByEmail(email);
    }

    @Test
    void existsByEmail_EmailNotExists_ReturnsFalse() {
        // Given
        String email = "nonexistent@example.com";
        when(employerRepository.findByEmail(email)).thenReturn(Optional.empty());

        // When
        boolean result = employerService.existsByEmail(email);

        // Then
        assertFalse(result);
        verify(employerRepository).findByEmail(email);
    }
}