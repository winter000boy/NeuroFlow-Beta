package com.jobapp.user.repository;

import com.jobapp.user.model.Admin;
import com.jobapp.user.model.AdminRole;
import com.jobapp.user.model.AdminPermission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Admin entity operations
 * Requirements: 5.1, 5.4
 */
@Repository
public interface AdminRepository extends MongoRepository<Admin, String> {
    
    /**
     * Find admin by email address
     * @param email the email to search for
     * @return Optional containing the admin if found
     */
    Optional<Admin> findByEmail(String email);
    
    /**
     * Find admin by email and active status
     * @param email the email to search for
     * @param isActive the active status
     * @return Optional containing the admin if found
     */
    Optional<Admin> findByEmailAndIsActive(String email, Boolean isActive);
    
    /**
     * Check if admin exists by email
     * @param email the email to check
     * @return true if admin exists
     */
    boolean existsByEmail(String email);
    
    /**
     * Find admins by role
     * @param role the admin role
     * @param pageable pagination information
     * @return Page of admins with the specified role
     */
    Page<Admin> findByRole(AdminRole role, Pageable pageable);
    
    /**
     * Find admins by active status
     * @param isActive the active status
     * @param pageable pagination information
     * @return Page of admins with the specified active status
     */
    Page<Admin> findByIsActive(Boolean isActive, Pageable pageable);
    
    /**
     * Find admins by role and active status
     * @param role the admin role
     * @param isActive the active status
     * @param pageable pagination information
     * @return Page of admins matching both criteria
     */
    Page<Admin> findByRoleAndIsActive(AdminRole role, Boolean isActive, Pageable pageable);
    
    /**
     * Find admins created by a specific admin
     * @param createdBy the ID of the admin who created them
     * @param pageable pagination information
     * @return Page of admins created by the specified admin
     */
    Page<Admin> findByCreatedBy(String createdBy, Pageable pageable);
    
    /**
     * Find admins with specific permission
     * @param permission the permission to search for
     * @param pageable pagination information
     * @return Page of admins with the specified permission
     */
    @Query("{ 'permissions': ?0 }")
    Page<Admin> findByPermission(AdminPermission permission, Pageable pageable);
    
    /**
     * Find admins created within a date range
     * @param startDate the start date
     * @param endDate the end date
     * @param pageable pagination information
     * @return Page of admins created within the date range
     */
    Page<Admin> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
    
    /**
     * Find admins who logged in within a date range
     * @param startDate the start date
     * @param endDate the end date
     * @param pageable pagination information
     * @return Page of admins who logged in within the date range
     */
    Page<Admin> findByLastLoginBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
    
    /**
     * Find admins who performed actions within a date range
     * @param startDate the start date
     * @param endDate the end date
     * @param pageable pagination information
     * @return Page of admins who performed actions within the date range
     */
    Page<Admin> findByLastActionAtBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
    
    /**
     * Find most active admins by action count
     * @param pageable pagination information
     * @return Page of admins ordered by actions performed (descending)
     */
    @Query("{ 'is_active': true }")
    Page<Admin> findMostActiveAdmins(Pageable pageable);
    
    /**
     * Find admins by name containing text (case-insensitive)
     * @param name the name to search for
     * @param pageable pagination information
     * @return Page of admins with matching names
     */
    Page<Admin> findByNameContainingIgnoreCase(String name, Pageable pageable);
    
    /**
     * Count admins by role
     * @param role the admin role
     * @return count of admins with the specified role
     */
    long countByRole(AdminRole role);
    
    /**
     * Count admins by active status
     * @param isActive the active status
     * @return count of admins with the specified active status
     */
    long countByIsActive(Boolean isActive);
    
    /**
     * Count admins by role and active status
     * @param role the admin role
     * @param isActive the active status
     * @return count of admins matching both criteria
     */
    long countByRoleAndIsActive(AdminRole role, Boolean isActive);
    
    /**
     * Count admins created by a specific admin
     * @param createdBy the ID of the admin who created them
     * @return count of admins created by the specified admin
     */
    long countByCreatedBy(String createdBy);
    
    /**
     * Find admins who haven't logged in for a specified number of days
     * @param daysAgo the number of days ago
     * @param pageable pagination information
     * @return Page of inactive admins
     */
    @Query("{ $or: [ { 'last_login': { $lt: ?0 } }, { 'last_login': null } ] }")
    Page<Admin> findInactiveAdmins(LocalDateTime daysAgo, Pageable pageable);
    
