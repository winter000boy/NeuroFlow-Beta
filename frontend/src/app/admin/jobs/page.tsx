import { AdminLayout } from '@/components/admin/admin-layout'
import { ContentModeration } from '@/components/admin/content-moderation'

export default function AdminJobsPage() {
  return (
    <AdminLayout>
      <div className="space-y-6">
        <div>
          <h1 className="text-3xl font-bold tracking-tight">Content Moderation</h1>
          <p className="text-muted-foreground">
            Review and moderate job postings and applications
          </p>
        </div>
        
        <ContentModeration />
      </div>
    </AdminLayout>
  )
}