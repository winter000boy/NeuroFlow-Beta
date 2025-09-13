'use client'

import { Job } from '@/types/job'

interface JobListingStructuredDataProps {
  jobs: Job[]
  totalJobs?: number
  searchQuery?: string
  location?: string
}

export function JobListingStructuredData({ 
  jobs, 
  totalJobs, 
  searchQuery, 
  location 
}: JobListingStructuredDataProps) {
  // Create structured data for job listing page
  const structuredData = {
    '@context': 'https://schema.org/',
    '@type': 'ItemList',
    name: searchQuery 
      ? `${searchQuery} Jobs${location ? ` in ${location}` : ''}` 
      : `Job Listings${location ? ` in ${location}` : ''}`,
    description: `Browse ${totalJobs || jobs.length} job opportunities${searchQuery ? ` for ${searchQuery}` : ''}${location ? ` in ${location}` : ''}`,
    numberOfItems: totalJobs || jobs.length,
    itemListElement: jobs.slice(0, 20).map((job, index) => ({ // Limit to first 20 for performance
      '@type': 'ListItem',
      position: index + 1,
      item: {
        '@type': 'JobPosting',
        '@id': `${process.env.NEXT_PUBLIC_SITE_URL || 'https://jobapp.com'}/jobs/${job.id}`,
        title: job.title,
        description: job.description.replace(/<[^>]*>/g, '').substring(0, 200) + '...', // Strip HTML and truncate
        datePosted: job.createdAt,
        employmentType: job.jobType.replace('_', ' '),
        hiringOrganization: {
          '@type': 'Organization',
          name: job.employer.companyName,
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
        url: `${process.env.NEXT_PUBLIC_SITE_URL || 'https://jobapp.com'}/jobs/${job.id}`,
      },
    })),
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