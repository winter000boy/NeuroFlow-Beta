import { render, screen, fireEvent } from '@testing-library/react'
import { Header } from '../header'
import { useAuth } from '@/hooks/use-auth'

// Mock dependencies
jest.mock('@/hooks/use-auth')

const mockUseAuth = useAuth as jest.MockedFunction<typeof useAuth>

describe('Header', () => {
  beforeEach(() => {
    jest.clearAllMocks()
  })

  test('renders header for unauthenticated user', () => {
    mockUseAuth.mockReturnValue({
      user: null,
      isLoading: false,
      login: jest.fn(),
      logout: jest.fn(),
      register: jest.fn(),
    })

    render(<Header />)

    expect(screen.getByText('JobApp')).toBeInTheDocument()
    expect(screen.getByText('Sign In')).toBeInTheDocument()
    expect(screen.getByText('Sign Up')).toBeInTheDocument()
  })

  test('renders header for authenticated candidate', () => {
    mockUseAuth.mockReturnValue({
      user: {
        id: '1',
        email: 'candidate@example.com',
        role: 'CANDIDATE',
        name: 'John Doe',
      },
      isLoading: false,
      login: jest.fn(),
      logout: jest.fn(),
      register: jest.fn(),
    })

    render(<Header />)

    expect(screen.getByText('JobApp')).toBeInTheDocument()
    expect(screen.getByText('Jobs')).toBeInTheDocument()
    expect(screen.getByText('My Applications')).toBeInTheDocument()
    expect(screen.getByText('Profile')).toBeInTheDocument()
    expect(screen.queryByText('Sign In')).not.toBeInTheDocument()
  })

  test('renders header for authenticated employer', () => {
    mockUseAuth.mockReturnValue({
      user: {
        id: '2',
        email: 'employer@example.com',
        role: 'EMPLOYER',
        companyName: 'Tech Corp',
      },
      isLoading: false,
      login: jest.fn(),
      logout: jest.fn(),
      register: jest.fn(),
    })

    render(<Header />)

    expect(screen.getByText('Dashboard')).toBeInTheDocument()
    expect(screen.getByText('Post Job')).toBeInTheDocument()
    expect(screen.getByText('Applications')).toBeInTheDocument()
  })

  test('renders header for admin user', () => {
    mockUseAuth.mockReturnValue({
      user: {
        id: '3',
        email: 'admin@example.com',
        role: 'ADMIN',
        name: 'Admin User',
      },
      isLoading: false,
      login: jest.fn(),
      logout: jest.fn(),
      register: jest.fn(),
    })

    render(<Header />)

    expect(screen.getByText('Admin Dashboard')).toBeInTheDocument()
    expect(screen.getByText('Users')).toBeInTheDocument()
    expect(screen.getByText('Analytics')).toBeInTheDocument()
  })

  test('handles logout', () => {
    const mockLogout = jest.fn()
    mockUseAuth.mockReturnValue({
      user: {
        id: '1',
        email: 'candidate@example.com',
        role: 'CANDIDATE',
        name: 'John Doe',
      },
      isLoading: false,
      login: jest.fn(),
      logout: mockLogout,
      register: jest.fn(),
    })

    render(<Header />)

    // Open user menu
    const userMenuButton = screen.getByTestId('user-menu-button')
    fireEvent.click(userMenuButton)

    // Click logout
    const logoutButton = screen.getByText('Logout')
    fireEvent.click(logoutButton)

    expect(mockLogout).toHaveBeenCalled()
  })

  test('shows mobile menu toggle on small screens', () => {
    mockUseAuth.mockReturnValue({
      user: null,
      isLoading: false,
      login: jest.fn(),
      logout: jest.fn(),
      register: jest.fn(),
    })

    render(<Header />)

    const mobileMenuButton = screen.getByTestId('mobile-menu-button')
    expect(mobileMenuButton).toBeInTheDocument()
  })

  test('toggles mobile menu', () => {
    mockUseAuth.mockReturnValue({
      user: null,
      isLoading: false,
      login: jest.fn(),
      logout: jest.fn(),
      register: jest.fn(),
    })

    render(<Header />)

    const mobileMenuButton = screen.getByTestId('mobile-menu-button')
    fireEvent.click(mobileMenuButton)

    expect(screen.getByTestId('mobile-menu')).toBeVisible()

    // Close menu
    fireEvent.click(mobileMenuButton)
    expect(screen.getByTestId('mobile-menu')).not.toBeVisible()
  })

  test('shows theme toggle', () => {
    mockUseAuth.mockReturnValue({
      user: null,
      isLoading: false,
      login: jest.fn(),
      logout: jest.fn(),
      register: jest.fn(),
    })

    render(<Header />)

    expect(screen.getByTestId('theme-toggle')).toBeInTheDocument()
  })

  test('shows loading state when auth is loading', () => {
    mockUseAuth.mockReturnValue({
      user: null,
      isLoading: true,
      login: jest.fn(),
      logout: jest.fn(),
      register: jest.fn(),
    })

    render(<Header />)

    expect(screen.getByTestId('header-loading')).toBeInTheDocument()
  })
})