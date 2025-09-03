'use client'

import { useState } from 'react'
import { JobApplication } from '@/types/job'
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Badge } from '@/components/ui/badge'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select'
import { Textarea } from '@/components/ui/textarea'
import { Dialog, DialogContent, DialogHeader, DialogTitle } from '@/components/ui/dialog'
import { useJobApplications } from '@/hooks/use-employer-jobs'

interface ApplicationsListProps {
  jobId: string
}

export function ApplicationsList({ jobId }: ApplicationsListProps) {
  const { applications, loading, error, updateApplicationStatus } = useJobApplications(jobId)
  const [selectedApplication, setSelectedApplication] = useState<JobApplication | null>(null)
  const [statusUpdate, setStatusUpdate] = useState<{
    applicationId: string
    status: JobApplication['status']
    notes: string
  } | null>(null)

  const handleStatusUpdate = async () => {
    if (!statusUpdate) return

    try {
      await updateApplicationStatus(
        statusUpdate.applicationId,
        statusUpdate.status,
        statusUpdate.notes
      )
      setStatusUpdate(null)
    } catch (error) {
      console.error('Failed to update application status:', error)
    }
  }

  const getStatusColor = (status: JobApplication['status']) => {
    switch (status) {
      case 'APPLIED':
        return 'default'
      case 'IN_REVIEW':
        return 'secondary'
      case 'HIRED':
        return 'default'
      case 'REJECTED':
        return 'destructive'
      default:
        return 'outline'
    }
  }

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString()
  }

  if (loading) {
    return <div className="text-center py-4">Loading applications...</div>
  }

  if (error) {
    return <div className="text-center py-4 text-destructive">Error: {error}</div>
  }

  if (applications.length === 0) {
    return (
      <div className="text-center py-8">
        <p className="text-muted-foreground">No applications yet for this job.</p>
      </div>
    )
  }

  return (
    <div className="space-y-4">
      <div className="flex justify-between items-center">
        <h3 className="text-lg font-semibold">
          {applications.length} Application{applications.length !== 1 ? 's' : ''}
        </h3>
        <div className="flex gap-2">
          <Badge variant="outline">
            Applied: {applications.filter(app => app.status === 'APPLIED').length}
          </Badge>
          <Badge variant="secondary">
            In Review: {applications.filter(app => app.status === 'IN_REVIEW').length}
          </Badge>
          <Badge variant="default">
            Hired: {applications.filter(app => app.status === 'HIRED').length}
          </Badge>
          <Badge variant="destructive">
            Rejected: {applications.filter(app => app.status === 'REJECTED').length}
          </Badge>
        </div>
      </div>

      {applications.map((application) => (
        <Card key={application.id}>
          <CardHeader>
            <div className="flex justify-between items-start">
              <div>
                <CardTitle className="text-lg">
                  {application.candidate?.name || 'Unknown Candidate'}
                </CardTitle>
                <p className="text-sm text-muted-foreground">
                  {application.candidate?.email}
                </p>
              </div>
              <Badge variant={getStatusColor(application.status)}>
                {application.status.replace('_', ' ')}
              </Badge>
            </div>
          </CardHeader>
          <CardContent>
            <div className="space-y-3">
              {application.candidate && (
                <div className="grid grid-cols-2 gap-4 text-sm">
                  <div>
                    <strong>Degree:</strong> {application.candidate.degree}
                  </div>
                  <div>
                    <strong>Graduation:</strong> {application.candidate.graduationYear}
                  </div>
                  {application.candidate.linkedinProfile && (
                    <div>
                      <strong>LinkedIn:</strong>{' '}
                      <a 
                        href={application.candidate.linkedinProfile}
                        target="_blank"
                        rel="noopener noreferrer"
                        className="text-blue-600 hover:underline"
                      >
                        View Profile
                      </a>
                    </div>
                  )}
                  {application.candidate.portfolioUrl && (
                    <div>
                      <strong>Portfolio:</strong>{' '}
                      <a 
                        href={application.candidate.portfolioUrl}
                        target="_blank"
                        rel="noopener noreferrer"
                        className="text-blue-600 hover:underline"
                      >
                        View Portfolio
                      </a>
                    </div>
                  )}
                </div>
              )}

              <div className="flex justify-between items-center">
                <div className="text-sm text-muted-foreground">
                  Applied on {formatDate(application.appliedAt)}
                  {application.updatedAt !== application.appliedAt && (
                    <span> • Updated {formatDate(application.updatedAt)}</span>
                  )}
                </div>
                
                <div className="flex gap-2">
                  {application.candidate?.resumeUrl && (
                    <Button
                      variant="outline"
                      size="sm"
                      onClick={() => window.open(application.candidate?.resumeUrl, '_blank')}
                    >
                      View Resume
                    </Button>
                  )}
                  <Button
                    variant="outline"
                    size="sm"
                    onClick={() => setSelectedApplication(application)}
                  >
                    View Details
                  </Button>
                  <Button
                    variant="default"
                    size="sm"
                    onClick={() => setStatusUpdate({
                      applicationId: application.id,
                      status: application.status,
                      notes: application.notes || ''
                    })}
                  >
                    Update Status
                  </Button>
                </div>
              </div>
            </div>
          </CardContent>
        </Card>
      ))}

      {/* Candidate Details Dialog */}
      <Dialog open={!!selectedApplication} onOpenChange={() => setSelectedApplication(null)}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Candidate Details</DialogTitle>
          </DialogHeader>
          {selectedApplication && (
            <div className="space-y-4">
              <div>
                <h4 className="font-semibold">{selectedApplication.candidate?.name}</h4>
                <p className="text-sm text-muted-foreground">
                  {selectedApplication.candidate?.email}
                </p>
              </div>
              
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <strong>Degree:</strong> {selectedApplication.candidate?.degree}
                </div>
                <div>
                  <strong>Graduation:</strong> {selectedApplication.candidate?.graduationYear}
                </div>
              </div>

              {selectedApplication.notes && (
                <div>
                  <strong>Notes:</strong>
                  <p className="mt-1 text-sm">{selectedApplication.notes}</p>
                </div>
              )}
            </div>
          )}
        </DialogContent>
      </Dialog>

      {/* Status Update Dialog */}
      <Dialog open={!!statusUpdate} onOpenChange={() => setStatusUpdate(null)}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Update Application Status</DialogTitle>
          </DialogHeader>
          {statusUpdate && (
            <div className="space-y-4">
              <div>
                <label className="text-sm font-medium">Status</label>
                <Select
                  value={statusUpdate.status}
                  onValueChange={(value: JobApplication['status']) =>
                    setStatusUpdate({ ...statusUpdate, status: value })
                  }
                >
                  <SelectTrigger>
                    <SelectValue />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="APPLIED">Applied</SelectItem>
                    <SelectItem value="IN_REVIEW">In Review</SelectItem>
                    <SelectItem value="HIRED">Hired</SelectItem>
                    <SelectItem value="REJECTED">Rejected</SelectItem>
                  </SelectContent>
                </Select>
              </div>

              <div>
                <label className="text-sm font-medium">Notes (Optional)</label>
                <Textarea
                  value={statusUpdate.notes}
                  onChange={(e) =>
                    setStatusUpdate({ ...statusUpdate, notes: e.target.value })
                  }
                  placeholder="Add any notes about this candidate..."
                  className="mt-1"
                />
              </div>

              <div className="flex justify-end gap-2">
                <Button variant="outline" onClick={() => setStatusUpdate(null)}>
                  Cancel
                </Button>
                <Button onClick={handleStatusUpdate}>
                  Update Status
                </Button>
              </div>
            </div>
          )}
        </DialogContent>
      </Dialog>
    </div>
  )
}