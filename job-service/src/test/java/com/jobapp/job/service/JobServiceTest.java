package com.jobapp.job.service;

import com.jobapp.job.dto.*;
import com.jobapp.job.exception.ResourceNotFoundException;
import com.jobapp.job.exception.UnauthorizedException;
import com.jobapp.job.model.Job;
import com.jobapp.job.model.JobType;
import com.jobapp.job.model.ExperienceLevel;
import com.jobapp.job.model.SalaryRange;
import com.jobapp.job.repository.JobRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for JobService
 * Requirements: 3.2, 3.3
 */
@ExtendWith(MockitoExtension.class)
class JobServiceTest {
    
    @Mock
    private JobRepository jobRepository;
    
    @InjectMocks
    private JobService jobService;
    
    private Job testJob;