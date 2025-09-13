'use client'

import { useEffect, useState } from 'react'
import { usePerformance } from '@/hooks/use-performance'
import { CacheManager } from '@/lib/service-worker'

interface PerformanceMonitorProps {
  showInDevelopment?: boolean
}

export function PerformanceMonitor({ showInDevelopment = true }: PerformanceMonitorProps) {
  const { metrics } = usePerformance()
  const [cacheSize, setCacheSize] = useState<number>(0)
  const [showMonitor, setShowMonitor] = useState(false)

  useEffect(() => {
    // Only show in development or when explicitly enabled
    if (process.env.NODE_ENV === 'development' && showInDevelopment) {
      setShowMonitor(true)
    }
  }, [showInDevelopment])

  useEffect(() => {
    // Update cache size periodically
    const updateCacheSize = async () => {
      const size = await CacheManager.getCacheSize()
      setCacheSize(size)
    }

    updateCacheSize()
    const interval = setInterval(updateCacheSize, 10000) // Every 10 seconds

    return () => clearInterval(interval)
  }, [])

  if (!showMonitor) {
    return null
  }

  const formatBytes = (bytes: number) => {
    if (bytes === 0) return '0 Bytes'
    const k = 1024
    const sizes = ['Bytes', 'KB', 'MB', 'GB']
    const i = Math.floor(Math.log(bytes) / Math.log(k))
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
  }

  const getConnectionColor = (type?: string) => {
    switch (type) {
      case '4g': return 'text-green-500'
      case '3g': return 'text-yellow-500'
      case '2g': return 'text-red-500'
      case 'slow-2g': return 'text-red-700'
      default: return 'text-gray-500'
    }
  }

  return (
    <div className="fixed bottom-4 right-4 z-50 bg-black/80 text-white text-xs p-3 rounded-lg font-mono max-w-xs">
      <div className="space-y-1">
        <div className="font-semibold border-b border-gray-600 pb-1 mb-2">
          Performance Monitor
        </div>
        
        <div className="flex justify-between">
          <span>Network:</span>
          <span className={metrics.isOnline ? 'text-green-500' : 'text-red-500'}>
            {metrics.isOnline ? 'Online' : 'Offline'}
          </span>
        </div>
        
        {metrics.connectionType && (
          <div className="flex justify-between">
            <span>Connection:</span>
            <span className={getConnectionColor(metrics.connectionType)}>
              {metrics.connectionType.toUpperCase()}
            </span>
          </div>
        )}
        
        {metrics.loadTime && (
          <div className="flex justify-between">
            <span>Last API:</span>
            <span className={metrics.loadTime > 2000 ? 'text-red-500' : 'text-green-500'}>
              {Math.round(metrics.loadTime)}ms
            </span>
          </div>
        )}
        
        {metrics.memoryUsage && (
          <div className="flex justify-between">
            <span>Memory:</span>
            <span className={metrics.memoryUsage > 0.8 ? 'text-red-500' : 'text-green-500'}>
              {Math.round(metrics.memoryUsage * 100)}%
            </span>
          </div>
        )}
        
        <div className="flex justify-between">
          <span>Cache:</span>
          <span className="text-blue-500">
            {formatBytes(cacheSize)}
          </span>
        </div>
        
        <button
          onClick={() => setShowMonitor(false)}
          className="mt-2 text-xs text-gray-400 hover:text-white transition-colors"
        >
          Hide Monitor
        </button>
      </div>
    </div>
  )
}

// Development-only performance overlay
export function DevPerformanceOverlay() {
  if (process.env.NODE_ENV !== 'development') {
    return null
  }

  return <PerformanceMonitor />
}