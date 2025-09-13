package com.jobapp.user.testdata;

import com.jobapp.user.model.Admin;
import com.jobapp.user.model.AdminRole;
import com.jobapp.user.model.Candidate;
import com.jobapp.user.model.Employer;
import com.jobapp.user.repository.AdminRepository;
import com.jobapp.user.repository.CandidateRepository;
import com.jobapp.user.repository.EmployerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * Test data seeder for user service
 * Requirements: 9.2
 */
@TestComponent
public class TestDataSeeder {

    @Autowired
    private CandidateRepository candidateRepository;

    @Autowired
    private EmployerRepository employerRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Seed all test data
     */
    public void seedAllTestData() {
        seedTestCandidates();
        seedTestEmployers();
        seedTestAdmins();
    }

    /**
     * Seed test candidates
     */
    public void seedTestCandidates() {
        if (candidateRepository.count() > 0) {
            return; // Data already seeded
        }

        List<Candidate> testCandidates = Arrays.asList(
            createTestCandidate("john.doe@example.com", "John Doe", "1234567890", 
                "Computer Science", 2022, "https://linkedin.com/in/johndoe"),
            createTestCandidate("jane.smith@example.com", "Jane Smith", "0987654321", 
                "Software Engineering", 2021, "https://linkedin.com/in/janesmith"),
            createTestCandidate("mike.johnson@example.com", "Mike Johnson", "5555555555", 
                "Information Technology", 2023, null),
            createTestCandidate("sarah.wilson@example.com", "Sarah Wilson", "4444444444", 
                "Computer Engineering", 2020, "https://linkedin.com/in/sarahwilson")
        );

        candidateRepository.saveAll(testCandidates);
    }

    /**
     * Seed test employers
     */
    public void seedTestEmployers() {
        if (employerRepository.count() > 0) {
            return; // Data already seeded
        }

        List<Employer> testEmployers = Arrays.asList(
            createTestEmployer("hr@techcorp.com", "TechCorp Inc", "https://techcorp.com", 
                "Leading technology company", "123 Tech Street, San Francisco, CA"),
            createTestEmployer("jobs@startupxyz.com", "StartupXYZ", "https://startupxyz.com", 
                "Innovative startup in AI/ML", "456 Innovation Ave, Austin, TX"),
            createTestEmployer("careers@megacorp.com", "MegaCorp", "https://megacorp.com", 
                "Fortune 500 company", "789 Corporate Blvd, New York, NY"),
            createTestEmployer("hiring@greentech.com", "GreenTech Solutions", "https://greentech.com", 
                "Sustainable technology solutions", "321 Green Way, Seattle, WA")
        );

        employerRepository.saveAll(testEmployers);
    }

    /**
     * Seed test admins
     */
    public void seedTestAdmins() {
        if (adminRepository.count() > 0) {
            return; // Data already seeded
        }

        List<Admin> testAdmins = Arrays.asList(
            createTestAdmin("admin@jobapp.com", "System Admin", AdminRole.SUPER_ADMIN),
            createTestAdmin("moderator@jobapp.com", "Content Moderator", AdminRole.MODERATOR),
            createTestAdmin("support@jobapp.com", "Support Admin", AdminRole.SUPPORT)
        );

        adminRepository.saveAll(testAdmins);
    }

    /**
     * Create a test candidate
     */
    private Candidate createTestCandidate(String email, String name, String phone, 
                                        String degree, Integer graduationYear, String linkedinProfile) {
        Candidate candidate = new Candidate();
        candidate.setEmail(email);
        candidate.setPassword(passwordEncoder.encode("password123"));
        candidate.setName(name);
        candidate.setPhone(phone);
        candidate.setDegree(degree);
        candidate.setGraduationYear(graduationYear);
        candidate.setLinkedinProfile(linkedinProfile);
        candidate.setIsActive(true);
        candidate.setCreatedAt(LocalDateTime.now());
        candidate.setUpdatedAt(LocalDateTime.now());
        return candidate;
    }

    /**
     * Create a test employer
     */
    private Employer createTestEmployer(String email, String companyName, String website, 
                                      String description, String address) {
        Employer employer = new Employer();
        employer.setEmail(email);
        employer.setPassword(passwordEncoder.encode("password123"));
        employer.setCompanyName(companyName);
        employer.setWebsite(website);
        employer.setDescription(description);
        employer.setAddress(address);
        employer.setIsApproved(true);
        employer.setIsActive(true);
        employer.setCreatedAt(LocalDateTime.now());
        employer.setUpdatedAt(LocalDateTime.now());
        return employer;
    }

    /**
     * Create a test admin
     */
    private Admin createTestAdmin(String email, String name, AdminRole role) {
        Admin admin = new Admin();
        admin.setEmail(email);
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setName(name);
        admin.setRole(role);
        admin.setIsActive(true);
        admin.setCreatedAt(LocalDateTime.now());
        admin.setUpdatedAt(LocalDateTime.now());
        return admin;
    }

    /**
     * Clean up all test data
     */
    public void cleanupAllTestData() {
        candidateRepository.deleteAll();
        employerRepository.deleteAll();
        adminRepository.deleteAll();
    }

    /**
     * Get test candidate by email
     */
    public Candidate getTestCandidate(String email) {
        return candidateRepository.findByEmail(email).orElse(null);
    }

    /**
     * Get test employer by email
     */
    public Employer getTestEmployer(String email) {
        return employerRepository.findByEmail(email).orElse(null);
    }

    /**
     * Get test admin by email
     */
    public Admin getTestAdmin(String email) {
        return adminRepository.findByEmail(email).orElse(null);
    }
}