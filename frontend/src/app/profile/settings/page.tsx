import { ProtectedRoute } from '@/components/auth/protected-route'
import { PasswordChange } from '@/components/profile'

export default function ProfileSettings() {
  return (
    <ProtectedRoute allowedRoles={['CANDIDATE', 'EMPLOYER']}>
      <div className="container mx-auto px-4 py-8">
        <div className="max-w-2xl mx-auto space-y-6">
          <div>
            <h1 className="text-2xl font-bold">Account Settings</h1>
            <p className="text-muted-foreground">Manage your account security and preferences</p>
          </div>
          
          <PasswordChange />
        </div>
      </div>
    </ProtectedRoute>
  )
}