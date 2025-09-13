import { render, screen } from '@testing-library/react'
import { ProtectedRoute } from '../protected-route'
import { useAuth } from '@/hooks/use-auth'

// Mock the useAuth hook
jest.mock('@/hooks/use-auth')

const mockUseAuth = useAuth as jest.MockedFunction<typeof useAuth>

describe('ProtectedRoute', () => {
  const TestComponent = () => <div>Protected Content</div>

  beforeEach(() => {
    jest.clearAllMocks()
  })

  test('renders children when user is authenticated', () => {
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

    render(
      <ProtectedRoute>
        <TestComponent />
      </ProtectedRoute>
    )

    expect(screen.getByText('Protected Content')).toBeInTheDocument()
  })

  test('shows loading when authentication is loading', () => {
    mockUseAuth.mockReturnValue({
      user: null,
      isLoading: true,
      login: jest.fn(),
      logout: jest.fn(),
      register: jest.fn(),
    })

    render(
      <ProtectedRoute>
        <TestComponent />
      </ProtectedRoute>
    )

    expect(screen.getByText(/loading/i)).toBeInTheDocument()
  })

  test('redirects to login when user is not authenticated', () => {
    mockUseAuth.mockReturnValue({
      user: null,
      isLoading: false,
      login: jest.fn(),
      logout: jest.fn(),
      register: jest.fn(),
    })

    render(
      <ProtectedRoute>
        <TestComponent />
      </ProtectedRoute>
    )

    expect(screen.getByText(/redirecting to login/i)).toBeInTheDocument()
  })

  test('shows unauthorized when user role does not match required role', () => {
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

    render(
      <ProtectedRoute requiredRole="ADMIN">
        <TestComponent />
      </ProtectedRoute>
    )

    expect(screen.getByText(/unauthorized/i)).toBeInTheDocument()
  })

  test('renders children when user role matches required role', () => {
    mockUseAuth.mockReturnValue({
      user: {
        id: '1',
        email: 'admin@example.com',
        role: 'ADMIN',
      },
      isLoading: false,
      login: jest.fn(),
      logout: jest.fn(),
      register: jest.fn(),
    })

    render(
      <ProtectedRoute requiredRole="ADMIN">
        <TestComponent />
      </ProtectedRoute>
    )

    expect(screen.getByText('Protected Content')).toBeInTheDocument()
  })
})