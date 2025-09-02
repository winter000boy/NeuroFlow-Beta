import { useState, useEffect } from 'react'
import { userService, type CandidateProfile, type EmployerProfile } from '@/services/user.service'
import { useAuth } from '@/contexts/auth-context'
import { CandidateProfileFormData, EmployerProfileFormData } from '@/lib/validations/profile'

export function useProfile() {
  const { user } = useAuth()
  const [profile, setProfile] = useState<CandidateProfile | EmployerProfile | null>(null)
  const [isLoading, setIsLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  const loadProfile = async () => {
    if (!user) return

    try {
      setIsLoading(true)
      setError(null)
      
      let profileData
      if (user.role === 'CANDIDATE') {
        profileData = await userService.getCandidateProfile()
      } else if (user.role === 'EMPLOYER') {
        profileData = await userService.getEmployerProfile()
      } else {
        setIsLoading(false)
        return
      }
      
      setProfile(profileData)
    } catch (err) {
      const error = err as { response?: { data?: { message?: string } } }
      setError(error.response?.data?.message || 'Failed to load profile')
    } finally {
      setIsLoading(false)
    }
  }

  const updateProfile = async (data: CandidateProfileFormData | EmployerProfileFormData) => {
    if (!user) throw new Error('User not authenticated')

    try {
      let updatedProfile
      if (user.role === 'CANDIDATE') {
        updatedProfile = await userService.updateCandidateProfile(data as CandidateProfileFormData)
      } else if (user.role === 'EMPLOYER') {
        updatedProfile = await userService.updateEmployerProfile(data as EmployerProfileFormData)
      } else {
        throw new Error('Invalid user role')
      }
      
      setProfile(updatedProfile)
      return updatedProfile
    } catch (err) {
      throw err
    }
  }

  const uploadFile = async (file: File, type: 'resume' | 'logo') => {
    try {
      let uploadResponse
      if (type === 'resume') {
        uploadResponse = await userService.uploadResume(file)
      } else {
        uploadResponse = await userService.uploadLogo(file)
      }
      
      // Update profile with new file URL
      if (profile) {
        const updatedProfile = {
          ...profile,
          [type === 'resume' ? 'resumeUrl' : 'logoUrl']: uploadResponse.url,
        }
        setProfile(updatedProfile)
      }
      
      return uploadResponse
    } catch (err) {
      throw err
    }
  }

  useEffect(() => {
    loadProfile()
  }, [user])

  return {
    profile,
    isLoading,
    error,
    loadProfile,
    updateProfile,
    uploadFile,
  }
}