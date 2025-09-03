import { render, screen, fireEvent } from '@testing-library/react'
import { JobForm } from '../job-form'
import { it } from 'zod/locales'
import { it } from 'zod/locales'
import { it } from 'zod/locales'
import { beforeEach } from 'node:test'
import { describe } from 'node:test'

// Mock the form dependencies
jest.mock('react-hook-form', () => ({
  useForm: () => ({
    control: {},
    handleSubmit: (fn: (data: unknown) => void) => (e: unknown) => {
      e.preventDefault()
      fn({
        title: 'Test Job',
        description: 'Test Description',
        location: 'Test Location',
        jobType: 'FULL_TIME',
        salaryMin: 50000,
        salaryMax: 80000,
        currency: 'USD'
      })
    },
    setError: jest.fn()
  })
}))

jest.mock('@hookform/resolvers/zod', () => ({
  zodResolver: () => jest.fn()
}))

describe('JobForm', () => {
  const mockOnSubmit = jest.fn()
  const mockOnCancel = jest.fn()

  beforeEach(() => {
    jest.clearAllMocks()
  })

  it('renders job form correctly', () => {
    render(
      <JobForm
        onSubmit={mockOnSubmit}
        onCancel={mockOnCancel}
      />
    )

    expect(screen.getByText('Job Title')).toBeInTheDocument()
    expect(screen.getByText('Job Description')).toBeInTheDocument()
    expect(screen.getByText('Location')).toBeInTheDocument()
    expect(screen.getByText('Job Type')).toBeInTheDocument()
  })

  it('calls onCancel when cancel button is clicked', () => {
    render(
      <JobForm
        onSubmit={mockOnSubmit}
        onCancel={mockOnCancel}
      />
    )

    fireEvent.click(screen.getByText('Cancel'))
    expect(mockOnCancel).toHaveBeenCalled()
  })

  it('shows correct button text for create vs update', () => {
    const { rerender } = render(
      <JobForm
        onSubmit={mockOnSubmit}
        onCancel={mockOnCancel}
      />
    )

    expect(screen.getByText('Create Job')).toBeInTheDocument()

    const mockJob = {
      id: '1',
      title: 'Existing Job',
      description: 'Existing Description',
      salary: { min: 50000, max: 80000, currency: 'USD' },
      location: 'Test Location',
      jobType: 'FULL_TIME' as const,
      isActive: true,
      createdAt: '2023-01-01',
      updatedAt: '2023-01-01',
      expiresAt: '2023-12-31',
      employer: {
        id: '1',
        companyName: 'Test Company'
      }
    }

    rerender(
      <JobForm
        job={mockJob}
        onSubmit={mockOnSubmit}
        onCancel={mockOnCancel}
      />
    )

    expect(screen.getByText('Update Job')).toBeInTheDocument()
  })
})