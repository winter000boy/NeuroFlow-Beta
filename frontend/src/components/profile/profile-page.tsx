'use client'

import { useAuth } from '@/contexts/auth-context'
import { CandidateProfile } from './candidate-profile'
import { EmployerProfile } from './employer-profile'
import { Loader2 } from 'lucide-react'

export function ProfilePage() {
  const { user, isLoading } = useAuth()

  if (isLoading) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <Loader2 className="h-8 w-8 animate-spin" />
      </div>
    )
  }

  if (!user) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <div className="text-center">
          <h2 className="text-2xl font-bold text-gray-900">Access Denied</h2>
          <p className="text-gray-600 mt-2">Please log in to view your profile.</p>
        </div>
      </div>
    )
  }

  if (user.role === 'CANDIDATE') {
    return <CandidateProfile />
  }

  if (user.role === 'EMPLOYER') {
    return <EmployerProfile />
  }

  // Admin users don't have profiles in the same way
  return (
    <div className="flex items-center justify-center min-h-screen">
      <div className="text-center">
        <h2 className="text-2xl font-bold text-gray-900">Profile Not Available</h2>
        <p className="text-gray-600 mt-2">Admin users don&apos;t have profile pages.</p>
      </div>
    </div>
  )
}