'use client'

import { Job, JobSearchFilters } from '@/types/job'
import { JobCard } from './job-card'
import { 
  Pagination, 
  PaginationContent, 
  PaginationItem, 
  PaginationLink, 
  PaginationNext, 
  PaginationPrevious,
  PaginationEllipsis 
} from '@/components/ui/pagination'
import { Card, CardContent } from '@/components/ui/card'
import { Search, Briefcase } from 'lucide-react'

interface JobListingProps {
  jobs: Job[]
  loading: boolean
  error: string | null
  totalPages: number
  currentPage: number
  totalElements: number
  filters: JobSearchFilters
  onPageChange: (page: number) => void
  onApply?: (jobId: string) => void
  highlightKeywords?: string[]
}

export function JobListing({
  jobs,
  loading,
  error,
  totalPages,
  currentPage,
  totalElements,
  filters,
  onPageChange,
  onApply,
  highlightKeywords = []
}: JobListingProps) {
  const generatePageNumbers = () => {
    const pages = []
    const maxVisiblePages = 5
    
    if (totalPages <= maxVisiblePages) {
      for (let i = 0; i < totalPages; i++) {
        pages.push(i)
      }
    } else {
      const startPage = Math.max(0, currentPage - 2)
      const endPage = Math.min(totalPages - 1, startPage + maxVisiblePages - 1)
      
      if (startPage > 0) {
        pages.push(0)
        if (startPage > 1) {
          pages.push(-1) // Ellipsis
        }
      }
      
      for (let i = startPage; i <= endPage; i++) {
        pages.push(i)
      }
      
      if (endPage < totalPages - 1) {
        if (endPage < totalPages - 2) {
          pages.push(-1) // Ellipsis
        }
        pages.push(totalPages - 1)
      }
    }
    
    return pages
  }

  if (loading) {
    return (
      <div className="space-y-4">
        {[...Array(5)].map((_, index) => (
          <Card key={index} className="animate-pulse">
            <CardContent className="p-6">
              <div className="space-y-3">
                <div className="h-6 bg-muted rounded w-3/4"></div>
                <div className="h-4 bg-muted rounded w-1/2"></div>
                <div className="h-4 bg-muted rounded w-full"></div>
                <div className="h-4 bg-muted rounded w-2/3"></div>
              </div>
            </CardContent>
          </Card>
        ))}
      </div>
    )
  }

  if (error) {
    return (
      <Card className="text-center py-12">
        <CardContent>
          <div className="flex flex-col items-center gap-4">
            <Search className="h-12 w-12 text-muted-foreground" />
            <div>
              <h3 className="text-lg font-semibold text-destructive">Search Error</h3>
              <p className="text-muted-foreground mt-1">{error}</p>
            </div>
          </div>
        </CardContent>
      </Card>
    )
  }

  if (jobs.length === 0) {
    return (
      <Card className="text-center py-12">
        <CardContent>
          <div className="flex flex-col items-center gap-4">
            <Briefcase className="h-12 w-12 text-muted-foreground" />
            <div>
              <h3 className="text-lg font-semibold">No jobs found</h3>
              <p className="text-muted-foreground mt-1">
                Try adjusting your search criteria or filters to find more opportunities.
              </p>
            </div>
          </div>
        </CardContent>
      </Card>
    )
  }

  return (
    <div className="space-y-6">
      {/* Results summary */}
      <div className="flex items-center justify-between">
        <p className="text-sm text-muted-foreground">
          Showing {currentPage * (filters.size || 10) + 1} to{' '}
          {Math.min((currentPage + 1) * (filters.size || 10), totalElements)} of{' '}
          {totalElements} jobs
        </p>
      </div>

      {/* Job cards */}
      <div className="space-y-4">
        {jobs.map((job) => (
          <JobCard
            key={job.id}
            job={job}
            onApply={onApply}
            highlightKeywords={highlightKeywords}
          />
        ))}
      </div>

      {/* Pagination */}
      {totalPages > 1 && (
        <div className="flex justify-center">
          <Pagination>
            <PaginationContent>
              <PaginationItem>
                <PaginationPrevious
                  onClick={() => onPageChange(currentPage - 1)}
                  className={currentPage === 0 ? 'pointer-events-none opacity-50' : 'cursor-pointer'}
                />
              </PaginationItem>
              
              {generatePageNumbers().map((pageNum, index) => (
                <PaginationItem key={index}>
                  {pageNum === -1 ? (
                    <PaginationEllipsis />
                  ) : (
                    <PaginationLink
                      onClick={() => onPageChange(pageNum)}
                      isActive={pageNum === currentPage}
                      className="cursor-pointer"
                    >
                      {pageNum + 1}
                    </PaginationLink>
                  )}
                </PaginationItem>
              ))}
              
              <PaginationItem>
                <PaginationNext
                  onClick={() => onPageChange(currentPage + 1)}
                  className={currentPage >= totalPages - 1 ? 'pointer-events-none opacity-50' : 'cursor-pointer'}
                />
              </PaginationItem>
            </PaginationContent>
          </Pagination>
        </div>
      )}
    </div>
  )
}