package com.jobapp.auth.testdata;

import com.jobapp.auth.model.Role;
import com.jobapp.auth.model.User;
import com.jobapp.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * Test data seeder for authentication service
 * Requirements: 9.2
 */
@TestComponent
public class TestDataSeeder {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Seed test users for different roles
     */
    public void seedTestUsers() {
        if (userRepository.count() > 0) {
            return; // Data already seeded
        }

        List<User> testUsers = Arrays.asList(
            createTestUser("candidate1@example.com", "John Doe", "password123", Role.CANDIDATE),
            createTestUser("candidate2@example.com", "Jane Smith", "password123", Role.CANDIDATE),
            createTestUser("employer1@example.com", "Tech Corp", "password123", Role.EMPLOYER),
            createTestUser("employer2@example.com", "StartupXYZ", "password123", Role.EMPLOYER),
            createTestUser("admin@example.com", "Admin User", "admin123", Role.ADMIN)
        );

        userRepository.saveAll(testUsers);
    }

    /**
     * Create a test user with given parameters
     */
    private User createTestUser(String email, String name, String password, Role role) {
        User user = new User();
        user.setEmail(email);
        user.setName(name);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);
        user.setActive(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        return user;
    }

    /**
     * Clean up test data
     */
    public void cleanupTestData() {
        userRepository.deleteAll();
    }

    /**
     * Get test user by email
     */
    public User getTestUser(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    /**
     * Create test candidate user
     */
    public User createTestCandidate() {
        return createTestUser("test.candidate@example.com", "Test Candidate", "password123", Role.CANDIDATE);
    }

    /**
     * Create test employer user
     */
    public User createTestEmployer() {
        return createTestUser("test.employer@example.com", "Test Employer", "password123", Role.EMPLOYER);
    }

    /**
     * Create test admin user
     */
    public User createTestAdmin() {
        return createTestUser("test.admin@example.com", "Test Admin", "admin123", Role.ADMIN);
    }
}