import { EmployerRoute } from '@/components/auth/protected-route'

export default function EmployerDashboardPage() {
  return (
    <EmployerRoute>
      <div className="container mx-auto px-4 py-8">
        <h1 className="text-3xl font-bold mb-6">Employer Dashboard</h1>
        <p className="text-muted-foreground">
          This page is only accessible to employers.
        </p>
        {/* Employer dashboard components will be implemented in later tasks */}
      </div>
    </EmployerRoute>
  )
}