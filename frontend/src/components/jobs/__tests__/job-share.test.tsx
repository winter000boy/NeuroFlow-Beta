import { render, screen, fireEvent, waitFor } from '@testing-library/react'
import { JobShare } from '../job-share'
import { toast } from 'sonner'

// Mock dependencies
jest.mock('sonner')

// Mock navigator.clipboard
Object.assign(navigator, {
  clipboard: {
    writeText: jest.fn(),
  },
})

// Mock window.open
global.open = jest.fn()

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

describe('JobShare', () => {
  beforeEach(() => {
    jest.clearAllMocks()
    // Mock window.location
    delete (window as any).location
    window.location = { href: 'http://localhost:3000/jobs/1' } as any
  })

  test('renders share button correctly', () => {
    render(<JobShare job={mockJob} />)

    expect(screen.getByText('Share')).toBeInTheDocument()
  })

  test('opens share modal when share button is clicked', () => {
    render(<JobShare job={mockJob} />)

    const shareButton = screen.getByText('Share')
    fireEvent.click(shareButton)

    expect(screen.getByText('Share Job')).toBeInTheDocument()
    expect(screen.getByText('Software Engineer')).toBeInTheDocument()
    expect(screen.getByText('Tech Corp')).toBeInTheDocument()
  })

  test('copies link to clipboard successfully', async () => {
    ;(navigator.clipboard.writeText as jest.Mock).mockResolvedValue(undefined)

    render(<JobShare job={mockJob} />)

    const shareButton = screen.getByText('Share')
    fireEvent.click(shareButton)

    const copyButton = screen.getByRole('button', { name: /copy/i })
    fireEvent.click(copyButton)

    await waitFor(() => {
      expect(navigator.clipboard.writeText).toHaveBeenCalledWith('http://localhost:3000/jobs/1')
      expect(toast.success).toHaveBeenCalledWith('Link copied to clipboard!')
    })
  })

  test('handles clipboard copy error', async () => {
    ;(navigator.clipboard.writeText as jest.Mock).mockRejectedValue(new Error('Clipboard error'))

    render(<JobShare job={mockJob} />)

    const shareButton = screen.getByText('Share')
    fireEvent.click(shareButton)

    const copyButton = screen.getByRole('button', { name: /copy/i })
    fireEvent.click(copyButton)

    await waitFor(() => {
      expect(toast.error).toHaveBeenCalledWith('Failed to copy link')
    })
  })

  test('opens social media sharing windows', () => {
    render(<JobShare job={mockJob} />)

    const shareButton = screen.getByText('Share')
    fireEvent.click(shareButton)

    // Test Facebook sharing
    const facebookButton = screen.getByText('Facebook')
    fireEvent.click(facebookButton)

    expect(global.open).toHaveBeenCalledWith(
      expect.stringContaining('facebook.com/sharer'),
      '_blank',
      'width=600,height=400,scrollbars=yes,resizable=yes'
    )

    // Test Twitter sharing
    const twitterButton = screen.getByText('Twitter')
    fireEvent.click(twitterButton)

    expect(global.open).toHaveBeenCalledWith(
      expect.stringContaining('twitter.com/intent/tweet'),
      '_blank',
      'width=600,height=400,scrollbars=yes,resizable=yes'
    )

    // Test LinkedIn sharing
    const linkedinButton = screen.getByText('LinkedIn')
    fireEvent.click(linkedinButton)

    expect(global.open).toHaveBeenCalledWith(
      expect.stringContaining('linkedin.com/sharing'),
      '_blank',
      'width=600,height=400,scrollbars=yes,resizable=yes'
    )
  })

  test('displays correct job URL in input field', () => {
    render(<JobShare job={mockJob} />)

    const shareButton = screen.getByText('Share')
    fireEvent.click(shareButton)

    const urlInput = screen.getByDisplayValue('http://localhost:3000/jobs/1')
    expect(urlInput).toBeInTheDocument()
    expect(urlInput).toHaveAttribute('readonly')
  })

  test('closes modal when close button is clicked', () => {
    render(<JobShare job={mockJob} />)

    const shareButton = screen.getByText('Share')
    fireEvent.click(shareButton)

    expect(screen.getByText('Share Job')).toBeInTheDocument()

    const closeButton = screen.getByRole('button', { name: /close/i })
    fireEvent.click(closeButton)

    expect(screen.queryByText('Share Job')).not.toBeInTheDocument()
  })
})