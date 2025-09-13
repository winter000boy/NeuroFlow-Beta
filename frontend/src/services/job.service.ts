import { apiClient } from './api'
import { Job, JobSearchFilters, JobSearchResponse, JobApplication, CreateJobRequest, JobAnalytics } from '@/types/job'
import { withCache, cacheKeys, cacheTTL, cacheInvalidation } from '@/lib/api-cache'

export class JobService {
  private static readonly BASE_URL = '/jobs'

  static searchJobs = withCache(
    async (filters: JobSearchFilters = {}): Promise<JobSearchResponse> => {
      const params = new URLSearchParams()
      
      if (filters.search) params.append('search', filters.search)
      if (filters.location) params.append('location', filters.location)
      if (filters.jobType) params.append('jobType', filters.jobType)
      if (filters.minSalary) params.append('minSalary', filters.minSalary.toString())
      if (filters.maxSalary) params.append('maxSalary', filters.maxSalary.toString())
      if (filters.page !== undefined) params.append('page', filters.page.toString())
      if (filters.size !== undefined) params.append('size', filters.size.toString())
      if (filters.sortBy) params.append('sortBy', filters.sortBy)
      if (filters.sortOrder) params.append('sortOrder', filters.sortOrder)

      const response = await apiClient.get(`${this.BASE_URL}?${params.toString()}`)
      return response.data
    },
    (filters) => cacheKeys.jobs.search(filters || {}),
    cacheTTL.jobs.search
  )

  static getJobById = withCache(
    async (jobId: string): Promise<Job> => {
      const response = await apiClient.get(`${this.BASE_URL}/${jobId}`)
      return response.data
    },
    (jobId) => cacheKeys.jobs.detail(jobId),
    cacheTTL.jobs.detail
  )

  static async applyForJob(jobId: string): Promise<JobApplication> {
    const response = await apiClient.post(`${this.BASE_URL}/${jobId}/apply`)
    
    // Invalidate application-related caches
    cacheInvalidation.invalidateApplications()
    
    return response.data
  }

  static async getMyApplications(): Promise<JobApplication[]> {
    const response = await apiClient.get('/applications/my')
    return response.data
  }

  static async getApplicationStatus(jobId: string): Promise<JobApplication | null> {
    try {
      const response = await apiClient.get(`/applications/job/${jobId}/status`)
      return response.data
    } catch (error) {
      if (error instanceof Error && 'response' in error && error.response && 
          typeof error.response === 'object' && 'status' in error.response && 
          error.response.status === 404) {
        return null // No application found
      }
      throw error
    }
  }

  // Employer-specific methods
  static async createJob(jobData: CreateJobRequest): Promise<Job> {
    const response = await apiClient.post(this.BASE_URL, jobData)
    
    // Invalidate job-related caches
    cacheInvalidation.invalidateJobs()
    
    return response.data
  }

  static async updateJob(jobId: string, jobData: Partial<Job>): Promise<Job> {
    const response = await apiClient.put(`${this.BASE_URL}/${jobId}`, jobData)
    
    // Invalidate specific job and related caches
    cacheInvalidation.invalidateJob(jobId)
    
    return response.data
  }

  static async deleteJob(jobId: string): Promise<void> {
    await apiClient.delete(`${this.BASE_URL}/${jobId}`)
  }

  static async getMyJobs(): Promise<Job[]> {
    const response = await apiClient.get(`${this.BASE_URL}/my`)
    return response.data
  }

  static async getJobApplications(jobId: string): Promise<JobApplication[]> {
    const response = await apiClient.get(`${this.BASE_URL}/${jobId}/applications`)
    return response.data
  }

  static async updateApplicationStatus(
    applicationId: string, 
    status: JobApplication['status'],
    notes?: string
  ): Promise<JobApplication> {
    const response = await apiClient.put(`/applications/${applicationId}/status`, {
      status,
      notes
    })
    return response.data
  }

  static async toggleJobStatus(jobId: string, isActive: boolean): Promise<Job> {
    const response = await apiClient.patch(`${this.BASE_URL}/${jobId}/status`, {
      isActive
    })
    return response.data
  }

  static async getJobAnalytics(jobId: string): Promise<JobAnalytics> {
    const response = await apiClient.get(`${this.BASE_URL}/${jobId}/analytics`)
    return response.data
  }

  static async getEmployerAnalytics(): Promise<{
    totalJobs: number
    activeJobs: number
    totalApplications: number
    recentApplications: JobApplication[]
  }> {
    const response = await apiClient.get('/employer/analytics')
    return response.data
  }
}