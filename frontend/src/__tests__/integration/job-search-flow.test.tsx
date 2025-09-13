import { render, screen, fireEvent, waitFor } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { JobListing } from '@/components/jobs/job-listing'
import { JobApplicationModal } from '@/components/jobs/job-application-modal'
import { JobService } from '@/services/job.service'
import { useAuth } from '@/hooks/use-auth'
import { toast } from 'sonner'

// Mock dependencies
jest.mock('@/services/job.service')
jest.mock('@/hooks/use-auth')
jest.mock('sonner')

const mockUseAuth = useAuth as jest.MockedFunction<typeof useAuth>

const mockJobs = [
  {
    id: '1',
    title: 'Software Engineer',
    description: 'A great job opportunity for a software engineer',
    salary: { min: 80000, max: 120000, currency: 'USD' },
    location: 'San Francisco, CA',
    jobType: 'FULL_TIME' as const,
    isActive: true,
    createdAt: '2024-01-01T00:00:00Z',
    updatedAt: '2024-01-01T00:00:00Z',
    expiresAt: '2024-12-31T00:00:00Z',
    employer: {
      id: 'emp1',
      companyName: 'Tech Corp',
      logoUrl: 'https://example.com/logo.png',
      website: 'https://techcorp.com'
    }
  },
  {
    id: '2',
    title: 'Frontend Developer',
    description: 'React developer needed for exciting projects',
    salary: { min: 70000, max: 100000, currency: 'USD' },
    location: 'New York, NY',
    jobType: 'REMOTE' as const,
    isActive: true,
    createdAt: '2024-01-02T00:00:00Z',
    updatedAt: '2024-01-02T00:00:00Z',
    expiresAt: '2024-12-31T00:00:00Z',
    employer: {
      id: 'emp2',
      companyName: 'Frontend Inc',
      logoUrl: 'https://example.com/logo2.png',
      website: 'https://frontend.com'
    }
  }
]

const mockJobsResponse = {
  content: mockJobs,
  totalPages: 1,
  totalElements: 2,
  number: 0,
  size: 10,
}

