import { Metadata } from 'next'
import { Suspense } from 'react'
import { JobsPageClient } from './jobs-client'

export const metadata: Metadata = {
  title: 'Browse Jobs - JobApp',
  description: 'Find your dream job from thousands of opportunities. Search by location, job type, salary, and more. Apply to jobs from top companies.',
  keywords: ['jobs', 'careers', 'employment', 'job search', 'hiring', 'opportunities'],
  openGraph: {
    title: 'Browse Jobs - JobApp',
    description: 'Find your dream job from thousands of opportunities. Search by location, job type, salary, and more.',
    type: 'website',
    url: '/jobs',
    images: [
      {
        url: '/og-jobs.jpg',
        width: 1200,
        height: 630,
        alt: 'Browse Jobs on JobApp',
      },
    ],
  },
  twitter: {
    card: 'summary_large_image',
    title: 'Browse Jobs - JobApp',
    description: 'Find your dream job from thousands of opportunities.',
    images: ['/og-jobs.jpg'],
  },
  alternates: {
    canonical: '/jobs',
  },
}

export default function JobsPage() {
  return (
    <Suspense fallback={
      <div className="container mx-auto px-4 py-8">
        <div className="animate-pulse space-y-6">
          <div className="h-8 bg-gray-200 dark:bg-gray-700 rounded w-1/3"></div>
          <div className="h-4 bg-gray-200 dark:bg-gray-700 rounded w-2/3"></div>
          <div className="h-32 bg-gray-200 dark:bg-gray-700 rounded"></div>
          <div className="space-y-4">
            {[...Array(5)].map((_, i) => (
              <div key={i} className="h-24 bg-gray-200 dark:bg-gray-700 rounded"></div>
            ))}
          </div>
        </div>
      </div>
    }>
      <JobsPageClient />
    </Suspense>
  )
}