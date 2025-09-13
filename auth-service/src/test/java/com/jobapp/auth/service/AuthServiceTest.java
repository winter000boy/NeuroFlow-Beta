package com.jobapp.auth.service;

import com.jobapp.auth.dto.*;
import com.jobapp.auth.model.Role;
import com.jobapp.auth.model.User;
import com.jobapp.auth.repository.UserRepository;
import com.jobapp.auth.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserDetailsServiceImpl userDetailsService;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private Authentication authentication;

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private User testUser;

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

        testUser = new User();
        testUser.setId("user123");
        testUser.setName("John Doe");
        testUser.setEmail("john.doe@example.com");
        testUser.setPassword("encodedPassword");
        testUser.setRole(Role.CANDIDATE);
        testUser.setActive(true);
        testUser.setCreatedAt(LocalDateTime.now());
        testUser.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void registerUser_ValidRequest_ReturnsSuccessMessage() {
        // Given
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        MessageResponse response = authService.registerUser(registerRequest);

        // Then
        assertNotNull(response);
        assertEquals("User registered successfully!", response.getMessage());
        verify(userRepository).existsByEmail(registerRequest.getEmail());
        verify(passwordEncoder).encode(registerRequest.getPassword());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void registerUser_EmailAlreadyExists_ThrowsException() {
        // Given
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(true);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> authService.registerUser(registerRequest));
        assertEquals("Error: Email is already in use!", exception.getMessage());
        verify(userRepository).existsByEmail(registerRequest.getEmail());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void authenticateUser_ValidCredentials_ReturnsJwtResponse() {
        // Given
        String jwt = "jwt-token";
        String refreshToken = "refresh-token";
        String authority = "ROLE_CANDIDATE";

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(testUser.getEmail());
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));
        when(jwtUtil.generateToken(userDetails, testUser.getRole().getAuthority())).thenReturn(jwt);
        when(jwtUtil.generateRefreshToken(userDetails, testUser.getRole().getAuthority())).thenReturn(refreshToken);

        // When
        JwtResponse response = authService.authenticateUser(loginRequest);

        // Then
        assertNotNull(response);
        assertEquals(jwt, response.getToken());
        assertEquals(refreshToken, response.getRefreshToken());
        assertEquals(testUser.getEmail(), response.getEmail());
        assertEquals(testUser.getRole().getAuthority(), response.getRole());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository).findByEmail(testUser.getEmail());
    }

    @Test
    void authenticateUser_InvalidCredentials_ThrowsException() {
        // Given
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenThrow(new BadCredentialsException("Bad credentials"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> authService.authenticateUser(loginRequest));
        assertEquals("Invalid email or password", exception.getMessage());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void authenticateUser_UserNotFound_ThrowsException() {
        // Given
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(testUser.getEmail());
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> authService.authenticateUser(loginRequest));
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void refreshToken_ValidToken_ReturnsNewJwtResponse() {
        // Given
        String refreshToken = "valid-refresh-token";
        String newToken = "new-jwt-token";
        String username = "john.doe@example.com";
        String role = "ROLE_CANDIDATE";

        TokenRefreshRequest request = new TokenRefreshRequest();
        request.setRefreshToken(refreshToken);

        when(jwtUtil.validateToken(refreshToken)).thenReturn(true);
        when(jwtUtil.extractUsername(refreshToken)).thenReturn(username);
        when(jwtUtil.extractRole(refreshToken)).thenReturn(role);
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
        when(jwtUtil.generateToken(userDetails, role)).thenReturn(newToken);

        // When
        JwtResponse response = authService.refreshToken(request);

        // Then
        assertNotNull(response);
        assertEquals(newToken, response.getToken());
        assertEquals(refreshToken, response.getRefreshToken());
        assertEquals(username, response.getEmail());
        assertEquals(role, response.getRole());
        verify(jwtUtil).validateToken(refreshToken);
        verify(jwtUtil).extractUsername(refreshToken);
        verify(jwtUtil).extractRole(refreshToken);
        verify(userDetailsService).loadUserByUsername(username);
        verify(jwtUtil).generateToken(userDetails, role);
    }

    @Test
    void refreshToken_InvalidToken_ThrowsException() {
        // Given
        String refreshToken = "invalid-refresh-token";
        TokenRefreshRequest request = new TokenRefreshRequest();
        request.setRefreshToken(refreshToken);

        when(jwtUtil.validateToken(refreshToken)).thenReturn(false);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> authService.refreshToken(request));
        assertEquals("Refresh token is not valid!", exception.getMessage());
        verify(jwtUtil).validateToken(refreshToken);
        verify(jwtUtil, never()).extractUsername(anyString());
    }

    @Test
    void isEmailAvailable_EmailNotExists_ReturnsTrue() {
        // Given
        String email = "new.user@example.com";
        when(userRepository.existsByEmail(email)).thenReturn(false);

        // When
        boolean result = authService.isEmailAvailable(email);

        // Then
        assertTrue(result);
        verify(userRepository).existsByEmail(email);
    }

    @Test
    void isEmailAvailable_EmailExists_ReturnsFalse() {
        // Given
        String email = "existing.user@example.com";
        when(userRepository.existsByEmail(email)).thenReturn(true);

        // When
        boolean result = authService.isEmailAvailable(email);

        // Then
        assertFalse(result);
        verify(userRepository).existsByEmail(email);
    }
}