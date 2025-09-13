import { NextResponse } from 'next/server'

interface Job {
  id: string
  title: string
  createdAt: string
  updatedAt?: string
}

interface Company {
  id: string
  companyName: string
  createdAt: string
  updatedAt?: string
}

// Mock function to fetch jobs - replace with actual API call
async function getAllJobs(): Promise<Job[]> {
  try {
    const response = await fetch(`${process.env.NEXT_PUBLIC_API_URL}/jobs?size=1000`, {
      cache: 'no-store',
    })
    
    if (!response.ok) {
      return []
    }
    
    const data = await response.json()
    return data.content || []
  } catch (error) {
    console.error('Error fetching jobs for sitemap:', error)
    return []
  }
}

// Mock function to fetch companies - replace with actual API call
async function getAllCompanies(): Promise<Company[]> {
  try {
    const response = await fetch(`${process.env.NEXT_PUBLIC_API_URL}/employers?size=1000`, {
      cache: 'no-store',
    })
    
    if (!response.ok) {
      return []
    }
    
    const data = await response.json()
    return data.content || []
  } catch (error) {
    console.error('Error fetching companies for sitemap:', error)
    return []
  }
}

export async function GET() {
  const baseUrl = process.env.NEXT_PUBLIC_SITE_URL || 'https://jobapp.com'
  
  // Static pages
  const staticPages = [
    '',
    '/jobs',
    '/login',
    '/register',
    '/about',
    '/contact',
    '/privacy',
    '/terms',
  ]

  // Fetch dynamic content
  const [jobs, companies] = await Promise.all([
    getAllJobs(),
    getAllCompanies(),
  ])

  const currentDate = new Date().toISOString()

  let sitemap = `<?xml version="1.0" encoding="UTF-8"?>
<urlset xmlns="http://www.sitemaps.org/schemas/sitemap/0.9">
`

  // Add static pages
  staticPages.forEach((page) => {
    const priority = page === '' ? '1.0' : '0.8'
    const changefreq = page === '' ? 'daily' : 'weekly'
    
    sitemap += `  <url>
    <loc>${baseUrl}${page}</loc>
    <lastmod>${currentDate}</lastmod>
    <changefreq>${changefreq}</changefreq>
    <priority>${priority}</priority>
  </url>
`
  })

  // Add job pages
  jobs.forEach((job) => {
    sitemap += `  <url>
    <loc>${baseUrl}/jobs/${job.id}</loc>
    <lastmod>${job.updatedAt || job.createdAt}</lastmod>
    <changefreq>weekly</changefreq>
    <priority>0.9</priority>
  </url>
`
  })

  // Add company pages (if you have them)
  companies.forEach((company) => {
    sitemap += `  <url>
    <loc>${baseUrl}/companies/${company.id}</loc>
    <lastmod>${company.updatedAt || company.createdAt}</lastmod>
    <changefreq>monthly</changefreq>
    <priority>0.7</priority>
  </url>
`
  })

  sitemap += `</urlset>`

  return new NextResponse(sitemap, {
    headers: {
      'Content-Type': 'application/xml',
      'Cache-Control': 'public, max-age=3600, s-maxage=3600', // Cache for 1 hour
    },
  })
}