package com.jobapp.application.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.IndexOperations;
import org.springframework.data.mongodb.core.index.IndexDefinition;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.domain.Sort;

import com.jobapp.application.model.Application;

import jakarta.annotation.PostConstruct;

/**
 * MongoDB configuration for setting up indexes in application-service
 * Requirements: 2.3, 2.5, 4.1, 4.2, 4.3 - Application tracking and optimization
 */
@Configuration
public class MongoConfig {
    
    private final MongoTemplate mongoTemplate;
    
    public MongoConfig(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }
    
    /**
     * Set up MongoDB indexes for Application collection
     */
    @PostConstruct
    public void setupIndexes() {
        setupApplicationIndexes();
    }
    
    private void setupApplicationIndexes() {
        IndexOperations indexOps = mongoTemplate.indexOps(Application.class);
        
        // Unique compound index on candidate_id and job_id to prevent duplicate applications
        IndexDefinition candidateJobIndex = new Index()
                .on("candidate_id", Sort.Direction.ASC)
                .on("job_id", Sort.Direction.ASC)
                .unique();
        indexOps.ensureIndex(candidateJobIndex);
        
        // Index on candidate_id for candidate-specific queries
        IndexDefinition candidateIndex = new Index()
                .on("candidate_id", Sort.Direction.ASC);
        indexOps.ensureIndex(candidateIndex);
        
        // Index on job_id for job-specific queries
        IndexDefinition jobIndex = new Index()
                .on("job_id", Sort.Direction.ASC);
        indexOps.ensureIndex(jobIndex);
        
        // Index on employer_id for employer-specific queries
        IndexDefinition employerIndex = new Index()
                .on("employer_id", Sort.Direction.ASC);
        indexOps.ensureIndex(employerIndex);
        
        // Index on status for status-based filtering
        IndexDefinition statusIndex = new Index()
                .on("status", Sort.Direction.ASC);
        indexOps.ensureIndex(statusIndex);
        
        // Index on applied_at for date-based queries and sorting
        IndexDefinition appliedAtIndex = new Index()
                .on("applied_at", Sort.Direction.DESC);
        indexOps.ensureIndex(appliedAtIndex);
        
        // Index on updated_at for tracking recent updates
        IndexDefinition updatedAtIndex = new Index()
                .on("updated_at", Sort.Direction.DESC);
        indexOps.ensureIndex(updatedAtIndex);
        
        // Compound index on candidate_id and status
        IndexDefinition candidateStatusIndex = new Index()
                .on("candidate_id", Sort.Direction.ASC)
                .on("status", Sort.Direction.ASC);
        indexOps.ensureIndex(candidateStatusIndex);
        
        // Compound index on job_id and status
        IndexDefinition jobStatusIndex = new Index()
                .on("job_id", Sort.Direction.ASC)
                .on("status", Sort.Direction.ASC);
        indexOps.ensureIndex(jobStatusIndex);
        
        // Compound index on employer_id and status
        IndexDefinition employerStatusIndex = new Index()
                .on("employer_id", Sort.Direction.ASC)
                .on("status", Sort.Direction.ASC);
        indexOps.ensureIndex(employerStatusIndex);
        
        // Compound index on candidate_id, status, and applied_at
        IndexDefinition candidateStatusDateIndex = new Index()
                .on("candidate_id", Sort.Direction.ASC)
                .on("status", Sort.Direction.ASC)
                .on("applied_at", Sort.Direction.DESC);
        indexOps.ensureIndex(candidateStatusDateIndex);
        
        // Compound index on employer_id, status, and applied_at
        IndexDefinition employerStatusDateIndex = new Index()
                .on("employer_id", Sort.Direction.ASC)
                .on("status", Sort.Direction.ASC)
                .on("applied_at", Sort.Direction.DESC);
        indexOps.ensureIndex(employerStatusDateIndex);
        
        // Index on interview_scheduled for interview management
        IndexDefinition interviewIndex = new Index()
                .on("interview_scheduled", Sort.Direction.ASC)
                .sparse();
        indexOps.ensureIndex(interviewIndex);
        
        // Index on offer_expires_at for offer management
        IndexDefinition offerExpiryIndex = new Index()
                .on("offer_expires_at", Sort.Direction.ASC)
                .sparse();
        indexOps.ensureIndex(offerExpiryIndex);
        
        // Index on salary_offered for offer tracking
        IndexDefinition salaryOfferedIndex = new Index()
                .on("salary_offered", Sort.Direction.DESC)
                .sparse();
        indexOps.ensureIndex(salaryOfferedIndex);
        
        // Compound index for employer dashboard queries
        IndexDefinition employerDashboardIndex = new Index()
                .on("employer_id", Sort.Direction.ASC)
                .on("status", Sort.Direction.ASC)
                .on("updated_at", Sort.Direction.DESC);
        indexOps.ensureIndex(employerDashboardIndex);
        
        // Compound index for candidate dashboard queries
        IndexDefinition candidateDashboardIndex = new Index()
                .on("candidate_id", Sort.Direction.ASC)
                .on("status", Sort.Direction.ASC)
                .on("applied_at", Sort.Direction.DESC);
        indexOps.ensureIndex(candidateDashboardIndex);
        
        // Index for applications requiring action (in review, interview scheduled, etc.)
        IndexDefinition actionRequiredIndex = new Index()
                .on("employer_id", Sort.Direction.ASC)
                .on("status", Sort.Direction.ASC)
                .on("updated_at", Sort.Direction.ASC);
        indexOps.ensureIndex(actionRequiredIndex);
        
        // Compound index for analytics queries
        IndexDefinition analyticsIndex = new Index()
                .on("status", Sort.Direction.ASC)
                .on("applied_at", Sort.Direction.DESC)
                .on("employer_id", Sort.Direction.ASC);
        indexOps.ensureIndex(analyticsIndex);
        
        // Index for bulk operations by job IDs
        IndexDefinition bulkJobIndex = new Index()
                .on("job_id", Sort.Direction.ASC)
                .on("status", Sort.Direction.ASC);
        indexOps.ensureIndex(bulkJobIndex);
    }
}