    /**
     * Find recently created admins
     * @param daysAgo the number of days ago
     * @param pageable pagination information
     * @return Page of recently created admins
     */
    @Query("{ 'created_at': { $gte: ?0 } }")
    Page<Admin> findRecentlyCreated(LocalDateTime daysAgo, Pageable pageable);
    
    /**
     * Find all super admins
     * @param pageable pagination information
     * @return Page of super admins
     */
    @Query("{ 'role': 'SUPER_ADMIN', 'is_active': true }")
    Page<Admin> findSuperAdmins(Pageable pageable);
    
    /**
     * Count total logins across all admins
     * @return total login count
     */
    @Aggregation(pipeline = {
        "{ $group: { _id: null, totalLogins: { $sum: '$login_count' } } }"
    })
    Long getTotalLoginCount();
    
    /**
     * Count total actions performed across all admins
     * @return total actions count
     */
    @Aggregation(pipeline = {
        "{ $group: { _id: null, totalActions: { $sum: '$actions_performed' } } }"
    })
    Long getTotalActionsCount();
    
    /**
     * Get admin activity statistics by role
     * @return list of admin activity statistics
     */
    @Aggregation(pipeline = {
        "{ $group: { " +
        "    _id: '$role', " +
        "    count: { $sum: 1 }, " +
        "    activeCount: { $sum: { $cond: ['$is_active', 1, 0] } }, " +
        "    totalLogins: { $sum: '$login_count' }, " +
        "    totalActions: { $sum: '$actions_performed' }, " +
        "    avgLogins: { $avg: '$login_count' }, " +
        "    avgActions: { $avg: '$actions_performed' } " +
        "} }",
        "{ $sort: { _id: 1 } }"
    })
    List<AdminActivityStats> getAdminActivityStatsByRole();
    
    /**
     * Get daily admin login statistics for the last N days
     * @param daysAgo the number of days ago to start from
     * @return list of daily login statistics
     */
    @Aggregation(pipeline = {
        "{ $match: { 'last_login': { $gte: ?0 } } }",
        "{ $group: { " +
        "    _id: { $dateToString: { format: '%Y-%m-%d', date: '$last_login' } }, " +
        "    loginCount: { $sum: 1 } " +
        "} }",
        "{ $sort: { _id: 1 } }"
    })
    List<DailyLoginStats> getDailyLoginStats(LocalDateTime daysAgo);
    
    /**
     * Get admin performance metrics
     * @return list of admin performance metrics
     */
    @Aggregation(pipeline = {
        "{ $match: { 'is_active': true } }",
        "{ $project: { " +
        "    name: 1, " +
        "    role: 1, " +
        "    loginCount: '$login_count', " +
        "    actionsPerformed: '$actions_performed', " +
        "    lastLogin: '$last_login', " +
        "    lastAction: '$last_action', " +
        "    lastActionAt: '$last_action_at', " +
        "    createdAt: '$created_at', " +
        "    daysSinceCreated: { $divide: [{ $subtract: [new Date(), '$created_at'] }, 86400000] }, " +
        "    daysSinceLastLogin: { $divide: [{ $subtract: [new Date(), '$last_login'] }, 86400000] } " +
        "} }",
        "{ $sort: { actionsPerformed: -1 } }"
    })
    List<AdminPerformanceMetrics> getAdminPerformanceMetrics();
    
    /**
     * Find all admins (for admin management purposes)
     * @param pageable pagination information
     * @return Page of all admins
     */
    Page<Admin> findAll(Pageable pageable);
    
    /**
     * Interface for admin activity statistics
     */
    interface AdminActivityStats {
        AdminRole getRole();
        Long getCount();
        Long getActiveCount();
        Long getTotalLogins();
        Long getTotalActions();
        Double getAvgLogins();
        Double getAvgActions();
    }
    
    /**
     * Interface for daily login statistics
     */
    interface DailyLoginStats {
        String getDate();
        Long getLoginCount();
    }
    
    /**
     * Interface for admin performance metrics
     */
    interface AdminPerformanceMetrics {
        String getName();
        AdminRole getRole();
        Integer getLoginCount();
        Integer getActionsPerformed();
        LocalDateTime getLastLogin();
        String getLastAction();
        LocalDateTime getLastActionAt();
        LocalDateTime getCreatedAt();
        Double getDaysSinceCreated();
        Double getDaysSinceLastLogin();
    }
}