describe('Job Search Flow Integration', () => {
  beforeEach(() => {
    jest.clearAllMocks()
    mockUseAuth.mockReturnValue({
      user: {
        id: '1',
        email: 'candidate@example.com',
        role: 'CANDIDATE',
      },
      isLoading: false,
      login: jest.fn(),
      logout: jest.fn(),
      register: jest.fn(),
    })
  })

  test('complete job search and application flow', async () => {
    const user = userEvent.setup()
    
    // Mock API responses
    ;(JobService.getJobs as jest.Mock).mockResolvedValue(mockJobsResponse)
    ;(JobService.applyForJob as jest.Mock).mockResolvedValue({
      id: 'app1',
      jobId: '1',
      candidateId: '1',
      status: 'APPLIED',
      appliedAt: '2024-01-01T00:00:00Z',
      updatedAt: '2024-01-01T00:00:00Z',
    })

    render(<JobListing />)

    // Wait for jobs to load
    await waitFor(() => {
      expect(screen.getByText('Software Engineer')).toBeInTheDocument()
      expect(screen.getByText('Frontend Developer')).toBeInTheDocument()
    })

    // Verify job information is displayed
    expect(screen.getByText('Tech Corp')).toBeInTheDocument()
    expect(screen.getByText('San Francisco, CA')).toBeInTheDocument()
    expect(screen.getByText('$80,000 - $120,000')).toBeInTheDocument()

    // Apply for the first job
    const applyButtons = screen.getAllByText('Apply Now')
    await user.click(applyButtons[0])

    // Verify application modal opens
    await waitFor(() => {
      expect(screen.getByText('Apply for Position')).toBeInTheDocument()
    })

    // Submit application
    const submitButton = screen.getByText('Submit Application')
    await user.click(submitButton)

    // Verify application submission
    await waitFor(() => {
      expect(JobService.applyForJob).toHaveBeenCalledWith('1')
      expect(toast.success).toHaveBeenCalledWith('Application submitted successfully!')
    })
  })

  test('job search with filters', async () => {
    const user = userEvent.setup()
    
    const filteredJobsResponse = {
      content: [mockJobs[1]], // Only Frontend Developer
      totalPages: 1,
      totalElements: 1,
      number: 0,
      size: 10,
    }

    ;(JobService.getJobs as jest.Mock)
      .mockResolvedValueOnce(mockJobsResponse) // Initial load
      .mockResolvedValueOnce(filteredJobsResponse) // Filtered search

    render(<JobListing />)

    // Wait for initial jobs to load
    await waitFor(() => {
      expect(screen.getByText('Software Engineer')).toBeInTheDocument()
      expect(screen.getByText('Frontend Developer')).toBeInTheDocument()
    })

    // Perform search with filters
    const searchInput = screen.getByPlaceholderText(/search jobs/i)
    const locationInput = screen.getByLabelText(/location/i)
    const jobTypeSelect = screen.getByLabelText(/job type/i)
    const searchButton = screen.getByRole('button', { name: /search/i })

    await user.type(searchInput, 'react')
    await user.type(locationInput, 'New York')
    await user.selectOptions(jobTypeSelect, 'REMOTE')
    await user.click(searchButton)

    // Verify filtered results
    await waitFor(() => {
      expect(JobService.getJobs).toHaveBeenCalledWith({
        page: 0,
        size: 10,
        search: 'react',
        location: 'New York',
        jobType: 'REMOTE',
      })
      expect(screen.getByText('Frontend Developer')).toBeInTheDocument()
      expect(screen.queryByText('Software Engineer')).not.toBeInTheDocument()
    })
  })

  test('pagination flow', async () => {
    const user = userEvent.setup()
    
    const firstPageResponse = {
      content: [mockJobs[0]],
      totalPages: 2,
      totalElements: 2,
      number: 0,
      size: 1,
    }

    const secondPageResponse = {
      content: [mockJobs[1]],
      totalPages: 2,
      totalElements: 2,
      number: 1,
      size: 1,
    }

    ;(JobService.getJobs as jest.Mock)
      .mockResolvedValueOnce(firstPageResponse)
      .mockResolvedValueOnce(secondPageResponse)

    render(<JobListing />)

    // Wait for first page to load
    await waitFor(() => {
      expect(screen.getByText('Software Engineer')).toBeInTheDocument()
      expect(screen.queryByText('Frontend Developer')).not.toBeInTheDocument()
    })

    // Load more jobs
    const loadMoreButton = screen.getByRole('button', { name: /load more/i })
    await user.click(loadMoreButton)

    // Verify second page loads
    await waitFor(() => {
      expect(screen.getByText('Software Engineer')).toBeInTheDocument()
      expect(screen.getByText('Frontend Developer')).toBeInTheDocument()
    })
  })

  test('handles job search errors gracefully', async () => {
    const user = userEvent.setup()
    
    ;(JobService.getJobs as jest.Mock).mockRejectedValue(new Error('Network error'))

    render(<JobListing />)

    // Wait for error state
    await waitFor(() => {
      expect(screen.getByText(/failed to load jobs/i)).toBeInTheDocument()
      expect(screen.getByRole('button', { name: /try again/i })).toBeInTheDocument()
    })

    // Try again
    ;(JobService.getJobs as jest.Mock).mockResolvedValue(mockJobsResponse)
    const tryAgainButton = screen.getByRole('button', { name: /try again/i })
    await user.click(tryAgainButton)

    // Verify jobs load after retry
    await waitFor(() => {
      expect(screen.getByText('Software Engineer')).toBeInTheDocument()
      expect(screen.getByText('Frontend Developer')).toBeInTheDocument()
    })
  })

  test('handles application errors', async () => {
    const user = userEvent.setup()
    
    ;(JobService.getJobs as jest.Mock).mockResolvedValue(mockJobsResponse)
    ;(JobService.applyForJob as jest.Mock).mockRejectedValue(new Error('Already applied'))

    render(<JobListing />)

    // Wait for jobs to load
    await waitFor(() => {
      expect(screen.getByText('Software Engineer')).toBeInTheDocument()
    })

    // Try to apply for job
    const applyButton = screen.getAllByText('Apply Now')[0]
    await user.click(applyButton)

    // Submit application
    await waitFor(() => {
      expect(screen.getByText('Apply for Position')).toBeInTheDocument()
    })

    const submitButton = screen.getByText('Submit Application')
    await user.click(submitButton)

    // Verify error handling
    await waitFor(() => {
      expect(toast.error).toHaveBeenCalledWith('Already applied')
    })
  })

  test('shows different UI for different user roles', async () => {
    // Test as employer
    mockUseAuth.mockReturnValue({
      user: {
        id: '1',
        email: 'employer@example.com',
        role: 'EMPLOYER',
      },
      isLoading: false,
      login: jest.fn(),
      logout: jest.fn(),
      register: jest.fn(),
    })

    ;(JobService.getJobs as jest.Mock).mockResolvedValue(mockJobsResponse)

    render(<JobListing />)

    await waitFor(() => {
      expect(screen.getByText('Software Engineer')).toBeInTheDocument()
    })

    // Employers should see "View Details" instead of "Apply Now"
    expect(screen.getAllByText('View Details')).toHaveLength(2)
    expect(screen.queryByText('Apply Now')).not.toBeInTheDocument()
  })

  test('shows login prompt for unauthenticated users', async () => {
    mockUseAuth.mockReturnValue({
      user: null,
      isLoading: false,
      login: jest.fn(),
      logout: jest.fn(),
      register: jest.fn(),
    })

    ;(JobService.getJobs as jest.Mock).mockResolvedValue(mockJobsResponse)

    render(<JobListing />)

    await waitFor(() => {
      expect(screen.getByText('Software Engineer')).toBeInTheDocument()
    })

    // Unauthenticated users should see "Login to Apply"
    expect(screen.getAllByText('Login to Apply')).toHaveLength(2)
    expect(screen.queryByText('Apply Now')).not.toBeInTheDocument()
  })
})