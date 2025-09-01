package com.jobapp.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobapp.auth.dto.LoginRequest;
import com.jobapp.auth.dto.RegisterRequest;
import com.jobapp.auth.dto.TokenRefreshRequest;
import com.jobapp.auth.model.Role;
import com.jobapp.auth.model.User;
import com.jobapp.auth.repository.UserRepository;
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

@SpringBootTest
@AutoConfigureWebMvc
@TestPropertySource(locations = "classpath:application-test.properties")
class AuthControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        objectMapper = new ObjectMapper();
        userRepository.deleteAll();
    }

    @Test
    void register_ValidRequest_ReturnsSuccess() throws Exception {
        RegisterRequest request = new RegisterRequest("John Doe", "john@example.com", "password123", Role.CANDIDATE);

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User registered successfully!"));
    }

    @Test
    void register_DuplicateEmail_ReturnsBadRequest() throws Exception {
        // Create existing user
        User existingUser = new User("existing@example.com", passwordEncoder.encode("password"), "Existing User", Role.CANDIDATE);
        userRepository.save(existingUser);

        RegisterRequest request = new RegisterRequest("John Doe", "existing@example.com", "password123", Role.CANDIDATE);

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Error: Email is already in use!"));
    }

    @Test
    void login_ValidCredentials_ReturnsJwtResponse() throws Exception {
        // Create test user
        User user = new User("test@example.com", passwordEncoder.encode("password123"), "Test User", Role.CANDIDATE);
        userRepository.save(user);

        LoginRequest request = new LoginRequest("test@example.com", "password123");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.refreshToken").exists())
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.role").value("ROLE_CANDIDATE"));
    }

    @Test
    void login_InvalidCredentials_ReturnsBadRequest() throws Exception {
        LoginRequest request = new LoginRequest("nonexistent@example.com", "wrongpassword");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void checkEmailAvailability_AvailableEmail_ReturnsAvailable() throws Exception {
        mockMvc.perform(get("/api/auth/check-email")
                .param("email", "available@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Email is available"));
    }

    @Test
    void checkEmailAvailability_TakenEmail_ReturnsTaken() throws Exception {
        // Create existing user
        User existingUser = new User("taken@example.com", passwordEncoder.encode("password"), "Existing User", Role.CANDIDATE);
        userRepository.save(existingUser);

        mockMvc.perform(get("/api/auth/check-email")
                .param("email", "taken@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Email is already taken"));
    }
}