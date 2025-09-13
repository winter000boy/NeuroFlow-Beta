import { render, screen, fireEvent } from '@testing-library/react'
import { JobCard } from '../job-card'
import { useAuth } from '@/hooks/use-auth'

// Mock dependencies
jest.mock('@/hooks/use-auth')

const mockUseAuth = useAuth as jest.MockedFunction<typeof useAuth>

const mockJob = {
  id: '1',
  title: 'Software Engineer',
  description: 'A great job opportunity for a software engineer',
  salary: {
    min: 80000,
    max: 120000,
    currency: 'USD'
  },
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
}

describe('JobCard', () => {
  const mockOnApply = jest.fn()

  beforeEach(() => {
    jest.clearAllMocks()
    mockUseAuth.mockReturnValue({
      user: {
        id: '1',
        email: 'test@example.com',
        role: 'CANDIDATE',
      },
      isLoading: false,
      login: jest.fn(),
      logout: jest.fn(),
      register: jest.fn(),
    })
  })

  test('renders job information correctly', () => {
    render(<JobCard job={mockJob} onApply={mockOnApply} />)

    expect(screen.getByText('Software Engineer')).toBeInTheDocument()
    expect(screen.getByText('Tech Corp')).toBeInTheDocument()
    expect(screen.getByText('San Francisco, CA')).toBeInTheDocument()
    expect(screen.getByText('Full Time')).toBeInTheDocument()
    expect(screen.getByText('$80,000 - $120,000')).toBeInTheDocument()
  })

  test('shows company logo when available', () => {
    render(<JobCard job={mockJob} onApply={mockOnApply} />)

    const logo = screen.getByAltText('Tech Corp logo')
    expect(logo).toBeInTheDocument()
    expect(logo).toHaveAttribute('src', 'https://example.com/logo.png')
  })

  test('shows fallback when no logo available', () => {
    const jobWithoutLogo = {
      ...mockJob,
      employer: {
        ...mockJob.employer,
        logoUrl: undefined
      }
    }

    render(<JobCard job={jobWithoutLogo} onApply={mockOnApply} />)

    expect(screen.getByText('TC')).toBeInTheDocument() // Company initials
  })

  test('calls onApply when apply button is clicked', () => {
    render(<JobCard job={mockJob} onApply={mockOnApply} />)

    const applyButton = screen.getByText('Apply Now')
    fireEvent.click(applyButton)

    expect(mockOnApply).toHaveBeenCalledWith(mockJob)
  })

  test('shows view details button for non-candidates', () => {
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

    render(<JobCard job={mockJob} onApply={mockOnApply} />)

    expect(screen.getByText('View Details')).toBeInTheDocument()
    expect(screen.queryByText('Apply Now')).not.toBeInTheDocument()
  })

  test('shows login prompt for unauthenticated users', () => {
    mockUseAuth.mockReturnValue({
      user: null,
      isLoading: false,
      login: jest.fn(),
      logout: jest.fn(),
      register: jest.fn(),
    })

    render(<JobCard job={mockJob} onApply={mockOnApply} />)

    expect(screen.getByText('Login to Apply')).toBeInTheDocument()
  })

  test('truncates long job descriptions', () => {
    const jobWithLongDescription = {
      ...mockJob,
      description: 'This is a very long job description that should be truncated after a certain number of characters to maintain a clean card layout and improve readability for users browsing through multiple job listings.'
    }

    render(<JobCard job={jobWithLongDescription} onApply={mockOnApply} />)

    const description = screen.getByText(/This is a very long job description/)
    expect(description.textContent).toHaveLength(150) // Assuming 150 char limit
  })

  test('displays job type badge correctly', () => {
    const partTimeJob = {
      ...mockJob,
      jobType: 'PART_TIME' as const
    }

    render(<JobCard job={partTimeJob} onApply={mockOnApply} />)

    expect(screen.getByText('Part Time')).toBeInTheDocument()
  })

  test('displays remote job type correctly', () => {
    const remoteJob = {
      ...mockJob,
      jobType: 'REMOTE' as const
    }

    render(<JobCard job={remoteJob} onApply={mockOnApply} />)

    expect(screen.getByText('Remote')).toBeInTheDocument()
  })

  test('shows posted date', () => {
    render(<JobCard job={mockJob} onApply={mockOnApply} />)

    expect(screen.getByText(/posted/i)).toBeInTheDocument()
  })
})