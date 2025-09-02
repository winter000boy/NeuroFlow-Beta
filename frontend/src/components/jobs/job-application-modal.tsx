'use client'

import { useState } from 'react'
import { toast } from 'sonner'
import { 
  Dialog, 
  DialogContent, 
  DialogHeader, 
  DialogTitle, 
  DialogDescription, 
  DialogFooter,
  DialogClose 
} from '@/components/ui/dialog'
import { Button } from '@/components/ui/button'
import { JobService } from '@/services/job.service'
import { Job } from '@/types/job'
import { CheckCircle, AlertCircle } from 'lucide-react'

interface JobApplicationModalProps {
  job: Job
  isOpen: boolean
  onClose: () => void
  onApplicationSubmitted: () => void
}

export function JobApplicationModal({ 
  job, 
  isOpen, 
  onClose, 
  onApplicationSubmitted 
}: JobApplicationModalProps) {
  const [isSubmitting, setIsSubmitting] = useState(false)
  const [isSubmitted, setIsSubmitted] = useState(false)

  const handleApply = async () => {
    setIsSubmitting(true)
    try {
      await JobService.applyForJob(job.id)
      setIsSubmitted(true)
      toast.success('Application submitted successfully!')
      onApplicationSubmitted()
      
      // Auto close after 2 seconds
      setTimeout(() => {
        onClose()
        setIsSubmitted(false)
      }, 2000)
    } catch (error) {
      console.error('Error applying for job:', error)
      toast.error('Failed to submit application. Please try again.')
    } finally {
      setIsSubmitting(false)
    }
  }

  const handleClose = () => {
    onClose()
    setIsSubmitted(false)
  }

  if (isSubmitted) {
    return (
      <Dialog open={isOpen} onOpenChange={handleClose}>
        <DialogContent className="sm:max-w-md">
          <DialogHeader className="text-center">
            <div className="mx-auto mb-4 flex h-12 w-12 items-center justify-center rounded-full bg-green-100 dark:bg-green-900">
              <CheckCircle className="h-6 w-6 text-green-600 dark:text-green-400" />
            </div>
            <DialogTitle>Application Submitted!</DialogTitle>
            <DialogDescription>
              Your application for <strong>{job.title}</strong> at{' '}
              <strong>{job.employer.companyName}</strong> has been submitted successfully.
              You will receive an email confirmation shortly.
            </DialogDescription>
          </DialogHeader>
        </DialogContent>
      </Dialog>
    )
  }

  return (
    <Dialog open={isOpen} onOpenChange={handleClose}>
      <DialogContent className="sm:max-w-md">
        <DialogClose onClick={handleClose} />
        <DialogHeader>
          <DialogTitle>Apply for Position</DialogTitle>
          <DialogDescription>
            You are about to apply for the following position:
          </DialogDescription>
        </DialogHeader>
        
        <div className="space-y-4">
          <div className="rounded-lg border border-gray-200 dark:border-gray-700 p-4">
            <h4 className="font-semibold text-lg">{job.title}</h4>
            <p className="text-gray-600 dark:text-gray-400">{job.employer.companyName}</p>
            <p className="text-sm text-gray-500 dark:text-gray-500 mt-1">
              {job.location} • {job.jobType.replace('_', ' ')}
            </p>
            <p className="text-sm font-medium text-green-600 dark:text-green-400 mt-2">
              ${job.salary.min.toLocaleString()} - ${job.salary.max.toLocaleString()} {job.salary.currency}
            </p>
          </div>

          <div className="flex items-start space-x-2 p-3 bg-blue-50 dark:bg-blue-900/20 rounded-lg">
            <AlertCircle className="h-5 w-5 text-blue-600 dark:text-blue-400 mt-0.5 flex-shrink-0" />
            <div className="text-sm text-blue-800 dark:text-blue-200">
              <p className="font-medium">Before you apply:</p>
              <ul className="mt-1 space-y-1 text-xs">
                <li>• Make sure your profile is complete</li>
                <li>• Your resume should be up to date</li>
                <li>• You can only apply once per job</li>
              </ul>
            </div>
          </div>
        </div>

        <DialogFooter className="mt-6">
          <Button variant="outline" onClick={handleClose} disabled={isSubmitting}>
            Cancel
          </Button>
          <Button onClick={handleApply} disabled={isSubmitting}>
            {isSubmitting ? 'Submitting...' : 'Submit Application'}
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  )
}