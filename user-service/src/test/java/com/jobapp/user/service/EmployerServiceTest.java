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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for EmployerService
 * Requirements: 3.1, 3.4
 */
@ExtendWith(MockitoExtension.class)
class EmployerServiceTest {
    
    @Mock
    private EmployerRepository employerRepository;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @InjectMocks
    private EmployerService employerService;
    
    private EmployerRegistrationRequest registrationRequest;
    private Employer employer;
    
    @BeforeEach
    void setUp() {
        registrationRequest = new EmployerRegistrationRequest(
            "hr@techcorp.com",
            "password123",
            "Tech Corp",
            "https://techcorp.com",
            "Leading technology company"
        );
        registrationRequest.setAddress("123 Tech Street");
        registrationRequest.setContactPerson("John HR Manager");
        registrationRequest.setContactPhone("+1234567890");
        
        employer = new Employer(
            "hr@techcorp.com",
            "encodedPassword",
            "Tech Corp",
            "https://techcorp.com",
            "Leading technology company"
        );
        employer.setId("employer123");
        employer.setAddress("123 Tech Street");
        employer.setContactPerson("John HR Manager");
        employer.setContactPhone("+1234567890");
        employer.setCreatedAt(LocalDateTime.now());
        employer.setUpdatedAt(LocalDateTime.now());
    }
    
    @Test
    void registerEmployer_ValidRequest_ReturnsResponse() {
        // Given
        when(employerRepository.findByEmail(registrationRequest.getEmail()))
            .thenReturn(Optional.empty());
        when(passwordEncoder.encode(registrationRequest.getPassword()))
            .thenReturn("encodedPassword");
        when(employerRepository.save(any(Employer.class)))
            .thenReturn(employer);
        
        // When
        EmployerResponse response = employerService.registerEmployer(registrationRequest);
        
        // Then
        assertNotNull(response);
        assertEquals("hr@techcorp.com", response.getEmail());
        assertEquals("Tech Corp", response.getCompanyName());
        assertEquals("https://techcorp.com", response.getWebsite());
        assertEquals("Leading technology company", response.getDescription());
        assertEquals("123 Tech Street", response.getAddress());
        assertEquals("John HR Manager", response.getContactPerson());
        assertEquals("+1234567890", response.getContactPhone());
        assertFalse(response.getIsApproved()); // Should require approval
        assertTrue(response.getIsActive());
        
        verify(employerRepository).findByEmail("hr@techcorp.com");
        verify(passwordEncoder).encode("password123");
        verify(employerRepository).save(any(Employer.class));
    }
    
    @Test
    void registerEmployer_DuplicateEmail_ThrowsException() {
        // Given
        when(employerRepository.findByEmail(registrationRequest.getEmail()))
            .thenReturn(Optional.of(employer));
        
        // When & Then
        EmailAlreadyExistsException exception = assertThrows(
            EmailAlreadyExistsException.class,
            () -> employerService.registerEmployer(registrationRequest)
        );
        
        assertEquals("Email is already registered", exception.getMessage());
        verify(employerRepository).findByEmail("hr@techcorp.com");
        verify(employerRepository, never()).save(any(Employer.class));
    }
    
    @Test
    void getEmployerProfile_ExistingEmployer_ReturnsResponse() {
        // Given
        when(employerRepository.findById("employer123"))
            .thenReturn(Optional.of(employer));
        
        // When
        EmployerResponse response = employerService.getEmployerProfile("employer123");
        
        // Then
        assertNotNull(response);
        assertEquals("employer123", response.getId());
        assertEquals("hr@techcorp.com", response.getEmail());
        assertEquals("Tech Corp", response.getCompanyName());
        
        verify(employerRepository).findById("employer123");
    }
    
    @Test
    void getEmployerProfile_NonExistentEmployer_ThrowsException() {
        // Given
        when(employerRepository.findById("nonexistent"))
            .thenReturn(Optional.empty());
        
        // When & Then
        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> employerService.getEmployerProfile("nonexistent")
        );
        
