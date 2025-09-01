package com.jobapp.application.repository;

import com.jobapp.application.model.Application;
import com.jobapp.application.model.ApplicationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Application entity operations
 * Requirements: 2.3, 2.5, 4.1, 4.2, 4.3
 */
@Repository
public interface ApplicationRepository extends MongoRepository<Application, String> {
    
    /**
     * Find application by candidate and job (unique constraint)
     * @param candidateId the candidate ID
     * @param jobId the job ID
     * @return Optional containing the application if found
     */
    Optional<Application> findByCandidateIdAndJobId(String candidateId, String jobId);
    
    /**
     * Check if application exists for candidate and job
     * @param candidateId the candidate ID
     * @param jobId the job ID
     * @return true if application exists
     */
    boolean existsByCandidateIdAndJobId(String candidateId, String jobId);
    
    /**
     * Find all applications by candidate ID
     * @param candidateId the candidate ID
     * @param pageable pagination information
     * @return Page of applications for the candidate
     */
    Page<Application> findByCandidateId(String candidateId, Pageable pageable);
    
    /**
     * Find applications by candidate ID and status
     * @param candidateId the candidate ID
     * @param status the application status
     * @param pageable pagination information
     * @return Page of applications matching the criteria
     */
    Page<Application> findByCandidateIdAndStatus(String candidateId, ApplicationStatus status, Pageable pageable);
    
    /**
     * Find all applications for a specific job
     * @param jobId the job ID
     * @param pageable pagination information
     * @return Page of applications for the job
     */
    Page<Application> findByJobId(String jobId, Pageable pageable);
    
    /**
     * Find applications by job ID and status
     * @param jobId the job ID
     * @param status the application status
     * @param pageable pagination information
     * @return Page of applications matching the criteria
     */
    Page<Application> findByJobIdAndStatus(String jobId, ApplicationStatus status, Pageable pageable);
    
    /**
     * Find all applications for an employer
     * @param employerId the employer ID
     * @param pageable pagination information
     * @return Page of applications for the employer
     */
    Page<Application> findByEmployerId(String employerId, Pageable pageable);
    
    /**
     * Find applications by employer ID and status
     * @param employerId the employer ID
     * @param status the application status
     * @param pageable pagination information
     * @return Page of applications matching the criteria
     */
    Page<Application> findByEmployerIdAndStatus(String employerId, ApplicationStatus status, Pageable pageable);
    
    /**
     * Find applications by status
     * @param status the application status
     * @param pageable pagination information
     * @return Page of applications with the specified status
     */
    Page<Application> findByStatus(ApplicationStatus status, Pageable pageable);
    
    /**
     * Find applications applied within a date range
     * @param startDate the start date
     * @param endDate the end date
     * @param pageable pagination information
     * @return Page of applications applied within the date range
     */
    Page<Application> findByAppliedAtBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
    
    /**
     * Find applications updated within a date range
     * @param startDate the start date
     * @param endDate the end date
     * @param pageable pagination information
     * @return Page of applications updated within the date range
     */
    Page<Application> findByUpdatedAtBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
    
    /**
     * Find applications with scheduled interviews
     * @param pageable pagination information
     * @return Page of applications with scheduled interviews
     */
    @Query("{ 'interview_scheduled': { $ne: null } }")
    Page<Application> findApplicationsWithScheduledInterviews(Pageable pageable);
    
    /**
     * Find applications with interviews scheduled within a date range
     * @param startDate the start date
     * @param endDate the end date
     * @param pageable pagination information
     * @return Page of applications with interviews in the date range
     */
    Page<Application> findByInterviewScheduledBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
    
    /**
     * Find applications with job offers
     * @param pageable pagination information
     * @return Page of applications with job offers
     */
    @Query("{ 'salary_offered': { $ne: null } }")
    Page<Application> findApplicationsWithOffers(Pageable pageable);
    
    /**
     * Find applications with expired offers
     * @param currentTime current time
     * @param pageable pagination information
     * @return Page of applications with expired offers
     */
    @Query("{ 'offer_expires_at': { $lt: ?0 } }")
    Page<Application> findApplicationsWithExpiredOffers(LocalDateTime currentTime, Pageable pageable);
    
