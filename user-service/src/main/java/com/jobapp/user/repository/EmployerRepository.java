package com.jobapp.user.repository;

import com.jobapp.user.model.Employer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Employer entity operations
 * Requirements: 3.1, 3.4
 */
@Repository
public interface EmployerRepository extends MongoRepository<Employer, String> {
    
    /**
     * Find employer by email address
     * @param email the email to search for
     * @return Optional containing the employer if found
     */
    Optional<Employer> findByEmail(String email);
    
    /**
     * Find employer by email and active status
     * @param email the email to search for
     * @param isActive the active status
     * @return Optional containing the employer if found
     */
    Optional<Employer> findByEmailAndIsActive(String email, Boolean isActive);
    
    /**
     * Check if employer exists by email
     * @param email the email to check
     * @return true if employer exists
     */
    boolean existsByEmail(String email);
    
    /**
     * Find employers by approval status
     * @param isApproved the approval status
     * @param pageable pagination information
     * @return Page of employers with the specified approval status
     */
    Page<Employer> findByIsApproved(Boolean isApproved, Pageable pageable);
    
    /**
     * Find employers by approval and active status
     * @param isApproved the approval status
     * @param isActive the active status
     * @param pageable pagination information
     * @return Page of employers matching both statuses
     */
    Page<Employer> findByIsApprovedAndIsActive(Boolean isApproved, Boolean isActive, Pageable pageable);
    
    /**
     * Find employers pending approval (not approved and active)
     * @param pageable pagination information
     * @return Page of employers pending approval
     */
    @Query("{ 'is_approved': false, 'is_active': true }")
    Page<Employer> findPendingApproval(Pageable pageable);
    
    /**
     * Find approved and active employers (can post jobs)
     * @param pageable pagination information
     * @return Page of employers who can post jobs
     */
    @Query("{ 'is_approved': true, 'is_active': true }")
    Page<Employer> findActiveApprovedEmployers(Pageable pageable);
    
    /**
     * Find employers by company name (case-insensitive partial match)
     * @param companyName the company name to search for
     * @param pageable pagination information
     * @return Page of employers matching the company name
     */
    Page<Employer> findByCompanyNameContainingIgnoreCase(String companyName, Pageable pageable);
    
    /**
     * Find employers approved by specific admin
     * @param adminId the admin ID who approved
     * @param pageable pagination information
     * @return Page of employers approved by the admin
     */
    Page<Employer> findByApprovedBy(String adminId, Pageable pageable);
    
    /**
     * Find employers approved within a date range
     * @param startDate the start date
     * @param endDate the end date
     * @param pageable pagination information
     * @return Page of employers approved within the date range
     */
    Page<Employer> findByApprovalDateBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
    
    /**
     * Find employers created within a date range
     * @param startDate the start date
     * @param endDate the end date
     * @param pageable pagination information
     * @return Page of employers created within the date range
     */
    Page<Employer> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
    
    /**
     * Find employers with logos uploaded
     * @param pageable pagination information
     * @return Page of employers who have uploaded logos
     */
    @Query("{ 'logo_url': { $ne: null, $ne: '' } }")
    Page<Employer> findEmployersWithLogo(Pageable pageable);
    
    /**
     * Find employers by multiple criteria
     * @param companyName the company name to search for (optional)
     * @param isApproved the approval status (optional)
     * @param isActive the active status
     * @param pageable pagination information
     * @return Page of employers matching the criteria
     */
    @Query("{ $and: [ " +
           "{ $or: [ { 'company_name': { $regex: ?0, $options: 'i' } }, { $expr: { $eq: [?0, null] } } ] }, " +
           "{ $or: [ { 'is_approved': ?1 }, { $expr: { $eq: [?1, null] } } ] }, " +
           "{ 'is_active': ?2 } " +
           "] }")
    Page<Employer> findByCriteria(String companyName, Boolean isApproved, Boolean isActive, Pageable pageable);
    
    /**
     * Count employers by approval status
     * @param isApproved the approval status
     * @return count of employers
     */
    long countByIsApproved(Boolean isApproved);
    
    /**
     * Count employers by approval and active status
     * @param isApproved the approval status
     * @param isActive the active status
     * @return count of employers
     */
    long countByIsApprovedAndIsActive(Boolean isApproved, Boolean isActive);
    
