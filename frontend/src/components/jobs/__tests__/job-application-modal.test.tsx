import { render, screen, fireEvent, waitFor } from '@testing-library/react'
import { JobApplicationModal } from '../job-application-modal'
import { JobService } from '@/services/job.service'
import { toast } from 'sonner'

// Mock dependencies
jest.mock('@/services/job.service')
jest.mock('sonner')

const mockJob = {
  id: '1',
  title: 'Software Engineer',
  description: 'A great job opportunity',
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

describe('JobApplicationModal', () => {
  const mockOnClose = jest.fn()
  const mockOnApplicationSubmitted = jest.fn()

  beforeEach(() => {
    jest.clearAllMocks()
  })

  test('renders job application modal correctly', () => {
    render(
      <JobApplicationModal
        job={mockJob}
        isOpen={true}
        onClose={mockOnClose}
        onApplicationSubmitted={mockOnApplicationSubmitted}
      />
    )

    expect(screen.getByText('Apply for Position')).toBeInTheDocument()
    expect(screen.getByText('Software Engineer')).toBeInTheDocument()
    expect(screen.getByText('Tech Corp')).toBeInTheDocument()
    expect(screen.getByText('San Francisco, CA • Full Time')).toBeInTheDocument()
    expect(screen.getByText('$80,000 - $120,000 USD')).toBeInTheDocument()
  })

  test('submits application successfully', async () => {
    const mockApplication = {
      id: 'app1',
      jobId: '1',
      candidateId: 'cand1',
      status: 'APPLIED' as const,
      appliedAt: '2024-01-01T00:00:00Z',
      updatedAt: '2024-01-01T00:00:00Z'
    }

    ;(JobService.applyForJob as jest.Mock).mockResolvedValue(mockApplication)

    render(
      <JobApplicationModal
        job={mockJob}
        isOpen={true}
        onClose={mockOnClose}
        onApplicationSubmitted={mockOnApplicationSubmitted}
      />
    )

    const submitButton = screen.getByText('Submit Application')
    fireEvent.click(submitButton)

    await waitFor(() => {
      expect(JobService.applyForJob).toHaveBeenCalledWith('1')
      expect(toast.success).toHaveBeenCalledWith('Application submitted successfully!')
      expect(mockOnApplicationSubmitted).toHaveBeenCalled()
    })
  })

  test('handles application submission error', async () => {
    ;(JobService.applyForJob as jest.Mock).mockRejectedValue(new Error('Network error'))

    render(
      <JobApplicationModal
        job={mockJob}
        isOpen={true}
        onClose={mockOnClose}
        onApplicationSubmitted={mockOnApplicationSubmitted}
      />
    )

    const submitButton = screen.getByText('Submit Application')
    fireEvent.click(submitButton)

    await waitFor(() => {
      expect(toast.error).toHaveBeenCalledWith('Failed to submit application. Please try again.')
    })
  })

  test('closes modal when cancel is clicked', () => {
    render(
      <JobApplicationModal
        job={mockJob}
        isOpen={true}
        onClose={mockOnClose}
        onApplicationSubmitted={mockOnApplicationSubmitted}
      />
    )

    const cancelButton = screen.getByText('Cancel')
    fireEvent.click(cancelButton)

    expect(mockOnClose).toHaveBeenCalled()
  })

  test('shows success state after submission', async () => {
    const mockApplication = {
      id: 'app1',
      jobId: '1',
      candidateId: 'cand1',
      status: 'APPLIED' as const,
      appliedAt: '2024-01-01T00:00:00Z',
      updatedAt: '2024-01-01T00:00:00Z'
    }

    ;(JobService.applyForJob as jest.Mock).mockResolvedValue(mockApplication)

    render(
      <JobApplicationModal
        job={mockJob}
        isOpen={true}
        onClose={mockOnClose}
        onApplicationSubmitted={mockOnApplicationSubmitted}
      />
    )

    const submitButton = screen.getByText('Submit Application')
    fireEvent.click(submitButton)

    await waitFor(() => {
      expect(screen.getByText('Application Submitted!')).toBeInTheDocument()
      expect(screen.getByText(/Your application for Software Engineer at Tech Corp has been submitted successfully/)).toBeInTheDocument()
    })
  })
})