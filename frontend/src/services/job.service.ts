import { apiClient } from './api'
import { Job, JobSearchFilters, JobSearchResponse, JobApplication } from '@/types/job'

export class JobService {
  private static readonly BASE_URL = '/jobs'

  static async searchJobs(filters: JobSearchFilters = {}): Promise<JobSearchResponse> {
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
  }

  static async getJobById(jobId: string): Promise<Job> {
    const response = await apiClient.get(`${this.BASE_URL}/${jobId}`)
    return response.data
  }

  static async applyForJob(jobId: string): Promise<JobApplication> {
    const response = await apiClient.post(`${this.BASE_URL}/${jobId}/apply`)
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
  static async createJob(jobData: Partial<Job>): Promise<Job> {
    const response = await apiClient.post(this.BASE_URL, jobData)
    return response.data
  }

  static async updateJob(jobId: string, jobData: Partial<Job>): Promise<Job> {
    const response = await apiClient.put(`${this.BASE_URL}/${jobId}`, jobData)
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
}