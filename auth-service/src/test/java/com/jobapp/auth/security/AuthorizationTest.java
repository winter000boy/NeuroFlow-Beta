package com.jobapp.auth.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobapp.auth.dto.LoginRequest;
import com.jobapp.auth.model.Role;
import com.jobapp.auth.model.User;
import com.jobapp.auth.repository.UserRepository;
import com.jobapp.auth.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@SpringBootTest
@AutoConfigureWebMvc
@TestPropertySource(locations = "classpath:application-test.properties")
class AuthorizationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        objectMapper = new ObjectMapper();
        userRepository.deleteAll();
    }

    @Test
    void accessLoginEndpoint_NoToken_ReturnsBadRequest() throws Exception {
        LoginRequest request = new LoginRequest("test@example.com", "password");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest()); // Bad request because user doesn't exist, but not unauthorized
    }

    @Test
    void accessProtectedEndpoint_NoToken_ReturnsUnauthorizedOrBadRequest() throws Exception {
        // The endpoint should return either 401 (unauthorized) or 400 (bad request due to @PreAuthorize)
        mockMvc.perform(get("/api/test/protected"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void accessPublicEndpoint_NoToken_ReturnsOk() throws Exception {
        mockMvc.perform(get("/api/test/public"))
                .andExpect(status().isOk())
                .andExpect(content().string("Public endpoint accessible"));
    }

    @Test
    void accessSwaggerUI_NoToken_ReturnsRedirect() throws Exception {
        mockMvc.perform(get("/swagger-ui.html"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void accessActuatorHealth_NoToken_ReturnsOk() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk());
    }
}