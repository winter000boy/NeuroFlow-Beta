'use client'

import { AdminSidebar } from './admin-sidebar'
import { AdminHeader } from './admin-header'
import { AdminRoute } from '@/components/auth/protected-route'

interface AdminLayoutProps {
  children: React.ReactNode
}

export function AdminLayout({ children }: AdminLayoutProps) {
  return (
    <AdminRoute>
      <div className="flex h-screen bg-background">
        {/* Sidebar */}
        <AdminSidebar />
        
        {/* Main Content */}
        <div className="flex-1 flex flex-col overflow-hidden">
          {/* Header */}
          <AdminHeader />
          
          {/* Content */}
          <main className="flex-1 overflow-y-auto p-6">
            {children}
          </main>
        </div>
      </div>
    </AdminRoute>
  )
}