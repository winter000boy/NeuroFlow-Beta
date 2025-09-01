package com.jobapp.auth.controller;

import com.jobapp.auth.dto.*;
import com.jobapp.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Authentication and authorization management APIs for the Job Application Platform")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    @Operation(
            summary = "User login", 
            description = "Authenticate user credentials and return JWT access and refresh tokens"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", 
                    description = "Login successful",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = JwtResponse.class),
                            examples = @ExampleObject(
                                    name = "Successful login",
                                    value = """
                                    {
                                        "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
                                        "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
                                        "tokenType": "Bearer",
                                        "expiresIn": 86400
                                    }
                                    """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400", 
                    description = "Invalid credentials",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Invalid credentials",
                                    value = """
                                    {
                                        "message": "Invalid email or password"
                                    }
                                    """
                            )
                    )
            )
    })
    public ResponseEntity<JwtResponse> login(
            @Parameter(description = "User login credentials", required = true)
            @Valid @RequestBody LoginRequest loginRequest) {
        try {
            JwtResponse jwtResponse = authService.authenticateUser(loginRequest);
            return ResponseEntity.ok(jwtResponse);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/register")
    @Operation(
            summary = "User registration", 
            description = "Register a new user account with role-based access (CANDIDATE, EMPLOYER, or ADMIN)"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", 
                    description = "Registration successful",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = MessageResponse.class),
                            examples = @ExampleObject(
                                    name = "Successful registration",
                                    value = """
                                    {
                                        "message": "User registered successfully"
                                    }
                                    """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400", 
                    description = "Registration failed - email already exists or invalid data",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Email already exists",
                                    value = """
                                    {
                                        "message": "Email is already in use"
                                    }
                                    """
                            )
                    )
            )
    })
    public ResponseEntity<MessageResponse> register(
            @Parameter(description = "User registration details", required = true)
            @Valid @RequestBody RegisterRequest registerRequest) {
        try {
            MessageResponse messageResponse = authService.registerUser(registerRequest);
            return ResponseEntity.ok(messageResponse);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse(e.getMessage()));
        }
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh token", description = "Refresh JWT access token using refresh token")
    public ResponseEntity<JwtResponse> refreshToken(@Valid @RequestBody TokenRefreshRequest request) {
        try {
            JwtResponse jwtResponse = authService.refreshToken(request);
            return ResponseEntity.ok(jwtResponse);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new JwtResponse());
        }
    }

    @GetMapping("/check-email")
    @Operation(summary = "Check email availability", description = "Check if email is available for registration")
    public ResponseEntity<MessageResponse> checkEmailAvailability(@RequestParam String email) {
        boolean isAvailable = authService.isEmailAvailable(email);
        String message = isAvailable ? "Email is available" : "Email is already taken";
        return ResponseEntity.ok(new MessageResponse(message));
    }
}