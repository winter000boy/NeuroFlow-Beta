package com.jobapp.user.controller;

import com.jobapp.user.dto.CandidateRegistrationRequest;
import com.jobapp.user.dto.CandidateProfileUpdateRequest;
import com.jobapp.user.model.Candidate;
import com.jobapp.user.repository.CandidateRepository;
import com.jobapp.user.testdata.TestDataSeeder;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
class CandidateControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CandidateRepository candidateRepository;

    @Autowired
    private TestDataSeeder testDataSeeder;

    @Autowired
    private ObjectMapper objectMapper;

    private CandidateRegistrationRequest registrationRequest;
    private CandidateProfileUpdateRequest updateRequest;

    @BeforeEach
    void setUp() {
        registrationRequest = new CandidateRegistrationRequest();
        registrationRequest.setEmail("newcandidate@example.com");
        registrationRequest.setPassword("password123");
        registrationRequest.setName("New Candidate");
        registrationRequest.setPhone("1234567890");
        registrationRequest.setDegree("Computer Science");
        registrationRequest.setGraduationYear(2022);

        updateRequest = new CandidateProfileUpdateRequest();
        updateRequest.setName("Updated Name");
        updateRequest.setPhone("0987654321");
        updateRequest.setLinkedinProfile("https://linkedin.com/in/updated");
    }

    @AfterEach
    void tearDown() {
        testDataSeeder.cleanupAllTestData();
    }

    @Test
    @WithMockUser(roles = "CANDIDATE")
    void registerCandidate_ValidRequest_ReturnsCreated() throws Exception {
        mockMvc.perform(post("/api/candidates/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registrationRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value(registrationRequest.getEmail()))
                .andExpect(jsonPath("$.name").value(registrationRequest.getName()))
                .andExpect(jsonPath("$.degree").value(registrationRequest.getDegree()));
    }

    @Test
    @WithMockUser(roles = "CANDIDATE")
    void registerCandidate_DuplicateEmail_ReturnsBadRequest() throws Exception {
        // First registration
        mockMvc.perform(post("/api/candidates/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registrationRequest)))
                .andExpect(status().isCreated());

        // Second registration with same email
        mockMvc.perform(post("/api/candidates/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registrationRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "CANDIDATE")
    void getCandidateProfile_ExistingCandidate_ReturnsProfile() throws Exception {
        // Create test candidate
        testDataSeeder.seedTestCandidates();
        Candidate testCandidate = testDataSeeder.getTestCandidate("john.doe@example.com");

        mockMvc.perform(get("/api/candidates/profile/{id}", testCandidate.getId())
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testCandidate.getId()))
                .andExpect(jsonPath("$.email").value(testCandidate.getEmail()))
                .andExpect(jsonPath("$.name").value(testCandidate.getName()));
    }

    @Test
    @WithMockUser(roles = "CANDIDATE")
    void getCandidateProfile_NonExistentCandidate_ReturnsNotFound() throws Exception {
        mockMvc.perform(get("/api/candidates/profile/{id}", "nonexistent-id")
                .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "CANDIDATE")
    void updateCandidateProfile_ValidRequest_ReturnsUpdatedProfile() throws Exception {
        // Create test candidate
        testDataSeeder.seedTestCandidates();
        Candidate testCandidate = testDataSeeder.getTestCandidate("john.doe@example.com");

        mockMvc.perform(put("/api/candidates/profile/{id}", testCandidate.getId())
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testCandidate.getId()))
                .andExpect(jsonPath("$.name").value(updateRequest.getName()))
                .andExpect(jsonPath("$.phone").value(updateRequest.getPhone()));
    }

    @Test
    @WithMockUser(roles = "CANDIDATE")
    void updateCandidateProfile_NonExistentCandidate_ReturnsNotFound() throws Exception {
        mockMvc.perform(put("/api/candidates/profile/{id}", "nonexistent-id")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getCandidateByEmail_AsAdmin_ReturnsProfile() throws Exception {
        // Create test candidate
        testDataSeeder.seedTestCandidates();
        Candidate testCandidate = testDataSeeder.getTestCandidate("john.doe@example.com");

        mockMvc.perform(get("/api/candidates/email/{email}", testCandidate.getEmail())
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(testCandidate.getEmail()))
                .andExpect(jsonPath("$.name").value(testCandidate.getName()));
    }

    @Test
    @WithMockUser(roles = "CANDIDATE")
    void getCandidateByEmail_AsCandidate_ReturnsForbidden() throws Exception {
        testDataSeeder.seedTestCandidates();
        Candidate testCandidate = testDataSeeder.getTestCandidate("john.doe@example.com");

        mockMvc.perform(get("/api/candidates/email/{email}", testCandidate.getEmail())
                .with(csrf()))
                .andExpect(status().isForbidden());
    }
}