package com.jobapp.job.repository;

import com.jobapp.job.model.Job;
import com.jobapp.job.model.JobType;
import com.jobapp.job.model.ExperienceLevel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Job entity operations
 * Requirements: 2.1, 2.2, 4.1, 4.2
 */
@Repository
public interface JobRepository extends MongoRepository<Job, String> {
    
    /**
     * Find jobs by employer ID
     * @param employerId the employer ID
     * @param pageable pagination information
     * @return Page of jobs for the employer
     */
    Page<Job> findByEmployerId(String employerId, Pageable pageable);
    
    /**
     * Find active jobs by employer ID
     * @param employerId the employer ID
     * @param isActive the active status
     * @param pageable pagination information
     * @return Page of active jobs for the employer
     */
    Page<Job> findByEmployerIdAndIsActive(String employerId, Boolean isActive, Pageable pageable);
    
    /**
     * Find all active jobs
     * @param isActive the active status
     * @param pageable pagination information
     * @return Page of active jobs
     */
    Page<Job> findByIsActive(Boolean isActive, Pageable pageable);
    
    /**
     * Find jobs by location (case-insensitive partial match)
     * @param location the location to search for
     * @param pageable pagination information
     * @return Page of jobs in the specified location
     */
    Page<Job> findByLocationContainingIgnoreCase(String location, Pageable pageable);
    
    /**
     * Find jobs by job type
     * @param jobType the job type
     * @param pageable pagination information
     * @return Page of jobs with the specified type
     */
    Page<Job> findByJobType(JobType jobType, Pageable pageable);
    
    /**
     * Find jobs by experience level
     * @param experienceLevel the experience level
     * @param pageable pagination information
     * @return Page of jobs with the specified experience level
     */
    Page<Job> findByExperienceLevel(ExperienceLevel experienceLevel, Pageable pageable);
    
    /**
     * Find jobs with salary range within specified bounds
     * @param minSalary minimum salary threshold
     * @param maxSalary maximum salary threshold
     * @param pageable pagination information
     * @return Page of jobs within salary range
     */
    @Query("{ $and: [ " +
           "{ $or: [ { 'salary.min': { $gte: ?0 } }, { 'salary.min': null } ] }, " +
           "{ $or: [ { 'salary.max': { $lte: ?1 } }, { 'salary.max': null } ] }, " +
           "{ 'is_active': true } " +
           "] }")
    Page<Job> findBySalaryRange(Double minSalary, Double maxSalary, Pageable pageable);
    
    /**
     * Full-text search on job title and description
     * @param searchText the text to search for
     * @param pageable pagination information
     * @return Page of jobs matching the search text
     */
    @Query("{ $text: { $search: ?0 }, 'is_active': true }")
    Page<Job> findByTextSearch(String searchText, Pageable pageable);
    
    /**
     * Advanced search with multiple criteria
     * @param searchText text search (optional)
     * @param location location filter (optional)
     * @param jobType job type filter (optional)
     * @param minSalary minimum salary (optional)
     * @param maxSalary maximum salary (optional)
     * @param experienceLevel experience level (optional)
     * @param pageable pagination information
     * @return Page of jobs matching the criteria
     */
    @Query("{ $and: [ " +
           "{ $or: [ { $text: { $search: ?0 } }, { $expr: { $eq: [?0, null] } } ] }, " +
           "{ $or: [ { 'location': { $regex: ?1, $options: 'i' } }, { $expr: { $eq: [?1, null] } } ] }, " +
           "{ $or: [ { 'job_type': ?2 }, { $expr: { $eq: [?2, null] } } ] }, " +
           "{ $or: [ { 'salary.min': { $gte: ?3 } }, { $expr: { $eq: [?3, null] } } ] }, " +
           "{ $or: [ { 'salary.max': { $lte: ?4 } }, { $expr: { $eq: [?4, null] } } ] }, " +
           "{ $or: [ { 'experience_level': ?5 }, { $expr: { $eq: [?5, null] } } ] }, " +
           "{ 'is_active': true } " +
           "] }")
    Page<Job> findByAdvancedSearch(String searchText, String location, JobType jobType, 
                                  Double minSalary, Double maxSalary, ExperienceLevel experienceLevel, 
                                  Pageable pageable);
    
    /**
     * Find featured jobs
     * @param isFeatured the featured status
     * @param isActive the active status
     * @param pageable pagination information
     * @return Page of featured jobs
     */
    Page<Job> findByIsFeaturedAndIsActive(Boolean isFeatured, Boolean isActive, Pageable pageable);
    
