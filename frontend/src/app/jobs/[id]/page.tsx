/* eslint-disable react/no-unescaped-entities */
import { Metadata } from 'next'
import { notFound } from 'next/navigation'
import { Suspense } from 'react'
import { JobDetailClient } from './job-detail-client'
import { JobStructuredData } from '@/components/seo/job-structured-data'
import { Breadcrumb } from '@/components/seo/breadcrumb'

// Mock function to fetch job data for SSR - replace with actual API call
async function getJobData(jobId: string) {
  try {
    // In a real implementation, you would fetch from your API
    // For now, we'll return a mock job or null
    const response = await fetch(`${process.env.NEXT_PUBLIC_API_URL}/jobs/${jobId}`, {
      cache: 'no-store', // Ensure fresh data for SEO
    })
    
    if (!response.ok) {
      return null
    }
    
    return await response.json()
  } catch (error) {
    console.error('Error fetching job data:', error)
    return null
  }
}

type Props = {
  params: Promise<{ id: string }>
}

export async function generateMetadata({ params }: Props): Promise<Metadata> {
  const { id } = await params
  const job = await getJobData(id)
  
  if (!job) {
    return {
      title: 'Job Not Found - JobApp',
      description: 'The job you are looking for could not be found.',
    }
  }

  const formatJobType = (jobType: string) => {
    return jobType.replace('_', ' ').toLowerCase().replace(/\b\w/g, l => l.toUpperCase())
  }

  const formatSalary = (salary: { min: number; max: number; currency: string }) => {
    return `${salary.min.toLocaleString()} - ${salary.max.toLocaleString()} ${salary.currency}`
  }

  const title = `${job.title} at ${job.employer.companyName} - JobApp`
  const description = `${formatJobType(job.jobType)} position in ${job.location}. Salary: ${formatSalary(job.salary)}. ${job.description.substring(0, 150)}...`

  return {
    title,
    description,
    keywords: [
      job.title,
      job.employer.companyName,
      job.location,
      formatJobType(job.jobType),
      'job',
      'career',
      'employment',
      'hiring'
    ],
    openGraph: {
      title,
      description,
      type: 'article',
      url: `/jobs/${job.id}`,
      images: [
        {
          url: job.employer.logoUrl || '/og-job-default.jpg',
          width: 1200,
          height: 630,
          alt: `${job.title} at ${job.employer.companyName}`,
        },
      ],
      siteName: 'JobApp',
      publishedTime: job.createdAt,
      modifiedTime: job.updatedAt,
    },
    twitter: {
      card: 'summary_large_image',
      title,
      description,
      images: [job.employer.logoUrl || '/og-job-default.jpg'],
    },
    alternates: {
      canonical: `/jobs/${job.id}`,
    },
  }
}

export default async function JobDetailPage({ params }: Props) {
  const { id } = await params
  const job = await getJobData(id)
  
  if (!job) {
    notFound()
  }

  return (
    <>
      <JobStructuredData job={job} />
      <div className="container mx-auto px-4 py-8">
        <Breadcrumb
          items={[
            { label: 'Jobs', href: '/jobs' },
            { label: job.title }
          ]}
        />
        <Suspense fallback={
          <div className="animate-pulse space-y-6">
            <div className="h-8 bg-gray-200 dark:bg-gray-700 rounded w-1/4"></div>
            <div className="h-12 bg-gray-200 dark:bg-gray-700 rounded w-3/4"></div>
            <div className="h-64 bg-gray-200 dark:bg-gray-700 rounded"></div>
          </div>
        }>
          <JobDetailClient jobId={id} initialJob={job} />
        </Suspense>
      </div>
    </>
  )
}