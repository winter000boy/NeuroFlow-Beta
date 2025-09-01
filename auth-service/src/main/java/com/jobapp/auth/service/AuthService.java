package com.jobapp.auth.service;

import com.jobapp.auth.dto.*;
import com.jobapp.auth.model.Role;
import com.jobapp.auth.model.User;
import com.jobapp.auth.repository.UserRepository;
import com.jobapp.auth.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    public MessageResponse registerUser(RegisterRequest registerRequest) {
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new RuntimeException("Error: Email is already in use!");
        }

        // Create new user
        User user = new User();
        user.setName(registerRequest.getName());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setRole(registerRequest.getRole());
        user.setActive(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);

        return new MessageResponse("User registered successfully!");
    }

    public JwtResponse authenticateUser(LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword())
            );

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User user = userRepository.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            String jwt = jwtUtil.generateToken(userDetails, user.getRole().getAuthority());
            String refreshToken = jwtUtil.generateRefreshToken(userDetails, user.getRole().getAuthority());

            return new JwtResponse(jwt, refreshToken, userDetails.getUsername(), user.getRole().getAuthority());

        } catch (BadCredentialsException e) {
            throw new RuntimeException("Invalid email or password");
        }
    }

    public JwtResponse refreshToken(TokenRefreshRequest request) {
        String refreshToken = request.getRefreshToken();

        if (jwtUtil.validateToken(refreshToken)) {
            String username = jwtUtil.extractUsername(refreshToken);
            String role = jwtUtil.extractRole(refreshToken);

            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            String newToken = jwtUtil.generateToken(userDetails, role);

            return new JwtResponse(newToken, refreshToken, username, role);
        } else {
            throw new RuntimeException("Refresh token is not valid!");
        }
    }

    public boolean isEmailAvailable(String email) {
        return !userRepository.existsByEmail(email);
    }
}