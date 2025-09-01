package com.jobapp.user.service;

import com.jobapp.user.dto.CandidateRegistrationRequest;
import com.jobapp.user.dto.CandidateProfileUpdateRequest;
import com.jobapp.user.dto.CandidateResponse;
import com.jobapp.user.model.Candidate;
import com.jobapp.user.repository.CandidateRepository;
import com.jobapp.user.exception.ResourceNotFoundException;
import com.jobapp.user.exception.EmailAlreadyExistsException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Service class for candidate management
 * Requirements: 1.1, 1.2, 1.4, 1.5
 */
@Service
@Transactional
public class CandidateService {
    
    @Autowired
    private CandidateRepository candidateRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    /**
     * Register a new candidate
     * Requirements: 1.1, 1.2
     */
    public CandidateResponse registerCandidate(CandidateRegistrationRequest request) {
        // Check if email already exists
        if (candidateRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("Email is already registered");
        }
        
        // Create new candidate
        Candidate candidate = new Candidate();
        candidate.setEmail(request.getEmail());
        candidate.setPassword(passwordEncoder.encode(request.getPassword()));
        candidate.setName(request.getName());
        candidate.setPhone(request.getPhone());
        candidate.setDegree(request.getDegree());
        candidate.setGraduationYear(request.getGraduationYear());
        candidate.setIsActive(true);
        candidate.setCreatedAt(LocalDateTime.now());
        candidate.setUpdatedAt(LocalDateTime.now());
        
        Candidate savedCandidate = candidateRepository.save(candidate);
        return convertToResponse(savedCandidate);
    }
    
    /**
     * Get candidate profile by ID
     * Requirements: 1.1
     */
    @Transactional(readOnly = true)
    public CandidateResponse getCandidateProfile(String candidateId) {
        Candidate candidate = candidateRepository.findById(candidateId)
            .orElseThrow(() -> new ResourceNotFoundException("Candidate not found with id: " + candidateId));
        
        return convertToResponse(candidate);
    }
    
    /**
     * Get candidate profile by email
     * Requirements: 1.1
     */
    @Transactional(readOnly = true)
    public CandidateResponse getCandidateByEmail(String email) {
        Candidate candidate = candidateRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("Candidate not found with email: " + email));
        
        return convertToResponse(candidate);
    }
    
    /**
     * Update candidate profile
     * Requirements: 1.4, 1.5
     */
    public CandidateResponse updateCandidateProfile(String candidateId, CandidateProfileUpdateRequest request) {
        Candidate candidate = candidateRepository.findById(candidateId)
            .orElseThrow(() -> new ResourceNotFoundException("Candidate not found with id: " + candidateId));
        
        // Update fields if provided
        if (request.getName() != null && !request.getName().trim().isEmpty()) {
            candidate.setName(request.getName());
        }
        
        if (request.getPhone() != null && !request.getPhone().trim().isEmpty()) {
            candidate.setPhone(request.getPhone());
        }
        
        if (request.getDegree() != null && !request.getDegree().trim().isEmpty()) {
            candidate.setDegree(request.getDegree());
        }
        
        if (request.getGraduationYear() != null) {
            candidate.setGraduationYear(request.getGraduationYear());
        }
        
        if (request.getLinkedinProfile() != null) {
            candidate.setLinkedinProfile(request.getLinkedinProfile().trim().isEmpty() ? 
                null : request.getLinkedinProfile());
        }
        
        if (request.getPortfolioUrl() != null) {
            candidate.setPortfolioUrl(request.getPortfolioUrl().trim().isEmpty() ? 
                null : request.getPortfolioUrl());
        }
        
        candidate.setUpdatedAt(LocalDateTime.now());
        
        Candidate updatedCandidate = candidateRepository.save(candidate);
        return convertToResponse(updatedCandidate);
    }
    
    /**
     * Update candidate resume URL
     * Requirements: 1.4
     */
    public CandidateResponse updateResumeUrl(String candidateId, String resumeUrl) {
        Candidate candidate = candidateRepository.findById(candidateId)
            .orElseThrow(() -> new ResourceNotFoundException("Candidate not found with id: " + candidateId));
        
        candidate.setResumeUrl(resumeUrl);
        candidate.setUpdatedAt(LocalDateTime.now());
        
        Candidate updatedCandidate = candidateRepository.save(candidate);
        return convertToResponse(updatedCandidate);
    }
    
    /**
     * Check if candidate exists by email
     */
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return candidateRepository.findByEmail(email).isPresent();
    }
    
    /**
     * Convert Candidate entity to CandidateResponse DTO
     */
    private CandidateResponse convertToResponse(Candidate candidate) {
        return new CandidateResponse(
            candidate.getId(),
            candidate.getEmail(),
            candidate.getName(),
            candidate.getPhone(),
            candidate.getDegree(),
            candidate.getGraduationYear(),
            candidate.getResumeUrl(),
            candidate.getLinkedinProfile(),
            candidate.getPortfolioUrl(),
            candidate.getIsActive(),
            candidate.getCreatedAt(),
            candidate.getUpdatedAt()
        );
    }
}