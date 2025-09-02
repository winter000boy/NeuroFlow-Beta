import { apiClient } from './api'
import type { User, RegisterData } from '@/contexts/auth-context'

export interface LoginResponse {
  token: string
  user: User
  refreshToken: string
}

export interface RegisterResponse {
  message: string
}

class AuthService {
  async login(email: string, password: string): Promise<LoginResponse> {
    const response = await apiClient.post('/auth/login', {
      email,
      password,
    })
    return response.data
  }

  async register(data: RegisterData): Promise<RegisterResponse> {
    const endpoint = data.role === 'CANDIDATE' 
      ? '/auth/register/candidate' 
      : '/auth/register/employer'
    
    const response = await apiClient.post(endpoint, data)
    return response.data
  }

  async refreshToken(token: string): Promise<LoginResponse> {
    const response = await apiClient.post('/auth/refresh', {
      refreshToken: token,
    })
    return response.data
  }

  async validateToken(token: string): Promise<User> {
    const response = await apiClient.get('/auth/validate', {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    })
    return response.data
  }

  async logout(): Promise<void> {
    await apiClient.post('/auth/logout')
  }
}

export const authService = new AuthService()