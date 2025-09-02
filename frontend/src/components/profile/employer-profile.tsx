'use client'

import { useState, useEffect } from 'react'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { Loader2, Save, Building, Globe, MapPin, AlertCircle, CheckCircle } from 'lucide-react'

import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { Form, FormControl, FormField, FormItem, FormLabel, FormMessage } from '@/components/ui/form'
import { FileUpload } from './file-upload'
import { userService, type EmployerProfile } from '@/services/user.service'
import { employerProfileSchema, type EmployerProfileFormData } from '@/lib/validations/profile'
import { useAuth } from '@/contexts/auth-context'

export function EmployerProfile() {
  const [profile, setProfile] = useState<EmployerProfile | null>(null)
  const [isLoading, setIsLoading] = useState(true)
  const [isSaving, setIsSaving] = useState(false)
  const [isUploadingLogo, setIsUploadingLogo] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const [successMessage, setSuccessMessage] = useState<string | null>(null)
  const { user } = useAuth()

  const form = useForm<EmployerProfileFormData>({
    resolver: zodResolver(employerProfileSchema),
    defaultValues: {
      name: '',
      email: '',
      companyName: '',
      website: '',
      description: '',
      address: '',
    },
  })

  useEffect(() => {
    loadProfile()
  }, [])

  const loadProfile = async () => {
    try {
      setIsLoading(true)
      const profileData = await userService.getEmployerProfile()
      setProfile(profileData)
      
      // Update form with loaded data
      form.reset({
        name: profileData.name,
        email: profileData.email,
        companyName: profileData.companyName,
        website: profileData.website || '',
        description: profileData.description,
        address: profileData.address || '',
      })
    } catch (err) {
      const error = err as { response?: { data?: { message?: string } } }
      setError(error.response?.data?.message || 'Failed to load profile')
    } finally {
      setIsLoading(false)
    }
  }

  const onSubmit = async (data: EmployerProfileFormData) => {
    setIsSaving(true)
    setError(null)
    setSuccessMessage(null)

    try {
      // Optimistic update
      const optimisticProfile = profile ? { ...profile, ...data } : null
      setProfile(optimisticProfile)

      const updatedProfile = await userService.updateEmployerProfile(data)
      setProfile(updatedProfile)
      setSuccessMessage('Profile updated successfully!')
      
      // Clear success message after 3 seconds
      setTimeout(() => setSuccessMessage(null), 3000)
    } catch (err) {
      // Revert optimistic update on error
      if (profile) {
        form.reset({
          name: profile.name,
          email: profile.email,
          companyName: profile.companyName,
          website: profile.website || '',
          description: profile.description,
          address: profile.address || '',
        })
      }
      
      const error = err as { response?: { data?: { message?: string } } }
      setError(error.response?.data?.message || 'Failed to update profile')
    } finally {
      setIsSaving(false)
    }
  }

  const handleLogoUpload = async (file: File) => {
    setIsUploadingLogo(true)
    try {
      const uploadResponse = await userService.uploadLogo(file)
      
      // Update profile with new logo URL
      if (profile) {
        setProfile({
          ...profile,
          logoUrl: uploadResponse.url,
        })
      }
      
      setSuccessMessage('Company logo uploaded successfully!')
      setTimeout(() => setSuccessMessage(null), 3000)
    } catch (err) {
      throw err // Let FileUpload component handle the error
    } finally {
      setIsUploadingLogo(false)
    }
  }

  if (isLoading) {
    return (
      <div className="flex items-center justify-center p-8">
        <Loader2 className="h-8 w-8 animate-spin" />
      </div>
    )
  }

  return (
    <div className="max-w-4xl mx-auto space-y-6">
      {/* Header */}
      <div className="flex items-center space-x-3">
        <Building className="h-8 w-8 text-primary" />
        <div>
          <h1 className="text-2xl font-bold">Company Profile</h1>
          <p className="text-muted-foreground">Manage your company information and branding</p>
        </div>
      </div>

      {/* Success/Error Messages */}
      {successMessage && (
        <div className="p-3 text-sm text-green-600 bg-green-50 border border-green-200 rounded-md">
          {successMessage}
        </div>
      )}
      
      {error && (
        <div className="p-3 text-sm text-red-600 bg-red-50 border border-red-200 rounded-md">
          {error}
        </div>
      )}

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Main Profile Form */}
        <div className="lg:col-span-2">
          <Card>
            <CardHeader>
              <CardTitle>Company Information</CardTitle>
              <CardDescription>
                Update your company details and contact information
              </CardDescription>
            </CardHeader>
            <CardContent>
              <Form {...form}>
                <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
                  <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                    <FormField
                      control={form.control}
                      name="name"
                      render={({ field }) => (
                        <FormItem>
                          <FormLabel>Contact Name</FormLabel>
                          <FormControl>
                            <Input placeholder="Enter contact name" {...field} />
                          </FormControl>
                          <FormMessage />
                        </FormItem>
                      )}
                    />

                    <FormField
                      control={form.control}
                      name="email"
                      render={({ field }) => (
                        <FormItem>
                          <FormLabel>Email</FormLabel>
                          <FormControl>
                            <Input type="email" placeholder="Enter your email" {...field} />
                          </FormControl>
                          <FormMessage />
                        </FormItem>
                      )}
                    />
                  </div>

                  <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                    <FormField
                      control={form.control}
                      name="companyName"
                      render={({ field }) => (
                        <FormItem>
                          <FormLabel>Company Name</FormLabel>
                          <FormControl>
                            <div className="relative">
                              <Building className="absolute left-3 top-3 h-4 w-4 text-gray-400" />
                              <Input className="pl-10" placeholder="Enter company name" {...field} />
                            </div>
                          </FormControl>
                          <FormMessage />
                        </FormItem>
                      )}
                    />

                    <FormField
                      control={form.control}
                      name="website"
                      render={({ field }) => (
                        <FormItem>
                          <FormLabel>Website (Optional)</FormLabel>
                          <FormControl>
                            <div className="relative">
                              <Globe className="absolute left-3 top-3 h-4 w-4 text-gray-400" />
                              <Input
                                className="pl-10"
                                placeholder="https://company.com"
                                {...field}
                              />
                            </div>
                          </FormControl>
                          <FormMessage />
                        </FormItem>
                      )}
                    />
                  </div>

                  <FormField
                    control={form.control}
                    name="address"
                    render={({ field }) => (
                      <FormItem>
                        <FormLabel>Address (Optional)</FormLabel>
                        <FormControl>
                          <div className="relative">
                            <MapPin className="absolute left-3 top-3 h-4 w-4 text-gray-400" />
                            <Input
                              className="pl-10"
                              placeholder="Enter company address"
                              {...field}
                            />
                          </div>
                        </FormControl>
                        <FormMessage />
                      </FormItem>
                    )}
                  />

                  <FormField
                    control={form.control}
                    name="description"
                    render={({ field }) => (
                      <FormItem>
                        <FormLabel>Company Description</FormLabel>
                        <FormControl>
                          <textarea
                            rows={4}
                            placeholder="Tell us about your company..."
                            {...field}
                            className="flex min-h-[80px] w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50"
                          />
                        </FormControl>
                        <FormMessage />
                      </FormItem>
                    )}
                  />

                  <Button type="submit" disabled={isSaving} className="w-full">
                    {isSaving ? (
                      <>
                        <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                        Saving...
                      </>
                    ) : (
                      <>
                        <Save className="mr-2 h-4 w-4" />
                        Save Changes
                      </>
                    )}
                  </Button>
                </form>
              </Form>
            </CardContent>
          </Card>
        </div>

        {/* Logo Upload and Status Sidebar */}
        <div className="space-y-6">
          <Card>
            <CardHeader>
              <CardTitle>Company Logo</CardTitle>
              <CardDescription>
                Upload your company logo for better brand recognition
              </CardDescription>
            </CardHeader>
            <CardContent>
              <FileUpload
                onFileUpload={handleLogoUpload}
                currentFileUrl={profile?.logoUrl}
                currentFileName={profile?.logoUrl ? 'Company Logo' : undefined}
                acceptedTypes=".jpg,.jpeg,.png,.svg"
                maxSize={2}
                label="Logo File"
                description="JPG, PNG, or SVG up to 2MB"
                isLoading={isUploadingLogo}
              />
            </CardContent>
          </Card>

          {/* Account Status */}
          <Card>
            <CardHeader>
              <CardTitle>Account Status</CardTitle>
            </CardHeader>
            <CardContent>
              <div className="space-y-4">
                <div className="flex items-center space-x-2">
                  {profile?.isApproved ? (
                    <>
                      <CheckCircle className="h-5 w-5 text-green-500" />
                      <span className="text-sm font-medium text-green-700">Approved</span>
                    </>
                  ) : (
                    <>
                      <AlertCircle className="h-5 w-5 text-yellow-500" />
                      <span className="text-sm font-medium text-yellow-700">Pending Approval</span>
                    </>
                  )}
                </div>
                
                {!profile?.isApproved && (
                  <div className="p-3 bg-yellow-50 border border-yellow-200 rounded-md">
                    <p className="text-xs text-yellow-800">
                      Your account is pending admin approval. You can update your profile, 
                      but job posting will be available after approval.
                    </p>
                  </div>
                )}

                <div className="space-y-2">
                  <div className="flex justify-between items-center">
                    <span className="text-sm">Profile Complete</span>
                    <span className={`text-sm font-medium ${
                      profile?.logoUrl && profile?.website ? 'text-green-600' : 'text-yellow-600'
                    }`}>
                      {profile?.logoUrl && profile?.website ? '100%' : 
                       profile?.logoUrl || profile?.website ? '90%' : '80%'}
                    </span>
                  </div>
                  <div className="w-full bg-gray-200 rounded-full h-2">
                    <div
                      className={`h-2 rounded-full ${
                        profile?.logoUrl && profile?.website ? 'bg-green-500' : 'bg-yellow-500'
                      }`}
                      style={{ 
                        width: profile?.logoUrl && profile?.website ? '100%' : 
                               profile?.logoUrl || profile?.website ? '90%' : '80%'
                      }}
                    />
                  </div>
                  {(!profile?.logoUrl || !profile?.website) && (
                    <p className="text-xs text-muted-foreground">
                      Add a logo and website to complete your profile
                    </p>
                  )}
                </div>
              </div>
            </CardContent>
          </Card>
        </div>
      </div>
    </div>
  )
}