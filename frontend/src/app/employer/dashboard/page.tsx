import { EmployerRoute } from '@/components/auth/protected-route'
import { EmployerDashboard } from '@/components/employer/employer-dashboard'

export default function EmployerDashboardPage() {
  return (
    <EmployerRoute>
      <EmployerDashboard />
    </EmployerRoute>
  )
}