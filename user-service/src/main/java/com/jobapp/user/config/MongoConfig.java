package com.jobapp.user.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.IndexOperations;
import org.springframework.data.mongodb.core.index.IndexDefinition;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.domain.Sort;

import com.jobapp.user.model.Candidate;
import com.jobapp.user.model.Employer;
import com.jobapp.user.model.Admin;

import jakarta.annotation.PostConstruct;

/**
 * MongoDB configuration for setting up indexes
 * Requirements: 1.1, 1.2, 1.5, 3.1, 3.4, 5.1, 5.4 - Email uniqueness and search optimization
 */
@Configuration
public class MongoConfig {
    
    private final MongoTemplate mongoTemplate;
    
    public MongoConfig(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }
    
    /**
     * Set up MongoDB indexes for all collections
     */
    @PostConstruct
    public void setupIndexes() {
        setupCandidateIndexes();
        setupEmployerIndexes();
        setupAdminIndexes();
    }
    
    private void setupCandidateIndexes() {
        IndexOperations indexOps = mongoTemplate.indexOps(Candidate.class);
        
        // Unique index on email for fast lookups and uniqueness constraint
        IndexDefinition emailIndex = new Index()
                .on("email", Sort.Direction.ASC)
                .unique();
        indexOps.ensureIndex(emailIndex);
        
        // Index on isActive for filtering active candidates
        IndexDefinition activeIndex = new Index()
                .on("is_active", Sort.Direction.ASC);
        indexOps.ensureIndex(activeIndex);
        
        // Compound index on email and isActive for authentication queries
        IndexDefinition emailActiveIndex = new Index()
                .on("email", Sort.Direction.ASC)
                .on("is_active", Sort.Direction.ASC);
        indexOps.ensureIndex(emailActiveIndex);
        
        // Index on degree for degree-based searches (case-insensitive)
        IndexDefinition degreeIndex = new Index()
                .on("degree", Sort.Direction.ASC);
        indexOps.ensureIndex(degreeIndex);
        
        // Index on graduation_year for year-based filtering
        IndexDefinition graduationYearIndex = new Index()
                .on("graduation_year", Sort.Direction.ASC);
        indexOps.ensureIndex(graduationYearIndex);
        
        // Index on created_at for date-based queries and sorting
        IndexDefinition createdAtIndex = new Index()
                .on("created_at", Sort.Direction.DESC);
        indexOps.ensureIndex(createdAtIndex);
        
        // Index on updated_at for tracking recent updates
        IndexDefinition updatedAtIndex = new Index()
                .on("updated_at", Sort.Direction.DESC);
        indexOps.ensureIndex(updatedAtIndex);
        
        // Index on name for name-based searches
        IndexDefinition nameIndex = new Index()
                .on("name", Sort.Direction.ASC);
        indexOps.ensureIndex(nameIndex);
        
        // Compound index for common search patterns
        IndexDefinition searchIndex = new Index()
                .on("degree", Sort.Direction.ASC)
                .on("graduation_year", Sort.Direction.ASC)
                .on("is_active", Sort.Direction.ASC);
        indexOps.ensureIndex(searchIndex);
        
        // Sparse index on resume_url for candidates with resumes
        IndexDefinition resumeIndex = new Index()
                .on("resume_url", Sort.Direction.ASC)
                .sparse();
        indexOps.ensureIndex(resumeIndex);
        
        // Sparse index on linkedin_profile for candidates with LinkedIn
        IndexDefinition linkedinIndex = new Index()
                .on("linkedin_profile", Sort.Direction.ASC)
                .sparse();
        indexOps.ensureIndex(linkedinIndex);
    }
    
    private void setupEmployerIndexes() {
        IndexOperations indexOps = mongoTemplate.indexOps(Employer.class);
        
        // Unique index on email for fast lookups and uniqueness constraint
        IndexDefinition emailIndex = new Index()
                .on("email", Sort.Direction.ASC)
                .unique();
        indexOps.ensureIndex(emailIndex);
        
        // Index on isApproved for approval status filtering
        IndexDefinition approvedIndex = new Index()
                .on("is_approved", Sort.Direction.ASC);
        indexOps.ensureIndex(approvedIndex);
        
        // Index on isActive for active status filtering
        IndexDefinition activeIndex = new Index()
                .on("is_active", Sort.Direction.ASC);
        indexOps.ensureIndex(activeIndex);
        
        // Compound index on email and isActive for authentication queries
        IndexDefinition emailActiveIndex = new Index()
                .on("email", Sort.Direction.ASC)
                .on("is_active", Sort.Direction.ASC);
        indexOps.ensureIndex(emailActiveIndex);
        
        // Compound index on approval and active status for job posting eligibility
        IndexDefinition approvalActiveIndex = new Index()
                .on("is_approved", Sort.Direction.ASC)
                .on("is_active", Sort.Direction.ASC);
        indexOps.ensureIndex(approvalActiveIndex);
        
        // Index on company_name for company-based searches
        IndexDefinition companyNameIndex = new Index()
                .on("company_name", Sort.Direction.ASC);
        indexOps.ensureIndex(companyNameIndex);
        
        // Index on created_at for date-based queries and sorting
        IndexDefinition createdAtIndex = new Index()
                .on("created_at", Sort.Direction.DESC);
        indexOps.ensureIndex(createdAtIndex);
        
        // Index on updated_at for tracking recent updates
        IndexDefinition updatedAtIndex = new Index()
                .on("updated_at", Sort.Direction.DESC);
        indexOps.ensureIndex(updatedAtIndex);
        
        // Index on approval_date for approval tracking
        IndexDefinition approvalDateIndex = new Index()
                .on("approval_date", Sort.Direction.DESC)
                .sparse();
        indexOps.ensureIndex(approvalDateIndex);
        
        // Index on approved_by for admin tracking
        IndexDefinition approvedByIndex = new Index()
                .on("approved_by", Sort.Direction.ASC)
                .sparse();
        indexOps.ensureIndex(approvedByIndex);
        
        // Sparse index on logo_url for employers with logos
        IndexDefinition logoIndex = new Index()
                .on("logo_url", Sort.Direction.ASC)
                .sparse();
        indexOps.ensureIndex(logoIndex);
        
        // Compound index for pending approval queries
        IndexDefinition pendingApprovalIndex = new Index()
                .on("is_approved", Sort.Direction.ASC)
                .on("is_active", Sort.Direction.ASC)
                .on("created_at", Sort.Direction.DESC);
        indexOps.ensureIndex(pendingApprovalIndex);
        
        // Text index on company_name and description for search
        IndexDefinition textSearchIndex = new Index()
                .on("company_name", Sort.Direction.ASC)
                .on("description", Sort.Direction.ASC);
        indexOps.ensureIndex(textSearchIndex);
    }
    
