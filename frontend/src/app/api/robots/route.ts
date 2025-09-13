import { NextResponse } from 'next/server'

export async function GET() {
  const baseUrl = process.env.NEXT_PUBLIC_SITE_URL || 'https://jobapp.com'
  
  const robotsTxt = `User-agent: *
Allow: /

# Disallow admin and private pages
Disallow: /admin/
Disallow: /api/
Disallow: /profile/
Disallow: /employer/dashboard/
Disallow: /unauthorized/

# Allow job pages for better SEO
Allow: /jobs/
Allow: /companies/

# Sitemap location
Sitemap: ${baseUrl}/sitemap.xml

# Crawl delay (optional)
Crawl-delay: 1`

  return new NextResponse(robotsTxt, {
    headers: {
      'Content-Type': 'text/plain',
      'Cache-Control': 'public, max-age=86400, s-maxage=86400', // Cache for 24 hours
    },
  })
}