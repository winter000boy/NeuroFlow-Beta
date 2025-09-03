'use client'

import { useState } from 'react'
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Dialog, DialogContent, DialogHeader, DialogTitle } from '@/components/ui/dialog'
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs'
import { JobForm } from './job-form'
import { JobManagementList } from './job-management-list'
import { useEmployerJobs, useEmployerAnalytics } from '@/hooks/use-employer-jobs'
import { CreateJobRequest } from '@/types/job'

export function EmployerDashboard() {
  const { jobs, loading, error, createJob, fetchJobs } = useEmployerJobs()
  const { analytics: employerAnalytics, loading: analyticsLoading } = useEmployerAnalytics()
  const [showCreateForm, setShowCreateForm] = useState(false)

  const handleCreateJob = async (jobData: CreateJobRequest) => {
    try {
      await createJob(jobData)
      setShowCreateForm(false)
      fetchJobs() // Refresh the job list
    } catch (error) {
      console.error('Failed to create job:', error)
    }
  }

  const handleJobUpdate = () => {
    fetchJobs() // Refresh the job list when a job is updated
  }

  if (loading) {
    return (
      <div className="container mx-auto px-4 py-8">
        <div className="text-center">Loading dashboard...</div>
      </div>
    )
  }

  if (error) {
    return (
      <div className="container mx-auto px-4 py-8">
        <div className="text-center text-destructive">Error: {error}</div>
      </div>
    )
  }

  return (
    <div className="container mx-auto px-4 py-8">
      <div className="flex justify-between items-center mb-8">
        <div>
          <h1 className="text-3xl font-bold">Employer Dashboard</h1>
          <p className="text-muted-foreground">Manage your job postings and applications</p>
        </div>
        <Button onClick={() => setShowCreateForm(true)}>
          Post New Job
        </Button>
      </div>

      <Tabs defaultValue="overview" className="space-y-6">
        <TabsList>
          <TabsTrigger value="overview">Overview</TabsTrigger>
          <TabsTrigger value="jobs">Job Management</TabsTrigger>
        </TabsList>

        <TabsContent value="overview" className="space-y-6">
          {/* Analytics Overview */}
          {!analyticsLoading && employerAnalytics && (
            <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
              <Card>
                <CardHeader className="pb-2">
                  <CardTitle className="text-sm font-medium">Total Jobs</CardTitle>
                </CardHeader>
                <CardContent>
                  <div className="text-2xl font-bold">{employerAnalytics.totalJobs}</div>
                </CardContent>
              </Card>

              <Card>
                <CardHeader className="pb-2">
                  <CardTitle className="text-sm font-medium">Active Jobs</CardTitle>
                </CardHeader>
                <CardContent>
                  <div className="text-2xl font-bold">{employerAnalytics.activeJobs}</div>
                </CardContent>
              </Card>

              <Card>
                <CardHeader className="pb-2">
                  <CardTitle className="text-sm font-medium">Total Applications</CardTitle>
                </CardHeader>
                <CardContent>
                  <div className="text-2xl font-bold">{employerAnalytics.totalApplications}</div>
                </CardContent>
              </Card>

              <Card>
                <CardHeader className="pb-2">
                  <CardTitle className="text-sm font-medium">Avg. Applications/Job</CardTitle>
                </CardHeader>
                <CardContent>
                  <div className="text-2xl font-bold">
                    {employerAnalytics.totalJobs > 0 
                      ? Math.round(employerAnalytics.totalApplications / employerAnalytics.totalJobs)
                      : 0}
                  </div>
                </CardContent>
              </Card>
            </div>
          )}

          {/* Recent Applications */}
          {employerAnalytics?.recentApplications && employerAnalytics.recentApplications.length > 0 && (
            <Card>
              <CardHeader>
                <CardTitle>Recent Applications</CardTitle>
              </CardHeader>
              <CardContent>
                <div className="space-y-3">
                  {employerAnalytics.recentApplications.slice(0, 5).map((application) => (
                    <div key={application.id} className="flex justify-between items-center p-3 border rounded">
                      <div>
                        <p className="font-medium">{application.candidate?.name || 'Unknown Candidate'}</p>
                        <p className="text-sm text-muted-foreground">
                          Applied {new Date(application.appliedAt).toLocaleDateString()}
                        </p>
                      </div>
                      <div className="text-right">
                        <p className="text-sm font-medium">{application.status.replace('_', ' ')}</p>
                      </div>
                    </div>
                  ))}
                </div>
              </CardContent>
            </Card>
          )}

          {/* Quick Actions */}
          <Card>
            <CardHeader>
              <CardTitle>Quick Actions</CardTitle>
            </CardHeader>
            <CardContent>
              <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                <Button 
                  variant="outline" 
                  className="h-20 flex-col"
                  onClick={() => setShowCreateForm(true)}
                >
                  <span className="text-lg mb-1">+</span>
                  Post New Job
                </Button>
                <Button 
                  variant="outline" 
                  className="h-20 flex-col"
                  onClick={() => {
                    const activeJobs = jobs.filter(job => job.isActive)
                    if (activeJobs.length > 0) {
                      // Could navigate to applications view
                      console.log('View applications for active jobs')
                    }
                  }}
                >
                  <span className="text-lg mb-1">📋</span>
                  Review Applications
                </Button>
                <Button 
                  variant="outline" 
                  className="h-20 flex-col"
                  onClick={() => {
                    // Could navigate to analytics view
                    console.log('View detailed analytics')
                  }}
                >
                  <span className="text-lg mb-1">📊</span>
                  View Analytics
                </Button>
              </div>
            </CardContent>
          </Card>
        </TabsContent>

        <TabsContent value="jobs">
          <JobManagementList jobs={jobs} onJobUpdate={handleJobUpdate} />
        </TabsContent>
      </Tabs>

      {/* Create Job Dialog */}
      <Dialog open={showCreateForm} onOpenChange={setShowCreateForm}>
        <DialogContent className="max-w-2xl max-h-[80vh] overflow-y-auto">
          <DialogHeader>
            <DialogTitle>Post New Job</DialogTitle>
          </DialogHeader>
          <JobForm
            onSubmit={handleCreateJob}
            onCancel={() => setShowCreateForm(false)}
          />
        </DialogContent>
      </Dialog>
    </div>
  )
}