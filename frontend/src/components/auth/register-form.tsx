'use client'

import { useState } from 'react'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import Link from 'next/link'
import { Eye, EyeOff, Loader2, User, Building } from 'lucide-react'

import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { useAuth } from '@/contexts/auth-context'
import {
  candidateRegistrationSchema,
  employerRegistrationSchema,
  type CandidateRegistrationFormData,
  type EmployerRegistrationFormData,
} from '@/lib/validations/auth'

type UserRole = 'CANDIDATE' | 'EMPLOYER'

export function RegisterForm() {
  const [selectedRole, setSelectedRole] = useState<UserRole>('CANDIDATE')
  const [showPassword, setShowPassword] = useState(false)
  const [showConfirmPassword, setShowConfirmPassword] = useState(false)
  const [isLoading, setIsLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const { register: registerUser } = useAuth()

  const candidateForm = useForm<CandidateRegistrationFormData>({
    resolver: zodResolver(candidateRegistrationSchema),
  })

  const employerForm = useForm<EmployerRegistrationFormData>({
    resolver: zodResolver(employerRegistrationSchema),
  })

  const onCandidateSubmit = async (data: CandidateRegistrationFormData) => {
    setIsLoading(true)
    setError(null)

    try {
      const registrationData = {
        ...data,
        role: 'CANDIDATE' as const,
      }
      
      // Remove confirmPassword from the data sent to API
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const { confirmPassword: _, ...apiData } = registrationData
      
      await registerUser(apiData)
    } catch (err: unknown) {
      const error = err as { response?: { data?: { message?: string } } }
      setError(
        error.response?.data?.message || 
        'Registration failed. Please try again.'
      )
    } finally {
      setIsLoading(false)
    }
  }

  const onEmployerSubmit = async (data: EmployerRegistrationFormData) => {
    setIsLoading(true)
    setError(null)

    setIsLoading(true)
    setError(null)

    try {
      const registrationData = {
        ...data,
        role: 'EMPLOYER' as const,
      }
      
      // Remove confirmPassword from the data sent to API
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const { confirmPassword: _, ...apiData } = registrationData
      
      await registerUser(apiData)
    } catch (err: unknown) {
      const error = err as { response?: { data?: { message?: string } } }
      setError(
        error.response?.data?.message || 
        'Registration failed. Please try again.'
      )
    } finally {
      setIsLoading(false)
    }
  }

  const handleRoleChange = (role: UserRole) => {
    setSelectedRole(role)
    setError(null)
    // Reset forms when switching roles
    candidateForm.reset()
    employerForm.reset()
  }



  return (
    <Card className="w-full max-w-2xl mx-auto">
      <CardHeader className="space-y-1">
        <CardTitle className="text-2xl font-bold text-center">Create Account</CardTitle>
        <CardDescription className="text-center">
          Choose your account type and fill in your details
        </CardDescription>
      </CardHeader>
      <CardContent>
        {/* Role Selection */}
        <div className="flex space-x-4 mb-6">
          <button
            type="button"
            onClick={() => handleRoleChange('CANDIDATE')}
            className={`flex-1 p-4 border-2 rounded-lg transition-colors ${
              selectedRole === 'CANDIDATE'
                ? 'border-primary bg-primary/5'
                : 'border-gray-200 hover:border-gray-300'
            }`}
          >
            <User className="h-6 w-6 mx-auto mb-2" />
            <div className="font-medium">Job Seeker</div>
            <div className="text-sm text-muted-foreground">Looking for opportunities</div>
          </button>
          <button
            type="button"
            onClick={() => handleRoleChange('EMPLOYER')}
            className={`flex-1 p-4 border-2 rounded-lg transition-colors ${
              selectedRole === 'EMPLOYER'
                ? 'border-primary bg-primary/5'
                : 'border-gray-200 hover:border-gray-300'
            }`}
          >
            <Building className="h-6 w-6 mx-auto mb-2" />
            <div className="font-medium">Employer</div>
            <div className="text-sm text-muted-foreground">Hiring talent</div>
          </button>
        </div>

        {selectedRole === 'CANDIDATE' ? (
          <form onSubmit={candidateForm.handleSubmit(onCandidateSubmit)} className="space-y-4">
            {error && (
              <div className="p-3 text-sm text-red-600 bg-red-50 border border-red-200 rounded-md">
                {error}
              </div>
            )}

            {/* Common Fields */}
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div className="space-y-2">
                <Label htmlFor="name">Full Name</Label>
                <Input
                  id="name"
                  placeholder="Enter your full name"
                  {...candidateForm.register('name')}
                  className={candidateForm.formState.errors.name ? 'border-red-500' : ''}
                />
                {candidateForm.formState.errors.name && (
                  <p className="text-sm text-red-600">{candidateForm.formState.errors.name.message}</p>
                )}
              </div>

              <div className="space-y-2">
                <Label htmlFor="email">Email</Label>
                <Input
                  id="email"
                  type="email"
                  placeholder="Enter your email"
                  {...candidateForm.register('email')}
                  className={candidateForm.formState.errors.email ? 'border-red-500' : ''}
                />
                {candidateForm.formState.errors.email && (
                  <p className="text-sm text-red-600">{candidateForm.formState.errors.email.message}</p>
                )}
              </div>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div className="space-y-2">
                <Label htmlFor="password">Password</Label>
                <div className="relative">
                  <Input
                    id="password"
                    type={showPassword ? 'text' : 'password'}
                    placeholder="Create a password"
                    {...candidateForm.register('password')}
                    className={candidateForm.formState.errors.password ? 'border-red-500 pr-10' : 'pr-10'}
                  />
                  <button
                    type="button"
                    className="absolute inset-y-0 right-0 flex items-center pr-3"
                    onClick={() => setShowPassword(!showPassword)}
                  >
                    {showPassword ? (
                      <EyeOff className="h-4 w-4 text-gray-400" />
                    ) : (
                      <Eye className="h-4 w-4 text-gray-400" />
                    )}
                  </button>
                </div>
                {candidateForm.formState.errors.password && (
                  <p className="text-sm text-red-600">{candidateForm.formState.errors.password.message}</p>
                )}
              </div>

              <div className="space-y-2">
                <Label htmlFor="confirmPassword">Confirm Password</Label>
                <div className="relative">
                  <Input
                    id="confirmPassword"
                    type={showConfirmPassword ? 'text' : 'password'}
                    placeholder="Confirm your password"
                    {...candidateForm.register('confirmPassword')}
                    className={candidateForm.formState.errors.confirmPassword ? 'border-red-500 pr-10' : 'pr-10'}
                  />
                  <button
                    type="button"
                    className="absolute inset-y-0 right-0 flex items-center pr-3"
                    onClick={() => setShowConfirmPassword(!showConfirmPassword)}
                  >
                    {showConfirmPassword ? (
                      <EyeOff className="h-4 w-4 text-gray-400" />
                    ) : (
                      <Eye className="h-4 w-4 text-gray-400" />
                    )}
                  </button>
                </div>
                {candidateForm.formState.errors.confirmPassword && (
                  <p className="text-sm text-red-600">{candidateForm.formState.errors.confirmPassword.message}</p>
                )}
              </div>
            </div>

            {/* Candidate-specific Fields */}
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div className="space-y-2">
                <Label htmlFor="phone">Phone Number</Label>
                <Input
                  id="phone"
                  placeholder="Enter your phone number"
                  {...candidateForm.register('phone')}
                  className={candidateForm.formState.errors.phone ? 'border-red-500' : ''}
                />
                {candidateForm.formState.errors.phone && (
                  <p className="text-sm text-red-600">{candidateForm.formState.errors.phone.message}</p>
                )}
              </div>

              <div className="space-y-2">
                <Label htmlFor="degree">Degree</Label>
                <Input
                  id="degree"
                  placeholder="e.g., Computer Science"
                  {...candidateForm.register('degree')}
                  className={candidateForm.formState.errors.degree ? 'border-red-500' : ''}
                />
                {candidateForm.formState.errors.degree && (
                  <p className="text-sm text-red-600">{candidateForm.formState.errors.degree.message}</p>
                )}
              </div>
            </div>

            <div className="space-y-2">
              <Label htmlFor="graduationYear">Graduation Year</Label>
              <Input
                id="graduationYear"
                type="number"
                placeholder="e.g., 2023"
                {...candidateForm.register('graduationYear', { valueAsNumber: true })}
                className={candidateForm.formState.errors.graduationYear ? 'border-red-500' : ''}
              />
              {candidateForm.formState.errors.graduationYear && (
                <p className="text-sm text-red-600">{candidateForm.formState.errors.graduationYear.message}</p>
              )}
            </div>

            <Button type="submit" className="w-full" disabled={isLoading}>
              {isLoading ? (
                <>
                  <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                  Creating Account...
                </>
              ) : (
                'Create Account'
              )}
            </Button>

            <div className="text-center text-sm">
              <span className="text-muted-foreground">Already have an account? </span>
              <Link href="/login" className="text-primary hover:underline">
                Sign in
              </Link>
            </div>
          </form>
        ) : (
          <form onSubmit={employerForm.handleSubmit(onEmployerSubmit)} className="space-y-4">
            {error && (
              <div className="p-3 text-sm text-red-600 bg-red-50 border border-red-200 rounded-md">
                {error}
              </div>
            )}

            {/* Common Fields */}
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div className="space-y-2">
                <Label htmlFor="name">Contact Name</Label>
                <Input
                  id="name"
                  placeholder="Enter contact name"
                  {...employerForm.register('name')}
                  className={employerForm.formState.errors.name ? 'border-red-500' : ''}
                />
                {employerForm.formState.errors.name && (
                  <p className="text-sm text-red-600">{employerForm.formState.errors.name.message}</p>
                )}
              </div>

              <div className="space-y-2">
                <Label htmlFor="email">Email</Label>
                <Input
                  id="email"
                  type="email"
                  placeholder="Enter your email"
                  {...employerForm.register('email')}
                  className={employerForm.formState.errors.email ? 'border-red-500' : ''}
                />
                {employerForm.formState.errors.email && (
                  <p className="text-sm text-red-600">{employerForm.formState.errors.email.message}</p>
                )}
              </div>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div className="space-y-2">
                <Label htmlFor="password">Password</Label>
                <div className="relative">
                  <Input
                    id="password"
                    type={showPassword ? 'text' : 'password'}
                    placeholder="Create a password"
                    {...employerForm.register('password')}
                    className={employerForm.formState.errors.password ? 'border-red-500 pr-10' : 'pr-10'}
                  />
                  <button
                    type="button"
                    className="absolute inset-y-0 right-0 flex items-center pr-3"
                    onClick={() => setShowPassword(!showPassword)}
                  >
                    {showPassword ? (
                      <EyeOff className="h-4 w-4 text-gray-400" />
                    ) : (
                      <Eye className="h-4 w-4 text-gray-400" />
                    )}
                  </button>
                </div>
                {employerForm.formState.errors.password && (
                  <p className="text-sm text-red-600">{employerForm.formState.errors.password.message}</p>
                )}
              </div>

              <div className="space-y-2">
                <Label htmlFor="confirmPassword">Confirm Password</Label>
                <div className="relative">
                  <Input
                    id="confirmPassword"
                    type={showConfirmPassword ? 'text' : 'password'}
                    placeholder="Confirm your password"
                    {...employerForm.register('confirmPassword')}
                    className={employerForm.formState.errors.confirmPassword ? 'border-red-500 pr-10' : 'pr-10'}
                  />
                  <button
                    type="button"
                    className="absolute inset-y-0 right-0 flex items-center pr-3"
                    onClick={() => setShowConfirmPassword(!showConfirmPassword)}
                  >
                    {showConfirmPassword ? (
                      <EyeOff className="h-4 w-4 text-gray-400" />
                    ) : (
                      <Eye className="h-4 w-4 text-gray-400" />
                    )}
                  </button>
                </div>
                {employerForm.formState.errors.confirmPassword && (
                  <p className="text-sm text-red-600">{employerForm.formState.errors.confirmPassword.message}</p>
                )}
              </div>
            </div>

            {/* Employer-specific Fields */}
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div className="space-y-2">
                <Label htmlFor="companyName">Company Name</Label>
                <Input
                  id="companyName"
                  placeholder="Enter company name"
                  {...employerForm.register('companyName')}
                  className={employerForm.formState.errors.companyName ? 'border-red-500' : ''}
                />
                {employerForm.formState.errors.companyName && (
                  <p className="text-sm text-red-600">{employerForm.formState.errors.companyName.message}</p>
                )}
              </div>

              <div className="space-y-2">
                <Label htmlFor="website">Website (Optional)</Label>
                <Input
                  id="website"
                  placeholder="https://company.com"
                  {...employerForm.register('website')}
                  className={employerForm.formState.errors.website ? 'border-red-500' : ''}
                />
                {employerForm.formState.errors.website && (
                  <p className="text-sm text-red-600">{employerForm.formState.errors.website.message}</p>
                )}
              </div>
            </div>

            <div className="space-y-2">
              <Label htmlFor="description">Company Description</Label>
              <textarea
                id="description"
                rows={3}
                placeholder="Tell us about your company..."
                {...employerForm.register('description')}
                className={`flex min-h-[80px] w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50 ${
                  employerForm.formState.errors.description ? 'border-red-500' : ''
                }`}
              />
              {employerForm.formState.errors.description && (
                <p className="text-sm text-red-600">{employerForm.formState.errors.description.message}</p>
              )}
            </div>

            <Button type="submit" className="w-full" disabled={isLoading}>
              {isLoading ? (
                <>
                  <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                  Creating Account...
                </>
              ) : (
                'Create Account'
              )}
            </Button>

            <div className="text-center text-sm">
              <span className="text-muted-foreground">Already have an account? </span>
              <Link href="/login" className="text-primary hover:underline">
                Sign in
              </Link>
            </div>
          </form>
        )}


      </CardContent>
    </Card>
  )
}