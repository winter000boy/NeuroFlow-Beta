import { AdminLayout } from '@/components/admin/admin-layout'

export default function AdminApplicationsPage() {
  return (
    <AdminLayout>
      <div className="space-y-6">
        <div>
          <h1 className="text-3xl font-bold tracking-tight">Applications</h1>
          <p className="text-muted-foreground">
            Monitor job applications across the platform
          </p>
        </div>
        
        <div className="text-center py-12">
          <p className="text-muted-foreground">
            Applications monitoring interface will be implemented in task 12.2
          </p>
        </div>
      </div>
    </AdminLayout>
  )
}