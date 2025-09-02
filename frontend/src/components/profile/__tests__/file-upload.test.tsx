import { render, screen, fireEvent, waitFor } from '@testing-library/react'
import { FileUpload } from '../file-upload'

// Mock file for testing
const createMockFile = (name: string, size: number, type: string) => {
  const file = new File([''], name, { type })
  Object.defineProperty(file, 'size', { value: size })
  return file
}

describe('FileUpload', () => {
  const mockOnFileUpload = jest.fn()

  beforeEach(() => {
    mockOnFileUpload.mockClear()
  })

  const defaultProps = {
    onFileUpload: mockOnFileUpload,
    acceptedTypes: '.pdf,.doc,.docx',
    maxSize: 5,
    label: 'Resume File',
    description: 'PDF, DOC, or DOCX up to 5MB',
  }

  test('renders upload area correctly', () => {
    render(<FileUpload {...defaultProps} />)
    
    expect(screen.getByText('Resume File')).toBeInTheDocument()
    expect(screen.getByText('Click to upload')).toBeInTheDocument()
    expect(screen.getByText('PDF, DOC, or DOCX up to 5MB')).toBeInTheDocument()
  })

  test('shows current file when provided', () => {
    render(
      <FileUpload
        {...defaultProps}
        currentFileUrl="https://example.com/resume.pdf"
        currentFileName="Resume.pdf"
      />
    )
    
    expect(screen.getByText('Resume.pdf')).toBeInTheDocument()
    expect(screen.getByText('View file')).toBeInTheDocument()
  })

  test('validates file size', async () => {
    render(<FileUpload {...defaultProps} />)
    
    const fileInput = screen.getByRole('button', { name: /click to upload/i })
    const largeFile = createMockFile('large.pdf', 10 * 1024 * 1024, 'application/pdf') // 10MB
    
    // Simulate file selection
    const input = document.querySelector('input[type="file"]') as HTMLInputElement
    if (input) {
      Object.defineProperty(input, 'files', {
        value: [largeFile],
        writable: false,
      })
      fireEvent.change(input)
    }
    
    await waitFor(() => {
      expect(screen.getByText('File size must be less than 5MB')).toBeInTheDocument()
    })
    
    expect(mockOnFileUpload).not.toHaveBeenCalled()
  })

  test('validates file type', async () => {
    render(<FileUpload {...defaultProps} />)
    
    const invalidFile = createMockFile('image.jpg', 1024, 'image/jpeg')
    
    const input = document.querySelector('input[type="file"]') as HTMLInputElement
    if (input) {
      Object.defineProperty(input, 'files', {
        value: [invalidFile],
        writable: false,
      })
      fireEvent.change(input)
    }
    
    await waitFor(() => {
      expect(screen.getByText(/File type not supported/)).toBeInTheDocument()
    })
    
    expect(mockOnFileUpload).not.toHaveBeenCalled()
  })

  test('calls onFileUpload with valid file', async () => {
    render(<FileUpload {...defaultProps} />)
    
    const validFile = createMockFile('resume.pdf', 1024, 'application/pdf')
    
    const input = document.querySelector('input[type="file"]') as HTMLInputElement
    if (input) {
      Object.defineProperty(input, 'files', {
        value: [validFile],
        writable: false,
      })
      fireEvent.change(input)
    }
    
    await waitFor(() => {
      expect(mockOnFileUpload).toHaveBeenCalledWith(validFile)
    })
  })

  test('shows loading state', () => {
    render(<FileUpload {...defaultProps} isLoading={true} />)
    
    expect(screen.getByText('Uploading...')).toBeInTheDocument()
    expect(screen.getByRole('button')).toBeDisabled()
  })
})