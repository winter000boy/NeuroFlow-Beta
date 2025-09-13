'use client'

import { useState } from 'react'
import { useRouter } from 'next/navigation'
import { useAuth } from '@/contexts/auth-context'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { Alert, AlertDescription } from '@/components/ui/alert'
import { Badge } from '@/components/ui/badge'
import { Shield, Eye, EyeOff, Lock, Mail, AlertTriangle } from 'lucide-react'
import { cn } from '@/lib/utils'

interface AdminLoginFormProps {
  className?: string
}

export function AdminLoginForm({ className }: AdminLoginFormProps) {
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [showPassword, setShowPassword] = useState(false)
  const [isLoading, setIsLoading] = useState(false)
  const [error, setError] = useState('')
  const [loginAttempts, setLoginAttempts] = useState(0)
  
  const { login } = useAuth()
  const router = useRouter()

  const maxAttempts = 3
  const isBlocked = loginAttempts >= maxAttempts

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    
    if (isBlocked) {
      setError('Too many failed attempts. Please wait before trying again.')
      return
    }

    if (!email || !password) {
      setError('Please fill in all fields')
      return
    }

    setIsLoading(true)
    setError('')

    try {
      await login(email, password)
      // If we get here, login was successful and user will be redirected
    } catch (error) {
      setError('Login failed. Please try again.')
      setLoginAttempts(prev => prev + 1)
    } finally {
      setIsLoading(false)
    }
  }

  const resetAttempts = () => {
    setLoginAttempts(0)
    setError('')
  }

  return (
    <div className={cn("flex items-center justify-center min-h-screen bg-gradient-to-br from-slate-900 to-slate-800", className)}>
      <Card className="w-full max-w-md mx-4 shadow-2xl border-slate-700">
        <CardHeader className="space-y-4 text-center">
          <div className="flex justify-center">
            <div className="p-3 bg-primary/10 rounded-full">
              <Shield className="h-8 w-8 text-primary" />
            </div>
          </div>
          <div>
            <CardTitle className="text-2xl font-bold">Admin Access</CardTitle>
            <CardDescription className="text-slate-400">
              Secure administrator login portal
            </CardDescription>
          </div>
          <div className="flex justify-center">
            <Badge variant="secondary" className="text-xs">
              Enhanced Security
            </Badge>
          </div>
        </CardHeader>
        
        <CardContent className="space-y-6">
          {error && (
            <Alert variant="destructive">
              <AlertTriangle className="h-4 w-4" />
              <AlertDescription>{error}</AlertDescription>
            </Alert>
          )}

          {isBlocked && (
            <Alert>
              <Lock className="h-4 w-4" />
              <AlertDescription>
                Account temporarily locked due to multiple failed attempts.
                <Button 
                  variant="link" 
                  className="p-0 h-auto ml-1 text-primary"
                  onClick={resetAttempts}
                >
                  Reset
                </Button>
              </AlertDescription>
            </Alert>
          )}

          <form onSubmit={handleSubmit} className="space-y-4">
            <div className="space-y-2">
              <label htmlFor="email" className="text-sm font-medium">
                Email Address
              </label>
              <div className="relative">
                <Mail className="absolute left-3 top-1/2 transform -translate-y-1/2 h-4 w-4 text-muted-foreground" />
                <Input
                  id="email"
                  type="email"
                  placeholder="admin@company.com"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  className="pl-10"
                  disabled={isLoading || isBlocked}
                  required
                />
              </div>
            </div>

            <div className="space-y-2">
              <label htmlFor="password" className="text-sm font-medium">
                Password
              </label>
              <div className="relative">
                <Lock className="absolute left-3 top-1/2 transform -translate-y-1/2 h-4 w-4 text-muted-foreground" />
                <Input
                  id="password"
                  type={showPassword ? 'text' : 'password'}
                  placeholder="Enter your password"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  className="pl-10 pr-10"
                  disabled={isLoading || isBlocked}
                  required
                />
                <Button
                  type="button"
                  variant="ghost"
                  size="icon"
                  className="absolute right-0 top-0 h-full px-3 hover:bg-transparent"
                  onClick={() => setShowPassword(!showPassword)}
                  disabled={isLoading || isBlocked}
                >
                  {showPassword ? (
                    <EyeOff className="h-4 w-4 text-muted-foreground" />
                  ) : (
                    <Eye className="h-4 w-4 text-muted-foreground" />
                  )}
                </Button>
              </div>
            </div>

            <Button
              type="submit"
              className="w-full"
              disabled={isLoading || isBlocked}
            >
              {isLoading ? (
                <>
                  <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-white mr-2" />
                  Authenticating...
                </>
              ) : (
                <>
                  <Shield className="h-4 w-4 mr-2" />
                  Secure Login
                </>
              )}
            </Button>
          </form>

          {/* Security Information */}
          <div className="pt-4 border-t">
            <div className="text-xs text-muted-foreground space-y-1">
              <div className="flex items-center justify-between">
                <span>Login Attempts:</span>
                <span className={cn(
                  "font-medium",
                  loginAttempts >= 2 ? "text-destructive" : "text-muted-foreground"
                )}>
                  {loginAttempts}/{maxAttempts}
                </span>
              </div>
              <div className="text-center pt-2">
                <span>🔒 Secured with enterprise-grade encryption</span>
              </div>
            </div>
          </div>
        </CardContent>
      </Card>
    </div>
  )
}