package com.jobapp.user.repository;

import com.jobapp.user.model.Admin;
import com.jobapp.user.model.AdminRole;
import com.jobapp.user.model.AdminPermission;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for AdminRepository
 * Requirements: 5.1, 5.4
 */
@DataMongoTest
@TestPropertySource(properties = {
    "spring.data.mongodb.uri=mongodb://localhost:27017/jobapp_test"
})
class AdminRepositoryTest {
    
    @Autowired
    private AdminRepository adminRepository;
    
    private Admin testAdmin1;
    private Admin testAdmin2;
    private Admin testAdmin3;
    
    @BeforeEach
    void setUp() {
        adminRepository.deleteAll();
        
        // Create test admin 1 - Super Admin
        testAdmin1 = new Admin("super.admin@jobapp.com", "hashedPassword123", "Super Admin");
        testAdmin1.setRole(AdminRole.SUPER_ADMIN);
        testAdmin1.setCreatedBy("system");
        testAdmin1.recordLogin();
        testAdmin1.recordAction("System initialization");
        testAdmin1.recordLogin(); // Second login
        testAdmin1.recordAction("User management");
        testAdmin1.recordAction("Job moderation");
        
        // Create test admin 2 - Regular Admin
        testAdmin2 = new Admin("admin@jobapp.com", "hashedPassword456", "Regular Admin");
        testAdmin2.setRole(AdminRole.ADMIN);
        testAdmin2.setPermissions(Arrays.asList(
            AdminPermission.VIEW_USERS,
            AdminPermission.MANAGE_USERS,
            AdminPermission.VIEW_JOBS,
            AdminPermission.MODERATE_JOBS
        ));
        testAdmin2.setCreatedBy(testAdmin1.getId());
        testAdmin2.recordLogin();
        testAdmin2.recordAction("Approved employer registration");
        
        // Create test admin 3 - Inactive Moderator
        testAdmin3 = new Admin("moderator@jobapp.com", "hashedPassword789", "Inactive Moderator");
        testAdmin3.setRole(AdminRole.MODERATOR);
        testAdmin3.setPermissions(Arrays.asList(
            AdminPermission.VIEW_USERS,
            AdminPermission.MODERATE_CONTENT,
            AdminPermission.HANDLE_REPORTS
        ));
        testAdmin3.setIsActive(false);
        testAdmin3.setCreatedBy(testAdmin1.getId());
        
        adminRepository.save(testAdmin1);
        adminRepository.save(testAdmin2);
        adminRepository.save(testAdmin3);
    }
    
    @Test
    @DisplayName("Should find admin by email")
    void testFindByEmail() {
        // When
        Optional<Admin> found = adminRepository.findByEmail("super.admin@jobapp.com");
        
        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Super Admin");
        assertThat(found.get().getRole()).isEqualTo(AdminRole.SUPER_ADMIN);
    }
    
    @Test
    @DisplayName("Should return empty when admin not found by email")
    void testFindByEmailNotFound() {
        // When
        Optional<Admin> found = adminRepository.findByEmail("nonexistent@jobapp.com");
        
        // Then
        assertThat(found).isEmpty();
    }
    
    @Test
    @DisplayName("Should find admin by email and active status")
    void testFindByEmailAndIsActive() {
        // When
        Optional<Admin> activeAdmin = adminRepository.findByEmailAndIsActive("admin@jobapp.com", true);
        Optional<Admin> inactiveAdmin = adminRepository.findByEmailAndIsActive("moderator@jobapp.com", false);
        Optional<Admin> wrongStatus = adminRepository.findByEmailAndIsActive("admin@jobapp.com", false);
        
        // Then
        assertThat(activeAdmin).isPresent();
        assertThat(activeAdmin.get().getName()).isEqualTo("Regular Admin");
        
        assertThat(inactiveAdmin).isPresent();
        assertThat(inactiveAdmin.get().getName()).isEqualTo("Inactive Moderator");
        
        assertThat(wrongStatus).isEmpty();
    }
    
    @Test
    @DisplayName("Should check if admin exists by email")
    void testExistsByEmail() {
        // When & Then
        assertThat(adminRepository.existsByEmail("super.admin@jobapp.com")).isTrue();
        assertThat(adminRepository.existsByEmail("nonexistent@jobapp.com")).isFalse();
    }
    