    /**
     * Count employers pending approval
     * @return count of employers pending approval
     */
    @Query(value = "{ 'is_approved': false, 'is_active': true }", count = true)
    long countPendingApproval();
    
    /**
     * Count employers with logos
     * @return count of employers with logos
     */
    @Query(value = "{ 'logo_url': { $ne: null, $ne: '' } }", count = true)
    long countEmployersWithLogo();
    
    /**
     * Find employers registered in the last N days
     * @param daysAgo the number of days ago
     * @param pageable pagination information
     * @return Page of recently registered employers
     */
    @Query("{ 'created_at': { $gte: ?0 } }")
    Page<Employer> findRecentlyRegistered(LocalDateTime daysAgo, Pageable pageable);
    
    /**
     * Find employers approved in the last N days
     * @param daysAgo the number of days ago
     * @param pageable pagination information
     * @return Page of recently approved employers
     */
    @Query("{ 'approval_date': { $gte: ?0 } }")
    Page<Employer> findRecentlyApproved(LocalDateTime daysAgo, Pageable pageable);
    
    /**
     * Find employers by rejection reason containing text
     * @param reason the reason text to search for
     * @param pageable pagination information
     * @return Page of employers with matching rejection reasons
     */
    Page<Employer> findByRejectionReasonContainingIgnoreCase(String reason, Pageable pageable);
    
    /**
     * Find all employers (for admin purposes)
     * @param pageable pagination information
     * @return Page of all employers
     */
    Page<Employer> findAll(Pageable pageable);
    
    /**
     * Find employers by active status
     * @param isActive the active status
     * @param pageable pagination information
     * @return Page of employers with the specified active status
     */
    Page<Employer> findByIsActive(Boolean isActive, Pageable pageable);
    
    /**
     * Find employers by company name or email containing text (case-insensitive)
     * @param companyName the company name to search for
     * @param email the email to search for
     * @param pageable pagination information
     * @return Page of employers matching company name or email
     */
    @Query("{ $or: [ { 'company_name': { $regex: ?0, $options: 'i' } }, { 'email': { $regex: ?1, $options: 'i' } } ] }")
    Page<Employer> findByCompanyNameContainingIgnoreCaseOrEmailContainingIgnoreCase(String companyName, String email, Pageable pageable);
    
    /**
     * Find employers by company name or email containing text and approval status
     * @param companyName the company name to search for
     * @param email the email to search for
     * @param isApproved the approval status
     * @param pageable pagination information
     * @return Page of employers matching criteria
     */
    @Query("{ $and: [ { $or: [ { 'company_name': { $regex: ?0, $options: 'i' } }, { 'email': { $regex: ?1, $options: 'i' } } ] }, { 'is_approved': ?2 } ] }")
    Page<Employer> findByCompanyNameContainingIgnoreCaseOrEmailContainingIgnoreCaseAndIsApproved(String companyName, String email, Boolean isApproved, Pageable pageable);
    
    /**
     * Find employers by company name or email containing text and active status
     * @param companyName the company name to search for
     * @param email the email to search for
     * @param isActive the active status
     * @param pageable pagination information
     * @return Page of employers matching criteria
     */
    @Query("{ $and: [ { $or: [ { 'company_name': { $regex: ?0, $options: 'i' } }, { 'email': { $regex: ?1, $options: 'i' } } ] }, { 'is_active': ?2 } ] }")
    Page<Employer> findByCompanyNameContainingIgnoreCaseOrEmailContainingIgnoreCaseAndIsActive(String companyName, String email, Boolean isActive, Pageable pageable);
    
    /**
     * Find employers by company name or email containing text, approval status and active status
     * @param companyName the company name to search for
     * @param email the email to search for
     * @param isApproved the approval status
     * @param isActive the active status
     * @param pageable pagination information
     * @return Page of employers matching criteria
     */
    @Query("{ $and: [ { $or: [ { 'company_name': { $regex: ?0, $options: 'i' } }, { 'email': { $regex: ?1, $options: 'i' } } ] }, { 'is_approved': ?2 }, { 'is_active': ?3 } ] }")
    Page<Employer> findByCompanyNameContainingIgnoreCaseOrEmailContainingIgnoreCaseAndIsApprovedAndIsActive(String companyName, String email, Boolean isApproved, Boolean isActive, Pageable pageable);
}