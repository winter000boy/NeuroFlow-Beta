import React, { ReactElement } from 'react'
import { render, RenderOptions } from '@testing-library/react'
import { AuthProvider } from '@/contexts/auth-context'
import { ThemeProvider } from '@/components/theme-provider'

// Mock Next.js router
const mockRouter = {
  push: jest.fn(),
  replace: jest.fn(),
  prefetch: jest.fn(),
  back: jest.fn(),
  forward: jest.fn(),
  refresh: jest.fn(),
}

jest.mock('next/navigation', () => ({
  useRouter: () => mockRouter,
  useSearchParams: () => new URLSearchParams(),
  usePathname: () => '/',
}))

// Custom render function that includes providers
const AllTheProviders = ({ children }: { children: React.ReactNode }) => {
  return (
    <ThemeProvider attribute="class" defaultTheme="light">
      <AuthProvider>
        {children}
      </AuthProvider>
    </ThemeProvider>
  )
}

const customRender = (
  ui: ReactElement,
  options?: Omit<RenderOptions, 'wrapper'>
) => render(ui, { wrapper: AllTheProviders, ...options })

export * from '@testing-library/react'
export { customRender as render }

// Mock user data
export const mockCandidate = {
  id: '1',
  email: 'candidate@example.com',
  role: 'CANDIDATE' as const,
  name: 'John Doe',
  phone: '+1234567890',
  degree: 'Computer Science',
  graduationYear: 2022,
  resumeUrl: 'https://example.com/resume.pdf',
  linkedinProfile: 'https://linkedin.com/in/johndoe',
  portfolioUrl: 'https://johndoe.dev',
}

export const mockEmployer = {
  id: '2',
  email: 'employer@example.com',
  role: 'EMPLOYER' as const,
  companyName: 'Tech Corp',
  website: 'https://techcorp.com',
  description: 'A great tech company',
  logoUrl: 'https://example.com/logo.png',
  address: '123 Tech Street, San Francisco, CA',
  isApproved: true,
}

export const mockAdmin = {
  id: '3',
  email: 'admin@example.com',
  role: 'ADMIN' as const,
  name: 'Admin User',
}

export const mockJob = {
  id: '1',
  title: 'Software Engineer',
  description: 'A great job opportunity for a software engineer',
  salary: {
    min: 80000,
    max: 120000,
    currency: 'USD'
  },
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
}

export const mockApplication = {
  id: 'app1',
  jobId: '1',
  candidateId: '1',
  employerId: 'emp1',
  status: 'APPLIED' as const,
  appliedAt: '2024-01-01T00:00:00Z',
  updatedAt: '2024-01-01T00:00:00Z',
}

// Helper functions for testing
export const createMockJobsResponse = (jobs: any[], page = 0, totalPages = 1) => ({
  content: jobs,
  totalPages,
  totalElements: jobs.length,
  number: page,
  size: 10,
})

export const waitForLoadingToFinish = () => {
  return new Promise(resolve => setTimeout(resolve, 0))
}

// Mock localStorage
export const mockLocalStorage = {
  getItem: jest.fn(),
  setItem: jest.fn(),
  removeItem: jest.fn(),
  clear: jest.fn(),
}

// Reset all mocks
export const resetAllMocks = () => {
  jest.clearAllMocks()
  mockLocalStorage.getItem.mockReturnValue(null)
  mockRouter.push.mockClear()
  mockRouter.replace.mockClear()
}