import { render, screen, fireEvent, waitFor } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { RegisterForm } from '../register-form'
import { AuthService } from '@/services/auth.service'
import { toast } from 'sonner'

// Mock dependencies
jest.mock('@/services/auth.service')
jest.mock('sonner')

const mockPush = jest.fn()
jest.mock('next/navigation', () => ({
  useRouter: () => ({
    push: mockPush,
  }),
}))

describe('RegisterForm', () => {
  beforeEach(() => {
    jest.clearAllMocks()
  })

  test('renders candidate registration form by default', () => {
    render(<RegisterForm />)

    expect(screen.getByLabelText(/full name/i)).toBeInTheDocument()
    expect(screen.getByLabelText(/email/i)).toBeInTheDocument()
    expect(screen.getByLabelText(/phone/i)).toBeInTheDocument()
    expect(screen.getByLabelText(/degree/i)).toBeInTheDocument()
    expect(screen.getByLabelText(/graduation year/i)).toBeInTheDocument()
    expect(screen.getByLabelText(/password/i)).toBeInTheDocument()
    expect(screen.getByRole('button', { name: /create account/i })).toBeInTheDocument()
  })

  test('switches to employer registration form', async () => {
    const user = userEvent.setup()
    render(<RegisterForm />)

    const employerTab = screen.getByText(/employer/i)
    await user.click(employerTab)

    expect(screen.getByLabelText(/company name/i)).toBeInTheDocument()
    expect(screen.getByLabelText(/website/i)).toBeInTheDocument()
    expect(screen.getByLabelText(/company description/i)).toBeInTheDocument()
  })

  test('validates required fields for candidate', async () => {
    const user = userEvent.setup()
    render(<RegisterForm />)

    const submitButton = screen.getByRole('button', { name: /create account/i })
    await user.click(submitButton)

    await waitFor(() => {
      expect(screen.getByText(/name is required/i)).toBeInTheDocument()
      expect(screen.getByText(/email is required/i)).toBeInTheDocument()
      expect(screen.getByText(/password is required/i)).toBeInTheDocument()
    })
  })

  test('validates password strength', async () => {
    const user = userEvent.setup()
    render(<RegisterForm />)

    const passwordInput = screen.getByLabelText(/password/i)
    await user.type(passwordInput, '123')

    const submitButton = screen.getByRole('button', { name: /create account/i })
    await user.click(submitButton)

    await waitFor(() => {
      expect(screen.getByText(/password must be at least 8 characters/i)).toBeInTheDocument()
    })
  })

  test('submits candidate registration successfully', async () => {
    const user = userEvent.setup()
    const mockRegisterResponse = {
      message: 'Registration successful',
    }

    ;(AuthService.registerCandidate as jest.Mock).mockResolvedValue(mockRegisterResponse)

    render(<RegisterForm />)

    // Fill out the form
    await user.type(screen.getByLabelText(/full name/i), 'John Doe')
    await user.type(screen.getByLabelText(/email/i), 'john@example.com')
    await user.type(screen.getByLabelText(/phone/i), '+1234567890')
    await user.type(screen.getByLabelText(/degree/i), 'Computer Science')
    await user.type(screen.getByLabelText(/graduation year/i), '2022')
    await user.type(screen.getByLabelText(/password/i), 'password123')

    const submitButton = screen.getByRole('button', { name: /create account/i })
    await user.click(submitButton)

    await waitFor(() => {
      expect(AuthService.registerCandidate).toHaveBeenCalledWith({
        name: 'John Doe',
        email: 'john@example.com',
        phone: '+1234567890',
        degree: 'Computer Science',
        graduationYear: 2022,
        password: 'password123',
      })
      expect(toast.success).toHaveBeenCalledWith('Registration successful! Please check your email to verify your account.')
      expect(mockPush).toHaveBeenCalledWith('/login')
    })
  })

  test('submits employer registration successfully', async () => {
    const user = userEvent.setup()
    const mockRegisterResponse = {
      message: 'Registration successful',
    }

    ;(AuthService.registerEmployer as jest.Mock).mockResolvedValue(mockRegisterResponse)

    render(<RegisterForm />)

    // Switch to employer tab
    const employerTab = screen.getByText(/employer/i)
    await user.click(employerTab)

    // Fill out the form
    await user.type(screen.getByLabelText(/company name/i), 'Tech Corp')
    await user.type(screen.getByLabelText(/email/i), 'hr@techcorp.com')
    await user.type(screen.getByLabelText(/website/i), 'https://techcorp.com')
    await user.type(screen.getByLabelText(/company description/i), 'A great tech company')
    await user.type(screen.getByLabelText(/password/i), 'password123')

    const submitButton = screen.getByRole('button', { name: /create account/i })
    await user.click(submitButton)

    await waitFor(() => {
      expect(AuthService.registerEmployer).toHaveBeenCalledWith({
        companyName: 'Tech Corp',
        email: 'hr@techcorp.com',
        website: 'https://techcorp.com',
        description: 'A great tech company',
        password: 'password123',
      })
      expect(toast.success).toHaveBeenCalledWith('Registration successful! Your account is pending approval.')
    })
  })

  test('handles registration error', async () => {
    const user = userEvent.setup()
    ;(AuthService.registerCandidate as jest.Mock).mockRejectedValue(new Error('Email already exists'))

    render(<RegisterForm />)

    // Fill out the form
    await user.type(screen.getByLabelText(/full name/i), 'John Doe')
    await user.type(screen.getByLabelText(/email/i), 'existing@example.com')
    await user.type(screen.getByLabelText(/password/i), 'password123')

    const submitButton = screen.getByRole('button', { name: /create account/i })
    await user.click(submitButton)

    await waitFor(() => {
      expect(toast.error).toHaveBeenCalledWith('Email already exists')
    })
  })
})