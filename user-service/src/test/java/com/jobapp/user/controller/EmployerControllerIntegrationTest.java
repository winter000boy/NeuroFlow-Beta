package com.jobapp.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobapp.user.dto.EmployerRegistrationRequest;
import com.jobapp.user.dto.EmployerProfileUpdateRequest;
import com.jobapp.user.model.Employer;
import com.jobapp.user.repository.EmployerRepository;

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
 * Integration tests for EmployerController
 * Requirements: 3.1, 3.4
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebMvc
@TestPropertySource(locations = "classpath:application-test.yml")
public class EmployerControllerIntegrationTest {
    
    @Autowired
    private WebApplicationContext webApplicationContext;
    
    @Autowired
    private EmployerRepository employerRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private MockMvc mockMvc;
    
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        employerRepository.deleteAll();
    }
    
    @Test
    void registerEmployer_ValidRequest_ReturnsCreated() throws Exception {
        // Given
        EmployerRegistrationRequest request = new EmployerRegistrationRequest(
            "hr@techcorp.com",
            "password123",
            "Tech Corp",
            "https://techcorp.com",
            "Leading technology company"
        );
        request.setAddress("123 Tech Street, Silicon Valley");
        request.setContactPerson("John HR Manager");
        request.setContactPhone("+1234567890");
        
        // When & Then
        mockMvc.perform(post("/api/employers/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email", is("hr@techcorp.com")))
                .andExpect(jsonPath("$.companyName", is("Tech Corp")))
                .andExpect(jsonPath("$.website", is("https://techcorp.com")))
                .andExpect(jsonPath("$.description", is("Leading technology company")))
                .andExpect(jsonPath("$.address", is("123 Tech Street, Silicon Valley")))
                .andExpect(jsonPath("$.contactPerson", is("John HR Manager")))
                .andExpect(jsonPath("$.contactPhone", is("+1234567890")))
                .andExpect(jsonPath("$.isApproved", is(false))) // Should require approval
                .andExpect(jsonPath("$.isActive", is(true)))
                .andExpect(jsonPath("$.password").doesNotExist()); // Password should not be returned
    }
    
    @Test
    void registerEmployer_DuplicateEmail_ReturnsConflict() throws Exception {
        // Given
        Employer existingEmployer = new Employer(
            "hr@techcorp.com",
            passwordEncoder.encode("password123"),
            "Tech Corp",
            "https://techcorp.com",
            "Leading technology company"
        );
        employerRepository.save(existingEmployer);
        
        EmployerRegistrationRequest request = new EmployerRegistrationRequest(
            "hr@techcorp.com",
            "password456",
            "Another Corp",
            "https://anothercorp.com",
            "Another company"
        );
        
        // When & Then
        mockMvc.perform(post("/api/employers/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code", is("EMAIL_ALREADY_EXISTS")))
                .andExpect(jsonPath("$.message", is("Email is already registered")));
    }
    
    @Test
    void registerEmployer_InvalidData_ReturnsBadRequest() throws Exception {
        // Given
        EmployerRegistrationRequest request = new EmployerRegistrationRequest(
            "invalid-email",
            "123", // Too short password
            "", // Empty company name
            "invalid-url",
            null
        );
        
        // When & Then
        mockMvc.perform(post("/api/employers/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code", is("VALIDATION_ERROR")))
                .andExpect(jsonPath("$.details").exists());
    }
    
    @Test
    void getEmployerProfile_ExistingEmployer_ReturnsOk() throws Exception {
        // Given
        Employer employer = new Employer(
            "hr@techcorp.com",
            passwordEncoder.encode("password123"),
            "Tech Corp",
            "https://techcorp.com",
            "Leading technology company"
        );
        employer.setAddress("123 Tech Street");
        employer.setContactPerson("John HR Manager");
        employer.setContactPhone("+1234567890");
        Employer savedEmployer = employerRepository.save(employer);
        
        // When & Then
        mockMvc.perform(get("/api/employers/{employerId}", savedEmployer.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(savedEmployer.getId())))
                .andExpect(jsonPath("$.email", is("hr@techcorp.com")))
                .andExpect(jsonPath("$.companyName", is("Tech Corp")))
                .andExpect(jsonPath("$.website", is("https://techcorp.com")))
                .andExpect(jsonPath("$.description", is("Leading technology company")))
                .andExpect(jsonPath("$.address", is("123 Tech Street")))
                .andExpect(jsonPath("$.contactPerson", is("John HR Manager")))
                .andExpect(jsonPath("$.contactPhone", is("+1234567890")))
                .andExpect(jsonPath("$.password").doesNotExist());
    }
    
    @Test
    void getEmployerProfile_NonExistentEmployer_ReturnsNotFound() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/employers/{employerId}", "nonexistent-id"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code", is("RESOURCE_NOT_FOUND")));
    }
    
    @Test
    void updateEmployerProfile_ValidRequest_ReturnsOk() throws Exception {
        // Given
        Employer employer = new Employer(
            "hr@techcorp.com",
            passwordEncoder.encode("password123"),
            "Tech Corp",
            "https://techcorp.com",
            "Leading technology company"
        );
        Employer savedEmployer = employerRepository.save(employer);
        
        EmployerProfileUpdateRequest updateRequest = new EmployerProfileUpdateRequest();
        updateRequest.setCompanyName("Updated Tech Corp");
        updateRequest.setWebsite("https://updated-techcorp.com");
        updateRequest.setDescription("Updated leading technology company");
        updateRequest.setAddress("456 Updated Tech Street");
        updateRequest.setContactPerson("Jane Updated HR Manager");
        updateRequest.setContactPhone("+1987654321");
        
        // When & Then
        mockMvc.perform(put("/api/employers/{employerId}/profile", savedEmployer.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.companyName", is("Updated Tech Corp")))
                .andExpect(jsonPath("$.website", is("https://updated-techcorp.com")))
                .andExpect(jsonPath("$.description", is("Updated leading technology company")))
                .andExpect(jsonPath("$.address", is("456 Updated Tech Street")))
                .andExpect(jsonPath("$.contactPerson", is("Jane Updated HR Manager")))
                .andExpect(jsonPath("$.contactPhone", is("+1987654321")));
    }
    
    @Test
    void updateLogoUrl_ValidRequest_ReturnsOk() throws Exception {
        // Given
        Employer employer = new Employer(
            "hr@techcorp.com",
            passwordEncoder.encode("password123"),
            "Tech Corp",
            "https://techcorp.com",
            "Leading technology company"
        );
        Employer savedEmployer = employerRepository.save(employer);
        String logoUrl = "https://s3.amazonaws.com/logos/techcorp-logo.png";
        
        // When & Then
        mockMvc.perform(put("/api/employers/{employerId}/logo", savedEmployer.getId())
                .param("logoUrl", logoUrl))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.logoUrl", is(logoUrl)));
    }
    
    @Test
    void approveEmployer_ValidRequest_ReturnsOk() throws Exception {
        // Given
        Employer employer = new Employer(
            "hr@techcorp.com",
            passwordEncoder.encode("password123"),
            "Tech Corp",
            "https://techcorp.com",
            "Leading technology company"
        );
        employer.setIsApproved(false);
        Employer savedEmployer = employerRepository.save(employer);
        String adminId = "admin123";
        
        // When & Then
        mockMvc.perform(put("/api/employers/{employerId}/approve", savedEmployer.getId())
                .param("adminId", adminId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isApproved", is(true)))
                .andExpect(jsonPath("$.approvedBy", is(adminId)))
                .andExpect(jsonPath("$.approvalDate").exists())
                .andExpect(jsonPath("$.rejectionReason").doesNotExist());
    }
    
    @Test
    void rejectEmployer_ValidRequest_ReturnsOk() throws Exception {
        // Given
        Employer employer = new Employer(
            "hr@techcorp.com",
            passwordEncoder.encode("password123"),
            "Tech Corp",
            "https://techcorp.com",
            "Leading technology company"
        );
        employer.setIsApproved(false);
        Employer savedEmployer = employerRepository.save(employer);
        String rejectionReason = "Incomplete company information";
        
        // When & Then
        mockMvc.perform(put("/api/employers/{employerId}/reject", savedEmployer.getId())
                .param("rejectionReason", rejectionReason))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isApproved", is(false)))
                .andExpect(jsonPath("$.rejectionReason", is(rejectionReason)))
                .andExpect(jsonPath("$.approvalDate").doesNotExist())
                .andExpect(jsonPath("$.approvedBy").doesNotExist());
    }
    
    @Test
    void getPendingEmployers_ReturnsOk() throws Exception {
        // Given
        Employer pendingEmployer1 = new Employer(
            "hr1@techcorp.com",
            passwordEncoder.encode("password123"),
            "Tech Corp 1",
            "https://techcorp1.com",
            "Company 1"
        );
        pendingEmployer1.setIsApproved(false);
        
        Employer pendingEmployer2 = new Employer(
            "hr2@techcorp.com",
            passwordEncoder.encode("password123"),
            "Tech Corp 2",
            "https://techcorp2.com",
            "Company 2"
        );
        pendingEmployer2.setIsApproved(false);
        
        Employer approvedEmployer = new Employer(
            "hr3@techcorp.com",
            passwordEncoder.encode("password123"),
            "Tech Corp 3",
            "https://techcorp3.com",
            "Company 3"
        );
        approvedEmployer.setIsApproved(true);
        
        employerRepository.save(pendingEmployer1);
        employerRepository.save(pendingEmployer2);
        employerRepository.save(approvedEmployer);
        
        // When & Then
        mockMvc.perform(get("/api/employers/pending"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].isApproved", everyItem(is(false))));
    }
    
    @Test
    void getApprovedEmployers_ReturnsOk() throws Exception {
        // Given
        Employer pendingEmployer = new Employer(
            "hr1@techcorp.com",
            passwordEncoder.encode("password123"),
            "Tech Corp 1",
            "https://techcorp1.com",
            "Company 1"
        );
        pendingEmployer.setIsApproved(false);
        
        Employer approvedEmployer1 = new Employer(
            "hr2@techcorp.com",
            passwordEncoder.encode("password123"),
            "Tech Corp 2",
            "https://techcorp2.com",
            "Company 2"
        );
        approvedEmployer1.setIsApproved(true);
        
        Employer approvedEmployer2 = new Employer(
            "hr3@techcorp.com",
            passwordEncoder.encode("password123"),
            "Tech Corp 3",
            "https://techcorp3.com",
            "Company 3"
        );
        approvedEmployer2.setIsApproved(true);
        
        employerRepository.save(pendingEmployer);
        employerRepository.save(approvedEmployer1);
        employerRepository.save(approvedEmployer2);
        
        // When & Then
        mockMvc.perform(get("/api/employers/approved"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].isApproved", everyItem(is(true))));
    }
    
    @Test
    void checkEmailExists_ExistingEmail_ReturnsExists() throws Exception {
        // Given
        Employer employer = new Employer(
            "hr@techcorp.com",
            passwordEncoder.encode("password123"),
            "Tech Corp",
            "https://techcorp.com",
            "Leading technology company"
        );
        employerRepository.save(employer);
        
        // When & Then
        mockMvc.perform(get("/api/employers/exists/{email}", "hr@techcorp.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Email already exists")));
    }
    
    @Test
    void checkEmailExists_NonExistentEmail_ReturnsAvailable() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/employers/exists/{email}", "nonexistent@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Email is available")));
    }
    
    @Test
    void canPostJobs_ApprovedEmployer_ReturnsCanPost() throws Exception {
        // Given
        Employer employer = new Employer(
            "hr@techcorp.com",
            passwordEncoder.encode("password123"),
            "Tech Corp",
            "https://techcorp.com",
            "Leading technology company"
        );
        employer.setIsApproved(true);
        employer.setIsActive(true);
        Employer savedEmployer = employerRepository.save(employer);
        
        // When & Then
        mockMvc.perform(get("/api/employers/{employerId}/can-post-jobs", savedEmployer.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Employer can post jobs")));
    }
    
    @Test
    void canPostJobs_PendingEmployer_ReturnsCannotPost() throws Exception {
        // Given
        Employer employer = new Employer(
            "hr@techcorp.com",
            passwordEncoder.encode("password123"),
            "Tech Corp",
            "https://techcorp.com",
            "Leading technology company"
        );
        employer.setIsApproved(false);
        employer.setIsActive(true);
        Employer savedEmployer = employerRepository.save(employer);
        
        // When & Then
        mockMvc.perform(get("/api/employers/{employerId}/can-post-jobs", savedEmployer.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Employer cannot post jobs - approval required")));
    }
}