        assertEquals("Employer not found with id: nonexistent", exception.getMessage());
        verify(employerRepository).findById("nonexistent");
    }
    
    @Test
    void getEmployerByEmail_ExistingEmployer_ReturnsResponse() {
        // Given
        when(employerRepository.findByEmail("hr@techcorp.com"))
            .thenReturn(Optional.of(employer));
        
        // When
        EmployerResponse response = employerService.getEmployerByEmail("hr@techcorp.com");
        
        // Then
        assertNotNull(response);
        assertEquals("hr@techcorp.com", response.getEmail());
        assertEquals("Tech Corp", response.getCompanyName());
        
        verify(employerRepository).findByEmail("hr@techcorp.com");
    }
    
    @Test
    void updateEmployerProfile_ValidRequest_ReturnsUpdatedResponse() {
        // Given
        EmployerProfileUpdateRequest updateRequest = new EmployerProfileUpdateRequest();
        updateRequest.setCompanyName("Updated Tech Corp");
        updateRequest.setWebsite("https://updated-techcorp.com");
        updateRequest.setDescription("Updated leading technology company");
        updateRequest.setAddress("456 Updated Tech Street");
        updateRequest.setContactPerson("Jane Updated HR Manager");
        updateRequest.setContactPhone("+1987654321");
        
        Employer updatedEmployer = new Employer(employer.getEmail(), employer.getPassword(),
            "Updated Tech Corp", "https://updated-techcorp.com", "Updated leading technology company");
        updatedEmployer.setId(employer.getId());
        updatedEmployer.setAddress("456 Updated Tech Street");
        updatedEmployer.setContactPerson("Jane Updated HR Manager");
        updatedEmployer.setContactPhone("+1987654321");
        updatedEmployer.setCreatedAt(employer.getCreatedAt());
        updatedEmployer.setUpdatedAt(LocalDateTime.now());
        
        when(employerRepository.findById("employer123"))
            .thenReturn(Optional.of(employer));
        when(employerRepository.save(any(Employer.class)))
            .thenReturn(updatedEmployer);
        
        // When
        EmployerResponse response = employerService.updateEmployerProfile("employer123", updateRequest);
        
        // Then
        assertNotNull(response);
        assertEquals("Updated Tech Corp", response.getCompanyName());
        assertEquals("https://updated-techcorp.com", response.getWebsite());
        assertEquals("Updated leading technology company", response.getDescription());
        assertEquals("456 Updated Tech Street", response.getAddress());
        assertEquals("Jane Updated HR Manager", response.getContactPerson());
        assertEquals("+1987654321", response.getContactPhone());
        
        verify(employerRepository).findById("employer123");
        verify(employerRepository).save(any(Employer.class));
    }
    
    @Test
    void updateLogoUrl_ValidRequest_ReturnsUpdatedResponse() {
        // Given
        String logoUrl = "https://s3.amazonaws.com/logos/techcorp-logo.png";
        Employer updatedEmployer = new Employer(employer.getEmail(), employer.getPassword(),
            employer.getCompanyName(), employer.getWebsite(), employer.getDescription());
        updatedEmployer.setId(employer.getId());
        updatedEmployer.setLogoUrl(logoUrl);
        updatedEmployer.setCreatedAt(employer.getCreatedAt());
        updatedEmployer.setUpdatedAt(LocalDateTime.now());
        
        when(employerRepository.findById("employer123"))
            .thenReturn(Optional.of(employer));
        when(employerRepository.save(any(Employer.class)))
            .thenReturn(updatedEmployer);
        
        // When
        EmployerResponse response = employerService.updateLogoUrl("employer123", logoUrl);
        
        // Then
        assertNotNull(response);
        assertEquals(logoUrl, response.getLogoUrl());
        
        verify(employerRepository).findById("employer123");
        verify(employerRepository).save(any(Employer.class));
    }
    
    @Test
    void approveEmployer_ValidRequest_ReturnsApprovedResponse() {
        // Given
        String adminId = "admin123";
        employer.setIsApproved(false);
        
        Employer approvedEmployer = new Employer(employer.getEmail(), employer.getPassword(),
            employer.getCompanyName(), employer.getWebsite(), employer.getDescription());
        approvedEmployer.setId(employer.getId());
        approvedEmployer.setIsApproved(true);
        approvedEmployer.setApprovedBy(adminId);
        approvedEmployer.setApprovalDate(LocalDateTime.now());
        approvedEmployer.setCreatedAt(employer.getCreatedAt());
        approvedEmployer.setUpdatedAt(LocalDateTime.now());
        
        when(employerRepository.findById("employer123"))
            .thenReturn(Optional.of(employer));
        when(employerRepository.save(any(Employer.class)))
            .thenReturn(approvedEmployer);
        
        // When
        EmployerResponse response = employerService.approveEmployer("employer123", adminId);
        
        // Then
        assertNotNull(response);
        assertTrue(response.getIsApproved());
        assertEquals(adminId, response.getApprovedBy());
        assertNotNull(response.getApprovalDate());
        
        verify(employerRepository).findById("employer123");
        verify(employerRepository).save(any(Employer.class));
    }
    
    @Test
    void rejectEmployer_ValidRequest_ReturnsRejectedResponse() {
        // Given
        String rejectionReason = "Incomplete company information";
        employer.setIsApproved(false);
        
        Employer rejectedEmployer = new Employer(employer.getEmail(), employer.getPassword(),
            employer.getCompanyName(), employer.getWebsite(), employer.getDescription());
        rejectedEmployer.setId(employer.getId());
        rejectedEmployer.setIsApproved(false);
        rejectedEmployer.setRejectionReason(rejectionReason);
        rejectedEmployer.setCreatedAt(employer.getCreatedAt());
        rejectedEmployer.setUpdatedAt(LocalDateTime.now());
        
        when(employerRepository.findById("employer123"))
            .thenReturn(Optional.of(employer));
        when(employerRepository.save(any(Employer.class)))
            .thenReturn(rejectedEmployer);
        
        // When
        EmployerResponse response = employerService.rejectEmployer("employer123", rejectionReason);
        
        // Then
        assertNotNull(response);
        assertFalse(response.getIsApproved());
        assertEquals(rejectionReason, response.getRejectionReason());
        
        verify(employerRepository).findById("employer123");
        verify(employerRepository).save(any(Employer.class));
    }
    
    @Test
    void getPendingEmployers_ReturnsOnlyPendingEmployers() {
        // Given
        Employer pendingEmployer1 = new Employer("hr1@techcorp.com", "pass", "Corp1", "https://corp1.com", "Desc1");
        pendingEmployer1.setId("pending1");
        pendingEmployer1.setIsApproved(false);
        pendingEmployer1.setIsActive(true);
        
        Employer pendingEmployer2 = new Employer("hr2@techcorp.com", "pass", "Corp2", "https://corp2.com", "Desc2");
        pendingEmployer2.setId("pending2");
        pendingEmployer2.setIsApproved(false);
        pendingEmployer2.setIsActive(true);
        
        List<Employer> pendingEmployers = Arrays.asList(pendingEmployer1, pendingEmployer2);
        
        when(employerRepository.findPendingApproval(any(org.springframework.data.domain.Pageable.class)))
            .thenReturn(new org.springframework.data.domain.PageImpl<>(pendingEmployers));
        
        // When
        List<EmployerResponse> response = employerService.getPendingEmployers();
        
        // Then
        assertNotNull(response);
        assertEquals(2, response.size());
        assertTrue(response.stream().allMatch(emp -> !emp.getIsApproved()));
        
        verify(employerRepository).findPendingApproval(any(org.springframework.data.domain.Pageable.class));
    }
    
    @Test
    void getApprovedEmployers_ReturnsOnlyApprovedEmployers() {
        // Given
        Employer approvedEmployer1 = new Employer("hr1@techcorp.com", "pass", "Corp1", "https://corp1.com", "Desc1");
        approvedEmployer1.setId("approved1");
        approvedEmployer1.setIsApproved(true);
        approvedEmployer1.setIsActive(true);
        
        Employer approvedEmployer2 = new Employer("hr2@techcorp.com", "pass", "Corp2", "https://corp2.com", "Desc2");
        approvedEmployer2.setId("approved2");
        approvedEmployer2.setIsApproved(true);
        approvedEmployer2.setIsActive(true);
        
        List<Employer> approvedEmployers = Arrays.asList(approvedEmployer1, approvedEmployer2);
        
        when(employerRepository.findActiveApprovedEmployers(any(org.springframework.data.domain.Pageable.class)))
            .thenReturn(new org.springframework.data.domain.PageImpl<>(approvedEmployers));
        
        // When
        List<EmployerResponse> response = employerService.getApprovedEmployers();
        
        // Then
        assertNotNull(response);
        assertEquals(2, response.size());
        assertTrue(response.stream().allMatch(emp -> emp.getIsApproved()));
        
        verify(employerRepository).findActiveApprovedEmployers(any(org.springframework.data.domain.Pageable.class));
    }
    
    @Test
    void existsByEmail_ExistingEmail_ReturnsTrue() {
        // Given
        when(employerRepository.findByEmail("hr@techcorp.com"))
            .thenReturn(Optional.of(employer));
        
        // When
        boolean exists = employerService.existsByEmail("hr@techcorp.com");
        
        // Then
        assertTrue(exists);
        verify(employerRepository).findByEmail("hr@techcorp.com");
    }
    
    @Test
    void existsByEmail_NonExistentEmail_ReturnsFalse() {
        // Given
        when(employerRepository.findByEmail("nonexistent@example.com"))
            .thenReturn(Optional.empty());
        
        // When
        boolean exists = employerService.existsByEmail("nonexistent@example.com");
        
        // Then
        assertFalse(exists);
        verify(employerRepository).findByEmail("nonexistent@example.com");
    }
    
    @Test
    void canPostJobs_ApprovedAndActiveEmployer_ReturnsTrue() {
        // Given
        employer.setIsApproved(true);
        employer.setIsActive(true);
        
        when(employerRepository.findById("employer123"))
            .thenReturn(Optional.of(employer));
        
        // When
        boolean canPost = employerService.canPostJobs("employer123");
        
        // Then
        assertTrue(canPost);
        verify(employerRepository).findById("employer123");
    }
    
    @Test
    void canPostJobs_PendingEmployer_ReturnsFalse() {
        // Given
        employer.setIsApproved(false);
        employer.setIsActive(true);
        
        when(employerRepository.findById("employer123"))
            .thenReturn(Optional.of(employer));
        
        // When
        boolean canPost = employerService.canPostJobs("employer123");
        
        // Then
        assertFalse(canPost);
        verify(employerRepository).findById("employer123");
    }
}