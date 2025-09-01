package com.jobapp.auth.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/public")
    public String publicEndpoint() {
        return "Public endpoint accessible";
    }

    @GetMapping("/protected")
    @PreAuthorize("hasRole('CANDIDATE') or hasRole('EMPLOYER') or hasRole('ADMIN')")
    public String protectedEndpoint() {
        return "Protected endpoint accessible";
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminEndpoint() {
        return "Admin endpoint accessible";
    }

    @GetMapping("/employer")
    @PreAuthorize("hasRole('EMPLOYER')")
    public String employerEndpoint() {
        return "Employer endpoint accessible";
    }

    @GetMapping("/candidate")
    @PreAuthorize("hasRole('CANDIDATE')")
    public String candidateEndpoint() {
        return "Candidate endpoint accessible";
    }
}