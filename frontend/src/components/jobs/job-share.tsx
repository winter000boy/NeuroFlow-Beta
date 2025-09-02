'use client'

import { useState } from 'react'
import { Share2, Copy, Check, Facebook, Twitter, Linkedin } from 'lucide-react'
import { Button } from '@/components/ui/button'
import { 
  Dialog, 
  DialogContent, 
  DialogHeader, 
  DialogTitle,
  DialogClose 
} from '@/components/ui/dialog'
import { Job } from '@/types/job'
import { toast } from 'sonner'

interface JobShareProps {
  job: Job
}

export function JobShare({ job }: JobShareProps) {
  const [isOpen, setIsOpen] = useState(false)
  const [copied, setCopied] = useState(false)

  const jobUrl = typeof window !== 'undefined' ? window.location.href : ''
  const shareText = `Check out this job opportunity: ${job.title} at ${job.employer.companyName}`

  const handleCopyLink = async () => {
    try {
      await navigator.clipboard.writeText(jobUrl)
      setCopied(true)
      toast.success('Link copied to clipboard!')
      setTimeout(() => setCopied(false), 2000)
    } catch (error) {
      console.error('Failed to copy link:', error)
      toast.error('Failed to copy link')
    }
  }

  const shareLinks = [
    {
      name: 'Facebook',
      icon: Facebook,
      url: `https://www.facebook.com/sharer/sharer.php?u=${encodeURIComponent(jobUrl)}`,
      color: 'text-blue-600 hover:text-blue-700'
    },
    {
      name: 'Twitter',
      icon: Twitter,
      url: `https://twitter.com/intent/tweet?text=${encodeURIComponent(shareText)}&url=${encodeURIComponent(jobUrl)}`,
      color: 'text-sky-500 hover:text-sky-600'
    },
    {
      name: 'LinkedIn',
      icon: Linkedin,
      url: `https://www.linkedin.com/sharing/share-offsite/?url=${encodeURIComponent(jobUrl)}`,
      color: 'text-blue-700 hover:text-blue-800'
    }
  ]

  const handleSocialShare = (url: string) => {
    window.open(url, '_blank', 'width=600,height=400,scrollbars=yes,resizable=yes')
  }

  return (
    <>
      <Button
        variant="outline"
        size="sm"
        onClick={() => setIsOpen(true)}
        className="flex items-center space-x-2"
      >
        <Share2 className="h-4 w-4" />
        <span>Share</span>
      </Button>

      <Dialog open={isOpen} onOpenChange={setIsOpen}>
        <DialogContent className="sm:max-w-md">
          <DialogClose onClick={() => setIsOpen(false)} />
          <DialogHeader>
            <DialogTitle>Share Job</DialogTitle>
          </DialogHeader>
          
          <div className="space-y-4">
            {/* Job info */}
            <div className="rounded-lg border border-gray-200 dark:border-gray-700 p-3">
              <h4 className="font-medium text-sm">{job.title}</h4>
              <p className="text-xs text-gray-600 dark:text-gray-400">{job.employer.companyName}</p>
            </div>

            {/* Copy link */}
            <div className="space-y-2">
              <label className="text-sm font-medium">Copy Link</label>
              <div className="flex items-center space-x-2">
                <input
                  type="text"
                  value={jobUrl}
                  readOnly
                  className="flex-1 px-3 py-2 text-sm border border-gray-200 dark:border-gray-700 rounded-md bg-gray-50 dark:bg-gray-800"
                />
                <Button
                  size="sm"
                  onClick={handleCopyLink}
                  className="flex items-center space-x-1"
                >
                  {copied ? (
                    <Check className="h-4 w-4" />
                  ) : (
                    <Copy className="h-4 w-4" />
                  )}
                </Button>
              </div>
            </div>

            {/* Social media sharing */}
            <div className="space-y-2">
              <label className="text-sm font-medium">Share on Social Media</label>
              <div className="flex space-x-2">
                {shareLinks.map((social) => (
                  <Button
                    key={social.name}
                    variant="outline"
                    size="sm"
                    onClick={() => handleSocialShare(social.url)}
                    className={`flex items-center space-x-2 ${social.color}`}
                  >
                    <social.icon className="h-4 w-4" />
                    <span className="hidden sm:inline">{social.name}</span>
                  </Button>
                ))}
              </div>
            </div>
          </div>
        </DialogContent>
      </Dialog>
    </>
  )
}