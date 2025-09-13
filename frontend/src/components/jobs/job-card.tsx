'use client'

import { useState, useEffect, memo } from 'react'
import Link from 'next/link'
import { MapPin, Clock, DollarSign, Building2, ExternalLink } from 'lucide-react'
import { Card, CardContent, CardFooter, CardHeader } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Badge } from '@/components/ui/badge'
import { OptimizedImage } from '@/components/ui/optimized-image'
import { LazyJobCard } from '@/components/ui/lazy-load'
import { Job, JobApplication } from '@/types/job'
import { useAuth } from '@/hooks/use-auth'
import { useJobApplications } from '@/hooks/use-jobs'
import { useRenderPerformance } from '@/hooks/use-performance'
import { formatDistanceToNow } from 'date-fns'

interface JobCardProps {
  job: Job
  onApply?: (jobId: string) => void
  showApplyButton?: boolean
  highlightKeywords?: string[]
}

const JOB_TYPE_LABELS = {
  FULL_TIME: 'Full Time',
  PART_TIME: 'Part Time',
  CONTRACT: 'Contract',
  REMOTE: 'Remote',
}

const JOB_TYPE_VARIANTS = {
  FULL_TIME: 'default',
  PART_TIME: 'secondary',
  CONTRACT: 'outline',
  REMOTE: 'success',
} as const

const APPLICATION_STATUS_LABELS = {
  APPLIED: 'Applied',
  IN_REVIEW: 'In Review',
  HIRED: 'Hired',
  REJECTED: 'Rejected',
}

const APPLICATION_STATUS_VARIANTS = {
  APPLIED: 'secondary',
  IN_REVIEW: 'warning',
  HIRED: 'success',
  REJECTED: 'destructive',
} as const

const JobCardContent = memo(function JobCardContent({ 
  job, 
  onApply, 
  showApplyButton = true, 
  highlightKeywords = [] 
}: JobCardProps) {
  const { user } = useAuth()
  const { getApplicationStatus } = useJobApplications()
  const [applicationStatus, setApplicationStatus] = useState<JobApplication | null>(null)
  const [applying, setApplying] = useState(false)

  // Monitor render performance in development
  useRenderPerformance('JobCard')

  useEffect(() => {
    if (user?.role === 'CANDIDATE' && showApplyButton) {
      getApplicationStatus(job.id).then(setApplicationStatus)
    }
  }, [job.id, user?.role, showApplyButton, getApplicationStatus])

  const handleApply = async () => {
    if (!onApply) return
    
    setApplying(true)
    try {
      await onApply(job.id)
      // Refresh application status
      const status = await getApplicationStatus(job.id)
      setApplicationStatus(status)
    } catch (error) {
      console.error('Failed to apply:', error)
    } finally {
      setApplying(false)
    }
  }

  const highlightText = (text: string, keywords: string[]) => {
    if (!keywords.length) return text
    
    const regex = new RegExp(`(${keywords.join('|')})`, 'gi')
    const parts = text.split(regex)
    
    return parts.map((part, index) => {
      const isKeyword = keywords.some(keyword => 
        part.toLowerCase() === keyword.toLowerCase()
      )
      return isKeyword ? (
        <mark key={index} className="bg-yellow-200 dark:bg-yellow-800 px-1 rounded">
          {part}
        </mark>
      ) : part
    })
  }

  const formatSalary = (salary: Job['salary']) => {
    const formatter = new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: salary.currency || 'USD',
      minimumFractionDigits: 0,
      maximumFractionDigits: 0,
    })
    
    if (salary.min === salary.max) {
      return formatter.format(salary.min)
    }
    
    return `${formatter.format(salary.min)} - ${formatter.format(salary.max)}`
  }

  const canApply = user?.role === 'CANDIDATE' && showApplyButton && !applicationStatus
  const timeAgo = formatDistanceToNow(new Date(job.createdAt), { addSuffix: true })

  return (
    <Card className="hover:shadow-md transition-shadow duration-200">
      <CardHeader className="pb-3">
        <div className="flex items-start justify-between">
          <div className="flex items-start gap-3 flex-1 min-w-0">
            {/* Company Logo */}
            {job.employer.logoUrl && (
              <div className="flex-shrink-0">
                <OptimizedImage
                  src={job.employer.logoUrl}
                  alt={`${job.employer.companyName} logo`}
                  width={48}
                  height={48}
                  className="rounded-lg border border-gray-200 dark:border-gray-700"
                  loading="lazy"
                  quality={60}
                />
              </div>
            )}
            
            <div className="flex-1 min-w-0">
              <Link 
                href={`/jobs/${job.id}`}
                className="block hover:text-primary transition-colors"
              >
                <h3 className="text-lg font-semibold truncate">
                  {highlightText(job.title, highlightKeywords)}
                </h3>
              </Link>
              
              <div className="flex items-center gap-2 mt-1 text-muted-foreground">
                <Building2 className="h-4 w-4 flex-shrink-0" />
                <span className="truncate">{job.employer.companyName}</span>
                {job.employer.website && (
                  <a
                    href={job.employer.website}
                    target="_blank"
                    rel="noopener noreferrer"
                    className="hover:text-primary transition-colors"
                  >
                    <ExternalLink className="h-3 w-3" />
                  </a>
                )}
              </div>
            </div>
          </div>
          
          <div className="flex flex-col items-end gap-2 ml-4">
            <Badge variant={JOB_TYPE_VARIANTS[job.jobType]}>
              {JOB_TYPE_LABELS[job.jobType]}
            </Badge>
            {applicationStatus && (
              <Badge variant={APPLICATION_STATUS_VARIANTS[applicationStatus.status]}>
                {APPLICATION_STATUS_LABELS[applicationStatus.status]}
              </Badge>
            )}
          </div>
        </div>
      </CardHeader>

      <CardContent className="pb-3">
        <div className="space-y-3">
          <p className="text-sm text-muted-foreground line-clamp-2">
            {highlightText(job.description, highlightKeywords)}
          </p>
          
          <div className="flex flex-wrap items-center gap-4 text-sm text-muted-foreground">
            <div className="flex items-center gap-1">
              <MapPin className="h-4 w-4" />
              <span>{job.location}</span>
            </div>
            
            <div className="flex items-center gap-1">
              <DollarSign className="h-4 w-4" />
              <span>{formatSalary(job.salary)}</span>
            </div>
            
            <div className="flex items-center gap-1">
              <Clock className="h-4 w-4" />
              <span>{timeAgo}</span>
            </div>
          </div>
        </div>
      </CardContent>

      <CardFooter className="pt-3">
        <div className="flex items-center justify-between w-full">
          <Link href={`/jobs/${job.id}`}>
            <Button variant="outline" size="sm">
              View Details
            </Button>
          </Link>
          
          {canApply && (
            <Button 
              onClick={handleApply}
              disabled={applying}
              size="sm"
            >
              {applying ? 'Applying...' : 'Apply Now'}
            </Button>
          )}
        </div>
      </CardFooter>
    </Card>
  )
})

// Main JobCard component with lazy loading wrapper
export function JobCard(props: JobCardProps) {
  return (
    <LazyJobCard>
      <JobCardContent {...props} />
    </LazyJobCard>
  )
}