    private void setupAdminIndexes() {
        IndexOperations indexOps = mongoTemplate.indexOps(Admin.class);
        
        // Unique index on email for fast lookups and uniqueness constraint
        IndexDefinition emailIndex = new Index()
                .on("email", Sort.Direction.ASC)
                .unique();
        indexOps.ensureIndex(emailIndex);
        
        // Index on role for role-based queries
        IndexDefinition roleIndex = new Index()
                .on("role", Sort.Direction.ASC);
        indexOps.ensureIndex(roleIndex);
        
        // Index on isActive for filtering active admins
        IndexDefinition activeIndex = new Index()
                .on("is_active", Sort.Direction.ASC);
        indexOps.ensureIndex(activeIndex);
        
        // Compound index on email and isActive for authentication queries
        IndexDefinition emailActiveIndex = new Index()
                .on("email", Sort.Direction.ASC)
                .on("is_active", Sort.Direction.ASC);
        indexOps.ensureIndex(emailActiveIndex);
        
        // Compound index on role and isActive for admin management
        IndexDefinition roleActiveIndex = new Index()
                .on("role", Sort.Direction.ASC)
                .on("is_active", Sort.Direction.ASC);
        indexOps.ensureIndex(roleActiveIndex);
        
        // Index on created_at for date-based queries and sorting
        IndexDefinition createdAtIndex = new Index()
                .on("created_at", Sort.Direction.DESC);
        indexOps.ensureIndex(createdAtIndex);
        
        // Index on updated_at for tracking recent updates
        IndexDefinition updatedAtIndex = new Index()
                .on("updated_at", Sort.Direction.DESC);
        indexOps.ensureIndex(updatedAtIndex);
        
        // Index on last_login for activity tracking
        IndexDefinition lastLoginIndex = new Index()
                .on("last_login", Sort.Direction.DESC)
                .sparse();
        indexOps.ensureIndex(lastLoginIndex);
        
        // Index on last_action_at for action tracking
        IndexDefinition lastActionIndex = new Index()
                .on("last_action_at", Sort.Direction.DESC)
                .sparse();
        indexOps.ensureIndex(lastActionIndex);
        
        // Index on created_by for tracking admin creation hierarchy
        IndexDefinition createdByIndex = new Index()
                .on("created_by", Sort.Direction.ASC)
                .sparse();
        indexOps.ensureIndex(createdByIndex);
        
        // Index on name for name-based searches
        IndexDefinition nameIndex = new Index()
                .on("name", Sort.Direction.ASC);
        indexOps.ensureIndex(nameIndex);
        
        // Index on login_count for activity analytics
        IndexDefinition loginCountIndex = new Index()
                .on("login_count", Sort.Direction.DESC);
        indexOps.ensureIndex(loginCountIndex);
        
        // Index on actions_performed for performance analytics
        IndexDefinition actionsIndex = new Index()
                .on("actions_performed", Sort.Direction.DESC);
        indexOps.ensureIndex(actionsIndex);
        
        // Index on permissions for permission-based queries
        IndexDefinition permissionsIndex = new Index()
                .on("permissions", Sort.Direction.ASC);
        indexOps.ensureIndex(permissionsIndex);
        
        // Compound index for admin analytics
        IndexDefinition analyticsIndex = new Index()
                .on("role", Sort.Direction.ASC)
                .on("is_active", Sort.Direction.ASC)
                .on("created_at", Sort.Direction.DESC);
        indexOps.ensureIndex(analyticsIndex);
        
        // Compound index for performance tracking
        IndexDefinition performanceIndex = new Index()
                .on("is_active", Sort.Direction.ASC)
                .on("actions_performed", Sort.Direction.DESC)
                .on("login_count", Sort.Direction.DESC);
        indexOps.ensureIndex(performanceIndex);
    }
}