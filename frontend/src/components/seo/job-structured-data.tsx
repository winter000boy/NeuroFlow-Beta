'use client'

import { Job } from '@/types/job'

interface JobStructuredDataProps {
  job: Job
}

export function JobStructuredData({ job }: JobStructuredDataProps) {
  const formatJobType = (jobType: string) => {
    const typeMap: { [key: string]: string } = {
      'FULL_TIME': 'FULL_TIME',
      'PART_TIME': 'PART_TIME',
      'CONTRACT': 'CONTRACTOR',
      'REMOTE': 'FULL_TIME', // Remote is typically full-time
    }
    return typeMap[jobType] || 'FULL_TIME'
  }

  const structuredData = {
    '@context': 'https://schema.org/',
    '@type': 'JobPosting',
    title: job.title,
    description: job.description.replace(/<[^>]*>/g, ''), // Strip HTML tags
    identifier: {
      '@type': 'PropertyValue',
      name: job.employer.companyName,
      value: job.id,
    },
    datePosted: job.createdAt,
    validThrough: job.expiresAt || new Date(Date.now() + 30 * 24 * 60 * 60 * 1000).toISOString(), // 30 days from now if no expiry
    employmentType: formatJobType(job.jobType),
    hiringOrganization: {
      '@type': 'Organization',
      name: job.employer.companyName,
      sameAs: job.employer.website,
      logo: job.employer.logoUrl,
    },
    jobLocation: {
      '@type': 'Place',
      address: {
        '@type': 'PostalAddress',
        addressLocality: job.location,
      },
    },
    baseSalary: {
      '@type': 'MonetaryAmount',
      currency: job.salary.currency,
      value: {
        '@type': 'QuantitativeValue',
        minValue: job.salary.min,
        maxValue: job.salary.max,
        unitText: 'YEAR',
      },
    },
    workHours: job.jobType === 'PART_TIME' ? '20-30' : '40',
    url: `${process.env.NEXT_PUBLIC_SITE_URL || 'https://jobapp.com'}/jobs/${job.id}`,
  }

  return (
    <script
      type="application/ld+json"
      dangerouslySetInnerHTML={{
        __html: JSON.stringify(structuredData),
      }}
    />
  )
}