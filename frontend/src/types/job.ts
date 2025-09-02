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
}