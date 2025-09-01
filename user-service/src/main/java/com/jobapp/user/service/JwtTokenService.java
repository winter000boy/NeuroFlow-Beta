package com.jobapp.user.service;

import com.jobapp.user.model.Admin;

/**
 * Service interface for JWT token operations
 * Requirements: 5.1
 */
public interface JwtTokenService {
    
    /**
     * Generate JWT token for admin
     * @param admin the admin to generate token for
     * @return JWT token string
     */
    String generateAdminToken(Admin admin);
    
    /**
     * Generate refresh token
     * @param email the email to generate refresh token for
     * @return refresh token string
     */
    String generateRefreshToken(String email);
    
    /**
     * Validate JWT token
     * @param token the token to validate
     * @return true if token is valid
     */
    boolean validateToken(String token);
    
    /**
     * Extract email from JWT token
     * @param token the JWT token
     * @return email from token
     */
    String getEmailFromToken(String token);
    
    /**
     * Extract role from JWT token
     * @param token the JWT token
     * @return role from token
     */
    String getRoleFromToken(String token);
}