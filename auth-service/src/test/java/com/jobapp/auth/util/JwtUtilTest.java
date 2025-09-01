package com.jobapp.auth.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret", "mySecretKeyForTestingPurposesOnly");
        ReflectionTestUtils.setField(jwtUtil, "expiration", 86400000L); // 24 hours
        ReflectionTestUtils.setField(jwtUtil, "refreshExpiration", 604800000L); // 7 days

        userDetails = new User("test@example.com", "password", 
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_CANDIDATE")));
    }

    @Test
    void generateToken_ValidUserDetails_ReturnsToken() {
        // When
        String token = jwtUtil.generateToken(userDetails, "ROLE_CANDIDATE");

        // Then
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void extractUsername_ValidToken_ReturnsUsername() {
        // Given
        String token = jwtUtil.generateToken(userDetails, "ROLE_CANDIDATE");

        // When
        String username = jwtUtil.extractUsername(token);

        // Then
        assertEquals("test@example.com", username);
    }

    @Test
    void extractRole_ValidToken_ReturnsRole() {
        // Given
        String token = jwtUtil.generateToken(userDetails, "ROLE_CANDIDATE");

        // When
        String role = jwtUtil.extractRole(token);

        // Then
        assertEquals("ROLE_CANDIDATE", role);
    }

    @Test
    void extractExpiration_ValidToken_ReturnsExpirationDate() {
        // Given
        String token = jwtUtil.generateToken(userDetails, "ROLE_CANDIDATE");

        // When
        Date expiration = jwtUtil.extractExpiration(token);

        // Then
        assertNotNull(expiration);
        assertTrue(expiration.after(new Date()));
    }

    @Test
    void validateToken_ValidTokenAndUserDetails_ReturnsTrue() {
        // Given
        String token = jwtUtil.generateToken(userDetails, "ROLE_CANDIDATE");

        // When
        Boolean isValid = jwtUtil.validateToken(token, userDetails);

        // Then
        assertTrue(isValid);
    }

    @Test
    void validateToken_ValidTokenOnly_ReturnsTrue() {
        // Given
        String token = jwtUtil.generateToken(userDetails, "ROLE_CANDIDATE");

        // When
        Boolean isValid = jwtUtil.validateToken(token);

        // Then
        assertTrue(isValid);
    }

    @Test
    void validateToken_InvalidToken_ReturnsFalse() {
        // Given
        String invalidToken = "invalid.token.here";

        // When
        Boolean isValid = jwtUtil.validateToken(invalidToken);

        // Then
        assertFalse(isValid);
    }

    @Test
    void generateRefreshToken_ValidUserDetails_ReturnsToken() {
        // When
        String refreshToken = jwtUtil.generateRefreshToken(userDetails, "ROLE_CANDIDATE");

        // Then
        assertNotNull(refreshToken);
        assertFalse(refreshToken.isEmpty());
    }

    @Test
    void validateToken_WrongUserDetails_ReturnsFalse() {
        // Given
        String token = jwtUtil.generateToken(userDetails, "ROLE_CANDIDATE");
        UserDetails wrongUserDetails = new User("wrong@example.com", "password", 
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_CANDIDATE")));

        // When
        Boolean isValid = jwtUtil.validateToken(token, wrongUserDetails);

        // Then
        assertFalse(isValid);
    }
}