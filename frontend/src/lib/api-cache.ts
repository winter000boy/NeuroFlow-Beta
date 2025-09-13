'use client'

// Client-side API caching utilities

interface CacheEntry<T> {
  data: T
  timestamp: number
  ttl: number
}

class APICache {
  private cache = new Map<string, CacheEntry<any>>()
  private readonly defaultTTL = 5 * 60 * 1000 // 5 minutes

  set<T>(key: string, data: T, ttl?: number): void {
    this.cache.set(key, {
      data,
      timestamp: Date.now(),
      ttl: ttl || this.defaultTTL,
    })
  }

  get<T>(key: string): T | null {
    const entry = this.cache.get(key)
    
    if (!entry) {
      return null
    }

    const isExpired = Date.now() - entry.timestamp > entry.ttl
    
    if (isExpired) {
      this.cache.delete(key)
      return null
    }

    return entry.data
  }

  has(key: string): boolean {
    const entry = this.cache.get(key)
    
    if (!entry) {
      return false
    }

    const isExpired = Date.now() - entry.timestamp > entry.ttl
    
    if (isExpired) {
      this.cache.delete(key)
      return false
    }

    return true
  }

  delete(key: string): void {
    this.cache.delete(key)
  }

  clear(): void {
    this.cache.clear()
  }

  // Clean up expired entries
  cleanup(): void {
    const now = Date.now()
    
    for (const [key, entry] of this.cache.entries()) {
      if (now - entry.timestamp > entry.ttl) {
        this.cache.delete(key)
      }
    }
  }

  // Get cache statistics
  getStats() {
    return {
      size: this.cache.size,
      keys: Array.from(this.cache.keys()),
    }
  }
}

// Global cache instance
export const apiCache = new APICache()

// Cache key generators
export const cacheKeys = {
  jobs: {
    search: (params: Record<string, any>) => 
      `jobs:search:${JSON.stringify(params)}`,
    detail: (id: string) => `jobs:detail:${id}`,
    featured: (page: number, size: number) => `jobs:featured:${page}:${size}`,
    recent: (page: number, size: number) => `jobs:recent:${page}:${size}`,
    byEmployer: (employerId: string, page: number, size: number) => 
      `jobs:employer:${employerId}:${page}:${size}`,
  },
  applications: {
    byCandidate: (candidateId: string) => `applications:candidate:${candidateId}`,
    status: (jobId: string, candidateId: string) => 
      `applications:status:${jobId}:${candidateId}`,
  },
  user: {
    profile: (userId: string) => `user:profile:${userId}`,
  },
}

// Cache TTL configurations (in milliseconds)
export const cacheTTL = {
  jobs: {
    search: 2 * 60 * 1000,      // 2 minutes
    detail: 10 * 60 * 1000,     // 10 minutes
    featured: 5 * 60 * 1000,    // 5 minutes
    recent: 3 * 60 * 1000,      // 3 minutes
  },
  applications: {
    list: 1 * 60 * 1000,       // 1 minute
    status: 30 * 1000,         // 30 seconds
  },
  user: {
    profile: 15 * 60 * 1000,   // 15 minutes
  },
}

// Cached API wrapper
export function withCache<T extends (...args: any[]) => Promise<any>>(
  fn: T,
  keyGenerator: (...args: Parameters<T>) => string,
  ttl?: number
): T {
  return (async (...args: Parameters<T>) => {
    const key = keyGenerator(...args)
    
    // Try to get from cache first
    const cached = apiCache.get(key)
    if (cached) {
      return cached
    }

    // If not in cache, call the function
    try {
      const result = await fn(...args)
      
      // Cache the result
      apiCache.set(key, result, ttl)
      
      return result
    } catch (error) {
      // Don't cache errors, but check if we have stale data
      const staleData = apiCache.get(key)
      if (staleData) {
        console.warn('Using stale data due to API error:', error)
        return staleData
      }
      
      throw error
    }
  }) as T
}

// Cache invalidation utilities
export const cacheInvalidation = {
  // Invalidate all job-related caches
  invalidateJobs(): void {
    const keys = Array.from(apiCache.getStats().keys)
    keys.forEach(key => {
      if (key.startsWith('jobs:')) {
        apiCache.delete(key)
      }
    })
  },

  // Invalidate specific job
  invalidateJob(jobId: string): void {
    apiCache.delete(cacheKeys.jobs.detail(jobId))
    
    // Also invalidate search results that might contain this job
    const keys = Array.from(apiCache.getStats().keys)
    keys.forEach(key => {
      if (key.startsWith('jobs:search:') || 
          key.startsWith('jobs:featured:') || 
          key.startsWith('jobs:recent:')) {
        apiCache.delete(key)
      }
    })
  },

  // Invalidate application-related caches
  invalidateApplications(candidateId?: string): void {
    const keys = Array.from(apiCache.getStats().keys)
    keys.forEach(key => {
      if (candidateId) {
        if (key.startsWith(`applications:candidate:${candidateId}`) ||
            key.includes(`:${candidateId}`)) {
          apiCache.delete(key)
        }
      } else if (key.startsWith('applications:')) {
        apiCache.delete(key)
      }
    })
  },

  // Invalidate user profile
  invalidateUserProfile(userId: string): void {
    apiCache.delete(cacheKeys.user.profile(userId))
  },
}

// Background cache cleanup
if (typeof window !== 'undefined') {
  // Clean up expired entries every 5 minutes
  setInterval(() => {
    apiCache.cleanup()
  }, 5 * 60 * 1000)

  // Clear cache on page unload to prevent memory leaks
  window.addEventListener('beforeunload', () => {
    apiCache.clear()
  })
}

// Cache warming utilities
export const cacheWarming = {
  // Preload critical job data
  async warmJobCache(): Promise<void> {
    try {
      // This would typically call your API service methods
      // For now, we'll just log the intent
      console.log('Warming job cache...')
      
      // Example: Preload featured jobs
      // await jobService.getFeaturedJobs(0, 10)
      
      // Example: Preload recent jobs
      // await jobService.getRecentJobs(0, 10)
    } catch (error) {
      console.error('Failed to warm job cache:', error)
    }
  },

  // Preload user-specific data
  async warmUserCache(userId: string): Promise<void> {
    try {
      console.log(`Warming user cache for ${userId}...`)
      
      // Example: Preload user profile
      // await userService.getProfile(userId)
      
      // Example: Preload user applications
      // await applicationService.getByCandidate(userId)
    } catch (error) {
      console.error('Failed to warm user cache:', error)
    }
  },
}