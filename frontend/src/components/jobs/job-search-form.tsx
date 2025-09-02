'use client'

import { useState } from 'react'
import { Search, MapPin, DollarSign, Filter, X } from 'lucide-react'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select'
import { Card, CardContent } from '@/components/ui/card'
import { JobSearchFilters } from '@/types/job'

interface JobSearchFormProps {
  onSearch: (filters: JobSearchFilters) => void
  initialFilters?: JobSearchFilters
  loading?: boolean
}

const JOB_TYPES = [
  { value: 'FULL_TIME', label: 'Full Time' },
  { value: 'PART_TIME', label: 'Part Time' },
  { value: 'CONTRACT', label: 'Contract' },
  { value: 'REMOTE', label: 'Remote' },
]

const SORT_OPTIONS = [
  { value: 'createdAt-desc', label: 'Newest First' },
  { value: 'createdAt-asc', label: 'Oldest First' },
  { value: 'salary-desc', label: 'Highest Salary' },
  { value: 'salary-asc', label: 'Lowest Salary' },
  { value: 'title-asc', label: 'Title A-Z' },
  { value: 'title-desc', label: 'Title Z-A' },
]

export function JobSearchForm({ onSearch, initialFilters = {}, loading = false }: JobSearchFormProps) {
  const [filters, setFilters] = useState<JobSearchFilters>({
    search: '',
    location: '',
    jobType: '',
    minSalary: undefined,
    maxSalary: undefined,
    sortBy: 'createdAt',
    sortOrder: 'desc',
    ...initialFilters
  })
  const [showAdvanced, setShowAdvanced] = useState(false)

  const handleInputChange = (field: keyof JobSearchFilters, value: string | number | undefined) => {
    setFilters(prev => ({
      ...prev,
      [field]: value === '' ? undefined : value
    }))
  }

  const handleSortChange = (value: string) => {
    const [sortBy, sortOrder] = value.split('-')
    setFilters(prev => ({
      ...prev,
      sortBy: sortBy as JobSearchFilters['sortBy'],
      sortOrder: sortOrder as JobSearchFilters['sortOrder']
    }))
  }

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault()
    onSearch({ ...filters, page: 0 })
  }

  const handleReset = () => {
    const resetFilters: JobSearchFilters = {
      search: '',
      location: '',
      jobType: '',
      minSalary: undefined,
      maxSalary: undefined,
      sortBy: 'createdAt',
      sortOrder: 'desc',
      page: 0
    }
    setFilters(resetFilters)
    onSearch(resetFilters)
  }

  const hasActiveFilters = filters.search || filters.location || filters.jobType || 
                          filters.minSalary || filters.maxSalary

  return (
    <Card className="w-full">
      <CardContent className="p-6">
        <form onSubmit={handleSubmit} className="space-y-4">
          {/* Main search row */}
          <div className="flex flex-col md:flex-row gap-4">
            <div className="flex-1">
              <div className="relative">
                <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-muted-foreground h-4 w-4" />
                <Input
                  placeholder="Search jobs, companies, or keywords..."
                  value={filters.search || ''}
                  onChange={(e) => handleInputChange('search', e.target.value)}
                  className="pl-10"
                />
              </div>
            </div>
            
            <div className="md:w-64">
              <div className="relative">
                <MapPin className="absolute left-3 top-1/2 transform -translate-y-1/2 text-muted-foreground h-4 w-4" />
                <Input
                  placeholder="Location"
                  value={filters.location || ''}
                  onChange={(e) => handleInputChange('location', e.target.value)}
                  className="pl-10"
                />
              </div>
            </div>

            <div className="flex gap-2">
              <Button type="submit" disabled={loading} className="px-6">
                {loading ? 'Searching...' : 'Search'}
              </Button>
              
              <Button
                type="button"
                variant="outline"
                onClick={() => setShowAdvanced(!showAdvanced)}
                className="px-3"
              >
                <Filter className="h-4 w-4" />
              </Button>
            </div>
          </div>

          {/* Advanced filters */}
          {showAdvanced && (
            <div className="border-t pt-4 space-y-4">
              <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                <div>
                  <Label htmlFor="jobType">Job Type</Label>
                  <Select
                    value={filters.jobType || ''}
                    onValueChange={(value) => handleInputChange('jobType', value)}
                  >
                    <SelectTrigger>
                      <SelectValue placeholder="Any type" />
                    </SelectTrigger>
                    <SelectContent>
                      <SelectItem value="">Any type</SelectItem>
                      {JOB_TYPES.map((type) => (
                        <SelectItem key={type.value} value={type.value}>
                          {type.label}
                        </SelectItem>
                      ))}
                    </SelectContent>
                  </Select>
                </div>

                <div>
                  <Label htmlFor="minSalary">Min Salary</Label>
                  <div className="relative">
                    <DollarSign className="absolute left-3 top-1/2 transform -translate-y-1/2 text-muted-foreground h-4 w-4" />
                    <Input
                      id="minSalary"
                      type="number"
                      placeholder="0"
                      value={filters.minSalary || ''}
                      onChange={(e) => handleInputChange('minSalary', e.target.value ? Number(e.target.value) : undefined)}
                      className="pl-10"
                    />
                  </div>
                </div>

                <div>
                  <Label htmlFor="maxSalary">Max Salary</Label>
                  <div className="relative">
                    <DollarSign className="absolute left-3 top-1/2 transform -translate-y-1/2 text-muted-foreground h-4 w-4" />
                    <Input
                      id="maxSalary"
                      type="number"
                      placeholder="No limit"
                      value={filters.maxSalary || ''}
                      onChange={(e) => handleInputChange('maxSalary', e.target.value ? Number(e.target.value) : undefined)}
                      className="pl-10"
                    />
                  </div>
                </div>
              </div>

              <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
                <div className="flex items-center gap-2">
                  <Label htmlFor="sort">Sort by:</Label>
                  <Select
                    value={`${filters.sortBy}-${filters.sortOrder}`}
                    onValueChange={handleSortChange}
                  >
                    <SelectTrigger className="w-48">
                      <SelectValue />
                    </SelectTrigger>
                    <SelectContent>
                      {SORT_OPTIONS.map((option) => (
                        <SelectItem key={option.value} value={option.value}>
                          {option.label}
                        </SelectItem>
                      ))}
                    </SelectContent>
                  </Select>
                </div>

                {hasActiveFilters && (
                  <Button
                    type="button"
                    variant="ghost"
                    onClick={handleReset}
                    className="text-muted-foreground hover:text-foreground"
                  >
                    <X className="h-4 w-4 mr-2" />
                    Clear filters
                  </Button>
                )}
              </div>
            </div>
          )}
        </form>
      </CardContent>
    </Card>
  )
}