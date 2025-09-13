import { render, screen, fireEvent, waitFor } from '@testing-library/react'
import { JobListing } from '../job-listing'
import { useJobs } from '@/hooks/use-jobs'

// Mock dependencies
jest.mock('@/hooks/use-jobs')

const mockUseJobs = useJobs as jest.MockedFunction<typeof useJobs>

const mockJobs = [
  {
    id: '1',
    title: 'Software Engineer',
    description: 'A great job opportunity',
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
    description: 'React developer needed',
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

describe('JobListing', () => {
  beforeEach(() => {
    jest.clearAllMocks()
  })

  test('renders job listings correctly', () => {
    mockUseJobs.mockReturnValue({
      jobs: mockJobs,
      isLoading: false,
      error: null,
      totalPages: 1,
      currentPage: 0,
      searchJobs: jest.fn(),
      loadMore: jest.fn(),
      hasMore: false,
    })

    render(<JobListing />)

    expect(screen.getByText('Software Engineer')).toBeInTheDocument()
    expect(screen.getByText('Frontend Developer')).toBeInTheDocument()
    expect(screen.getByText('Tech Corp')).toBeInTheDocument()
    expect(screen.getByText('Frontend Inc')).toBeInTheDocument()
  })

  test('shows loading state', () => {
    mockUseJobs.mockReturnValue({
      jobs: [],
      isLoading: true,
      error: null,
      totalPages: 0,
      currentPage: 0,
      searchJobs: jest.fn(),
      loadMore: jest.fn(),
      hasMore: false,
    })

    render(<JobListing />)

    expect(screen.getByText(/loading jobs/i)).toBeInTheDocument()
  })

  test('shows error state', () => {
    mockUseJobs.mockReturnValue({
      jobs: [],
      isLoading: false,
      error: 'Failed to load jobs',
      totalPages: 0,
      currentPage: 0,
      searchJobs: jest.fn(),
      loadMore: jest.fn(),
      hasMore: false,
    })

    render(<JobListing />)

    expect(screen.getByText(/failed to load jobs/i)).toBeInTheDocument()
    expect(screen.getByRole('button', { name: /try again/i })).toBeInTheDocument()
  })

  test('shows empty state when no jobs found', () => {
    mockUseJobs.mockReturnValue({
      jobs: [],
      isLoading: false,
      error: null,
      totalPages: 0,
      currentPage: 0,
      searchJobs: jest.fn(),
      loadMore: jest.fn(),
      hasMore: false,
    })

    render(<JobListing />)

    expect(screen.getByText(/no jobs found/i)).toBeInTheDocument()
    expect(screen.getByText(/try adjusting your search criteria/i)).toBeInTheDocument()
  })

  test('handles search functionality', async () => {
    const mockSearchJobs = jest.fn()
    mockUseJobs.mockReturnValue({
      jobs: mockJobs,
      isLoading: false,
      error: null,
      totalPages: 1,
      currentPage: 0,
      searchJobs: mockSearchJobs,
      loadMore: jest.fn(),
      hasMore: false,
    })

    render(<JobListing />)

    const searchInput = screen.getByPlaceholderText(/search jobs/i)
    const searchButton = screen.getByRole('button', { name: /search/i })

    fireEvent.change(searchInput, { target: { value: 'react' } })
    fireEvent.click(searchButton)

    expect(mockSearchJobs).toHaveBeenCalledWith({
      search: 'react',
      location: '',
      jobType: '',
      salaryMin: '',
      salaryMax: '',
    })
  })

  test('handles load more functionality', async () => {
    const mockLoadMore = jest.fn()
    mockUseJobs.mockReturnValue({
      jobs: mockJobs,
      isLoading: false,
      error: null,
      totalPages: 2,
      currentPage: 0,
      searchJobs: jest.fn(),
      loadMore: mockLoadMore,
      hasMore: true,
    })

    render(<JobListing />)

    const loadMoreButton = screen.getByRole('button', { name: /load more/i })
    fireEvent.click(loadMoreButton)

    expect(mockLoadMore).toHaveBeenCalled()
  })

  test('shows pagination when multiple pages exist', () => {
    mockUseJobs.mockReturnValue({
      jobs: mockJobs,
      isLoading: false,
      error: null,
      totalPages: 3,
      currentPage: 1,
      searchJobs: jest.fn(),
      loadMore: jest.fn(),
      hasMore: true,
    })

    render(<JobListing />)

    expect(screen.getByText('Page 2 of 3')).toBeInTheDocument()
  })

  test('displays job count', () => {
    mockUseJobs.mockReturnValue({
      jobs: mockJobs,
      isLoading: false,
      error: null,
      totalPages: 1,
      currentPage: 0,
      searchJobs: jest.fn(),
      loadMore: jest.fn(),
      hasMore: false,
    })

    render(<JobListing />)

    expect(screen.getByText('2 jobs found')).toBeInTheDocument()
  })

  test('handles job application', async () => {
    const mockApplyForJob = jest.fn()
    mockUseJobs.mockReturnValue({
      jobs: mockJobs,
      isLoading: false,
      error: null,
      totalPages: 1,
      currentPage: 0,
      searchJobs: jest.fn(),
      loadMore: jest.fn(),
      hasMore: false,
      applyForJob: mockApplyForJob,
    })

    render(<JobListing />)

    const applyButtons = screen.getAllByText('Apply Now')
    fireEvent.click(applyButtons[0])

    await waitFor(() => {
      expect(mockApplyForJob).toHaveBeenCalledWith(mockJobs[0])
    })
  })
})