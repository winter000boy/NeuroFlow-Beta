import { render, screen, fireEvent, waitFor } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { AuthProvider } from '@/contexts/auth-context'
import { LoginForm } from '@/components/auth/login-form'
import { RegisterForm } from '@/components/auth/register-form'
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

const AuthWrapper = ({ children }: { children: React.ReactNode }) => (
  <AuthProvider>{children}</AuthProvider>
)

describe('Authentication Flow Integration', () => {
  beforeEach(() => {
    jest.clearAllMocks()
    localStorage.clear()
  })

  describe('Login Flow', () => {
    test('complete login flow with valid credentials', async () => {
      const user = userEvent.setup()
      const mockLoginResponse = {
        token: 'mock-token',
        user: {
          id: '1',
          email: 'test@example.com',
          role: 'CANDIDATE',
        },
      }

      ;(AuthService.login as jest.Mock).mockResolvedValue(mockLoginResponse)

      render(
        <AuthWrapper>
          <LoginForm />
        </AuthWrapper>
      )

      // Fill out login form
      const emailInput = screen.getByLabelText(/email/i)
      const passwordInput = screen.getByLabelText(/password/i)
      const submitButton = screen.getByRole('button', { name: /sign in/i })

      await user.type(emailInput, 'test@example.com')
      await user.type(passwordInput, 'password123')
      await user.click(submitButton)

      // Verify API call and success handling
      await waitFor(() => {
        expect(AuthService.login).toHaveBeenCalledWith({
          email: 'test@example.com',
          password: 'password123',
        })
        expect(toast.success).toHaveBeenCalledWith('Login successful!')
        expect(mockPush).toHaveBeenCalledWith('/')
      })
    })

    test('handles login with invalid credentials', async () => {
      const user = userEvent.setup()
      ;(AuthService.login as jest.Mock).mockRejectedValue(new Error('Invalid credentials'))

      render(
        <AuthWrapper>
          <LoginForm />
        </AuthWrapper>
      )

      const emailInput = screen.getByLabelText(/email/i)
      const passwordInput = screen.getByLabelText(/password/i)
      const submitButton = screen.getByRole('button', { name: /sign in/i })

      await user.type(emailInput, 'test@example.com')
      await user.type(passwordInput, 'wrongpassword')
      await user.click(submitButton)

      await waitFor(() => {
        expect(toast.error).toHaveBeenCalledWith('Invalid credentials')
      })

      // Form should remain visible for retry
      expect(screen.getByLabelText(/email/i)).toBeInTheDocument()
      expect(screen.getByLabelText(/password/i)).toBeInTheDocument()
    })
  })

  describe('Registration Flow', () => {
    test('complete candidate registration flow', async () => {
      const user = userEvent.setup()
      const mockRegisterResponse = {
        message: 'Registration successful',
      }

      ;(AuthService.registerCandidate as jest.Mock).mockResolvedValue(mockRegisterResponse)

      render(
        <AuthWrapper>
          <RegisterForm />
        </AuthWrapper>
      )

      // Fill out candidate registration form
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

    test('complete employer registration flow', async () => {
      const user = userEvent.setup()
      const mockRegisterResponse = {
        message: 'Registration successful',
      }

      ;(AuthService.registerEmployer as jest.Mock).mockResolvedValue(mockRegisterResponse)

      render(
        <AuthWrapper>
          <RegisterForm />
        </AuthWrapper>
      )

      // Switch to employer tab
      const employerTab = screen.getByText(/employer/i)
      await user.click(employerTab)

      // Fill out employer registration form
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

    test('handles registration validation errors', async () => {
      const user = userEvent.setup()

      render(
        <AuthWrapper>
          <RegisterForm />
        </AuthWrapper>
      )

      // Try to submit without filling required fields
      const submitButton = screen.getByRole('button', { name: /create account/i })
      await user.click(submitButton)

      await waitFor(() => {
        expect(screen.getByText(/name is required/i)).toBeInTheDocument()
        expect(screen.getByText(/email is required/i)).toBeInTheDocument()
        expect(screen.getByText(/password is required/i)).toBeInTheDocument()
      })

      // Should not call API
      expect(AuthService.registerCandidate).not.toHaveBeenCalled()
    })

    test('handles registration server errors', async () => {
      const user = userEvent.setup()
      ;(AuthService.registerCandidate as jest.Mock).mockRejectedValue(new Error('Email already exists'))

      render(
        <AuthWrapper>
          <RegisterForm />
        </AuthWrapper>
      )

      // Fill out form with existing email
      await user.type(screen.getByLabelText(/full name/i), 'John Doe')
      await user.type(screen.getByLabelText(/email/i), 'existing@example.com')
      await user.type(screen.getByLabelText(/password/i), 'password123')

      const submitButton = screen.getByRole('button', { name: /create account/i })
      await user.click(submitButton)

      await waitFor(() => {
        expect(toast.error).toHaveBeenCalledWith('Email already exists')
      })

      // Form should remain visible for correction
      expect(screen.getByLabelText(/full name/i)).toBeInTheDocument()
    })
  })

  describe('Form Validation', () => {
    test('validates email format in login form', async () => {
      const user = userEvent.setup()

      render(
        <AuthWrapper>
          <LoginForm />
        </AuthWrapper>
      )

      const emailInput = screen.getByLabelText(/email/i)
      const submitButton = screen.getByRole('button', { name: /sign in/i })

      await user.type(emailInput, 'invalid-email')
      await user.click(submitButton)

      await waitFor(() => {
        expect(screen.getByText(/invalid email format/i)).toBeInTheDocument()
      })
    })

    test('validates password strength in registration form', async () => {
      const user = userEvent.setup()

      render(
        <AuthWrapper>
          <RegisterForm />
        </AuthWrapper>
      )

      const passwordInput = screen.getByLabelText(/password/i)
      const submitButton = screen.getByRole('button', { name: /create account/i })

      await user.type(passwordInput, '123')
      await user.click(submitButton)

      await waitFor(() => {
        expect(screen.getByText(/password must be at least 8 characters/i)).toBeInTheDocument()
      })
    })
  })
})