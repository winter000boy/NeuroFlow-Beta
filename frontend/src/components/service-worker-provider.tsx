'use client'

import { useEffect } from 'react'
import { registerServiceWorker, showUpdateNotification } from '@/lib/service-worker'
import { toast } from 'sonner'

export function ServiceWorkerProvider() {
  useEffect(() => {
    // Register service worker for offline functionality and caching
    registerServiceWorker({
      onUpdate: (registration) => {
        toast.info('App update available', {
          description: 'A new version is ready. Refresh to update.',
          action: {
            label: 'Refresh',
            onClick: () => {
              if (registration.waiting) {
                registration.waiting.postMessage({ type: 'SKIP_WAITING' })
              }
            },
          },
          duration: 10000,
        })
      },
      onSuccess: (registration) => {
        console.log('App is ready for offline use')
        
        // Show offline capability notification
        toast.success('App ready for offline use', {
          description: 'You can now use the app even without internet connection.',
          duration: 5000,
        })
      },
      onError: (error) => {
        console.error('Service worker registration failed:', error)
      },
    })

    // Preload critical resources
    const preloadCriticalResources = async () => {
      if ('serviceWorker' in navigator) {
        try {
          const registration = await navigator.serviceWorker.ready
          if (registration.active) {
            registration.active.postMessage({
              type: 'CACHE_URLS',
              urls: [
                '/api/jobs/featured',
                '/api/jobs/recent',
                '/jobs',
                '/login',
                '/register',
              ],
            })
          }
        } catch (error) {
          console.error('Failed to preload resources:', error)
        }
      }
    }

    // Preload after a short delay to not block initial render
    const timer = setTimeout(preloadCriticalResources, 2000)

    return () => clearTimeout(timer)
  }, [])

  return null
}