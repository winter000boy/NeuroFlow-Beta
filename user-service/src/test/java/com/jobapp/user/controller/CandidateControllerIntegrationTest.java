package com.jobapp.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobapp.user.dto.CandidateRegistrationRequest;
import com.jobapp.user.dto.CandidateProfileUpdateRequest;
import com.jobapp.user.model.Candidate;
import com.jobapp.user.repository.CandidateRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

/**
 * Integration tests for CandidateController
 * Requirements: 1.1, 1.2, 1.4, 1.5
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebMvc
@TestPropertySource(locations = "classpath:application-test.yml")
public class CandidateControllerIntegrationTest {
    
    @Autowired
    private WebApplicationContext webApplicationContext;
    
    @Autowired
    private CandidateRepository candidateRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private MockMvc mockMvc;
    
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        candidateRepository.deleteAll();
    }
    
    @Test
    void registerCandidate_ValidRequest_ReturnsCreated() throws Exception {
        // Given
        CandidateRegistrationRequest request = new CandidateRegistrationRequest(
            "john.doe@example.com",
            "password123",
            "John Doe",
            "+1234567890",
            "Computer Science",
            2022
        );
        
        // When & Then
        mockMvc.perform(post("/api/candidates/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email", is("john.doe@example.com")))
                .andExpect(jsonPath("$.name", is("John Doe")))
                .andExpect(jsonPath("$.phone", is("+1234567890")))
                .andExpect(jsonPath("$.degree", is("Computer Science")))
                .andExpect(jsonPath("$.graduationYear", is(2022)))
                .andExpect(jsonPath("$.isActive", is(true)))
                .andExpect(jsonPath("$.password").doesNotExist()); // Password should not be returned
    }
    
    @Test
    void registerCandidate_DuplicateEmail_ReturnsConflict() throws Exception {
        // Given
        Candidate existingCandidate = new Candidate(
            "john.doe@example.com",
            passwordEncoder.encode("password123"),
            "John Doe",
            "+1234567890",
            "Computer Science",
            2022
        );
        candidateRepository.save(existingCandidate);
        
        CandidateRegistrationRequest request = new CandidateRegistrationRequest(
            "john.doe@example.com",
            "password456",
            "Jane Doe",
            "+0987654321",
            "Software Engineering",
            2023
        );
        
        // When & Then
        mockMvc.perform(post("/api/candidates/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code", is("EMAIL_ALREADY_EXISTS")))
                .andExpect(jsonPath("$.message", is("Email is already registered")));
    }
    
    @Test
    void registerCandidate_InvalidData_ReturnsBadRequest() throws Exception {
        // Given
        CandidateRegistrationRequest request = new CandidateRegistrationRequest(
            "invalid-email",
            "123", // Too short password
            "", // Empty name
            "invalid-phone",
            "",
            1900 // Invalid graduation year
        );
        
        // When & Then
        mockMvc.perform(post("/api/candidates/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code", is("VALIDATION_ERROR")))
                .andExpect(jsonPath("$.details").exists());
    }
    
    @Test
    void getCandidateProfile_ExistingCandidate_ReturnsOk() throws Exception {
        // Given
        Candidate candidate = new Candidate(
            "john.doe@example.com",
            passwordEncoder.encode("password123"),
            "John Doe",
            "+1234567890",
            "Computer Science",
            2022
        );
        candidate.setLinkedinProfile("https://linkedin.com/in/johndoe");
        candidate.setPortfolioUrl("https://johndoe.dev");
        Candidate savedCandidate = candidateRepository.save(candidate);
        
        // When & Then
        mockMvc.perform(get("/api/candidates/{candidateId}", savedCandidate.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(savedCandidate.getId())))
                .andExpect(jsonPath("$.email", is("john.doe@example.com")))
                .andExpect(jsonPath("$.name", is("John Doe")))
                .andExpect(jsonPath("$.linkedinProfile", is("https://linkedin.com/in/johndoe")))
                .andExpect(jsonPath("$.portfolioUrl", is("https://johndoe.dev")))
                .andExpect(jsonPath("$.password").doesNotExist());
    }
    
    @Test
    void getCandidateProfile_NonExistentCandidate_ReturnsNotFound() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/candidates/{candidateId}", "nonexistent-id"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code", is("RESOURCE_NOT_FOUND")));
    }
    
    @Test
    void updateCandidateProfile_ValidRequest_ReturnsOk() throws Exception {
        // Given
        Candidate candidate = new Candidate(
            "john.doe@example.com",
            passwordEncoder.encode("password123"),
            "John Doe",
            "+1234567890",
            "Computer Science",
            2022
        );
        Candidate savedCandidate = candidateRepository.save(candidate);
        
        CandidateProfileUpdateRequest updateRequest = new CandidateProfileUpdateRequest();
        updateRequest.setName("John Updated Doe");
        updateRequest.setPhone("+1987654321");
        updateRequest.setLinkedinProfile("https://linkedin.com/in/johnupdated");
        updateRequest.setPortfolioUrl("https://johnupdated.dev");
        
        // When & Then
        mockMvc.perform(put("/api/candidates/{candidateId}/profile", savedCandidate.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("John Updated Doe")))
                .andExpect(jsonPath("$.phone", is("+1987654321")))
                .andExpect(jsonPath("$.linkedinProfile", is("https://linkedin.com/in/johnupdated")))
                .andExpect(jsonPath("$.portfolioUrl", is("https://johnupdated.dev")));
    }
    
    @Test
    void updateResumeUrl_ValidRequest_ReturnsOk() throws Exception {
        // Given
        Candidate candidate = new Candidate(
            "john.doe@example.com",
            passwordEncoder.encode("password123"),
            "John Doe",
            "+1234567890",
            "Computer Science",
            2022
        );
        Candidate savedCandidate = candidateRepository.save(candidate);
        String resumeUrl = "https://s3.amazonaws.com/resumes/john-doe-resume.pdf";
        
        // When & Then
        mockMvc.perform(put("/api/candidates/{candidateId}/resume", savedCandidate.getId())
                .param("resumeUrl", resumeUrl))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resumeUrl", is(resumeUrl)));
    }
    
    @Test
    void checkEmailExists_ExistingEmail_ReturnsExists() throws Exception {
        // Given
        Candidate candidate = new Candidate(
            "john.doe@example.com",
            passwordEncoder.encode("password123"),
            "John Doe",
            "+1234567890",
            "Computer Science",
            2022
        );
        candidateRepository.save(candidate);
        
        // When & Then
        mockMvc.perform(get("/api/candidates/exists/{email}", "john.doe@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Email already exists")));
    }
    
    @Test
    void checkEmailExists_NonExistentEmail_ReturnsAvailable() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/candidates/exists/{email}", "nonexistent@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Email is available")));
    }
}