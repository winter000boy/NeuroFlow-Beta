'use client'

// Service Worker registration and management utilities

export interface ServiceWorkerConfig {
  onUpdate?: (registration: ServiceWorkerRegistration) => void
  onSuccess?: (registration: ServiceWorkerRegistration) => void
  onError?: (error: Error) => void
}

export function registerServiceWorker(config?: ServiceWorkerConfig) {
  if (typeof window === 'undefined' || !('serviceWorker' in navigator)) {
    console.log('Service Worker not supported')
    return
  }

  window.addEventListener('load', async () => {
    try {
      const registration = await navigator.serviceWorker.register('/sw.js', {
        scope: '/',
      })

      console.log('Service Worker registered successfully:', registration)

      // Handle updates
      registration.addEventListener('updatefound', () => {
        const newWorker = registration.installing
        if (!newWorker) return

        newWorker.addEventListener('statechange', () => {
          if (newWorker.state === 'installed') {
            if (navigator.serviceWorker.controller) {
              // New content is available
              console.log('New content available, please refresh')
              config?.onUpdate?.(registration)
            } else {
              // Content is cached for offline use
              console.log('Content is cached for offline use')
              config?.onSuccess?.(registration)
            }
          }
        })
      })

      // Handle controller change
      navigator.serviceWorker.addEventListener('controllerchange', () => {
        window.location.reload()
      })

    } catch (error) {
      console.error('Service Worker registration failed:', error)
      config?.onError?.(error as Error)
    }
  })
}

export function unregisterServiceWorker() {
  if (typeof window === 'undefined' || !('serviceWorker' in navigator)) {
    return
  }

  navigator.serviceWorker.ready
    .then((registration) => {
      registration.unregister()
      console.log('Service Worker unregistered')
    })
    .catch((error) => {
      console.error('Service Worker unregistration failed:', error)
    })
}

// Cache management utilities
export class CacheManager {
  static async preloadCriticalResources(urls: string[]) {
    if (!('serviceWorker' in navigator)) return

    const registration = await navigator.serviceWorker.ready
    if (registration.active) {
      registration.active.postMessage({
        type: 'CACHE_URLS',
        urls,
      })
    }
  }

  static async clearCache() {
    if (!('serviceWorker' in navigator)) return

    const registration = await navigator.serviceWorker.ready
    if (registration.active) {
      registration.active.postMessage({
        type: 'CLEAR_CACHE',
      })
    }
  }

  static async getCacheSize(): Promise<number> {
    if (!('caches' in window)) return 0

    try {
      const cacheNames = await caches.keys()
      let totalSize = 0

      for (const cacheName of cacheNames) {
        const cache = await caches.open(cacheName)
        const requests = await cache.keys()
        
        for (const request of requests) {
          const response = await cache.match(request)
          if (response) {
            const blob = await response.blob()
            totalSize += blob.size
          }
        }
      }

      return totalSize
    } catch (error) {
      console.error('Failed to calculate cache size:', error)
      return 0
    }
  }
}

// Network status utilities
export class NetworkManager {
  private static listeners: Array<(online: boolean) => void> = []

  static isOnline(): boolean {
    return typeof navigator !== 'undefined' ? navigator.onLine : true
  }

  static addListener(callback: (online: boolean) => void) {
    this.listeners.push(callback)
    
    if (typeof window !== 'undefined') {
      window.addEventListener('online', () => callback(true))
      window.addEventListener('offline', () => callback(false))
    }
  }

  static removeListener(callback: (online: boolean) => void) {
    const index = this.listeners.indexOf(callback)
    if (index > -1) {
      this.listeners.splice(index, 1)
    }
  }
}

// Push notification utilities
export class NotificationManager {
  static async requestPermission(): Promise<NotificationPermission> {
    if (!('Notification' in window)) {
      throw new Error('Notifications not supported')
    }

    if (Notification.permission === 'granted') {
      return 'granted'
    }

    if (Notification.permission === 'denied') {
      return 'denied'
    }

    return await Notification.requestPermission()
  }

  static async subscribeToPush(): Promise<PushSubscription | null> {
    if (!('serviceWorker' in navigator) || !('PushManager' in window)) {
      console.log('Push messaging not supported')
      return null
    }

    try {
      const registration = await navigator.serviceWorker.ready
      const subscription = await registration.pushManager.subscribe({
        userVisibleOnly: true,
        applicationServerKey: process.env.NEXT_PUBLIC_VAPID_PUBLIC_KEY,
      })

      return subscription
    } catch (error) {
      console.error('Failed to subscribe to push notifications:', error)
      return null
    }
  }

  static async unsubscribeFromPush(): Promise<boolean> {
    if (!('serviceWorker' in navigator)) return false

    try {
      const registration = await navigator.serviceWorker.ready
      const subscription = await registration.pushManager.getSubscription()
      
      if (subscription) {
        return await subscription.unsubscribe()
      }
      
      return true
    } catch (error) {
      console.error('Failed to unsubscribe from push notifications:', error)
      return false
    }
  }
}

// Performance monitoring
export class PerformanceMonitor {
  static measurePageLoad() {
    if (typeof window === 'undefined' || !window.performance) return

    window.addEventListener('load', () => {
      const navigation = performance.getEntriesByType('navigation')[0] as PerformanceNavigationTiming
      
      const metrics = {
        dns: navigation.domainLookupEnd - navigation.domainLookupStart,
        tcp: navigation.connectEnd - navigation.connectStart,
        ttfb: navigation.responseStart - navigation.requestStart,
        download: navigation.responseEnd - navigation.responseStart,
        domParse: navigation.domContentLoadedEventEnd - navigation.domContentLoadedEventStart,
        domReady: navigation.domContentLoadedEventEnd - navigation.fetchStart,
        load: navigation.loadEventEnd - navigation.fetchStart,
      }

      console.log('Performance metrics:', metrics)
      
      // Send to analytics if needed
      if (typeof window !== 'undefined' && 'gtag' in window) {
        (window as any).gtag('event', 'page_load_metrics', {
          custom_map: { metric_1: 'load_time' },
          metric_1: metrics.load,
        })
      }
    })
  }

  static measureResourceLoad() {
    if (typeof window === 'undefined' || !window.performance) return

    const observer = new PerformanceObserver((list) => {
      for (const entry of list.getEntries()) {
        if (entry.entryType === 'resource') {
          const resource = entry as PerformanceResourceTiming
          
          // Log slow resources
          if (resource.duration > 1000) {
            console.warn('Slow resource:', resource.name, `${resource.duration}ms`)
          }
        }
      }
    })

    observer.observe({ entryTypes: ['resource'] })
  }
}

// Update notification component
export function showUpdateNotification(registration: ServiceWorkerRegistration) {
  // This would typically integrate with your toast/notification system
  const updateAvailable = confirm(
    'A new version of the app is available. Would you like to update now?'
  )

  if (updateAvailable && registration.waiting) {
    registration.waiting.postMessage({ type: 'SKIP_WAITING' })
  }
}