'use client'

import { useEffect } from 'react'
import { useRouter } from 'next/navigation'
import { useAuth } from '@/contexts/auth-context'
import { Loader2 } from 'lucide-react'

interface ProtectedRouteProps {
  children: React.ReactNode
  allowedRoles?: ('CANDIDATE' | 'EMPLOYER' | 'ADMIN')[]
  requireAuth?: boolean
  redirectTo?: string
}

export function ProtectedRoute({
  children,
  allowedRoles,
  requireAuth = true,
  redirectTo = '/login',
}: ProtectedRouteProps) {
  const { user, isLoading } = useAuth()
  const router = useRouter()

  useEffect(() => {
    if (isLoading) return

    // If authentication is required but user is not logged in
    if (requireAuth && !user) {
      router.push(redirectTo)
      return
    }

    // If user is logged in but doesn't have required role
    if (user && allowedRoles && !allowedRoles.includes(user.role)) {
      // Redirect based on user role
      if (user.role === 'ADMIN') {
        router.push('/admin/dashboard')
      } else if (user.role === 'EMPLOYER') {
        router.push('/employer/dashboard')
      } else {
        router.push('/jobs')
      }
      return
    }
  }, [user, isLoading, requireAuth, allowedRoles, router, redirectTo])

  // Show loading spinner while checking authentication
  if (isLoading) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <div className="text-center">
          <Loader2 className="h-8 w-8 animate-spin mx-auto mb-4" />
          <p className="text-muted-foreground">Loading...</p>
        </div>
      </div>
    )
  }

  // If authentication is required but user is not logged in, don't render children
  if (requireAuth && !user) {
    return null
  }

  // If user doesn't have required role, don't render children
  if (user && allowedRoles && !allowedRoles.includes(user.role)) {
    return null
  }

  return <>{children}</>
}

// Higher-order component for protecting pages
export function withAuth<P extends object>(
  Component: React.ComponentType<P>,
  options?: Omit<ProtectedRouteProps, 'children'>
) {
  return function AuthenticatedComponent(props: P) {
    return (
      <ProtectedRoute {...options}>
        <Component {...props} />
      </ProtectedRoute>
    )
  }
}

// Specific role-based protection components
export function CandidateRoute({ children }: { children: React.ReactNode }) {
  return (
    <ProtectedRoute allowedRoles={['CANDIDATE']}>
      {children}
    </ProtectedRoute>
  )
}

export function EmployerRoute({ children }: { children: React.ReactNode }) {
  return (
    <ProtectedRoute allowedRoles={['EMPLOYER']}>
      {children}
    </ProtectedRoute>
  )
}

export function AdminRoute({ children }: { children: React.ReactNode }) {
  return (
    <ProtectedRoute allowedRoles={['ADMIN']} redirectTo="/admin/login">
      {children}
    </ProtectedRoute>
  )
}

// Component for routes that should only be accessible to non-authenticated users
export function GuestRoute({ children }: { children: React.ReactNode }) {
  const { user, isLoading } = useAuth()
  const router = useRouter()

  useEffect(() => {
    if (isLoading) return

    if (user) {
      // Redirect authenticated users based on their role
      if (user.role === 'ADMIN') {
        router.push('/admin/dashboard')
      } else if (user.role === 'EMPLOYER') {
        router.push('/employer/dashboard')
      } else {
        router.push('/jobs')
      }
    }
  }, [user, isLoading, router])

  if (isLoading) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <div className="text-center">
          <Loader2 className="h-8 w-8 animate-spin mx-auto mb-4" />
          <p className="text-muted-foreground">Loading...</p>
        </div>
      </div>
    )
  }

  // If user is authenticated, don't render children (they'll be redirected)
  if (user) {
    return null
  }

  return <>{children}</>
}