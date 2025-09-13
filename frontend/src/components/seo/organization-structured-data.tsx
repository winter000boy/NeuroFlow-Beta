'use client'

interface OrganizationStructuredDataProps {
  name: string
  description?: string
  url?: string
  logo?: string
  address?: {
    streetAddress?: string
    addressLocality?: string
    addressRegion?: string
    postalCode?: string
    addressCountry?: string
  }
  contactPoint?: {
    telephone?: string
    email?: string
    contactType?: string
  }
  sameAs?: string[]
}

export function OrganizationStructuredData({
  name,
  description,
  url,
  logo,
  address,
  contactPoint,
  sameAs = [],
}: OrganizationStructuredDataProps) {
  const baseUrl = process.env.NEXT_PUBLIC_SITE_URL || 'https://jobapp.com'
  
  const structuredData = {
    '@context': 'https://schema.org',
    '@type': 'Organization',
    name,
    ...(description && { description }),
    url: url || baseUrl,
    ...(logo && { 
      logo: {
        '@type': 'ImageObject',
        url: logo.startsWith('http') ? logo : `${baseUrl}${logo}`,
      }
    }),
    ...(address && {
      address: {
        '@type': 'PostalAddress',
        ...address,
      },
    }),
    ...(contactPoint && {
      contactPoint: {
        '@type': 'ContactPoint',
        ...contactPoint,
      },
    }),
    ...(sameAs.length > 0 && { sameAs }),
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