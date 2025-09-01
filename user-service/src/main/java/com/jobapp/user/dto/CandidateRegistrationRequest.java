package com.jobapp.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

/**
 * DTO for candidate registration request
 * Requirements: 1.1, 1.2
 */
public class CandidateRegistrationRequest {
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;
    
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;
    
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;
    
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Phone number should be valid")
    private String phone;
    
    @NotBlank(message = "Degree is required")
    @Size(max = 100, message = "Degree must not exceed 100 characters")
    private String degree;
    
    @NotNull(message = "Graduation year is required")
    @Min(value = 1950, message = "Graduation year must be after 1950")
    @Max(value = 2030, message = "Graduation year must be before 2030")
    private Integer graduationYear;
    
    // Constructors
    public CandidateRegistrationRequest() {}
    
    public CandidateRegistrationRequest(String email, String password, String name, 
                                      String phone, String degree, Integer graduationYear) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.phone = phone;
        this.degree = degree;
        this.graduationYear = graduationYear;
    }
    
    // Getters and Setters
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public String getDegree() {
        return degree;
    }
    
    public void setDegree(String degree) {
        this.degree = degree;
    }
    
    public Integer getGraduationYear() {
        return graduationYear;
    }
    
    public void setGraduationYear(Integer graduationYear) {
        this.graduationYear = graduationYear;
    }
}