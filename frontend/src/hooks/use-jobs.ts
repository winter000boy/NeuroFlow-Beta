'use client'

import { useState, useEffect, useCallback } from 'react'
import { JobService } from '@/services/job.service'
import { Job, JobSearchFilters, JobSearchResponse, JobApplication } from '@/types/job'

export function useJobSearch(initialFilters: JobSearchFilters = {}) {
  const [jobs, setJobs] = useState<Job[]>([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const [totalPages, setTotalPages] = useState(0)
  const [totalElements, setTotalElements] = useState(0)
  const [currentPage, setCurrentPage] = useState(0)
  const [filters, setFilters] = useState<JobSearchFilters>(initialFilters)

  const searchJobs = useCallback(async (searchFilters: JobSearchFilters = {}) => {
    setLoading(true)
    setError(null)
    
    try {
      const mergedFilters = { ...filters, ...searchFilters }
      const response: JobSearchResponse = await JobService.searchJobs(mergedFilters)
      
      setJobs(response.content)
      setTotalPages(response.totalPages)
      setTotalElements(response.totalElements)
      setCurrentPage(response.currentPage)
      setFilters(mergedFilters)
    } catch (err) {
      const errorMessage = err instanceof Error && 'response' in err && err.response && 
        typeof err.response === 'object' && 'data' in err.response && 
        err.response.data && typeof err.response.data === 'object' && 'message' in err.response.data
        ? String(err.response.data.message)
        : 'Failed to search jobs'
      setError(errorMessage)
      setJobs([])
    } finally {
      setLoading(false)
    }
  }, [filters])

  const updateFilters = useCallback((newFilters: Partial<JobSearchFilters>) => {
    const updatedFilters = { ...filters, ...newFilters, page: 0 }
    setFilters(updatedFilters)
    searchJobs(updatedFilters)
  }, [filters, searchJobs])

  const goToPage = useCallback((page: number) => {
    const updatedFilters = { ...filters, page }
    setFilters(updatedFilters)
    searchJobs(updatedFilters)
  }, [filters, searchJobs])

  const resetFilters = useCallback(() => {
    const resetFilters = { page: 0, size: 10 }
    setFilters(resetFilters)
    searchJobs(resetFilters)
  }, [searchJobs])

  useEffect(() => {
    searchJobs(filters)
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []) // Only run on mount

  return {
    jobs,
    loading,
    error,
    totalPages,
    totalElements,
    currentPage,
    filters,
    searchJobs,
    updateFilters,
    goToPage,
    resetFilters
  }
}

export function useJobApplications() {
  const [applications, setApplications] = useState<JobApplication[]>([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  const fetchApplications = useCallback(async () => {
    setLoading(true)
    setError(null)
    
    try {
      const data = await JobService.getMyApplications()
      setApplications(data)
    } catch (err) {
      const errorMessage = err instanceof Error && 'response' in err && err.response && 
        typeof err.response === 'object' && 'data' in err.response && 
        err.response.data && typeof err.response.data === 'object' && 'message' in err.response.data
        ? String(err.response.data.message)
        : 'Failed to fetch applications'
      setError(errorMessage)
    } finally {
      setLoading(false)
    }
  }, [])

  const applyForJob = useCallback(async (jobId: string) => {
    setError(null)
    
    try {
      const application = await JobService.applyForJob(jobId)
      setApplications(prev => [...prev, application])
      return application
    } catch (err) {
      const errorMessage = err instanceof Error && 'response' in err && err.response && 
        typeof err.response === 'object' && 'data' in err.response && 
        err.response.data && typeof err.response.data === 'object' && 'message' in err.response.data
        ? String(err.response.data.message)
        : 'Failed to apply for job'
      setError(errorMessage)
      throw new Error(errorMessage)
    }
  }, [])

  const getApplicationStatus = useCallback(async (jobId: string) => {
    try {
      return await JobService.getApplicationStatus(jobId)
    } catch (err) {
      console.error('Failed to get application status:', err)
      return null
    }
  }, [])

  useEffect(() => {
    fetchApplications()
  }, [fetchApplications])

  return {
    applications,
    loading,
    error,
    fetchApplications,
    applyForJob,
    getApplicationStatus
  }
}

export function useJob(jobId: string) {
  const [job, setJob] = useState<Job | null>(null)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  const fetchJob = useCallback(async () => {
    if (!jobId) return
    
    setLoading(true)
    setError(null)
    
    try {
      const data = await JobService.getJobById(jobId)
      setJob(data)
    } catch (err) {
      const errorMessage = err instanceof Error && 'response' in err && err.response && 
        typeof err.response === 'object' && 'data' in err.response && 
        err.response.data && typeof err.response.data === 'object' && 'message' in err.response.data
        ? String(err.response.data.message)
        : 'Failed to fetch job'
      setError(errorMessage)
    } finally {
      setLoading(false)
    }
  }, [jobId])

  useEffect(() => {
    fetchJob()
  }, [fetchJob])

  return {
    job,
    loading,
    error,
    refetch: fetchJob
  }
}