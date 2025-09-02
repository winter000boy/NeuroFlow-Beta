import { AdminRoute } from '@/components/auth/protected-route'

export default function AdminDashboardPage() {
  return (
    <AdminRoute>
      <div className="container mx-auto px-4 py-8">
        <h1 className="text-3xl font-bold mb-6">Admin Dashboard</h1>
        <p className="text-muted-foreground">
          This page is only accessible to administrators.
        </p>
        {/* Admin dashboard components will be implemented in later tasks */}
      </div>
    </AdminRoute>
  )
}