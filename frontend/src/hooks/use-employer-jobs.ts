'use client'

import { useState, useEffect, useCallback } from 'react'
import { JobService } from '@/services/job.service'
import { Job, JobApplication, CreateJobRequest, JobAnalytics } from '@/types/job'

export function useEmployerJobs() {
  const [jobs, setJobs] = useState<Job[]>([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  const fetchJobs = useCallback(async () => {
    setLoading(true)
    setError(null)
    
    try {
      const data = await JobService.getMyJobs()
      setJobs(data)
    } catch (err) {
      const errorMessage = err instanceof Error && 'response' in err && err.response && 
        typeof err.response === 'object' && 'data' in err.response && 
        err.response.data && typeof err.response.data === 'object' && 'message' in err.response.data
        ? String(err.response.data.message)
        : 'Failed to fetch jobs'
      setError(errorMessage)
    } finally {
      setLoading(false)
    }
  }, [])

  const createJob = useCallback(async (jobData: CreateJobRequest) => {
    setError(null)
    
    try {
      const newJob = await JobService.createJob(jobData)
      setJobs(prev => [newJob, ...prev])
      return newJob
    } catch (err) {
      const errorMessage = err instanceof Error && 'response' in err && err.response && 
        typeof err.response === 'object' && 'data' in err.response && 
        err.response.data && typeof err.response.data === 'object' && 'message' in err.response.data
        ? String(err.response.data.message)
        : 'Failed to create job'
      setError(errorMessage)
      throw new Error(errorMessage)
    }
  }, [])

  const updateJob = useCallback(async (jobId: string, jobData: Partial<Job>) => {
    setError(null)
    
    try {
      const updatedJob = await JobService.updateJob(jobId, jobData)
      setJobs(prev => prev.map(job => job.id === jobId ? updatedJob : job))
      return updatedJob
    } catch (err) {
      const errorMessage = err instanceof Error && 'response' in err && err.response && 
        typeof err.response === 'object' && 'data' in err.response && 
        err.response.data && typeof err.response.data === 'object' && 'message' in err.response.data
        ? String(err.response.data.message)
        : 'Failed to update job'
      setError(errorMessage)
      throw new Error(errorMessage)
    }
  }, [])

  const toggleJobStatus = useCallback(async (jobId: string, isActive: boolean) => {
    setError(null)
    
    try {
      const updatedJob = await JobService.toggleJobStatus(jobId, isActive)
      setJobs(prev => prev.map(job => job.id === jobId ? updatedJob : job))
      return updatedJob
    } catch (err) {
      const errorMessage = err instanceof Error && 'response' in err && err.response && 
        typeof err.response === 'object' && 'data' in err.response && 
        err.response.data && typeof err.response.data === 'object' && 'message' in err.response.data
        ? String(err.response.data.message)
        : 'Failed to toggle job status'
      setError(errorMessage)
      throw new Error(errorMessage)
    }
  }, [])

  const deleteJob = useCallback(async (jobId: string) => {
    setError(null)
    
    try {
      await JobService.deleteJob(jobId)
      setJobs(prev => prev.filter(job => job.id !== jobId))
    } catch (err) {
      const errorMessage = err instanceof Error && 'response' in err && err.response && 
        typeof err.response === 'object' && 'data' in err.response && 
        err.response.data && typeof err.response.data === 'object' && 'message' in err.response.data
        ? String(err.response.data.message)
        : 'Failed to delete job'
      setError(errorMessage)
      throw new Error(errorMessage)
    }
  }, [])

  useEffect(() => {
    fetchJobs()
  }, [fetchJobs])

  return {
    jobs,
    loading,
    error,
    fetchJobs,
    createJob,
    updateJob,
    toggleJobStatus,
    deleteJob
  }
}

export function useJobApplications(jobId: string) {
  const [applications, setApplications] = useState<JobApplication[]>([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  const fetchApplications = useCallback(async () => {
    if (!jobId) return
    
    setLoading(true)
    setError(null)
    
    try {
      const data = await JobService.getJobApplications(jobId)
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
  }, [jobId])

  const updateApplicationStatus = useCallback(async (
    applicationId: string, 
    status: JobApplication['status'],
    notes?: string
  ) => {
    setError(null)
    
    try {
      const updatedApplication = await JobService.updateApplicationStatus(applicationId, status, notes)
      setApplications(prev => prev.map(app => 
        app.id === applicationId ? updatedApplication : app
      ))
      return updatedApplication
    } catch (err) {
      const errorMessage = err instanceof Error && 'response' in err && err.response && 
        typeof err.response === 'object' && 'data' in err.response && 
        err.response.data && typeof err.response.data === 'object' && 'message' in err.response.data
        ? String(err.response.data.message)
        : 'Failed to update application status'
      setError(errorMessage)
      throw new Error(errorMessage)
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
    updateApplicationStatus
  }
}

export function useJobAnalytics(jobId: string) {
  const [analytics, setAnalytics] = useState<JobAnalytics | null>(null)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  const fetchAnalytics = useCallback(async () => {
    if (!jobId) return
    
    setLoading(true)
    setError(null)
    
    try {
      const data = await JobService.getJobAnalytics(jobId)
      setAnalytics(data)
    } catch (err) {
      const errorMessage = err instanceof Error && 'response' in err && err.response && 
        typeof err.response === 'object' && 'data' in err.response && 
        err.response.data && typeof err.response.data === 'object' && 'message' in err.response.data
        ? String(err.response.data.message)
        : 'Failed to fetch analytics'
      setError(errorMessage)
    } finally {
      setLoading(false)
    }
  }, [jobId])

  useEffect(() => {
    fetchAnalytics()
  }, [fetchAnalytics])

  return {
    analytics,
    loading,
    error,
    refetch: fetchAnalytics
  }
}

export function useEmployerAnalytics() {
  const [analytics, setAnalytics] = useState<{
    totalJobs: number
    activeJobs: number
    totalApplications: number
    recentApplications: JobApplication[]
  } | null>(null)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  const fetchAnalytics = useCallback(async () => {
    setLoading(true)
    setError(null)
    
    try {
      const data = await JobService.getEmployerAnalytics()
      setAnalytics(data)
    } catch (err) {
      const errorMessage = err instanceof Error && 'response' in err && err.response && 
        typeof err.response === 'object' && 'data' in err.response && 
        err.response.data && typeof err.response.data === 'object' && 'message' in err.response.data
        ? String(err.response.data.message)
        : 'Failed to fetch analytics'
      setError(errorMessage)
    } finally {
      setLoading(false)
    }
  }, [])

  useEffect(() => {
    fetchAnalytics()
  }, [fetchAnalytics])

  return {
    analytics,
    loading,
    error,
    refetch: fetchAnalytics
  }
}