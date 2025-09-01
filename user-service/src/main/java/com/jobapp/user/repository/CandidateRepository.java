package com.jobapp.user.repository;

import com.jobapp.user.model.Candidate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Candidate entity operations
 * Requirements: 1.1, 1.2, 1.5
 */
@Repository
public interface CandidateRepository extends MongoRepository<Candidate, String> {
    
    /**
     * Find candidate by email address
     * @param email the email to search for
     * @return Optional containing the candidate if found
     */
    Optional<Candidate> findByEmail(String email);
    
    /**
     * Find candidate by email and active status
     * @param email the email to search for
     * @param isActive the active status
     * @return Optional containing the candidate if found
     */
    Optional<Candidate> findByEmailAndIsActive(String email, Boolean isActive);
    
    /**
     * Check if candidate exists by email
     * @param email the email to check
     * @return true if candidate exists
     */
    boolean existsByEmail(String email);
    
    /**
     * Find all active candidates
     * @param isActive the active status
     * @param pageable pagination information
     * @return Page of active candidates
     */
    Page<Candidate> findByIsActive(Boolean isActive, Pageable pageable);
    
    /**
     * Find candidates by degree
     * @param degree the degree to search for
     * @param pageable pagination information
     * @return Page of candidates with the specified degree
     */
    Page<Candidate> findByDegreeContainingIgnoreCase(String degree, Pageable pageable);
    
    /**
     * Find candidates by graduation year range
     * @param startYear the start year (inclusive)
     * @param endYear the end year (inclusive)
     * @param pageable pagination information
     * @return Page of candidates within the graduation year range
     */
    Page<Candidate> findByGraduationYearBetween(Integer startYear, Integer endYear, Pageable pageable);
    
    /**
     * Find candidates with resume uploaded
     * @param pageable pagination information
     * @return Page of candidates who have uploaded resumes
     */
    @Query("{ 'resume_url': { $ne: null, $ne: '' } }")
    Page<Candidate> findCandidatesWithResume(Pageable pageable);
    
    /**
     * Find candidates with LinkedIn profiles
     * @param pageable pagination information
     * @return Page of candidates who have LinkedIn profiles
     */
    @Query("{ 'linkedin_profile': { $ne: null, $ne: '' } }")
    Page<Candidate> findCandidatesWithLinkedIn(Pageable pageable);
    
    /**
     * Find candidates created within a date range
     * @param startDate the start date
     * @param endDate the end date
     * @param pageable pagination information
     * @return Page of candidates created within the date range
     */
    Page<Candidate> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
    
    /**
     * Search candidates by name (case-insensitive partial match)
     * @param name the name to search for
     * @param pageable pagination information
     * @return Page of candidates matching the name
     */
    Page<Candidate> findByNameContainingIgnoreCase(String name, Pageable pageable);
    
    /**
     * Find candidates by multiple criteria
     * @param degree the degree to search for (optional)
     * @param graduationYear the graduation year (optional)
     * @param isActive the active status
     * @param pageable pagination information
     * @return Page of candidates matching the criteria
     */
    @Query("{ $and: [ " +
           "{ $or: [ { 'degree': { $regex: ?0, $options: 'i' } }, { $expr: { $eq: [?0, null] } } ] }, " +
           "{ $or: [ { 'graduation_year': ?1 }, { $expr: { $eq: [?1, null] } } ] }, " +
           "{ 'is_active': ?2 } " +
           "] }")
    Page<Candidate> findByCriteria(String degree, Integer graduationYear, Boolean isActive, Pageable pageable);
    
    /**
     * Count candidates by active status
     * @param isActive the active status
     * @return count of candidates
     */
    long countByIsActive(Boolean isActive);
    
    /**
     * Count candidates with resumes
     * @return count of candidates with resumes
     */
    @Query(value = "{ 'resume_url': { $ne: null, $ne: '' } }", count = true)
    long countCandidatesWithResume();
    
    /**
     * Find candidates registered in the last N days
     * @param daysAgo the number of days ago
     * @param pageable pagination information
     * @return Page of recently registered candidates
     */
    @Query("{ 'created_at': { $gte: ?0 } }")
    Page<Candidate> findRecentlyRegistered(LocalDateTime daysAgo, Pageable pageable);
    
    /**
     * Find all candidates (for admin purposes)
     * @param pageable pagination information
     * @return Page of all candidates
     */
    Page<Candidate> findAll(Pageable pageable);
    
    /**
     * Find candidates by name or email containing text (case-insensitive)
     * @param name the name to search for
     * @param email the email to search for
     * @param pageable pagination information
     * @return Page of candidates matching name or email
     */
    @Query("{ $or: [ { 'name': { $regex: ?0, $options: 'i' } }, { 'email': { $regex: ?1, $options: 'i' } } ] }")
    Page<Candidate> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(String name, String email, Pageable pageable);
    
    /**
     * Find candidates by name or email containing text and active status
     * @param name the name to search for
     * @param email the email to search for
     * @param isActive the active status
     * @param pageable pagination information
     * @return Page of candidates matching criteria
     */
    @Query("{ $and: [ { $or: [ { 'name': { $regex: ?0, $options: 'i' } }, { 'email': { $regex: ?1, $options: 'i' } } ] }, { 'is_active': ?2 } ] }")
    Page<Candidate> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseAndIsActive(String name, String email, Boolean isActive, Pageable pageable);
}