    @Test
    @DisplayName("Should find admins by role")
    void testFindByRole() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        
        // When
        Page<Admin> superAdmins = adminRepository.findByRole(AdminRole.SUPER_ADMIN, pageable);
        Page<Admin> regularAdmins = adminRepository.findByRole(AdminRole.ADMIN, pageable);
        Page<Admin> moderators = adminRepository.findByRole(AdminRole.MODERATOR, pageable);
        
        // Then
        assertThat(superAdmins.getContent()).hasSize(1);
        assertThat(superAdmins.getContent().get(0).getName()).isEqualTo("Super Admin");
        
        assertThat(regularAdmins.getContent()).hasSize(1);
        assertThat(regularAdmins.getContent().get(0).getName()).isEqualTo("Regular Admin");
        
        assertThat(moderators.getContent()).hasSize(1);
        assertThat(moderators.getContent().get(0).getName()).isEqualTo("Inactive Moderator");
    }
    
    @Test
    @DisplayName("Should find admins by active status")
    void testFindByIsActive() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        
        // When
        Page<Admin> activeAdmins = adminRepository.findByIsActive(true, pageable);
        Page<Admin> inactiveAdmins = adminRepository.findByIsActive(false, pageable);
        
        // Then
        assertThat(activeAdmins.getContent()).hasSize(2);
        assertThat(activeAdmins.getContent())
            .extracting(Admin::getName)
            .containsExactlyInAnyOrder("Super Admin", "Regular Admin");
        
        assertThat(inactiveAdmins.getContent()).hasSize(1);
        assertThat(inactiveAdmins.getContent().get(0).getName()).isEqualTo("Inactive Moderator");
    }
    
    @Test
    @DisplayName("Should find admins by role and active status")
    void testFindByRoleAndIsActive() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        
        // When
        Page<Admin> activeSuperAdmins = adminRepository.findByRoleAndIsActive(AdminRole.SUPER_ADMIN, true, pageable);
        Page<Admin> activeRegularAdmins = adminRepository.findByRoleAndIsActive(AdminRole.ADMIN, true, pageable);
        Page<Admin> inactiveModerators = adminRepository.findByRoleAndIsActive(AdminRole.MODERATOR, false, pageable);
        
        // Then
        assertThat(activeSuperAdmins.getContent()).hasSize(1);
        assertThat(activeSuperAdmins.getContent().get(0).getName()).isEqualTo("Super Admin");
        
        assertThat(activeRegularAdmins.getContent()).hasSize(1);
        assertThat(activeRegularAdmins.getContent().get(0).getName()).isEqualTo("Regular Admin");
        
        assertThat(inactiveModerators.getContent()).hasSize(1);
        assertThat(inactiveModerators.getContent().get(0).getName()).isEqualTo("Inactive Moderator");
    }
    
    @Test
    @DisplayName("Should find admins created by specific admin")
    void testFindByCreatedBy() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        String superAdminId = testAdmin1.getId();
        
        // When
        Page<Admin> createdBySuperAdmin = adminRepository.findByCreatedBy(superAdminId, pageable);
        Page<Admin> createdBySystem = adminRepository.findByCreatedBy("system", pageable);
        
        // Then
        assertThat(createdBySuperAdmin.getContent()).hasSize(2);
        assertThat(createdBySuperAdmin.getContent())
            .extracting(Admin::getName)
            .containsExactlyInAnyOrder("Regular Admin", "Inactive Moderator");
        
        assertThat(createdBySystem.getContent()).hasSize(1);
        assertThat(createdBySystem.getContent().get(0).getName()).isEqualTo("Super Admin");
    }
    
    @Test
    @DisplayName("Should find admins by name containing text")
    void testFindByNameContainingIgnoreCase() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        
        // When
        Page<Admin> adminsByName = adminRepository.findByNameContainingIgnoreCase("admin", pageable);
        Page<Admin> moderatorsByName = adminRepository.findByNameContainingIgnoreCase("moderator", pageable);
        
        // Then
        assertThat(adminsByName.getContent()).hasSize(2);
        assertThat(adminsByName.getContent())
            .extracting(Admin::getName)
            .containsExactlyInAnyOrder("Super Admin", "Regular Admin");
        
        assertThat(moderatorsByName.getContent()).hasSize(1);
        assertThat(moderatorsByName.getContent().get(0).getName()).isEqualTo("Inactive Moderator");
    }
    
    @Test
    @DisplayName("Should find super admins")
    void testFindSuperAdmins() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        
        // When
        Page<Admin> superAdmins = adminRepository.findSuperAdmins(pageable);
        
        // Then
        assertThat(superAdmins.getContent()).hasSize(1);
        assertThat(superAdmins.getContent().get(0).getName()).isEqualTo("Super Admin");
        assertThat(superAdmins.getContent().get(0).getRole()).isEqualTo(AdminRole.SUPER_ADMIN);
        assertThat(superAdmins.getContent().get(0).getIsActive()).isTrue();
    }
    
    @Test
    @DisplayName("Should find inactive admins")
    void testFindInactiveAdmins() {
        // Given
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        Pageable pageable = PageRequest.of(0, 10);
        
        // When
        Page<Admin> inactiveAdmins = adminRepository.findInactiveAdmins(thirtyDaysAgo, pageable);
        
        // Then
        assertThat(inactiveAdmins.getContent()).hasSize(1);
        assertThat(inactiveAdmins.getContent().get(0).getName()).isEqualTo("Inactive Moderator");
    }
    
    @Test
    @DisplayName("Should find recently created admins")
    void testFindRecentlyCreated() {
        // Given
        LocalDateTime twoDaysAgo = LocalDateTime.now().minusDays(2);
        Pageable pageable = PageRequest.of(0, 10);
        
        // When
        Page<Admin> recentAdmins = adminRepository.findRecentlyCreated(twoDaysAgo, pageable);
        
        // Then
        assertThat(recentAdmins.getContent()).hasSize(3); // All admins are recent in test
    }
    
    @Test
    @DisplayName("Should find admins created within date range")
    void testFindByCreatedAtBetween() {
        // Given
        LocalDateTime startDate = LocalDateTime.now().minusDays(1);
        LocalDateTime endDate = LocalDateTime.now().plusDays(1);
        Pageable pageable = PageRequest.of(0, 10);
        
        // When
        Page<Admin> adminsInRange = adminRepository.findByCreatedAtBetween(startDate, endDate, pageable);
        
        // Then
        assertThat(adminsInRange.getContent()).hasSize(3); // All admins created today
    }
    
    @Test
    @DisplayName("Should count admins by various criteria")
    void testCountMethods() {
        // When & Then
        assertThat(adminRepository.countByRole(AdminRole.SUPER_ADMIN)).isEqualTo(1);
        assertThat(adminRepository.countByRole(AdminRole.ADMIN)).isEqualTo(1);
        assertThat(adminRepository.countByRole(AdminRole.MODERATOR)).isEqualTo(1);
        
        assertThat(adminRepository.countByIsActive(true)).isEqualTo(2);
        assertThat(adminRepository.countByIsActive(false)).isEqualTo(1);
        
        assertThat(adminRepository.countByRoleAndIsActive(AdminRole.SUPER_ADMIN, true)).isEqualTo(1);
        assertThat(adminRepository.countByRoleAndIsActive(AdminRole.MODERATOR, false)).isEqualTo(1);
        
        assertThat(adminRepository.countByCreatedBy("system")).isEqualTo(1);
        assertThat(adminRepository.countByCreatedBy(testAdmin1.getId())).isEqualTo(2);
    }
    
    @Test
    @DisplayName("Should test admin business methods")
    void testAdminBusinessMethods() {
        // Test login recording
        int initialLoginCount = testAdmin2.getLoginCount();
        testAdmin2.recordLogin();
        assertThat(testAdmin2.getLoginCount()).isEqualTo(initialLoginCount + 1);
        assertThat(testAdmin2.getLastLogin()).isNotNull();
        
        // Test action recording
        int initialActionCount = testAdmin2.getActionsPerformed();
        testAdmin2.recordAction("Test action");
        assertThat(testAdmin2.getActionsPerformed()).isEqualTo(initialActionCount + 1);
        assertThat(testAdmin2.getLastAction()).isEqualTo("Test action");
        assertThat(testAdmin2.getLastActionAt()).isNotNull();
        
        // Test permission management
        testAdmin2.addPermission(AdminPermission.VIEW_ANALYTICS);
        assertThat(testAdmin2.hasPermission(AdminPermission.VIEW_ANALYTICS)).isTrue();
        
        testAdmin2.removePermission(AdminPermission.VIEW_ANALYTICS);
        assertThat(testAdmin2.hasPermission(AdminPermission.VIEW_ANALYTICS)).isFalse();
        
        // Test super admin permissions
        assertThat(testAdmin1.hasPermission(AdminPermission.MANAGE_ADMINS)).isTrue(); // Super admin has all permissions
        
        // Test canPerformActions
        assertThat(testAdmin1.canPerformActions()).isTrue(); // Active admin
        assertThat(testAdmin3.canPerformActions()).isFalse(); // Inactive admin
        
        // Test display name
        assertThat(testAdmin1.getDisplayName()).contains("Super Admin");
        assertThat(testAdmin1.getDisplayName()).contains("Super Admin");
    }
    
    @Test
    @DisplayName("Should test admin role methods")
    void testAdminRoleMethods() {
        // Test role hierarchy
        assertThat(AdminRole.SUPER_ADMIN.hasHigherPrivilegesThan(AdminRole.ADMIN)).isTrue();
        assertThat(AdminRole.ADMIN.hasHigherPrivilegesThan(AdminRole.MODERATOR)).isTrue();
        assertThat(AdminRole.MODERATOR.hasHigherPrivilegesThan(AdminRole.SUPPORT)).isTrue();
        
        // Test role management capabilities
        assertThat(AdminRole.SUPER_ADMIN.canManage(AdminRole.ADMIN)).isTrue();
        assertThat(AdminRole.ADMIN.canManage(AdminRole.MODERATOR)).isTrue();
        assertThat(AdminRole.MODERATOR.canManage(AdminRole.ADMIN)).isFalse();
        
        // Test display names
        assertThat(AdminRole.SUPER_ADMIN.getDisplayName()).isEqualTo("Super Admin");
        assertThat(AdminRole.ADMIN.getDisplayName()).isEqualTo("Admin");
    }
    
    @Test
    @DisplayName("Should test admin permission methods")
    void testAdminPermissionMethods() {
        // Test permission categories
        AdminPermission[] userManagementPerms = AdminPermission.getByCategory(AdminPermission.PermissionCategory.USER_MANAGEMENT);
        assertThat(userManagementPerms).contains(
            AdminPermission.VIEW_USERS,
            AdminPermission.MANAGE_USERS,
            AdminPermission.APPROVE_EMPLOYERS,
            AdminPermission.BLOCK_USERS
        );
        
        AdminPermission[] jobManagementPerms = AdminPermission.getByCategory(AdminPermission.PermissionCategory.JOB_MANAGEMENT);
        assertThat(jobManagementPerms).contains(
            AdminPermission.VIEW_JOBS,
            AdminPermission.MANAGE_JOBS,
            AdminPermission.MODERATE_JOBS,
            AdminPermission.FEATURE_JOBS
        );
        
        // Test all permissions
        AdminPermission[] allPerms = AdminPermission.getAllPermissions();
        assertThat(allPerms).contains(AdminPermission.VIEW_USERS, AdminPermission.MANAGE_ADMINS, AdminPermission.VIEW_ANALYTICS);
        
        // Test display names
        assertThat(AdminPermission.VIEW_USERS.getDisplayName()).isEqualTo("View Users");
        assertThat(AdminPermission.MANAGE_USERS.getDescription()).contains("Create, update, and delete");
    }
    
    @Test
    @DisplayName("Should handle pagination correctly")
    void testPagination() {
        // Given
        Pageable firstPage = PageRequest.of(0, 2);
        Pageable secondPage = PageRequest.of(1, 2);
        
        // When
        Page<Admin> page1 = adminRepository.findByIsActive(true, firstPage);
        Page<Admin> page2 = adminRepository.findByIsActive(true, secondPage);
        
        // Then
        assertThat(page1.getContent()).hasSize(2);
        assertThat(page1.getTotalElements()).isEqualTo(2);
        assertThat(page1.getTotalPages()).isEqualTo(1);
        assertThat(page1.hasNext()).isFalse();
        
        assertThat(page2.getContent()).isEmpty();
    }
}