import { renderHook, act, waitFor } from '@testing-library/react'
import { useAuth } from '../use-auth'
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

// Mock localStorage
const localStorageMock = {
  getItem: jest.fn(),
  setItem: jest.fn(),
  removeItem: jest.fn(),
}
Object.defineProperty(window, 'localStorage', {
  value: localStorageMock,
})

describe('useAuth', () => {
  beforeEach(() => {
    jest.clearAllMocks()
    localStorageMock.getItem.mockReturnValue(null)
  })

  test('initializes with no user when no token in localStorage', () => {
    const { result } = renderHook(() => useAuth())

    expect(result.current.user).toBeNull()
    expect(result.current.isLoading).toBe(false)
  })

  test('initializes with user when valid token in localStorage', async () => {
    const mockUser = {
      id: '1',
      email: 'test@example.com',
      role: 'CANDIDATE',
    }

    localStorageMock.getItem.mockReturnValue('valid-token')
    ;(AuthService.getCurrentUser as jest.Mock).mockResolvedValue(mockUser)

    const { result } = renderHook(() => useAuth())

    expect(result.current.isLoading).toBe(true)

    await waitFor(() => {
      expect(result.current.user).toEqual(mockUser)
      expect(result.current.isLoading).toBe(false)
    })
  })

  test('handles login successfully', async () => {
    const mockLoginResponse = {
      token: 'new-token',
      user: {
        id: '1',
        email: 'test@example.com',
        role: 'CANDIDATE',
      },
    }

    ;(AuthService.login as jest.Mock).mockResolvedValue(mockLoginResponse)

    const { result } = renderHook(() => useAuth())

    await act(async () => {
      await result.current.login({
        email: 'test@example.com',
        password: 'password123',
      })
    })

    expect(AuthService.login).toHaveBeenCalledWith({
      email: 'test@example.com',
      password: 'password123',
    })
    expect(localStorageMock.setItem).toHaveBeenCalledWith('token', 'new-token')
    expect(result.current.user).toEqual(mockLoginResponse.user)
    expect(toast.success).toHaveBeenCalledWith('Login successful!')
  })

  test('handles login error', async () => {
    ;(AuthService.login as jest.Mock).mockRejectedValue(new Error('Invalid credentials'))

    const { result } = renderHook(() => useAuth())

    await act(async () => {
      try {
        await result.current.login({
          email: 'test@example.com',
          password: 'wrongpassword',
        })
      } catch (error) {
        // Expected to throw
      }
    })

    expect(toast.error).toHaveBeenCalledWith('Invalid credentials')
    expect(result.current.user).toBeNull()
  })

  test('handles logout', async () => {
    const mockUser = {
      id: '1',
      email: 'test@example.com',
      role: 'CANDIDATE',
    }

    localStorageMock.getItem.mockReturnValue('valid-token')
    ;(AuthService.getCurrentUser as jest.Mock).mockResolvedValue(mockUser)

    const { result } = renderHook(() => useAuth())

    await waitFor(() => {
      expect(result.current.user).toEqual(mockUser)
    })

    act(() => {
      result.current.logout()
    })

    expect(localStorageMock.removeItem).toHaveBeenCalledWith('token')
    expect(result.current.user).toBeNull()
    expect(mockPush).toHaveBeenCalledWith('/login')
    expect(toast.success).toHaveBeenCalledWith('Logged out successfully')
  })

  test('handles candidate registration', async () => {
    const mockRegisterResponse = {
      message: 'Registration successful',
    }

    ;(AuthService.registerCandidate as jest.Mock).mockResolvedValue(mockRegisterResponse)

    const { result } = renderHook(() => useAuth())

    const registrationData = {
      name: 'John Doe',
      email: 'john@example.com',
      phone: '+1234567890',
      degree: 'Computer Science',
      graduationYear: 2022,
      password: 'password123',
    }

    await act(async () => {
      await result.current.register(registrationData, 'candidate')
    })

    expect(AuthService.registerCandidate).toHaveBeenCalledWith(registrationData)
    expect(toast.success).toHaveBeenCalledWith('Registration successful! Please check your email to verify your account.')
    expect(mockPush).toHaveBeenCalledWith('/login')
  })

  test('handles employer registration', async () => {
    const mockRegisterResponse = {
      message: 'Registration successful',
    }

    ;(AuthService.registerEmployer as jest.Mock).mockResolvedValue(mockRegisterResponse)

    const { result } = renderHook(() => useAuth())

    const registrationData = {
      companyName: 'Tech Corp',
      email: 'hr@techcorp.com',
      website: 'https://techcorp.com',
      description: 'A great tech company',
      password: 'password123',
    }

    await act(async () => {
      await result.current.register(registrationData, 'employer')
    })

    expect(AuthService.registerEmployer).toHaveBeenCalledWith(registrationData)
    expect(toast.success).toHaveBeenCalledWith('Registration successful! Your account is pending approval.')
  })

  test('handles registration error', async () => {
    ;(AuthService.registerCandidate as jest.Mock).mockRejectedValue(new Error('Email already exists'))

    const { result } = renderHook(() => useAuth())

    const registrationData = {
      name: 'John Doe',
      email: 'existing@example.com',
      password: 'password123',
    }

    await act(async () => {
      try {
        await result.current.register(registrationData, 'candidate')
      } catch (error) {
        // Expected to throw
      }
    })

    expect(toast.error).toHaveBeenCalledWith('Email already exists')
  })

  test('clears user when token is invalid', async () => {
    localStorageMock.getItem.mockReturnValue('invalid-token')
    ;(AuthService.getCurrentUser as jest.Mock).mockRejectedValue(new Error('Invalid token'))

    const { result } = renderHook(() => useAuth())

    await waitFor(() => {
      expect(result.current.user).toBeNull()
      expect(result.current.isLoading).toBe(false)
      expect(localStorageMock.removeItem).toHaveBeenCalledWith('token')
    })
  })
})