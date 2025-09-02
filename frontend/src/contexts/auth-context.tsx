'use client'

import React, { createContext, useContext, useEffect, useState } from 'react'
import { useRouter } from 'next/navigation'
import Cookies from 'js-cookie'
import { authService } from '@/services/auth.service'

export interface User {
  id: string
  email: string
  name: string
  role: 'CANDIDATE' | 'EMPLOYER' | 'ADMIN'
}

export interface AuthContextType {
  user: User | null
  token: string | null
  isLoading: boolean
  login: (email: string, password: string) => Promise<void>
  register: (data: RegisterData) => Promise<void>
  logout: () => void
  refreshToken: () => Promise<void>
}

export interface RegisterData {
  email: string
  password: string
  name: string
  role: 'CANDIDATE' | 'EMPLOYER'
  // Candidate specific fields
  phone?: string
  degree?: string
  graduationYear?: number
  // Employer specific fields
  companyName?: string
  website?: string
  description?: string
}

const AuthContext = createContext<AuthContextType | undefined>(undefined)

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [user, setUser] = useState<User | null>(null)
  const [token, setToken] = useState<string | null>(null)
  const [isLoading, setIsLoading] = useState(true)
  const router = useRouter()

  useEffect(() => {
    // Check for existing token on mount
    const savedToken = Cookies.get('auth-token')
    if (savedToken) {
      setToken(savedToken)
      // Validate token and get user info
      validateToken(savedToken)
    } else {
      setIsLoading(false)
    }
  }, [])

  const validateToken = async (token: string) => {
    try {
      const userData = await authService.validateToken(token)
      setUser(userData)
      setToken(token)
    } catch {
      // Token is invalid, remove it
      Cookies.remove('auth-token')
      setToken(null)
      setUser(null)
    } finally {
      setIsLoading(false)
    }
  }

  const login = async (email: string, password: string) => {
    try {
      const response = await authService.login(email, password)
      const { token: newToken, user: userData } = response
      
      setToken(newToken)
      setUser(userData)
      
      // Store token in cookie (expires in 7 days)
      Cookies.set('auth-token', newToken, { expires: 7, secure: true, sameSite: 'strict' })
      
      // Redirect based on role
      if (userData.role === 'ADMIN') {
        router.push('/admin/dashboard')
      } else if (userData.role === 'EMPLOYER') {
        router.push('/employer/dashboard')
      } else {
        router.push('/jobs')
      }
    } catch (error) {
      throw error
    }
  }

  const register = async (data: RegisterData) => {
    try {
      await authService.register(data)
      // After successful registration, redirect to login
      router.push('/login?message=Registration successful. Please log in.')
    } catch (error) {
      throw error
    }
  }

  const logout = () => {
    setUser(null)
    setToken(null)
    Cookies.remove('auth-token')
    router.push('/')
  }

  const refreshToken = async () => {
    try {
      const currentToken = token || Cookies.get('auth-token')
      if (!currentToken) throw new Error('No token available')
      
      const response = await authService.refreshToken(currentToken)
      const { token: newToken, user: userData } = response
      
      setToken(newToken)
      setUser(userData)
      Cookies.set('auth-token', newToken, { expires: 7, secure: true, sameSite: 'strict' })
    } catch (error) {
      logout()
      throw error
    }
  }

  const value: AuthContextType = {
    user,
    token,
    isLoading,
    login,
    register,
    logout,
    refreshToken,
  }

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

export function useAuth() {
  const context = useContext(AuthContext)
  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider')
  }
  return context
}