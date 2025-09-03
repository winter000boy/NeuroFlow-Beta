import { AdminLayout } from '@/components/admin/admin-layout'
import { UserManagement } from '@/components/admin/user-management'

export default function AdminUsersPage() {
  return (
    <AdminLayout>
      <div className="space-y-6">
        <div>
          <h1 className="text-3xl font-bold tracking-tight">User Management</h1>
          <p className="text-muted-foreground">
            Manage candidates and employers on the platform
          </p>
        </div>
        
        <UserManagement />
      </div>
    </AdminLayout>
  )
}