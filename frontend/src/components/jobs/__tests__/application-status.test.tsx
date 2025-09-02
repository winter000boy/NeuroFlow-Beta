import { render, screen, waitFor } from '@testing-library/react'
import { ApplicationStatus } from '../application-status'
import { JobService } from '@/services/job.service'

// Mock dependencies
jest.mock('@/services/job.service')

describe('ApplicationStatus', () => {
  beforeEach(() => {
    jest.clearAllMocks()
  })

  test('renders application status correctly', async () => {
    const mockApplication = {
      id: 'app1',
      jobId: '1',
      candidateId: 'cand1',
      status: 'APPLIED' as const,
      appliedAt: '2024-01-01T00:00:00Z',
      updatedAt: '2024-01-01T00:00:00Z'
    }

    ;(JobService.getApplicationStatus as jest.Mock).mockResolvedValue(mockApplication)

    render(<ApplicationStatus jobId="1" />)

    await waitFor(() => {
      expect(screen.getByText('Applied')).toBeInTheDocument()
      expect(screen.getByText('Your application has been submitted')).toBeInTheDocument()
      expect(screen.getByText('Applied on 1/1/2024')).toBeInTheDocument()
    })
  })

  test('renders in review status correctly', async () => {
    const mockApplication = {
      id: 'app1',
      jobId: '1',
      candidateId: 'cand1',
      status: 'IN_REVIEW' as const,
      appliedAt: '2024-01-01T00:00:00Z',
      updatedAt: '2024-01-02T00:00:00Z'
    }

    ;(JobService.getApplicationStatus as jest.Mock).mockResolvedValue(mockApplication)

    render(<ApplicationStatus jobId="1" />)

    await waitFor(() => {
      expect(screen.getByText('In Review')).toBeInTheDocument()
      expect(screen.getByText('Your application is being reviewed')).toBeInTheDocument()
    })
  })

  test('renders hired status correctly', async () => {
    const mockApplication = {
      id: 'app1',
      jobId: '1',
      candidateId: 'cand1',
      status: 'HIRED' as const,
      appliedAt: '2024-01-01T00:00:00Z',
      updatedAt: '2024-01-03T00:00:00Z'
    }

    ;(JobService.getApplicationStatus as jest.Mock).mockResolvedValue(mockApplication)

    render(<ApplicationStatus jobId="1" />)

    await waitFor(() => {
      expect(screen.getByText('Hired')).toBeInTheDocument()
      expect(screen.getByText('Congratulations! You got the job')).toBeInTheDocument()
    })
  })

  test('renders rejected status correctly', async () => {
    const mockApplication = {
      id: 'app1',
      jobId: '1',
      candidateId: 'cand1',
      status: 'REJECTED' as const,
      appliedAt: '2024-01-01T00:00:00Z',
      updatedAt: '2024-01-04T00:00:00Z'
    }

    ;(JobService.getApplicationStatus as jest.Mock).mockResolvedValue(mockApplication)

    render(<ApplicationStatus jobId="1" />)

    await waitFor(() => {
      expect(screen.getByText('Not Selected')).toBeInTheDocument()
      expect(screen.getByText('Your application was not selected')).toBeInTheDocument()
    })
  })

  test('renders employer notes when available', async () => {
    const mockApplication = {
      id: 'app1',
      jobId: '1',
      candidateId: 'cand1',
      status: 'REJECTED' as const,
      appliedAt: '2024-01-01T00:00:00Z',
      updatedAt: '2024-01-04T00:00:00Z',
      notes: 'Thank you for your interest. We found a candidate with more experience.'
    }

    ;(JobService.getApplicationStatus as jest.Mock).mockResolvedValue(mockApplication)

    render(<ApplicationStatus jobId="1" />)

    await waitFor(() => {
      expect(screen.getByText('Note from employer:')).toBeInTheDocument()
      expect(screen.getByText('Thank you for your interest. We found a candidate with more experience.')).toBeInTheDocument()
    })
  })

  test('renders nothing when no application exists', async () => {
    ;(JobService.getApplicationStatus as jest.Mock).mockResolvedValue(null)

    const { container } = render(<ApplicationStatus jobId="1" />)

    await waitFor(() => {
      expect(container.firstChild).toBeNull()
    })
  })

  test('shows loading state initially', () => {
    ;(JobService.getApplicationStatus as jest.Mock).mockImplementation(() => new Promise(() => {}))

    render(<ApplicationStatus jobId="1" />)

    expect(screen.getByRole('generic')).toHaveClass('animate-pulse')
  })
})