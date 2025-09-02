import { ProtectedRoute } from '@/components/auth/protected-route'
import { ProfilePage } from '@/components/profile'

export default function Profile() {
  return (
    <ProtectedRoute allowedRoles={['CANDIDATE', 'EMPLOYER']}>
      <div className="container mx-auto px-4 py-8">
        <ProfilePage />
      </div>
    </ProtectedRoute>
  )
}