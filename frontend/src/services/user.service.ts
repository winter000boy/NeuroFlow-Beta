import { apiClient } from './api'
import type { CandidateProfileFormData, EmployerProfileFormData, PasswordChangeFormData } from '@/lib/validations/profile'

export interface CandidateProfile extends CandidateProfileFormData {
  id: string
  resumeUrl?: string
  isActive: boolean
  createdAt: string
  updatedAt: string
}

export interface EmployerProfile extends EmployerProfileFormData {
  id: string
  logoUrl?: string
  isApproved: boolean
  isActive: boolean
  createdAt: string
  updatedAt: string
}

export interface FileUploadResponse {
  url: string
  filename: string
  size: number
}

class UserService {
  async getCandidateProfile(): Promise<CandidateProfile> {
    const response = await apiClient.get('/users/profile/candidate')
    return response.data
  }

  async getEmployerProfile(): Promise<EmployerProfile> {
    const response = await apiClient.get('/users/profile/employer')
    return response.data
  }

  async updateCandidateProfile(data: CandidateProfileFormData): Promise<CandidateProfile> {
    const response = await apiClient.put('/users/profile/candidate', data)
    return response.data
  }

  async updateEmployerProfile(data: EmployerProfileFormData): Promise<EmployerProfile> {
    const response = await apiClient.put('/users/profile/employer', data)
    return response.data
  }

  async uploadResume(file: File): Promise<FileUploadResponse> {
    const formData = new FormData()
    formData.append('file', file)

    const response = await apiClient.post('/users/upload/resume', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    })
    return response.data
  }

  async uploadLogo(file: File): Promise<FileUploadResponse> {
    const formData = new FormData()
    formData.append('file', file)

    const response = await apiClient.post('/users/upload/logo', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    })
    return response.data
  }

  async changePassword(data: PasswordChangeFormData): Promise<{ message: string }> {
    const response = await apiClient.put('/users/change-password', {
      currentPassword: data.currentPassword,
      newPassword: data.newPassword,
    })
    return response.data
  }

  async deleteAccount(): Promise<{ message: string }> {
    const response = await apiClient.delete('/users/profile')
    return response.data
  }
}

export const userService = new UserService()