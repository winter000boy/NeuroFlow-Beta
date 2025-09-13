'use client'

import { useState } from 'react'
import { useAuth } from '@/hooks/use-auth'
import { Job } from '@/types/job'
import { JobApplicationModal } from '@/components/jobs/job-application-modal'
import { JobShare } from '@/components/jobs/job-share'
import { Button } from '@/components/ui/button'
import { Badge } from '@/components/ui/badge'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { MapPin, Clock, DollarSign, Building2, Calendar } from 'lucide-react'

interface JobDetailClientProps {
  jobId: string
  initialJob: Job
}

export function JobDetailClient({ initialJob }: JobDetailClientProps) {
  const [job] = useState<Job>(initialJob)
  const [showApplicationModal, setShowApplicationModal] = useState(false)
  const { user } = useAuth()

  const formatJobType = (jobType: string) => {
    return jobType.replace('_', ' ').toLowerCase().replace(/\b\w/g, l => l.toUpperCase())
  }

  const formatSalary = (salary: any) => {
    return `${salary.min.toLocaleString()} - ${salary.max.toLocaleString()} ${salary.currency}`
  }

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    })
  }

  const handleApply = () => {
    if (!user) {
      // Redirect to login
      window.location.href = '/login'
      return
    }
    setShowApplicationModal(true)
  }

  return (
    <div className="max-w-4xl mx-auto">
        {/* Job Header */}
        <div className="mb-8">
          <div className="flex items-start justify-between mb-4">
            <div className="flex-1">
              <h1 className="text-3xl font-bold text-gray-900 dark:text-white mb-2">
                {job.title}
              </h1>
              <div className="flex items-center space-x-4 text-gray-600 dark:text-gray-300">
                <div className="flex items-center">
                  <Building2 className="h-4 w-4 mr-1" />
                  {job.employer.companyName}
                </div>
                <div className="flex items-center">
                  <MapPin className="h-4 w-4 mr-1" />
                  {job.location}
                </div>
                <div className="flex items-center">
                  <Calendar className="h-4 w-4 mr-1" />
                  Posted {formatDate(job.createdAt)}
                </div>
              </div>
            </div>
            <div className="flex items-center space-x-2">
              <JobShare job={job} />
              {user?.role === 'CANDIDATE' && (
                <Button onClick={handleApply} size="lg">
                  Apply Now
                </Button>
              )}
            </div>
          </div>

          {/* Job Meta Info */}
          <div className="flex flex-wrap gap-2 mb-6">
            <Badge variant="secondary">
              <Clock className="h-3 w-3 mr-1" />
              {formatJobType(job.jobType)}
            </Badge>
            <Badge variant="secondary">
              <DollarSign className="h-3 w-3 mr-1" />
              {formatSalary(job.salary)}
            </Badge>
          </div>
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
          {/* Main Content */}
          <div className="lg:col-span-2">
            <Card>
              <CardHeader>
                <CardTitle>Job Description</CardTitle>
              </CardHeader>
              <CardContent>
                <div 
                  className="prose dark:prose-invert max-w-none"
                  dangerouslySetInnerHTML={{ __html: job.description }}
                />
              </CardContent>
            </Card>
          </div>

          {/* Sidebar */}
          <div className="space-y-6">
            {/* Company Info */}
            <Card>
              <CardHeader>
                <CardTitle>About {job.employer.companyName}</CardTitle>
              </CardHeader>
              <CardContent>
                {job.employer.logoUrl && (
                  <div className="w-16 h-16 mb-4 bg-gray-100 dark:bg-gray-800 rounded flex items-center justify-center">
                    <span className="text-xs text-gray-500">Logo</span>
                  </div>
                )}
                <p className="text-gray-600 dark:text-gray-300 mb-4">
                  {job.employer.description}
                </p>
                {job.employer.website && (
                  <a
                    href={job.employer.website}
                    target="_blank"
                    rel="noopener noreferrer"
                    className="text-blue-600 hover:text-blue-800 dark:text-blue-400 dark:hover:text-blue-300"
                  >
                    Visit Company Website
                  </a>
                )}
              </CardContent>
            </Card>

            {/* Job Details */}
            <Card>
              <CardHeader>
                <CardTitle>Job Details</CardTitle>
              </CardHeader>
              <CardContent className="space-y-3">
                <div>
                  <span className="font-medium">Job Type:</span>
                  <span className="ml-2 text-gray-600 dark:text-gray-300">
                    {formatJobType(job.jobType)}
                  </span>
                </div>
                <div>
                  <span className="font-medium">Location:</span>
                  <span className="ml-2 text-gray-600 dark:text-gray-300">
                    {job.location}
                  </span>
                </div>
                <div>
                  <span className="font-medium">Salary:</span>
                  <span className="ml-2 text-gray-600 dark:text-gray-300">
                    {formatSalary(job.salary)}
                  </span>
                </div>
                <div>
                  <span className="font-medium">Posted:</span>
                  <span className="ml-2 text-gray-600 dark:text-gray-300">
                    {formatDate(job.createdAt)}
                  </span>
                </div>
              </CardContent>
            </Card>
          </div>
        </div>

      {/* Application Modal */}
      <JobApplicationModal
        job={job}
        isOpen={showApplicationModal}
        onClose={() => setShowApplicationModal(false)}
        onApplicationSubmitted={() => {
          setShowApplicationModal(false)
          // Optionally refresh job data or show success message
        }}
      />
    </div>
  )
}