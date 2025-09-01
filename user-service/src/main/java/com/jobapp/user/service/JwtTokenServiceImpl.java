package com.jobapp.user.service;

import com.jobapp.user.model.Admin;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.UUID;

/**
 * Simple implementation of JWT token service for admin authentication
 * Requirements: 5.1
 * Note: This is a simplified implementation for testing purposes
 */
@Service
public class JwtTokenServiceImpl implements JwtTokenService {
    
    @Override
    public String generateAdminToken(Admin admin) {
        // Simple token generation for testing
        String tokenData = admin.getEmail() + ":" + admin.getRole().name() + ":" + System.currentTimeMillis();
        return Base64.getEncoder().encodeToString(tokenData.getBytes());
    }
    
    @Override
    public String generateRefreshToken(String email) {
        // Simple refresh token generation
        String tokenData = email + ":refresh:" + UUID.randomUUID().toString();
        return Base64.getEncoder().encodeToString(tokenData.getBytes());
    }
    
    @Override
    public boolean validateToken(String token) {
        try {
            String decoded = new String(Base64.getDecoder().decode(token));
            return decoded.contains(":") && decoded.split(":").length >= 3;
        } catch (Exception e) {
            return false;
        }
    }
    
    @Override
    public String getEmailFromToken(String token) {
        try {
            String decoded = new String(Base64.getDecoder().decode(token));
            return decoded.split(":")[0];
        } catch (Exception e) {
            return null;
        }
    }
    
    @Override
    public String getRoleFromToken(String token) {
        try {
            String decoded = new String(Base64.getDecoder().decode(token));
            String[] parts = decoded.split(":");
            if (parts.length >= 2) {
                return parts[1];
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }
}