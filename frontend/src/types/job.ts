export interface Job {
  id: string
  title: string
  description: string
  salary: {
    min: number
    max: number
    currency: string
  }
  location: string
  jobType: 'FULL_TIME' | 'PART_TIME' | 'CONTRACT' | 'REMOTE'
  isActive: boolean
  createdAt: string
  updatedAt: string
  expiresAt: string
  employer: {
    id: string
    companyName: string
    logoUrl?: string
    website?: string
  }
}

export interface JobSearchFilters {
  search?: string
  location?: string
  jobType?: string
  minSalary?: number
  maxSalary?: number
  page?: number
  size?: number
  sortBy?: 'createdAt' | 'salary' | 'title'
  sortOrder?: 'asc' | 'desc'
}

export interface JobSearchResponse {
  content: Job[]
  totalElements: number
  totalPages: number
  currentPage: number
  size: number
  hasNext: boolean
  hasPrevious: boolean
}

export interface JobApplication {
  id: string
  jobId: string
  candidateId: string
  status: 'APPLIED' | 'IN_REVIEW' | 'HIRED' | 'REJECTED'
  appliedAt: string
  updatedAt: string
  notes?: string
  candidate?: {
    id: string
    name: string
    email: string
    degree: string
    graduationYear: number
    resumeUrl?: string
    linkedinProfile?: string
    portfolioUrl?: string
  }
}

export interface CreateJobRequest {
  title: string
  description: string
  salary: {
    min: number
    max: number
    currency: string
  }
  location: string
  jobType: 'FULL_TIME' | 'PART_TIME' | 'CONTRACT' | 'REMOTE'
  expiresAt?: string
}

export interface JobAnalytics {
  jobId: string
  totalApplications: number
  applicationsByStatus: {
    APPLIED: number
    IN_REVIEW: number
    HIRED: number
    REJECTED: number
  }
  viewCount: number
  createdAt: string
}