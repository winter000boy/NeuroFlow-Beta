import { AdminLayout } from '@/components/admin/admin-layout'

export default function AdminSettingsPage() {
  return (
    <AdminLayout>
      <div className="space-y-6">
        <div>
          <h1 className="text-3xl font-bold tracking-tight">Settings</h1>
          <p className="text-muted-foreground">
            System configuration and preferences
          </p>
        </div>
        
        <div className="text-center py-12">
          <p className="text-muted-foreground">
            Settings interface will be implemented in future tasks
          </p>
        </div>
      </div>
    </AdminLayout>
  )
}