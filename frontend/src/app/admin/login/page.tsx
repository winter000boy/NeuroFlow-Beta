import { AdminLoginForm } from '@/components/admin/admin-login-form'
import { GuestRoute } from '@/components/auth/protected-route'

export default function AdminLoginPage() {
  return (
    <GuestRoute>
      <AdminLoginForm />
    </GuestRoute>
  )
}