    /**
     * Find applications by multiple criteria
     * @param employerId the employer ID (optional)
     * @param status the application status (optional)
     * @param startDate the start date for applied_at (optional)
     * @param endDate the end date for applied_at (optional)
     * @param pageable pagination information
     * @return Page of applications matching the criteria
     */
    @Query("{ $and: [ " +
           "{ $or: [ { 'employer_id': ?0 }, { $expr: { $eq: [?0, null] } } ] }, " +
           "{ $or: [ { 'status': ?1 }, { $expr: { $eq: [?1, null] } } ] }, " +
           "{ $or: [ { 'applied_at': { $gte: ?2 } }, { $expr: { $eq: [?2, null] } } ] }, " +
           "{ $or: [ { 'applied_at': { $lte: ?3 } }, { $expr: { $eq: [?3, null] } } ] } " +
           "] }")
    Page<Application> findByCriteria(String employerId, ApplicationStatus status, 
                                   LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
    
    /**
     * Count applications by candidate ID
     * @param candidateId the candidate ID
     * @return count of applications for the candidate
     */
    long countByCandidateId(String candidateId);
    
    /**
     * Count applications by candidate ID and status
     * @param candidateId the candidate ID
     * @param status the application status
     * @return count of applications matching the criteria
     */
    long countByCandidateIdAndStatus(String candidateId, ApplicationStatus status);
    
    /**
     * Count applications by job ID
     * @param jobId the job ID
     * @return count of applications for the job
     */
    long countByJobId(String jobId);
    
    /**
     * Count applications by job ID and status
     * @param jobId the job ID
     * @param status the application status
     * @return count of applications matching the criteria
     */
    long countByJobIdAndStatus(String jobId, ApplicationStatus status);
    
    /**
     * Count applications by employer ID
     * @param employerId the employer ID
     * @return count of applications for the employer
     */
    long countByEmployerId(String employerId);
    
    /**
     * Count applications by employer ID and status
     * @param employerId the employer ID
     * @param status the application status
     * @return count of applications matching the criteria
     */
    long countByEmployerIdAndStatus(String employerId, ApplicationStatus status);
    
    /**
     * Count applications by status
     * @param status the application status
     * @return count of applications with the specified status
     */
    long countByStatus(ApplicationStatus status);
    
    /**
     * Find recent applications for a candidate
     * @param candidateId the candidate ID
     * @param daysAgo the number of days ago
     * @param pageable pagination information
     * @return Page of recent applications for the candidate
     */
    @Query("{ 'candidate_id': ?0, 'applied_at': { $gte: ?1 } }")
    Page<Application> findRecentApplicationsByCandidate(String candidateId, LocalDateTime daysAgo, Pageable pageable);
    
    /**
     * Find recent applications for an employer
     * @param employerId the employer ID
     * @param daysAgo the number of days ago
     * @param pageable pagination information
     * @return Page of recent applications for the employer
     */
    @Query("{ 'employer_id': ?0, 'applied_at': { $gte: ?1 } }")
    Page<Application> findRecentApplicationsByEmployer(String employerId, LocalDateTime daysAgo, Pageable pageable);
    
    /**
     * Find applications requiring action (in review, interview scheduled)
     * @param employerId the employer ID
     * @param pageable pagination information
     * @return Page of applications requiring employer action
     */
    @Query("{ 'employer_id': ?0, 'status': { $in: ['IN_REVIEW', 'INTERVIEW_SCHEDULED', 'INTERVIEWED'] } }")
    Page<Application> findApplicationsRequiringAction(String employerId, Pageable pageable);
    
    /**
     * Find applications by job IDs (for bulk operations)
     * @param jobIds list of job IDs
     * @param pageable pagination information
     * @return Page of applications for the specified jobs
     */
    Page<Application> findByJobIdIn(List<String> jobIds, Pageable pageable);
    
    /**
     * Find applications with cover letters
     * @param pageable pagination information
     * @return Page of applications that include cover letters
     */
    @Query("{ 'cover_letter': { $ne: null, $ne: '' } }")
    Page<Application> findApplicationsWithCoverLetters(Pageable pageable);
    
    /**
     * Find applications with additional documents
     * @param pageable pagination information
     * @return Page of applications with additional documents
     */
    @Query("{ 'additional_documents': { $ne: null, $not: { $size: 0 } } }")
    Page<Application> findApplicationsWithAdditionalDocuments(Pageable pageable);
    
    /**
     * Find all applications (for admin purposes)
     * @param pageable pagination information
     * @return Page of all applications
     */
    Page<Application> findAll(Pageable pageable);
    
    /**
     * Delete applications by job ID (when job is deleted)
     * @param jobId the job ID
     */
    void deleteByJobId(String jobId);
    
    /**
     * Delete applications by candidate ID (when candidate is deleted)
     * @param candidateId the candidate ID
     */
    void deleteByCandidateId(String candidateId);
    
    /**
     * Delete applications by employer ID (when employer is deleted)
     * @param employerId the employer ID
     */
    void deleteByEmployerId(String employerId);
}