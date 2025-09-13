'use client'

import React, { useState, useEffect } from 'react'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Badge } from '@/components/ui/badge'
import { Textarea } from '@/components/ui/textarea'
import { 
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table'
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu'
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from '@/components/ui/dialog'
import { 
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select'
import { 
  Search, 
  MoreHorizontal, 
  Eye, 
  CheckCircle, 
  XCircle, 
  Flag,
  AlertTriangle,
  Clock,
  Briefcase,
  FileText,
  Building,
  User
} from 'lucide-react'

interface ContentItem {
  id: string
  type: 'job' | 'application'
  title: string
  company?: string
  candidate?: string
  status: 'pending' | 'approved' | 'rejected' | 'flagged'
  reportCount: number
  createdAt: string
  lastReported?: string
  content: string
  reportReasons: string[]
}

// Mock data
const mockContentItems: ContentItem[] = [
  {
    id: '1',
    type: 'job',
    title: 'Senior Software Engineer',
    company: 'TechCorp Inc.',
    status: 'pending',
    reportCount: 0,
    createdAt: '2024-02-01',
    content: 'We are looking for a senior software engineer with 5+ years of experience...',
    reportReasons: []
  },
  {
    id: '2',
    type: 'job',
    title: 'Marketing Manager - Suspicious Content',
    company: 'QuickMoney Ltd.',
    status: 'flagged',
    reportCount: 3,
    createdAt: '2024-01-28',
    lastReported: '2024-02-02',
    content: 'Make $5000 per week working from home! No experience needed...',
    reportReasons: ['Spam', 'Misleading content', 'Suspicious offer']
  },
  {
    id: '3',
    type: 'application',
    title: 'Application for Data Scientist',
    candidate: 'John Doe',
    company: 'DataCorp',
    status: 'approved',
    reportCount: 0,
    createdAt: '2024-01-30',
    content: 'I am very interested in this position and have relevant experience...',
    reportReasons: []
  },
  {
    id: '4',
    type: 'job',
    title: 'Frontend Developer',
    company: 'StartupCo',
    status: 'approved',
    reportCount: 0,
    createdAt: '2024-01-25',
    content: 'Join our dynamic team as a frontend developer. React experience required...',
    reportReasons: []
  },
  {
    id: '5',
    type: 'application',
    title: 'Application for Sales Manager',
    candidate: 'Jane Smith',
    company: 'SalesCorp',
    status: 'flagged',
    reportCount: 1,
    createdAt: '2024-01-29',
    lastReported: '2024-02-01',
    content: 'Inappropriate content in cover letter...',
    reportReasons: ['Inappropriate content']
  }
]

export function ContentModeration() {
  const [items, setItems] = useState<ContentItem[]>(mockContentItems)
  const [filteredItems, setFilteredItems] = useState<ContentItem[]>(mockContentItems)
  const [searchTerm, setSearchTerm] = useState('')
  const [typeFilter, setTypeFilter] = useState<string>('all')
  const [statusFilter, setStatusFilter] = useState<string>('all')
  const [selectedItem, setSelectedItem] = useState<ContentItem | null>(null)
  const [moderationNote, setModerationNote] = useState('')
  const [isLoading, setIsLoading] = useState(false)

  // Filter items based on search and filters
  useEffect(() => {
    let filtered = items

    if (searchTerm) {
      filtered = filtered.filter(item =>
        item.title.toLowerCase().includes(searchTerm.toLowerCase()) ||
        item.company?.toLowerCase().includes(searchTerm.toLowerCase()) ||
        item.candidate?.toLowerCase().includes(searchTerm.toLowerCase())
      )
    }

    if (typeFilter !== 'all') {
      filtered = filtered.filter(item => item.type === typeFilter)
    }

    if (statusFilter !== 'all') {
      filtered = filtered.filter(item => item.status === statusFilter)
    }

    setFilteredItems(filtered)
  }, [items, searchTerm, typeFilter, statusFilter])

  const handleModeration = async (itemId: string, action: 'approve' | 'reject') => {
    setIsLoading(true)
    
    // Simulate API call
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    setItems(prevItems =>
      prevItems.map(item => {
        if (item.id === itemId) {
          return { 
            ...item, 
            status: action === 'approve' ? 'approved' : 'rejected' as const
          }
        }
        return item
      })
    )
    
    setIsLoading(false)
    setSelectedItem(null)
    setModerationNote('')
  }

  const getStatusBadge = (status: ContentItem['status']) => {
    switch (status) {
      case 'approved':
        return (
          <Badge variant="secondary" className="bg-green-100 text-green-800">
            <CheckCircle className="h-3 w-3 mr-1" />
            Approved
          </Badge>
        )
      case 'rejected':
        return (
          <Badge variant="secondary" className="bg-red-100 text-red-800">
            <XCircle className="h-3 w-3 mr-1" />
            Rejected
          </Badge>
        )
      case 'flagged':
        return (
          <Badge variant="secondary" className="bg-yellow-100 text-yellow-800">
            <Flag className="h-3 w-3 mr-1" />
            Flagged
          </Badge>
        )
      case 'pending':
        return (
          <Badge variant="secondary" className="bg-blue-100 text-blue-800">
            <Clock className="h-3 w-3 mr-1" />
            Pending
          </Badge>
        )
    }
  }

  const getTypeIcon = (type: ContentItem['type']) => {
    return type === 'job' ? (
      <Briefcase className="h-4 w-4 text-blue-600" />
    ) : (
      <FileText className="h-4 w-4 text-green-600" />
    )
  }

  const stats = {
    total: items.length,
    pending: items.filter(i => i.status === 'pending').length,
    flagged: items.filter(i => i.status === 'flagged').length,
    approved: items.filter(i => i.status === 'approved').length,
    rejected: items.filter(i => i.status === 'rejected').length
  }

  return (
    <div className="space-y-6">
      {/* Stats Cards */}
      <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-5">
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Total Items</CardTitle>
            <FileText className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{stats.total}</div>
          </CardContent>
        </Card>
        
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Pending Review</CardTitle>
            <Clock className="h-4 w-4 text-blue-600" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{stats.pending}</div>
          </CardContent>
        </Card>
        
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Flagged</CardTitle>
            <Flag className="h-4 w-4 text-yellow-600" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{stats.flagged}</div>
          </CardContent>
        </Card>
        
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Approved</CardTitle>
            <CheckCircle className="h-4 w-4 text-green-600" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{stats.approved}</div>
          </CardContent>
        </Card>
        
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Rejected</CardTitle>
            <XCircle className="h-4 w-4 text-red-600" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{stats.rejected}</div>
          </CardContent>
        </Card>
      </div>

      {/* Content Moderation Table */}
      <Card>
        <CardHeader>
          <CardTitle>Content Moderation</CardTitle>
          <CardDescription>
            Review and moderate job postings and applications
          </CardDescription>
        </CardHeader>
        <CardContent>
          {/* Filters */}
          <div className="flex flex-col sm:flex-row gap-4 mb-6">
            <div className="relative flex-1">
              <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 h-4 w-4 text-muted-foreground" />
              <Input
                placeholder="Search content..."
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                className="pl-10"
              />
            </div>
            
            <Select value={typeFilter} onValueChange={setTypeFilter}>
              <SelectTrigger className="w-full sm:w-[180px]">
                <SelectValue placeholder="Filter by type" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="all">All Types</SelectItem>
                <SelectItem value="job">Jobs</SelectItem>
                <SelectItem value="application">Applications</SelectItem>
              </SelectContent>
            </Select>
            
            <Select value={statusFilter} onValueChange={setStatusFilter}>
              <SelectTrigger className="w-full sm:w-[180px]">
                <SelectValue placeholder="Filter by status" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="all">All Status</SelectItem>
                <SelectItem value="pending">Pending</SelectItem>
                <SelectItem value="flagged">Flagged</SelectItem>
                <SelectItem value="approved">Approved</SelectItem>
                <SelectItem value="rejected">Rejected</SelectItem>
              </SelectContent>
            </Select>
          </div>

          {/* Content Table */}
          <div className="rounded-md border">
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>Content</TableHead>
                  <TableHead>Type</TableHead>
                  <TableHead>Status</TableHead>
                  <TableHead>Reports</TableHead>
                  <TableHead>Created</TableHead>
                  <TableHead className="text-right">Actions</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {filteredItems.map((item) => (
                  <TableRow key={item.id}>
                    <TableCell>
                      <div className="flex flex-col">
                        <div className="font-medium">{item.title}</div>
                        <div className="text-sm text-muted-foreground flex items-center space-x-2">
                          {item.company && (
                            <>
                              <Building className="h-3 w-3" />
                              <span>{item.company}</span>
                            </>
                          )}
                          {item.candidate && (
                            <>
                              <User className="h-3 w-3" />
                              <span>{item.candidate}</span>
                            </>
                          )}
                        </div>
                      </div>
                    </TableCell>
                    <TableCell>
                      <div className="flex items-center space-x-2">
                        {getTypeIcon(item.type)}
                        <span className="capitalize">{item.type}</span>
                      </div>
                    </TableCell>
                    <TableCell>
                      {getStatusBadge(item.status)}
                    </TableCell>
                    <TableCell>
                      {item.reportCount > 0 ? (
                        <div className="flex items-center space-x-1">
                          <AlertTriangle className="h-4 w-4 text-yellow-600" />
                          <span className="text-sm font-medium">{item.reportCount}</span>
                        </div>
                      ) : (
                        <span className="text-sm text-muted-foreground">None</span>
                      )}
                    </TableCell>
                    <TableCell>
                      <div className="text-sm">
                        {new Date(item.createdAt).toLocaleDateString()}
                      </div>
                      {item.lastReported && (
                        <div className="text-xs text-muted-foreground">
                          Last reported: {new Date(item.lastReported).toLocaleDateString()}
                        </div>
                      )}
                    </TableCell>
                    <TableCell className="text-right">
                      <DropdownMenu>
                        <DropdownMenuTrigger asChild>
                          <Button variant="ghost" className="h-8 w-8 p-0">
                            <MoreHorizontal className="h-4 w-4" />
                          </Button>
                        </DropdownMenuTrigger>
                        <DropdownMenuContent align="end">
                          <DropdownMenuLabel>Actions</DropdownMenuLabel>
                          <DropdownMenuItem onSelect={(e) => {
                            e.preventDefault()
                            setSelectedItem(item)
                          }}>
                            <Eye className="mr-2 h-4 w-4" />
                            Review Content
                          </DropdownMenuItem>
                          <DropdownMenuSeparator />
                          {(item.status === 'pending' || item.status === 'flagged') && (
                            <>
                              <DropdownMenuItem
                                onClick={() => handleModeration(item.id, 'approve')}
                                className="text-green-600"
                              >
                                <CheckCircle className="mr-2 h-4 w-4" />
                                Approve
                              </DropdownMenuItem>
                              <DropdownMenuItem
                                onClick={() => handleModeration(item.id, 'reject')}
                                className="text-red-600"
                              >
                                <XCircle className="mr-2 h-4 w-4" />
                                Reject
                              </DropdownMenuItem>
                            </>
                          )}
                        </DropdownMenuContent>
                      </DropdownMenu>
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </div>

          {filteredItems.length === 0 && (
            <div className="text-center py-8">
              <p className="text-muted-foreground">No content found matching your criteria.</p>
            </div>
          )}
        </CardContent>
      </Card>

      {/* Content Review Dialog */}
      {selectedItem && (
        <Dialog open={!!selectedItem} onOpenChange={() => setSelectedItem(null)}>
          <DialogContent className="max-w-2xl">
            <DialogHeader>
              <DialogTitle className="flex items-center space-x-2">
                {getTypeIcon(selectedItem.type)}
                <span>Review {selectedItem.type === 'job' ? 'Job Posting' : 'Application'}</span>
              </DialogTitle>
              <DialogDescription>
                Review the content and take appropriate moderation action
              </DialogDescription>
            </DialogHeader>
            
            <div className="space-y-4">
              <div>
                <h4 className="font-medium mb-2">Title</h4>
                <p className="text-sm">{selectedItem.title}</p>
              </div>
              
              {selectedItem.company && (
                <div>
                  <h4 className="font-medium mb-2">Company</h4>
                  <p className="text-sm">{selectedItem.company}</p>
                </div>
              )}
              
              {selectedItem.candidate && (
                <div>
                  <h4 className="font-medium mb-2">Candidate</h4>
                  <p className="text-sm">{selectedItem.candidate}</p>
                </div>
              )}
              
              <div>
                <h4 className="font-medium mb-2">Content</h4>
                <div className="p-3 bg-muted rounded-md">
                  <p className="text-sm">{selectedItem.content}</p>
                </div>
              </div>
              
              {selectedItem.reportReasons.length > 0 && (
                <div>
                  <h4 className="font-medium mb-2">Report Reasons</h4>
                  <div className="flex flex-wrap gap-2">
                    {selectedItem.reportReasons.map((reason, index) => (
                      <Badge key={index} variant="secondary" className="bg-yellow-100 text-yellow-800">
                        {reason}
                      </Badge>
                    ))}
                  </div>
                </div>
              )}
              
              <div>
                <h4 className="font-medium mb-2">Moderation Note (Optional)</h4>
                <Textarea
                  placeholder="Add a note about your moderation decision..."
                  value={moderationNote}
                  onChange={(e) => setModerationNote(e.target.value)}
                />
              </div>
            </div>
            
            <DialogFooter>
              <Button variant="outline" onClick={() => setSelectedItem(null)}>
                Cancel
              </Button>
              {(selectedItem.status === 'pending' || selectedItem.status === 'flagged') && (
                <>
                  <Button
                    variant="destructive"
                    onClick={() => handleModeration(selectedItem.id, 'reject')}
                    disabled={isLoading}
                  >
                    <XCircle className="h-4 w-4 mr-2" />
                    Reject
                  </Button>
                  <Button
                    onClick={() => handleModeration(selectedItem.id, 'approve')}
                    disabled={isLoading}
                  >
                    <CheckCircle className="h-4 w-4 mr-2" />
                    Approve
                  </Button>
                </>
              )}
            </DialogFooter>
          </DialogContent>
        </Dialog>
      )}
    </div>
  )
}