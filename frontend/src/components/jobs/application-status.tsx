'use client'

import { useEffect, useState } from 'react'
import { JobApplication } from '@/types/job'
import { JobService } from '@/services/job.service'
import { Badge } from '@/components/ui/badge'
import { Clock, CheckCircle, XCircle, Eye } from 'lucide-react'

interface ApplicationStatusProps {
  jobId: string
  className?: string
}

const statusConfig = {
  APPLIED: {
    label: 'Applied',
    icon: Clock,
    color: 'bg-blue-100 text-blue-800 dark:bg-blue-900 dark:text-blue-300',
    description: 'Your application has been submitted'
  },
  IN_REVIEW: {
    label: 'In Review',
    icon: Eye,
    color: 'bg-yellow-100 text-yellow-800 dark:bg-yellow-900 dark:text-yellow-300',
    description: 'Your application is being reviewed'
  },
  HIRED: {
    label: 'Hired',
    icon: CheckCircle,
    color: 'bg-green-100 text-green-800 dark:bg-green-900 dark:text-green-300',
    description: 'Congratulations! You got the job'
  },
  REJECTED: {
    label: 'Not Selected',
    icon: XCircle,
    color: 'bg-red-100 text-red-800 dark:bg-red-900 dark:text-red-300',
    description: 'Your application was not selected'
  }
}

export function ApplicationStatus({ jobId, className }: ApplicationStatusProps) {
  const [application, setApplication] = useState<JobApplication | null>(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    const fetchApplicationStatus = async () => {
      try {
        const status = await JobService.getApplicationStatus(jobId)
        setApplication(status)
      } catch (error) {
        console.error('Error fetching application status:', error)
      } finally {
        setLoading(false)
      }
    }

    fetchApplicationStatus()
  }, [jobId])

  if (loading) {
    return (
      <div className={`animate-pulse ${className}`}>
        <div className="h-6 bg-gray-200 dark:bg-gray-700 rounded"></div>
      </div>
    )
  }

  if (!application) {
    return null
  }

  const config = statusConfig[application.status]
  const StatusIcon = config.icon

  return (
    <div className={`space-y-2 ${className}`}>
      <div className="flex items-center space-x-2">
        <Badge className={config.color}>
          <StatusIcon className="h-3 w-3 mr-1" />
          {config.label}
        </Badge>
        <span className="text-xs text-gray-500 dark:text-gray-400">
          Applied on {new Date(application.appliedAt).toLocaleDateString()}
        </span>
      </div>
      <p className="text-sm text-gray-600 dark:text-gray-400">
        {config.description}
      </p>
      {application.notes && (
        <div className="mt-2 p-2 bg-gray-50 dark:bg-gray-800 rounded text-sm">
          <p className="font-medium text-gray-700 dark:text-gray-300">Note from employer:</p>
          <p className="text-gray-600 dark:text-gray-400 mt-1">{application.notes}</p>
        </div>
      )}
    </div>
  )
}