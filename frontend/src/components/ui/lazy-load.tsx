'use client'

import { ReactNode, useState } from 'react'
import { useInView } from 'react-intersection-observer'
import { cn } from '@/lib/utils'

interface LazyLoadProps {
  children: ReactNode
  className?: string
  threshold?: number
  rootMargin?: string
  triggerOnce?: boolean
  fallback?: ReactNode
  onInView?: () => void
}

export function LazyLoad({
  children,
  className,
  threshold = 0.1,
  rootMargin = '50px',
  triggerOnce = true,
  fallback,
  onInView,
}: LazyLoadProps) {
  const [hasLoaded, setHasLoaded] = useState(false)
  
  const { ref, inView } = useInView({
    threshold,
    rootMargin,
    triggerOnce,
    onChange: (inView) => {
      if (inView && !hasLoaded) {
        setHasLoaded(true)
        onInView?.()
      }
    },
  })

  const shouldRender = inView || hasLoaded

  return (
    <div ref={ref} className={cn('min-h-[1px]', className)}>
      {shouldRender ? children : (fallback || <LazyLoadSkeleton />)}
    </div>
  )
}

function LazyLoadSkeleton() {
  return (
    <div className="animate-pulse">
      <div className="bg-gray-200 dark:bg-gray-700 rounded-lg h-48 w-full" />
    </div>
  )
}

// Specialized lazy loading for job cards
export function LazyJobCard({ children, className }: { children: ReactNode; className?: string }) {
  return (
    <LazyLoad
      className={className}
      threshold={0.1}
      rootMargin="100px"
      fallback={<JobCardSkeleton />}
    >
      {children}
    </LazyLoad>
  )
}

function JobCardSkeleton() {
  return (
    <div className="bg-white dark:bg-gray-800 rounded-lg border border-gray-200 dark:border-gray-700 p-6 animate-pulse">
      <div className="space-y-4">
        {/* Title */}
        <div className="h-6 bg-gray-200 dark:bg-gray-700 rounded w-3/4" />
        
        {/* Company and location */}
        <div className="flex items-center space-x-4">
          <div className="h-4 bg-gray-200 dark:bg-gray-700 rounded w-1/3" />
          <div className="h-4 bg-gray-200 dark:bg-gray-700 rounded w-1/4" />
        </div>
        
        {/* Description */}
        <div className="space-y-2">
          <div className="h-4 bg-gray-200 dark:bg-gray-700 rounded w-full" />
          <div className="h-4 bg-gray-200 dark:bg-gray-700 rounded w-5/6" />
          <div className="h-4 bg-gray-200 dark:bg-gray-700 rounded w-4/6" />
        </div>
        
        {/* Tags */}
        <div className="flex space-x-2">
          <div className="h-6 bg-gray-200 dark:bg-gray-700 rounded-full w-16" />
          <div className="h-6 bg-gray-200 dark:bg-gray-700 rounded-full w-20" />
          <div className="h-6 bg-gray-200 dark:bg-gray-700 rounded-full w-14" />
        </div>
        
        {/* Footer */}
        <div className="flex justify-between items-center pt-4">
          <div className="h-4 bg-gray-200 dark:bg-gray-700 rounded w-1/4" />
          <div className="h-8 bg-gray-200 dark:bg-gray-700 rounded w-20" />
        </div>
      </div>
    </div>
  )
}