package com.jobapp.auth.controller;

import com.jobapp.auth.dto.*;
import com.jobapp.auth.model.Role;
import com.jobapp.auth.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private TokenRefreshRequest tokenRefreshRequest;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setName("John Doe");
        registerRequest.setEmail("john.doe@example.com");
        registerRequest.setPassword("password123");
        registerRequest.setRole(Role.CANDIDATE);

        loginRequest = new LoginRequest();
        loginRequest.setEmail("john.doe@example.com");
        loginRequest.setPassword("password123");

        tokenRefreshRequest = new TokenRefreshRequest();
        tokenRefreshRequest.setRefreshToken("refresh-token");
    }

    @Test
    @WithMockUser
    void registerUser_ValidRequest_ReturnsSuccessMessage() throws Exception {
        // Given
        MessageResponse messageResponse = new MessageResponse("User registered successfully!");
        when(authService.registerUser(any(RegisterRequest.class))).thenReturn(messageResponse);

        // When & Then
        mockMvc.perform(post("/api/auth/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User registered successfully!"));
    }

    @Test
    @WithMockUser
    void registerUser_InvalidRequest_ReturnsBadRequest() throws Exception {
        // Given
        RegisterRequest invalidRequest = new RegisterRequest();
        // Missing required fields

        // When & Then
        mockMvc.perform(post("/api/auth/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void authenticateUser_ValidCredentials_ReturnsJwtResponse() throws Exception {
        // Given
        JwtResponse jwtResponse = new JwtResponse(
            "jwt-token", 
            "refresh-token", 
            "john.doe@example.com", 
            "ROLE_CANDIDATE"
        );
        when(authService.authenticateUser(any(LoginRequest.class))).thenReturn(jwtResponse);

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"))
                .andExpect(jsonPath("$.refreshToken").value("refresh-token"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.role").value("ROLE_CANDIDATE"));
    }

    @Test
    @WithMockUser
    void authenticateUser_InvalidCredentials_ReturnsUnauthorized() throws Exception {
        // Given
        when(authService.authenticateUser(any(LoginRequest.class)))
            .thenThrow(new RuntimeException("Invalid email or password"));

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void refreshToken_ValidToken_ReturnsNewJwtResponse() throws Exception {
        // Given
        JwtResponse jwtResponse = new JwtResponse(
            "new-jwt-token", 
            "refresh-token", 
            "john.doe@example.com", 
            "ROLE_CANDIDATE"
        );
        when(authService.refreshToken(any(TokenRefreshRequest.class))).thenReturn(jwtResponse);

        // When & Then
        mockMvc.perform(post("/api/auth/refresh")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tokenRefreshRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("new-jwt-token"))
                .andExpect(jsonPath("$.refreshToken").value("refresh-token"));
    }

    @Test
    @WithMockUser
    void refreshToken_InvalidToken_ReturnsUnauthorized() throws Exception {
        // Given
        when(authService.refreshToken(any(TokenRefreshRequest.class)))
            .thenThrow(new RuntimeException("Refresh token is not valid!"));

        // When & Then
        mockMvc.perform(post("/api/auth/refresh")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tokenRefreshRequest)))
                .andExpect(status().isUnauthorized());
    }
}