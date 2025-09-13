'use client'

import { useState } from 'react'
import { toast } from 'sonner'
import { ProtectedRoute } from '@/components/auth/protected-route'
import { JobSearchForm } from '@/components/jobs/job-search-form'
import { JobListing } from '@/components/jobs/job-listing'
import { JobListingStructuredData } from '@/components/seo/job-listing-structured-data'
import { Breadcrumb } from '@/components/seo/breadcrumb'
import { useJobSearch, useJobApplications } from '@/hooks/use-jobs'
import { useAuth } from '@/hooks/use-auth'
import { JobSearchFilters } from '@/types/job'

export function JobsPageClient() {
  const { user } = useAuth()
  const { applyForJob } = useJobApplications()
  const {
    jobs,
    loading,
    error,
    totalPages,
    totalElements,
    currentPage,
    filters,
    updateFilters,
    goToPage
  } = useJobSearch({ page: 0, size: 10 })

  const [searchKeywords, setSearchKeywords] = useState<string[]>([])

  const handleSearch = (newFilters: JobSearchFilters) => {
    // Extract keywords for highlighting
    const keywords = newFilters.search 
      ? newFilters.search.split(' ').filter(word => word.length > 2)
      : []
    setSearchKeywords(keywords)
    
    updateFilters(newFilters)
  }

  const handleApply = async (jobId: string) => {
    try {
      await applyForJob(jobId)
      toast.success('Application submitted successfully!')
    } catch (error) {
      const errorMessage = error instanceof Error ? error.message : 'Failed to apply for job'
      toast.error(errorMessage)
    }
  }

  return (
    <ProtectedRoute allowedRoles={['CANDIDATE', 'EMPLOYER', 'ADMIN']}>
      <JobListingStructuredData 
        jobs={jobs}
        totalJobs={totalElements}
        searchQuery={filters.search}
        location={filters.location}
      />
      <div className="container mx-auto px-4 py-8">
        <div className="space-y-6">
          <Breadcrumb items={[{ label: 'Jobs' }]} />
          
          <div>
            <h1 className="text-3xl font-bold mb-2">Find Your Next Opportunity</h1>
            <p className="text-muted-foreground">
              Discover amazing job opportunities from top companies
              {totalElements > 0 && ` (${totalElements.toLocaleString()} jobs available)`}
            </p>
          </div>

          <JobSearchForm
            onSearch={handleSearch}
            initialFilters={filters}
            loading={loading}
          />

          <JobListing
            jobs={jobs}
            loading={loading}
            error={error}
            totalPages={totalPages}
            currentPage={currentPage}
            totalElements={totalElements}
            filters={filters}
            onPageChange={goToPage}
            onApply={user?.role === 'CANDIDATE' ? handleApply : undefined}
            highlightKeywords={searchKeywords}
          />
        </div>
      </div>
    </ProtectedRoute>
  )
}