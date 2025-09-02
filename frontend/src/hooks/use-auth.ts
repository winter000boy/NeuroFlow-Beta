import { useAuth as useAuthContext } from '@/contexts/auth-context'

// Re-export the useAuth hook for convenience
export const useAuth = useAuthContext

// Additional auth-related hooks can be added here

export function useIsAuthenticated() {
  const { user, isLoading } = useAuthContext()
  return { isAuthenticated: !!user, isLoading }
}

export function useUserRole() {
  const { user } = useAuthContext()
  return user?.role || null
}

export function useIsRole(role: 'CANDIDATE' | 'EMPLOYER' | 'ADMIN') {
  const { user } = useAuthContext()
  return user?.role === role
}

export function useCanAccess(allowedRoles: ('CANDIDATE' | 'EMPLOYER' | 'ADMIN')[]) {
  const { user } = useAuthContext()
  return user ? allowedRoles.includes(user.role) : false
}