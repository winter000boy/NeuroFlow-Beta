import { renderHook, act, waitFor } from '@testing-library/react'
import { useJobs } from '../use-jobs'
import { JobService } from '@/services/job.service'
import { toast } from 'sonner'

// Mock dependencies
jest.mock('@/services/job.service')
jest.mock('sonner')

const mockJobs = [
  {
    id: '1',
    title: 'Software Engineer',
    description: 'A great job opportunity',
    salary: { min: 80000, max: 120000, currency: 'USD' },
    location: 'San Francisco, CA',
    jobType: 'FULL_TIME' as const,
    isActive: true,
    createdAt: '2024-01-01T00:00:00Z',
    updatedAt: '2024-01-01T00:00:00Z',
    expiresAt: '2024-12-31T00:00:00Z',
    employer: {
      id: 'emp1',
      companyName: 'Tech Corp',
      logoUrl: 'https://example.com/logo.png',
      website: 'https://techcorp.com'
    }
  },
  {
    id: '2',
    title: 'Frontend Developer',
    description: 'React developer needed',
    salary: { min: 70000, max: 100000, currency: 'USD' },
    location: 'New York, NY',
    jobType: 'REMOTE' as const,
    isActive: true,
    createdAt: '2024-01-02T00:00:00Z',
    updatedAt: '2024-01-02T00:00:00Z',
    expiresAt: '2024-12-31T00:00:00Z',
    employer: {
      id: 'emp2',
      companyName: 'Frontend Inc',
      logoUrl: 'https://example.com/logo2.png',
      website: 'https://frontend.com'
    }
  }
]

const mockJobsResponse = {
  content: mockJobs,
  totalPages: 1,
  totalElements: 2,
  number: 0,
  size: 10,
}

