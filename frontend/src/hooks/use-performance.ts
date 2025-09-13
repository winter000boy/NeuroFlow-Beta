'use client'

import { useEffect, useState, useCallback } from 'react'
import { NetworkManager, PerformanceMonitor } from '@/lib/service-worker'

interface PerformanceMetrics {
  isOnline: boolean
  connectionType?: string
  loadTime?: number
  cacheHitRate?: number
  memoryUsage?: number
}

export function usePerformance() {
  const [metrics, setMetrics] = useState<PerformanceMetrics>({
    isOnline: true,
  })

  const [isLoading, setIsLoading] = useState(false)

  useEffect(() => {
    // Monitor network status
    const handleNetworkChange = (online: boolean) => {
      setMetrics(prev => ({ ...prev, isOnline: online }))
    }

    NetworkManager.addListener(handleNetworkChange)
    setMetrics(prev => ({ ...prev, isOnline: NetworkManager.isOnline() }))

    // Monitor connection type
    if ('connection' in navigator) {
      const connection = (navigator as any).connection
      setMetrics(prev => ({ 
        ...prev, 
        connectionType: connection?.effectiveType || 'unknown' 
      }))

      const handleConnectionChange = () => {
        setMetrics(prev => ({ 
          ...prev, 
          connectionType: connection?.effectiveType || 'unknown' 
        }))
      }

      connection?.addEventListener('change', handleConnectionChange)

      return () => {
        NetworkManager.removeListener(handleNetworkChange)
        connection?.removeEventListener('change', handleConnectionChange)
      }
    }

    return () => {
      NetworkManager.removeListener(handleNetworkChange)
    }
  }, [])

  useEffect(() => {
    // Measure page performance
    PerformanceMonitor.measurePageLoad()
    PerformanceMonitor.measureResourceLoad()

    // Monitor memory usage
    const measureMemory = () => {
      if ('memory' in performance) {
        const memory = (performance as any).memory
        setMetrics(prev => ({
          ...prev,
          memoryUsage: memory.usedJSHeapSize / memory.jsHeapSizeLimit
        }))
      }
    }

    measureMemory()
    const memoryInterval = setInterval(measureMemory, 30000) // Every 30 seconds

    return () => clearInterval(memoryInterval)
  }, [])

  const measureApiPerformance = useCallback(async (apiCall: () => Promise<any>) => {
    const startTime = performance.now()
    setIsLoading(true)

    try {
      const result = await apiCall()
      const endTime = performance.now()
      const duration = endTime - startTime

      // Log slow API calls
      if (duration > 2000) {
        console.warn(`Slow API call: ${duration}ms`)
      }

      setMetrics(prev => ({ ...prev, loadTime: duration }))
      return result
    } finally {
      setIsLoading(false)
    }
  }, [])

  const preloadResources = useCallback(async (urls: string[]) => {
    if ('serviceWorker' in navigator) {
      const registration = await navigator.serviceWorker.ready
      if (registration.active) {
        registration.active.postMessage({
          type: 'CACHE_URLS',
          urls,
        })
      }
    }
  }, [])

  return {
    metrics,
    isLoading,
    measureApiPerformance,
    preloadResources,
  }
}

// Hook for monitoring component render performance
export function useRenderPerformance(componentName: string) {
  useEffect(() => {
    const startTime = performance.now()

    return () => {
      const endTime = performance.now()
      const renderTime = endTime - startTime

      if (renderTime > 16) { // More than one frame (60fps)
        console.warn(`Slow render in ${componentName}: ${renderTime}ms`)
      }
    }
  })
}

// Hook for lazy loading with performance tracking
export function useLazyLoad<T>(
  loadFunction: () => Promise<T>,
  dependencies: any[] = []
) {
  const [data, setData] = useState<T | null>(null)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<Error | null>(null)

  const load = useCallback(async () => {
    if (loading || data) return

    setLoading(true)
    setError(null)

    const startTime = performance.now()

    try {
      const result = await loadFunction()
      setData(result)

      const endTime = performance.now()
      const loadTime = endTime - startTime

      // Track loading performance
      if (loadTime > 1000) {
        console.warn(`Slow lazy load: ${loadTime}ms`)
      }
    } catch (err) {
      setError(err as Error)
    } finally {
      setLoading(false)
    }
  }, [loadFunction, loading, data])

  useEffect(() => {
    load()
  }, dependencies)

  return { data, loading, error, reload: load }
}

// Hook for image preloading
export function useImagePreload(src: string) {
  const [loaded, setLoaded] = useState(false)
  const [error, setError] = useState(false)

  useEffect(() => {
    if (!src) return

    const img = new Image()
    
    img.onload = () => setLoaded(true)
    img.onerror = () => setError(true)
    
    img.src = src

    return () => {
      img.onload = null
      img.onerror = null
    }
  }, [src])

  return { loaded, error }
}