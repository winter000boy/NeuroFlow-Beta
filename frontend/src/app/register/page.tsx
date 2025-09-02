import { Suspense } from 'react'
import { RegisterForm } from '@/components/auth/register-form'
import { GuestRoute } from '@/components/auth/protected-route'
import { Loader2 } from 'lucide-react'

function RegisterPageContent() {
  return (
    <div className="container mx-auto px-4 py-8">
      <div className="flex items-center justify-center min-h-[calc(100vh-200px)]">
        <RegisterForm />
      </div>
    </div>
  )
}

export default function RegisterPage() {
  return (
    <GuestRoute>
      <Suspense
        fallback={
          <div className="flex items-center justify-center min-h-screen">
            <div className="text-center">
              <Loader2 className="h-8 w-8 animate-spin mx-auto mb-4" />
              <p className="text-muted-foreground">Loading...</p>
            </div>
          </div>
        }
      >
        <RegisterPageContent />
      </Suspense>
    </GuestRoute>
  )
}