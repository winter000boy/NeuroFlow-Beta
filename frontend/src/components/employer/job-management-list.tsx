'use client'

import { useState } from 'react'
import { Job } from '@/types/job'
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Badge } from '@/components/ui/badge'
import { Dialog, DialogContent, DialogHeader, DialogTitle } from '@/components/ui/dialog'
import { JobForm } from './job-form'
import { ApplicationsList } from './applications-list'
import { JobAnalytics } from './job-analytics'
import { useEmployerJobs } from '@/hooks/use-employer-jobs'
import { CreateJobRequest } from '@/types/job'

interface JobManagementListProps {
  jobs: Job[]
  onJobUpdate: () => void
}

export function JobManagementList({ jobs, onJobUpdate }: JobManagementListProps) {
  const { updateJob, toggleJobStatus, deleteJob } = useEmployerJobs()

  const [showEditForm, setShowEditForm] = useState<Job | null>(null)
  const [showApplications, setShowApplications] = useState<Job | null>(null)
  const [showAnalytics, setShowAnalytics] = useState<Job | null>(null)

  const handleToggleStatus = async (job: Job) => {
    try {
      await toggleJobStatus(job.id, !job.isActive)
      onJobUpdate()
    } catch (error) {
      console.error('Failed to toggle job status:', error)
    }
  }

  const handleDeleteJob = async (jobId: string) => {
    if (!window.confirm('Are you sure you want to delete this job? This action cannot be undone.')) {
      return
    }
    
    try {
      await deleteJob(jobId)
      onJobUpdate()
    } catch (error) {
      console.error('Failed to delete job:', error)
    }
  }

  const handleUpdateJob = async (jobData: CreateJobRequest) => {
    if (!showEditForm) return
    
    try {
      await updateJob(showEditForm.id, jobData)
      setShowEditForm(null)
      onJobUpdate()
    } catch (error) {
      console.error('Failed to update job:', error)
    }
  }

  const formatSalary = (salary: Job['salary']) => {
    return `${salary.currency} ${salary.min.toLocaleString()} - ${salary.max.toLocaleString()}`
  }

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString()
  }

  if (jobs.length === 0) {
    return (
      <div className="text-center py-8">
        <p className="text-muted-foreground mb-4">You haven&apos;t posted any jobs yet.</p>
        <p className="text-sm text-muted-foreground">Use the &quot;Post New Job&quot; button above to get started.</p>
      </div>
    )
  }

  return (
    <div className="space-y-4">
      {jobs.map((job) => (
        <Card key={job.id}>
          <CardHeader>
            <div className="flex justify-between items-start">
              <div>
                <CardTitle className="text-xl">{job.title}</CardTitle>
                <div className="flex items-center gap-2 mt-2">
                  <Badge variant={job.isActive ? 'default' : 'secondary'}>
                    {job.isActive ? 'Active' : 'Inactive'}
                  </Badge>
                  <Badge variant="outline">{job.jobType.replace('_', ' ')}</Badge>
                  <span className="text-sm text-muted-foreground">{job.location}</span>
                </div>
              </div>
              <div className="flex gap-2">
                <Button
                  variant="outline"
                  size="sm"
                  onClick={() => setShowAnalytics(job)}
                >
                  Analytics
                </Button>
                <Button
                  variant="outline"
                  size="sm"
                  onClick={() => setShowApplications(job)}
                >
                  Applications
                </Button>
                <Button
                  variant="outline"
                  size="sm"
                  onClick={() => setShowEditForm(job)}
                >
                  Edit
                </Button>
              </div>
            </div>
          </CardHeader>
          <CardContent>
            <div className="space-y-3">
              <div>
                <p className="text-sm text-muted-foreground line-clamp-2">
                  {job.description.replace(/[*#]/g, '').substring(0, 150)}...
                </p>
              </div>
              
              <div className="flex justify-between items-center text-sm">
                <div className="space-y-1">
                  <p><strong>Salary:</strong> {formatSalary(job.salary)}</p>
                  <p><strong>Posted:</strong> {formatDate(job.createdAt)}</p>
                  {job.expiresAt && (
                    <p><strong>Expires:</strong> {formatDate(job.expiresAt)}</p>
                  )}
                </div>
                
                <div className="flex gap-2">
                  <Button
                    variant={job.isActive ? 'destructive' : 'default'}
                    size="sm"
                    onClick={() => handleToggleStatus(job)}
                  >
                    {job.isActive ? 'Deactivate' : 'Activate'}
                  </Button>
                  <Button
                    variant="destructive"
                    size="sm"
                    onClick={() => handleDeleteJob(job.id)}
                  >
                    Delete
                  </Button>
                </div>
              </div>
            </div>
          </CardContent>
        </Card>
      ))}

      {/* Applications Dialog */}
      <Dialog open={!!showApplications} onOpenChange={() => setShowApplications(null)}>
        <DialogContent className="max-w-4xl max-h-[80vh] overflow-y-auto">
          <DialogHeader>
            <DialogTitle>Applications for {showApplications?.title}</DialogTitle>
          </DialogHeader>
          {showApplications && (
            <ApplicationsList jobId={showApplications.id} />
          )}
        </DialogContent>
      </Dialog>

      {/* Analytics Dialog */}
      <Dialog open={!!showAnalytics} onOpenChange={() => setShowAnalytics(null)}>
        <DialogContent className="max-w-2xl">
          <DialogHeader>
            <DialogTitle>Analytics for {showAnalytics?.title}</DialogTitle>
          </DialogHeader>
          {showAnalytics && (
            <JobAnalytics jobId={showAnalytics.id} />
          )}
        </DialogContent>
      </Dialog>

      {/* Edit Job Dialog */}
      <Dialog open={!!showEditForm} onOpenChange={() => setShowEditForm(null)}>
        <DialogContent className="max-w-2xl max-h-[80vh] overflow-y-auto">
          <DialogHeader>
            <DialogTitle>Edit Job</DialogTitle>
          </DialogHeader>
          {showEditForm && (
            <JobForm
              job={showEditForm}
              onSubmit={handleUpdateJob}
              onCancel={() => setShowEditForm(null)}
            />
          )}
        </DialogContent>
      </Dialog>
    </div>
  )
}