describe('useJobs', () => {
  beforeEach(() => {
    jest.clearAllMocks()
  })

  test('initializes with empty state', () => {
    const { result } = renderHook(() => useJobs())

    expect(result.current.jobs).toEqual([])
    expect(result.current.isLoading).toBe(false)
    expect(result.current.error).toBeNull()
    expect(result.current.totalPages).toBe(0)
    expect(result.current.currentPage).toBe(0)
    expect(result.current.hasMore).toBe(false)
  })

  test('loads jobs successfully', async () => {
    ;(JobService.getJobs as jest.Mock).mockResolvedValue(mockJobsResponse)

    const { result } = renderHook(() => useJobs())

    await act(async () => {
      await result.current.searchJobs({})
    })

    expect(JobService.getJobs).toHaveBeenCalledWith({
      page: 0,
      size: 10,
    })
    expect(result.current.jobs).toEqual(mockJobs)
    expect(result.current.totalPages).toBe(1)
    expect(result.current.isLoading).toBe(false)
    expect(result.current.error).toBeNull()
  })

  test('handles search with filters', async () => {
    ;(JobService.getJobs as jest.Mock).mockResolvedValue(mockJobsResponse)

    const { result } = renderHook(() => useJobs())

    const searchParams = {
      search: 'react',
      location: 'New York',
      jobType: 'REMOTE',
      salaryMin: '70000',
      salaryMax: '100000',
    }

    await act(async () => {
      await result.current.searchJobs(searchParams)
    })

    expect(JobService.getJobs).toHaveBeenCalledWith({
      page: 0,
      size: 10,
      search: 'react',
      location: 'New York',
      jobType: 'REMOTE',
      salaryMin: 70000,
      salaryMax: 100000,
    })
  })

  test('handles loading state', async () => {
    let resolvePromise: (value: any) => void
    const promise = new Promise(resolve => {
      resolvePromise = resolve
    })
    ;(JobService.getJobs as jest.Mock).mockReturnValue(promise)

    const { result } = renderHook(() => useJobs())

    act(() => {
      result.current.searchJobs({})
    })

    expect(result.current.isLoading).toBe(true)

    await act(async () => {
      resolvePromise!(mockJobsResponse)
      await promise
    })

    expect(result.current.isLoading).toBe(false)
  })

  test('handles error state', async () => {
    const errorMessage = 'Failed to load jobs'
    ;(JobService.getJobs as jest.Mock).mockRejectedValue(new Error(errorMessage))

    const { result } = renderHook(() => useJobs())

    await act(async () => {
      await result.current.searchJobs({})
    })

    expect(result.current.error).toBe(errorMessage)
    expect(result.current.isLoading).toBe(false)
    expect(toast.error).toHaveBeenCalledWith('Failed to load jobs')
  })

  test('loads more jobs (pagination)', async () => {
    const firstPageResponse = {
      ...mockJobsResponse,
      totalPages: 2,
      number: 0,
    }
    const secondPageResponse = {
      content: [mockJobs[1]],
      totalPages: 2,
      totalElements: 2,
      number: 1,
      size: 10,
    }

    ;(JobService.getJobs as jest.Mock)
      .mockResolvedValueOnce(firstPageResponse)
      .mockResolvedValueOnce(secondPageResponse)

    const { result } = renderHook(() => useJobs())

    // Load first page
    await act(async () => {
      await result.current.searchJobs({})
    })

    expect(result.current.jobs).toEqual(mockJobs)
    expect(result.current.hasMore).toBe(true)

    // Load more
    await act(async () => {
      await result.current.loadMore()
    })

    expect(JobService.getJobs).toHaveBeenCalledWith({
      page: 1,
      size: 10,
    })
    expect(result.current.jobs).toEqual([...mockJobs, mockJobs[1]])
    expect(result.current.currentPage).toBe(1)
  })

  test('applies for job successfully', async () => {
    const mockApplication = {
      id: 'app1',
      jobId: '1',
      candidateId: 'cand1',
      status: 'APPLIED' as const,
      appliedAt: '2024-01-01T00:00:00Z',
      updatedAt: '2024-01-01T00:00:00Z',
    }

    ;(JobService.applyForJob as jest.Mock).mockResolvedValue(mockApplication)

    const { result } = renderHook(() => useJobs())

    await act(async () => {
      await result.current.applyForJob(mockJobs[0])
    })

    expect(JobService.applyForJob).toHaveBeenCalledWith('1')
    expect(toast.success).toHaveBeenCalledWith('Application submitted successfully!')
  })

  test('handles job application error', async () => {
    ;(JobService.applyForJob as jest.Mock).mockRejectedValue(new Error('Already applied'))

    const { result } = renderHook(() => useJobs())

    await act(async () => {
      await result.current.applyForJob(mockJobs[0])
    })

    expect(toast.error).toHaveBeenCalledWith('Already applied')
  })

  test('gets job by id successfully', async () => {
    ;(JobService.getJobById as jest.Mock).mockResolvedValue(mockJobs[0])

    const { result } = renderHook(() => useJobs())

    let job: any
    await act(async () => {
      job = await result.current.getJobById('1')
    })

    expect(JobService.getJobById).toHaveBeenCalledWith('1')
    expect(job).toEqual(mockJobs[0])
  })

  test('handles get job by id error', async () => {
    ;(JobService.getJobById as jest.Mock).mockRejectedValue(new Error('Job not found'))

    const { result } = renderHook(() => useJobs())

    await act(async () => {
      try {
        await result.current.getJobById('invalid-id')
      } catch (error) {
        expect(error).toEqual(new Error('Job not found'))
      }
    })

    expect(toast.error).toHaveBeenCalledWith('Job not found')
  })

  test('clears jobs when new search is performed', async () => {
    ;(JobService.getJobs as jest.Mock).mockResolvedValue(mockJobsResponse)

    const { result } = renderHook(() => useJobs())

    // Load initial jobs
    await act(async () => {
      await result.current.searchJobs({})
    })

    expect(result.current.jobs).toEqual(mockJobs)

    // Perform new search
    const newJobsResponse = {
      content: [mockJobs[0]],
      totalPages: 1,
      totalElements: 1,
      number: 0,
      size: 10,
    }
    ;(JobService.getJobs as jest.Mock).mockResolvedValue(newJobsResponse)

    await act(async () => {
      await result.current.searchJobs({ search: 'software' })
    })

    expect(result.current.jobs).toEqual([mockJobs[0]])
  })
})