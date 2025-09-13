import { http, HttpResponse } from 'msw'
import { mockCandidate, mockEmployer, mockJob, mockApplication } from '../utils/test-utils'

const baseURL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080'

export const handlers = [
  // Auth endpoints
  http.post(`${baseURL}/api/auth/login`, async ({ request }) => {
    const body = await request.json() as any
    
    if (body.email === 'candidate@example.com' && body.password === 'password123') {
      return HttpResponse.json({
        token: 'mock-candidate-token',
        user: mockCandidate,
      })
    }
    
    if (body.email === 'employer@example.com' && body.password === 'password123') {
      return HttpResponse.json({
        token: 'mock-employer-token',
        user: mockEmployer,
      })
    }
    
    return HttpResponse.json(
      { message: 'Invalid credentials' },
      { status: 401 }
    )
  }),

  http.post(`${baseURL}/api/auth/register/candidate`, async ({ request }) => {
    const body = await request.json() as any
    
    if (body.email === 'existing@example.com') {
      return HttpResponse.json(
        { message: 'Email already exists' },
        { status: 400 }
      )
    }
    
    return HttpResponse.json({
      message: 'Registration successful',
    })
  }),

  http.post(`${baseURL}/api/auth/register/employer`, async ({ request }) => {
    return HttpResponse.json({
      message: 'Registration successful',
    })
  }),

  http.get(`${baseURL}/api/auth/me`, () => {
    return HttpResponse.json(mockCandidate)
  }),

  // Job endpoints
  http.get(`${baseURL}/api/jobs`, ({ request }) => {
    const url = new URL(request.url)
    const search = url.searchParams.get('search')
    const jobType = url.searchParams.get('jobType')
    const page = parseInt(url.searchParams.get('page') || '0')
    const size = parseInt(url.searchParams.get('size') || '10')
    
    let jobs = [mockJob]
    
    // Filter by search
    if (search) {
      jobs = jobs.filter(job => 
        job.title.toLowerCase().includes(search.toLowerCase()) ||
        job.description.toLowerCase().includes(search.toLowerCase())
      )
    }
    
    // Filter by job type
    if (jobType) {
      jobs = jobs.filter(job => job.jobType === jobType)
    }
    
    return HttpResponse.json({
      content: jobs,
      totalPages: Math.ceil(jobs.length / size),
      totalElements: jobs.length,
      number: page,
      size,
    })
  }),

  http.get(`${baseURL}/api/jobs/:id`, ({ params }) => {
    if (params.id === '1') {
      return HttpResponse.json(mockJob)
    }
    
    return HttpResponse.json(
      { message: 'Job not found' },
      { status: 404 }
    )
  }),

  http.post(`${baseURL}/api/jobs/:id/apply`, ({ params }) => {
    if (params.id === '1') {
      return HttpResponse.json({
        ...mockApplication,
        jobId: params.id,
      })
    }
    
    return HttpResponse.json(
      { message: 'Job not found' },
      { status: 404 }
    )
  }),

  // User endpoints
  http.get(`${baseURL}/api/users/profile`, () => {
    return HttpResponse.json(mockCandidate)
  }),

  http.put(`${baseURL}/api/users/profile`, async ({ request }) => {
    const body = await request.json() as any
    return HttpResponse.json({
      ...mockCandidate,
      ...body,
    })
  }),

  http.post(`${baseURL}/api/users/upload/resume`, () => {
    return HttpResponse.json({
      url: 'https://example.com/new-resume.pdf',
      filename: 'resume.pdf',
    })
  }),

  http.post(`${baseURL}/api/users/change-password`, async ({ request }) => {
    const body = await request.json() as any
    
    if (body.currentPassword === 'oldpassword' && body.newPassword === 'newpassword123') {
      return HttpResponse.json({
        message: 'Password changed successfully',
      })
    }
    
    return HttpResponse.json(
      { message: 'Current password is incorrect' },
      { status: 400 }
    )
  }),

  // Application endpoints
  http.get(`${baseURL}/api/applications/my`, () => {
    return HttpResponse.json([mockApplication])
  }),

  // Admin endpoints
  http.get(`${baseURL}/api/admin/users`, () => {
    return HttpResponse.json({
      content: [mockCandidate, mockEmployer],
      totalPages: 1,
      totalElements: 2,
      number: 0,
      size: 10,
    })
  }),

  http.get(`${baseURL}/api/admin/analytics`, () => {
    return HttpResponse.json({
      totalJobs: 150,
      totalApplications: 1200,
      totalCandidates: 800,
      totalEmployers: 50,
      activeJobs: 120,
      pendingApplications: 300,
    })
  }),
]