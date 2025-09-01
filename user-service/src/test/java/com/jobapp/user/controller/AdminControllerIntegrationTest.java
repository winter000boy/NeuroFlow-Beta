package com.jobapp.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobapp.user.dto.*;
import com.jobapp.user.model.*;
import com.jobapp.user.repository.AdminRepository;
import com.jobapp.user.repository.CandidateRepository;
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

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for AdminController
 * Requirements: 5.1, 5.2, 5.3
 */
@SpringBootTest
@AutoConfigureWebMvc
@TestPropertySource(locations = "classpath:application-test.properties")
public class AdminControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private CandidateRepository candidateRepository;

    @Autowired
    private EmployerRepository employerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    private Admin testAdmin;
    private Candidate testCandidate;
    private Employer testEmployer;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        // Clean up test data
        adminRepository.deleteAll();
        candidateRepository.deleteAll();
        employerRepository.deleteAll();

        // Create test admin
        testAdmin = new Admin();
        testAdmin.setEmail("admin@test.com");
        testAdmin.setPassword(passwordEncoder.encode("password123"));
        testAdmin.setName("Test Admin");
        testAdmin.setRole(AdminRole.SUPER_ADMIN);
        testAdmin.setPermissions(Arrays.asList(AdminPermission.getAllPermissions()));
        testAdmin.setIsActive(true);
        testAdmin = adminRepository.save(testAdmin);

        // Create test candidate
        testCandidate = new Candidate();
        testCandidate.setEmail("candidate@test.com");
        testCandidate.setPassword(passwordEncoder.encode("password123"));
        testCandidate.setName("Test Candidate");
        testCandidate.setPhone("+1234567890");
        testCandidate.setDegree("Computer Science");
        testCandidate.setGraduationYear(2022);
        testCandidate.setIsActive(true);
        testCandidate = candidateRepository.save(testCandidate);

        // Create test employer
        testEmployer = new Employer();
        testEmployer.setEmail("employer@test.com");
        testEmployer.setPassword(passwordEncoder.encode("password123"));
        testEmployer.setCompanyName("Test Company");
        testEmployer.setWebsite("https://testcompany.com");
        testEmployer.setDescription("Test company description");
        testEmployer.setIsApproved(false);
        testEmployer.setIsActive(true);
        testEmployer = employerRepository.save(testEmployer);
    }

    @Test
    void testAdminLogin_ValidCredentials_ReturnsToken() throws Exception {
        AdminLoginRequest loginRequest = new AdminLoginRequest("admin@test.com", "password123");

        mockMvc.perform(post("/api/admin/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.refreshToken").exists())
                .andExpect(jsonPath("$.adminId").value(testAdmin.getId()))
                .andExpect(jsonPath("$.email").value("admin@test.com"))
                .andExpect(jsonPath("$.name").value("Test Admin"))
                .andExpect(jsonPath("$.role").value("SUPER_ADMIN"));
    }

    @Test
    void testAdminLogin_InvalidCredentials_ReturnsUnauthorized() throws Exception {
        AdminLoginRequest loginRequest = new AdminLoginRequest("admin@test.com", "wrongpassword");

        mockMvc.perform(post("/api/admin/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testAdminLogin_InactiveAdmin_ReturnsUnauthorized() throws Exception {
        testAdmin.setIsActive(false);
        adminRepository.save(testAdmin);

        AdminLoginRequest loginRequest = new AdminLoginRequest("admin@test.com", "password123");

        mockMvc.perform(post("/api/admin/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetAllCandidates_WithAdminAuth_ReturnsPagedCandidates() throws Exception {
        String token = getAdminToken();

        mockMvc.perform(get("/api/admin/users/candidates")
                .header("Authorization", "Bearer " + token)
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].email").value("candidate@test.com"))
                .andExpect(jsonPath("$.content[0].name").value("Test Candidate"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void testGetAllEmployers_WithAdminAuth_ReturnsPagedEmployers() throws Exception {
        String token = getAdminToken();

        mockMvc.perform(get("/api/admin/users/employers")
                .header("Authorization", "Bearer " + token)
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].email").value("employer@test.com"))
                .andExpect(jsonPath("$.content[0].companyName").value("Test Company"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void testApproveEmployer_WithAdminAuth_ApprovesSuccessfully() throws Exception {
        String token = getAdminToken();
        AdminActionRequest actionRequest = new AdminActionRequest("Employer meets all requirements");

        mockMvc.perform(put("/api/admin/users/employers/" + testEmployer.getId() + "/approve")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(actionRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Employer approved successfully"));

        // Verify employer is approved in database
        Employer updatedEmployer = employerRepository.findById(testEmployer.getId()).orElseThrow();
        assertTrue(updatedEmployer.getIsApproved());
        assertEquals("admin@test.com", updatedEmployer.getApprovedBy());
    }

    @Test
    void testRejectEmployer_WithAdminAuth_RejectsSuccessfully() throws Exception {
        String token = getAdminToken();
        AdminActionRequest actionRequest = new AdminActionRequest("Company does not meet requirements", "Additional notes");

        mockMvc.perform(put("/api/admin/users/employers/" + testEmployer.getId() + "/reject")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(actionRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Employer rejected successfully"));

        // Verify employer is rejected in database
        Employer updatedEmployer = employerRepository.findById(testEmployer.getId()).orElseThrow();
        assertFalse(updatedEmployer.getIsApproved());
        assertEquals("admin@test.com", updatedEmployer.getRejectedBy());
        assertEquals("Company does not meet requirements", updatedEmployer.getRejectionReason());
    }

    @Test
    void testBlockCandidate_WithAdminAuth_BlocksSuccessfully() throws Exception {
        String token = getAdminToken();
        AdminActionRequest actionRequest = new AdminActionRequest("Violation of terms of service", "Inappropriate behavior");

        mockMvc.perform(put("/api/admin/users/candidates/" + testCandidate.getId() + "/block")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(actionRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Candidate blocked successfully"));

        // Verify candidate is blocked in database
        Candidate updatedCandidate = candidateRepository.findById(testCandidate.getId()).orElseThrow();
        assertFalse(updatedCandidate.getIsActive());
        assertEquals("admin@test.com", updatedCandidate.getBlockedBy());
        assertEquals("Violation of terms of service", updatedCandidate.getBlockReason());
    }

    @Test
    void testUnblockCandidate_WithAdminAuth_UnblocksSuccessfully() throws Exception {
        // First block the candidate
        testCandidate.setIsActive(false);
        testCandidate.setBlockedBy("admin@test.com");
        testCandidate.setBlockReason("Test block");
        candidateRepository.save(testCandidate);

        String token = getAdminToken();

        mockMvc.perform(put("/api/admin/users/candidates/" + testCandidate.getId() + "/unblock")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Candidate unblocked successfully"));

        // Verify candidate is unblocked in database
        Candidate updatedCandidate = candidateRepository.findById(testCandidate.getId()).orElseThrow();
        assertTrue(updatedCandidate.getIsActive());
        assertNull(updatedCandidate.getBlockedBy());
        assertNull(updatedCandidate.getBlockReason());
    }

    @Test
    void testCreateAdmin_WithSuperAdminAuth_CreatesSuccessfully() throws Exception {
        String token = getAdminToken();
        AdminCreateRequest createRequest = new AdminCreateRequest(
                "newadmin@test.com", 
                "password123", 
                "New Admin", 
                AdminRole.ADMIN
        );

        mockMvc.perform(post("/api/admin/admins")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("newadmin@test.com"))
                .andExpect(jsonPath("$.name").value("New Admin"))
                .andExpect(jsonPath("$.role").value("ADMIN"));

        // Verify admin is created in database
        assertTrue(adminRepository.existsByEmail("newadmin@test.com"));
    }

    @Test
    void testGetCandidateDetails_WithAdminAuth_ReturnsDetails() throws Exception {
        String token = getAdminToken();

        mockMvc.perform(get("/api/admin/users/candidates/" + testCandidate.getId())
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testCandidate.getId()))
                .andExpect(jsonPath("$.email").value("candidate@test.com"))
                .andExpect(jsonPath("$.name").value("Test Candidate"))
                .andExpect(jsonPath("$.degree").value("Computer Science"))
                .andExpect(jsonPath("$.graduationYear").value(2022));
    }

    @Test
    void testGetEmployerDetails_WithAdminAuth_ReturnsDetails() throws Exception {
        String token = getAdminToken();

        mockMvc.perform(get("/api/admin/users/employers/" + testEmployer.getId())
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testEmployer.getId()))
                .andExpect(jsonPath("$.email").value("employer@test.com"))
                .andExpect(jsonPath("$.companyName").value("Test Company"))
                .andExpect(jsonPath("$.website").value("https://testcompany.com"))
                .andExpect(jsonPath("$.isApproved").value(false));
    }

    @Test
    void testAdminActions_WithoutAuth_ReturnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/admin/users/candidates"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/api/admin/users/employers"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(put("/api/admin/users/employers/" + testEmployer.getId() + "/approve"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testSearchCandidates_WithSearchTerm_ReturnsFilteredResults() throws Exception {
        String token = getAdminToken();

        mockMvc.perform(get("/api/admin/users/candidates")
                .header("Authorization", "Bearer " + token)
                .param("search", "Test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].name").value("Test Candidate"));

        mockMvc.perform(get("/api/admin/users/candidates")
                .header("Authorization", "Bearer " + token)
                .param("search", "nonexistent"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)));
    }

    @Test
    void testSearchEmployers_WithSearchTerm_ReturnsFilteredResults() throws Exception {
        String token = getAdminToken();

        mockMvc.perform(get("/api/admin/users/employers")
                .header("Authorization", "Bearer " + token)
                .param("search", "Test Company"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].companyName").value("Test Company"));

        mockMvc.perform(get("/api/admin/users/employers")
                .header("Authorization", "Bearer " + token)
                .param("search", "nonexistent"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)));
    }

    /**
     * Helper method to get admin authentication token
     */
    private String getAdminToken() throws Exception {
        AdminLoginRequest loginRequest = new AdminLoginRequest("admin@test.com", "password123");

        String response = mockMvc.perform(post("/api/admin/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        AdminLoginResponse loginResponse = objectMapper.readValue(response, AdminLoginResponse.class);
        return loginResponse.getToken();
    }
}