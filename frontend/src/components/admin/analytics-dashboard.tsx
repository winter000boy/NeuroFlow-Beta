'use client'

import { useState } from 'react'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { Badge } from '@/components/ui/badge'
import { Button } from '@/components/ui/button'
import { 
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select'
import { 
  BarChart3, 
  TrendingUp, 
  TrendingDown, 
  Users, 
  Briefcase, 
  FileText,
  Target,
  Calendar,
  Download,
  RefreshCw
} from 'lucide-react'

interface MetricCard {
  title: string
  value: string | number
  change: number
  changeType: 'increase' | 'decrease'
  icon: React.ComponentType<{ className?: string }>
  description: string
}

interface ChartData {
  name: string
  value: number
  color: string
}

export function AnalyticsDashboard() {
  const [timeRange, setTimeRange] = useState('30d')
  const [isLoading, setIsLoading] = useState(false)

  // Mock analytics data
  const metrics: MetricCard[] = [
    {
      title: 'Total Users',
      value: '2,847',
      change: 12.5,
      changeType: 'increase',
      icon: Users,
      description: 'Active platform users'
    },
    {
      title: 'Job Postings',
      value: '456',
      change: 8.2,
      changeType: 'increase',
      icon: Briefcase,
      description: 'Active job listings'
    },
    {
      title: 'Applications',
      value: '12,890',
      change: 23.1,
      changeType: 'increase',
      icon: FileText,
      description: 'Total applications submitted'
    },
    {
      title: 'Success Rate',
      value: '18.2%',
      change: -2.1,
      changeType: 'decrease',
      icon: Target,
      description: 'Application to hire ratio'
    }
  ]

  const userGrowthData: ChartData[] = [
    { name: 'Jan', value: 400, color: '#8884d8' },
    { name: 'Feb', value: 300, color: '#82ca9d' },
    { name: 'Mar', value: 500, color: '#ffc658' },
    { name: 'Apr', value: 280, color: '#ff7300' },
    { name: 'May', value: 590, color: '#8dd1e1' },
    { name: 'Jun', value: 320, color: '#d084d0' }
  ]

  const jobCategoryData: ChartData[] = [
    { name: 'Technology', value: 35, color: '#0088FE' },
    { name: 'Marketing', value: 25, color: '#00C49F' },
    { name: 'Sales', value: 20, color: '#FFBB28' },
    { name: 'Design', value: 12, color: '#FF8042' },
    { name: 'Other', value: 8, color: '#8884D8' }
  ]

  const recentActivity = [
    {
      id: 1,
      type: 'user_registration',
      description: 'New candidate registered',
      user: 'John Doe',
      timestamp: '2 minutes ago',
      status: 'success'
    },
    {
      id: 2,
      type: 'job_posting',
      description: 'New job posted',
      user: 'TechCorp Inc.',
      timestamp: '5 minutes ago',
      status: 'pending'
    },
    {
      id: 3,
      type: 'application',
      description: 'Job application submitted',
      user: 'Jane Smith',
      timestamp: '8 minutes ago',
      status: 'success'
    },
    {
      id: 4,
      type: 'user_blocked',
      description: 'User account blocked',
      user: 'Spam User',
      timestamp: '15 minutes ago',
      status: 'warning'
    }
  ]

  const handleRefresh = async () => {
    setIsLoading(true)
    // Simulate API call
    await new Promise(resolve => setTimeout(resolve, 1000))
    setIsLoading(false)
  }

  const handleExport = () => {
    // Simulate export functionality
    console.log('Exporting analytics data...')
  }

  const getChangeIcon = (changeType: 'increase' | 'decrease') => {
    return changeType === 'increase' ? (
      <TrendingUp className="h-4 w-4 text-green-600" />
    ) : (
      <TrendingDown className="h-4 w-4 text-red-600" />
    )
  }

  const getActivityStatusColor = (status: string) => {
    switch (status) {
      case 'success':
        return 'bg-green-500'
      case 'warning':
        return 'bg-yellow-500'
      case 'pending':
        return 'bg-blue-500'
      default:
        return 'bg-gray-500'
    }
  }

  return (
    <div className="space-y-6">
      {/* Header with Controls */}
      <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
        <div>
          <h2 className="text-2xl font-bold tracking-tight">Analytics Overview</h2>
          <p className="text-muted-foreground">
            Platform performance metrics and insights
          </p>
        </div>
        
        <div className="flex items-center space-x-2">
          <Select value={timeRange} onValueChange={setTimeRange}>
            <SelectTrigger className="w-[180px]">
              <SelectValue placeholder="Select time range" />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="7d">Last 7 days</SelectItem>
              <SelectItem value="30d">Last 30 days</SelectItem>
              <SelectItem value="90d">Last 90 days</SelectItem>
              <SelectItem value="1y">Last year</SelectItem>
            </SelectContent>
          </Select>
          
          <Button variant="outline" onClick={handleRefresh} disabled={isLoading}>
            <RefreshCw className={`h-4 w-4 mr-2 ${isLoading ? 'animate-spin' : ''}`} />
            Refresh
          </Button>
          
          <Button variant="outline" onClick={handleExport}>
            <Download className="h-4 w-4 mr-2" />
            Export
          </Button>
        </div>
      </div>

      {/* Key Metrics */}
      <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
        {metrics.map((metric, index) => {
          const Icon = metric.icon
          return (
            <Card key={index}>
              <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                <CardTitle className="text-sm font-medium">{metric.title}</CardTitle>
                <Icon className="h-4 w-4 text-muted-foreground" />
              </CardHeader>
              <CardContent>
                <div className="text-2xl font-bold">{metric.value}</div>
                <div className="flex items-center space-x-1 text-xs text-muted-foreground">
                  {getChangeIcon(metric.changeType)}
                  <span className={metric.changeType === 'increase' ? 'text-green-600' : 'text-red-600'}>
                    {Math.abs(metric.change)}%
                  </span>
                  <span>from last period</span>
                </div>
                <p className="text-xs text-muted-foreground mt-1">
                  {metric.description}
                </p>
              </CardContent>
            </Card>
          )
        })}
      </div>

      {/* Charts and Detailed Analytics */}
      <div className="grid gap-4 md:grid-cols-2">
        {/* User Growth Chart */}
        <Card>
          <CardHeader>
            <CardTitle className="flex items-center space-x-2">
              <BarChart3 className="h-5 w-5" />
              <span>User Growth</span>
            </CardTitle>
            <CardDescription>
              Monthly user registration trends
            </CardDescription>
          </CardHeader>
          <CardContent>
            <div className="space-y-4">
              {userGrowthData.map((item, index) => (
                <div key={index} className="flex items-center space-x-4">
                  <div className="w-12 text-sm font-medium">{item.name}</div>
                  <div className="flex-1">
                    <div className="h-4 bg-muted rounded-full overflow-hidden">
                      <div 
                        className="h-full bg-primary transition-all duration-300"
                        style={{ 
                          width: `${(item.value / Math.max(...userGrowthData.map(d => d.value))) * 100}%`,
                          backgroundColor: item.color 
                        }}
                      />
                    </div>
                  </div>
                  <div className="w-12 text-sm text-right">{item.value}</div>
                </div>
              ))}
            </div>
          </CardContent>
        </Card>

        {/* Job Categories */}
        <Card>
          <CardHeader>
            <CardTitle>Job Categories</CardTitle>
            <CardDescription>
              Distribution of job postings by category
            </CardDescription>
          </CardHeader>
          <CardContent>
            <div className="space-y-4">
              {jobCategoryData.map((item, index) => (
                <div key={index} className="flex items-center justify-between">
                  <div className="flex items-center space-x-3">
                    <div 
                      className="w-3 h-3 rounded-full"
                      style={{ backgroundColor: item.color }}
                    />
                    <span className="text-sm font-medium">{item.name}</span>
                  </div>
                  <div className="flex items-center space-x-2">
                    <span className="text-sm text-muted-foreground">{item.value}%</span>
                    <div className="w-16 h-2 bg-muted rounded-full overflow-hidden">
                      <div 
                        className="h-full transition-all duration-300"
                        style={{ 
                          width: `${item.value}%`,
                          backgroundColor: item.color 
                        }}
                      />
                    </div>
                  </div>
                </div>
              ))}
            </div>
          </CardContent>
        </Card>
      </div>

      {/* Recent Activity */}
      <Card>
        <CardHeader>
          <CardTitle className="flex items-center space-x-2">
            <Calendar className="h-5 w-5" />
            <span>Recent Activity</span>
          </CardTitle>
          <CardDescription>
            Latest platform activities and events
          </CardDescription>
        </CardHeader>
        <CardContent>
          <div className="space-y-4">
            {recentActivity.map((activity) => (
              <div key={activity.id} className="flex items-center space-x-4 p-3 rounded-lg border">
                <div className={`w-2 h-2 rounded-full ${getActivityStatusColor(activity.status)}`} />
                <div className="flex-1">
                  <p className="text-sm font-medium">{activity.description}</p>
                  <p className="text-xs text-muted-foreground">
                    {activity.user} • {activity.timestamp}
                  </p>
                </div>
                <Badge variant="secondary" className="text-xs">
                  {activity.type.replace('_', ' ')}
                </Badge>
              </div>
            ))}
          </div>
        </CardContent>
      </Card>

      {/* Performance Summary */}
      <div className="grid gap-4 md:grid-cols-3">
        <Card>
          <CardHeader>
            <CardTitle className="text-lg">Platform Health</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="space-y-3">
              <div className="flex justify-between items-center">
                <span className="text-sm">System Uptime</span>
                <Badge variant="secondary" className="bg-green-100 text-green-800">99.9%</Badge>
              </div>
              <div className="flex justify-between items-center">
                <span className="text-sm">Response Time</span>
                <Badge variant="secondary">&lt; 200ms</Badge>
              </div>
              <div className="flex justify-between items-center">
                <span className="text-sm">Error Rate</span>
                <Badge variant="secondary" className="bg-green-100 text-green-800">0.01%</Badge>
              </div>
            </div>
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <CardTitle className="text-lg">User Engagement</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="space-y-3">
              <div className="flex justify-between items-center">
                <span className="text-sm">Daily Active Users</span>
                <span className="font-medium">1,247</span>
              </div>
              <div className="flex justify-between items-center">
                <span className="text-sm">Avg. Session Duration</span>
                <span className="font-medium">12m 34s</span>
              </div>
              <div className="flex justify-between items-center">
                <span className="text-sm">Bounce Rate</span>
                <span className="font-medium">23.4%</span>
              </div>
            </div>
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <CardTitle className="text-lg">Conversion Metrics</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="space-y-3">
              <div className="flex justify-between items-center">
                <span className="text-sm">Registration Rate</span>
                <span className="font-medium">3.2%</span>
              </div>
              <div className="flex justify-between items-center">
                <span className="text-sm">Job Application Rate</span>
                <span className="font-medium">45.6%</span>
              </div>
              <div className="flex justify-between items-center">
                <span className="text-sm">Hire Success Rate</span>
                <span className="font-medium">18.2%</span>
              </div>
            </div>
          </CardContent>
        </Card>
      </div>
    </div>
  )
}