    /**
     * Find jobs created within a date range
     * @param startDate the start date
     * @param endDate the end date
     * @param pageable pagination information
     * @return Page of jobs created within the date range
     */
    Page<Job> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
    
    /**
     * Find jobs expiring soon
     * @param currentTime current time
     * @param expirationThreshold expiration threshold
     * @param pageable pagination information
     * @return Page of jobs expiring soon
     */
    @Query("{ 'expires_at': { $gte: ?0, $lte: ?1 }, 'is_active': true }")
    Page<Job> findJobsExpiringSoon(LocalDateTime currentTime, LocalDateTime expirationThreshold, Pageable pageable);
    
    /**
     * Find expired jobs
     * @param currentTime current time
     * @param pageable pagination information
     * @return Page of expired jobs
     */
    @Query("{ $or: [ " +
           "{ 'expires_at': { $lt: ?0 } }, " +
           "{ 'application_deadline': { $lt: ?0 } } " +
           "] }")
    Page<Job> findExpiredJobs(LocalDateTime currentTime, Pageable pageable);
    
    /**
     * Find jobs by required skills
     * @param skills list of required skills
     * @param pageable pagination information
     * @return Page of jobs requiring any of the specified skills
     */
    @Query("{ 'required_skills': { $in: ?0 }, 'is_active': true }")
    Page<Job> findByRequiredSkillsIn(List<String> skills, Pageable pageable);
    
    /**
     * Find jobs accepting applications
     * @param currentTime current time
     * @param pageable pagination information
     * @return Page of jobs currently accepting applications
     */
    @Query("{ $and: [ " +
           "{ 'is_active': true }, " +
           "{ $or: [ { 'expires_at': { $gt: ?0 } }, { 'expires_at': null } ] }, " +
           "{ $or: [ { 'application_deadline': { $gt: ?0 } }, { 'application_deadline': null } ] } " +
           "] }")
    Page<Job> findJobsAcceptingApplications(LocalDateTime currentTime, Pageable pageable);
    
    /**
     * Count jobs by employer ID
     * @param employerId the employer ID
     * @return count of jobs for the employer
     */
    long countByEmployerId(String employerId);
    
    /**
     * Count active jobs by employer ID
     * @param employerId the employer ID
     * @param isActive the active status
     * @return count of active jobs for the employer
     */
    long countByEmployerIdAndIsActive(String employerId, Boolean isActive);
    
    /**
     * Count jobs by active status
     * @param isActive the active status
     * @return count of jobs with the specified active status
     */
    long countByIsActive(Boolean isActive);
    
    /**
     * Count jobs by job type
     * @param jobType the job type
     * @return count of jobs with the specified type
     */
    long countByJobType(JobType jobType);
    
    /**
     * Count jobs by location
     * @param location the location
     * @return count of jobs in the specified location
     */
    long countByLocationContainingIgnoreCase(String location);
    
    /**
     * Find top jobs by application count
     * @param isActive the active status
     * @param pageable pagination information
     * @return Page of jobs ordered by application count
     */
    @Query("{ 'is_active': ?0 }")
    Page<Job> findTopJobsByApplicationCount(Boolean isActive, Pageable pageable);
    
    /**
     * Find top jobs by view count
     * @param isActive the active status
     * @param pageable pagination information
     * @return Page of jobs ordered by view count
     */
    @Query("{ 'is_active': ?0 }")
    Page<Job> findTopJobsByViewCount(Boolean isActive, Pageable pageable);
    
    /**
     * Find recently posted jobs
     * @param daysAgo the number of days ago
     * @param isActive the active status
     * @param pageable pagination information
     * @return Page of recently posted jobs
     */
    @Query("{ 'created_at': { $gte: ?0 }, 'is_active': ?1 }")
    Page<Job> findRecentlyPosted(LocalDateTime daysAgo, Boolean isActive, Pageable pageable);
    
    /**
     * Find jobs by title containing text (case-insensitive)
     * @param title the title text to search for
     * @param pageable pagination information
     * @return Page of jobs with matching titles
     */
    Page<Job> findByTitleContainingIgnoreCase(String title, Pageable pageable);
    
    /**
     * Find all jobs (for admin purposes)
     * @param pageable pagination information
     * @return Page of all jobs
     */
    Page<Job> findAll(Pageable pageable);
}