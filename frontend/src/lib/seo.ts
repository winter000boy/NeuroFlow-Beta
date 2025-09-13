import { Metadata } from 'next'

interface SEOConfig {
  title?: string
  description?: string
  keywords?: string[]
  image?: string
  url?: string
  type?: 'website' | 'article' | 'profile'
  publishedTime?: string
  modifiedTime?: string
  author?: string
  section?: string
  tags?: string[]
}

export function generateSEO(config: SEOConfig): Metadata {
  const {
    title,
    description,
    keywords = [],
    image,
    url,
    type = 'website',
    publishedTime,
    modifiedTime,
    author,
    section,
    tags = [],
  } = config

  const baseUrl = process.env.NEXT_PUBLIC_SITE_URL || 'https://jobapp.com'
  const fullUrl = url ? `${baseUrl}${url}` : baseUrl
  const ogImage = image || '/og-default.jpg'
  const fullImageUrl = ogImage.startsWith('http') ? ogImage : `${baseUrl}${ogImage}`

  const metadata: Metadata = {
    title: title ? `${title} | JobApp` : 'JobApp - Find Your Dream Job',
    description: description || 'Connect with top employers and find your perfect job opportunity.',
    keywords: keywords.length > 0 ? keywords : ['jobs', 'careers', 'employment', 'hiring'],
    openGraph: {
      title: title || 'JobApp - Find Your Dream Job',
      description: description || 'Connect with top employers and find your perfect job opportunity.',
      type,
      url: fullUrl,
      images: [
        {
          url: fullImageUrl,
          width: 1200,
          height: 630,
          alt: title || 'JobApp',
        },
      ],
      siteName: 'JobApp',
      ...(publishedTime && { publishedTime }),
      ...(modifiedTime && { modifiedTime }),
      ...(author && { authors: [author] }),
      ...(section && { section }),
      ...(tags.length > 0 && { tags }),
    },
    twitter: {
      card: 'summary_large_image',
      title: title || 'JobApp - Find Your Dream Job',
      description: description || 'Connect with top employers and find your perfect job opportunity.',
      images: [fullImageUrl],
      creator: '@jobapp',
    },
    alternates: {
      canonical: fullUrl,
    },
  }

  return metadata
}

interface JobForSEO {
  id: string
  title: string
  description: string
  jobType: string
  location: string
  salary: {
    min: number
    max: number
    currency: string
  }
  employer: {
    companyName: string
    logoUrl?: string
  }
  createdAt: string
  updatedAt?: string
}

export function generateJobSEO(job: JobForSEO): Metadata {
  const formatJobType = (jobType: string) => {
    return jobType.replace('_', ' ').toLowerCase().replace(/\b\w/g, l => l.toUpperCase())
  }

  const formatSalary = (salary: { min: number; max: number; currency: string }) => {
    return `${salary.min.toLocaleString()} - ${salary.max.toLocaleString()} ${salary.currency}`
  }

  const title = `${job.title} at ${job.employer.companyName}`
  const description = `${formatJobType(job.jobType)} position in ${job.location}. Salary: ${formatSalary(job.salary)}. ${job.description.replace(/<[^>]*>/g, '').substring(0, 150)}...`

  return generateSEO({
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
    image: job.employer.logoUrl,
    url: `/jobs/${job.id}`,
    type: 'article',
    publishedTime: job.createdAt,
    modifiedTime: job.updatedAt,
    author: job.employer.companyName,
    section: 'Jobs',
    tags: [job.title, job.location, formatJobType(job.jobType)],
  })
}

interface JobListingFilters {
  search?: string
  location?: string
  jobType?: string
}

export function generateJobListingSEO(filters: JobListingFilters, totalJobs: number): Metadata {
  let title = 'Browse Jobs'
  let description = `Find your dream job from ${totalJobs.toLocaleString()} opportunities`

  if (filters.search) {
    title = `${filters.search} Jobs`
    description = `${totalJobs.toLocaleString()} ${filters.search} job opportunities`
  }

  if (filters.location) {
    title += ` in ${filters.location}`
    description += ` in ${filters.location}`
  }

  if (filters.jobType) {
    const formattedType = filters.jobType.replace('_', ' ').toLowerCase()
    title += ` - ${formattedType}`
    description += ` for ${formattedType} positions`
  }

  const keywords = [
    'jobs',
    'careers',
    'employment',
    'job search',
    'hiring',
    ...(filters.search ? [filters.search] : []),
    ...(filters.location ? [filters.location] : []),
    ...(filters.jobType ? [filters.jobType.replace('_', ' ').toLowerCase()] : []),
  ]

  return generateSEO({
    title,
    description,
    keywords,
    url: '/jobs',
  })
}