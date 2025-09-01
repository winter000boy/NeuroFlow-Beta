package com.jobapp.user.service;

import com.jobapp.user.dto.EmployerRegistrationRequest;
import com.jobapp.user.dto.EmployerProfileUpdateRequest;
import com.jobapp.user.dto.EmployerResponse;
import com.jobapp.user.model.Employer;
import com.jobapp.user.repository.EmployerRepository;
import com.jobapp.user.exception.ResourceNotFoundException;
import com.jobapp.user.exception.EmailAlreadyExistsException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Pageable;

/**
 * Service class for employer management
 * Requirements: 3.1, 3.4
 */
@Service
@Transactional
public class EmployerService {
    
    @Autowired
    private EmployerRepository employerRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    /**
     * Register a new employer
     * Requirements: 3.1
     */
    public EmployerResponse registerEmployer(EmployerRegistrationRequest request) {
        // Check if email already exists
        if (employerRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("Email is already registered");
        }
        
        // Create new employer
        Employer employer = new Employer();
        employer.setEmail(request.getEmail());
        employer.setPassword(passwordEncoder.encode(request.getPassword()));
        employer.setCompanyName(request.getCompanyName());
        employer.setWebsite(request.getWebsite());
        employer.setDescription(request.getDescription());
        employer.setAddress(request.getAddress());
        employer.setContactPerson(request.getContactPerson());
        employer.setContactPhone(request.getContactPhone());
        employer.setIsApproved(false); // Requires admin approval
        employer.setIsActive(true);
        employer.setCreatedAt(LocalDateTime.now());
        employer.setUpdatedAt(LocalDateTime.now());
        
        Employer savedEmployer = employerRepository.save(employer);
        return convertToResponse(savedEmployer);
    }
    
    /**
     * Get employer profile by ID
     * Requirements: 3.1
     */
    @Transactional(readOnly = true)
    public EmployerResponse getEmployerProfile(String employerId) {
        Employer employer = employerRepository.findById(employerId)
            .orElseThrow(() -> new ResourceNotFoundException("Employer not found with id: " + employerId));
        
        return convertToResponse(employer);
    }
    
    /**
     * Get employer profile by email
     * Requirements: 3.1
     */
    @Transactional(readOnly = true)
    public EmployerResponse getEmployerByEmail(String email) {
        Employer employer = employerRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("Employer not found with email: " + email));
        
        return convertToResponse(employer);
    }
    
    /**
     * Update employer profile
     * Requirements: 3.4
     */
    public EmployerResponse updateEmployerProfile(String employerId, EmployerProfileUpdateRequest request) {
        Employer employer = employerRepository.findById(employerId)
            .orElseThrow(() -> new ResourceNotFoundException("Employer not found with id: " + employerId));
        
        // Update fields if provided
        if (request.getCompanyName() != null && !request.getCompanyName().trim().isEmpty()) {
            employer.setCompanyName(request.getCompanyName());
        }
        
        if (request.getWebsite() != null) {
            employer.setWebsite(request.getWebsite().trim().isEmpty() ? null : request.getWebsite());
        }
        
        if (request.getDescription() != null) {
            employer.setDescription(request.getDescription().trim().isEmpty() ? null : request.getDescription());
        }
        
        if (request.getAddress() != null) {
            employer.setAddress(request.getAddress().trim().isEmpty() ? null : request.getAddress());
        }
        
        if (request.getContactPerson() != null) {
            employer.setContactPerson(request.getContactPerson().trim().isEmpty() ? null : request.getContactPerson());
        }
        
        if (request.getContactPhone() != null) {
            employer.setContactPhone(request.getContactPhone().trim().isEmpty() ? null : request.getContactPhone());
        }
        
        employer.setUpdatedAt(LocalDateTime.now());
        
        Employer updatedEmployer = employerRepository.save(employer);
        return convertToResponse(updatedEmployer);
    }
    
    /**
     * Update employer logo URL
     * Requirements: 3.4
     */
    public EmployerResponse updateLogoUrl(String employerId, String logoUrl) {
        Employer employer = employerRepository.findById(employerId)
            .orElseThrow(() -> new ResourceNotFoundException("Employer not found with id: " + employerId));
        
        employer.setLogoUrl(logoUrl);
        employer.setUpdatedAt(LocalDateTime.now());
        
        Employer updatedEmployer = employerRepository.save(employer);
        return convertToResponse(updatedEmployer);
    }
    
    /**
     * Approve employer account
     * Requirements: 3.4
     */
    public EmployerResponse approveEmployer(String employerId, String adminId) {
        Employer employer = employerRepository.findById(employerId)
            .orElseThrow(() -> new ResourceNotFoundException("Employer not found with id: " + employerId));
        
        employer.approve(adminId);
        
        Employer updatedEmployer = employerRepository.save(employer);
        return convertToResponse(updatedEmployer);
    }
    
    /**
     * Reject employer account
     * Requirements: 3.4
     */
    public EmployerResponse rejectEmployer(String employerId, String rejectionReason) {
        Employer employer = employerRepository.findById(employerId)
            .orElseThrow(() -> new ResourceNotFoundException("Employer not found with id: " + employerId));
        
        employer.reject(rejectionReason);
        
        Employer updatedEmployer = employerRepository.save(employer);
        return convertToResponse(updatedEmployer);
    }
    
    /**
     * Get all pending employers (for admin approval)
     * Requirements: 3.4
     */
    @Transactional(readOnly = true)
    public List<EmployerResponse> getPendingEmployers() {
        List<Employer> pendingEmployers = employerRepository.findPendingApproval(org.springframework.data.domain.Pageable.unpaged()).getContent();
        return pendingEmployers.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Get all approved employers
     * Requirements: 3.1
     */
    @Transactional(readOnly = true)
    public List<EmployerResponse> getApprovedEmployers() {
        List<Employer> approvedEmployers = employerRepository.findActiveApprovedEmployers(org.springframework.data.domain.Pageable.unpaged()).getContent();
        return approvedEmployers.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Check if employer exists by email
     */
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return employerRepository.findByEmail(email).isPresent();
    }
    
    /**
     * Check if employer can post jobs
     */
    @Transactional(readOnly = true)
    public boolean canPostJobs(String employerId) {
        Employer employer = employerRepository.findById(employerId)
            .orElseThrow(() -> new ResourceNotFoundException("Employer not found with id: " + employerId));
        
        return employer.canPostJobs();
    }
    
    /**
     * Convert Employer entity to EmployerResponse DTO
     */
    private EmployerResponse convertToResponse(Employer employer) {
        return new EmployerResponse(
            employer.getId(),
            employer.getEmail(),
            employer.getCompanyName(),
            employer.getWebsite(),
            employer.getDescription(),
            employer.getLogoUrl(),
            employer.getAddress(),
            employer.getContactPerson(),
            employer.getContactPhone(),
            employer.getIsApproved(),
            employer.getIsActive(),
            employer.getApprovalDate(),
            employer.getApprovedBy(),
            employer.getRejectionReason(),
            employer.getCreatedAt(),
            employer.getUpdatedAt()
        );
    }
}