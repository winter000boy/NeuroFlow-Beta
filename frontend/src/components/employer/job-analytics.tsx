'use client'

import type { JobAnalytics } from '@/types/job'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Badge } from '@/components/ui/badge'
import { useJobAnalytics } from '@/hooks/use-employer-jobs'

interface JobAnalyticsProps {
  jobId: string
}

export function JobAnalytics({ jobId }: JobAnalyticsProps) {
  const { analytics, loading, error } = useJobAnalytics(jobId)

  if (loading) {
    return <div className="text-center py-4">Loading analytics...</div>
  }

  if (error) {
    return <div className="text-center py-4 text-destructive">Error: {error}</div>
  }

  if (!analytics) {
    return <div className="text-center py-4">No analytics data available.</div>
  }

  const totalApplications = analytics.totalApplications
  const applicationsByStatus = analytics.applicationsByStatus

  const getPercentage = (count: number) => {
    return totalApplications > 0 ? Math.round((count / totalApplications) * 100) : 0
  }

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString()
  }

  return (
    <div className="space-y-6">
      {/* Overview Cards */}
      <div className="grid grid-cols-2 gap-4">
        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm font-medium">Total Applications</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{totalApplications}</div>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm font-medium">Job Views</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{analytics.viewCount}</div>
          </CardContent>
        </Card>
      </div>

      {/* Application Status Breakdown */}
      <Card>
        <CardHeader>
          <CardTitle>Application Status Breakdown</CardTitle>
        </CardHeader>
        <CardContent>
          <div className="space-y-4">
            <div className="flex justify-between items-center">
              <div className="flex items-center gap-2">
                <Badge variant="default">Applied</Badge>
                <span className="text-sm text-muted-foreground">
                  {getPercentage(applicationsByStatus.APPLIED)}%
                </span>
              </div>
              <span className="font-semibold">{applicationsByStatus.APPLIED}</span>
            </div>

            <div className="flex justify-between items-center">
              <div className="flex items-center gap-2">
                <Badge variant="secondary">In Review</Badge>
                <span className="text-sm text-muted-foreground">
                  {getPercentage(applicationsByStatus.IN_REVIEW)}%
                </span>
              </div>
              <span className="font-semibold">{applicationsByStatus.IN_REVIEW}</span>
            </div>

            <div className="flex justify-between items-center">
              <div className="flex items-center gap-2">
                <Badge variant="default">Hired</Badge>
                <span className="text-sm text-muted-foreground">
                  {getPercentage(applicationsByStatus.HIRED)}%
                </span>
              </div>
              <span className="font-semibold">{applicationsByStatus.HIRED}</span>
            </div>

            <div className="flex justify-between items-center">
              <div className="flex items-center gap-2">
                <Badge variant="destructive">Rejected</Badge>
                <span className="text-sm text-muted-foreground">
                  {getPercentage(applicationsByStatus.REJECTED)}%
                </span>
              </div>
              <span className="font-semibold">{applicationsByStatus.REJECTED}</span>
            </div>
          </div>
        </CardContent>
      </Card>

      {/* Performance Metrics */}
      <Card>
        <CardHeader>
          <CardTitle>Performance Metrics</CardTitle>
        </CardHeader>
        <CardContent>
          <div className="space-y-3">
            <div className="flex justify-between">
              <span className="text-sm">Application Rate</span>
              <span className="font-semibold">
                {analytics.viewCount > 0 
                  ? Math.round((totalApplications / analytics.viewCount) * 100) 
                  : 0}%
              </span>
            </div>

            <div className="flex justify-between">
              <span className="text-sm">Hire Rate</span>
              <span className="font-semibold">
                {totalApplications > 0 
                  ? Math.round((applicationsByStatus.HIRED / totalApplications) * 100) 
                  : 0}%
              </span>
            </div>

            <div className="flex justify-between">
              <span className="text-sm">Response Rate</span>
              <span className="font-semibold">
                {totalApplications > 0 
                  ? Math.round(((applicationsByStatus.IN_REVIEW + applicationsByStatus.HIRED + applicationsByStatus.REJECTED) / totalApplications) * 100) 
                  : 0}%
              </span>
            </div>

            <div className="flex justify-between">
              <span className="text-sm">Posted Date</span>
              <span className="font-semibold">{formatDate(analytics.createdAt)}</span>
            </div>
          </div>
        </CardContent>
      </Card>

      {/* Visual Progress Bars */}
      <Card>
        <CardHeader>
          <CardTitle>Application Pipeline</CardTitle>
        </CardHeader>
        <CardContent>
          <div className="space-y-4">
            {Object.entries(applicationsByStatus).map(([status, count]) => {
              const percentage = getPercentage(count)
              const statusLabel = status.replace('_', ' ')
              
              return (
                <div key={status} className="space-y-2">
                  <div className="flex justify-between text-sm">
                    <span>{statusLabel}</span>
                    <span>{count} ({percentage}%)</span>
                  </div>
                  <div className="w-full bg-gray-200 rounded-full h-2">
                    <div
                      className="bg-blue-600 h-2 rounded-full transition-all duration-300"
                      style={{ width: `${percentage}%` }}
                    />
                  </div>
                </div>
              )
            })}
          </div>
        </CardContent>
      </Card>
    </div>
  )
}