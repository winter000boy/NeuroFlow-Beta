'use client'

import { useState, useEffect } from 'react'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { Loader2, Save, User, Phone, GraduationCap, Calendar, Link as LinkIcon } from 'lucide-react'

import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { Form, FormControl, FormField, FormItem, FormLabel, FormMessage } from '@/components/ui/form'
import { FileUpload } from './file-upload'
import { userService, type CandidateProfile } from '@/services/user.service'
import { candidateProfileSchema, type CandidateProfileFormData } from '@/lib/validations/profile'
import { useAuth } from '@/contexts/auth-context'

export function CandidateProfile() {
  const [profile, setProfile] = useState<CandidateProfile | null>(null)
  const [isLoading, setIsLoading] = useState(true)
  const [isSaving, setIsSaving] = useState(false)
  const [isUploadingResume, setIsUploadingResume] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const [successMessage, setSuccessMessage] = useState<string | null>(null)
  const { user } = useAuth()

  const form = useForm<CandidateProfileFormData>({
    resolver: zodResolver(candidateProfileSchema),
    defaultValues: {
      name: '',
      email: '',
      phone: '',
      degree: '',
      graduationYear: new Date().getFullYear(),
      linkedinProfile: '',
      portfolioUrl: '',
    },
  })

  useEffect(() => {
    loadProfile()
  }, [])

  const loadProfile = async () => {
    try {
      setIsLoading(true)
      const profileData = await userService.getCandidateProfile()
      setProfile(profileData)
      
      // Update form with loaded data
      form.reset({
        name: profileData.name,
        email: profileData.email,
        phone: profileData.phone,
        degree: profileData.degree,
        graduationYear: profileData.graduationYear,
        linkedinProfile: profileData.linkedinProfile || '',
        portfolioUrl: profileData.portfolioUrl || '',
      })
    } catch (err) {
      const error = err as { response?: { data?: { message?: string } } }
      setError(error.response?.data?.message || 'Failed to load profile')
    } finally {
      setIsLoading(false)
    }
  }

  const onSubmit = async (data: CandidateProfileFormData) => {
    setIsSaving(true)
    setError(null)
    setSuccessMessage(null)

    try {
      // Optimistic update
      const optimisticProfile = profile ? { ...profile, ...data } : null
      setProfile(optimisticProfile)

      const updatedProfile = await userService.updateCandidateProfile(data)
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
          phone: profile.phone,
          degree: profile.degree,
          graduationYear: profile.graduationYear,
          linkedinProfile: profile.linkedinProfile || '',
          portfolioUrl: profile.portfolioUrl || '',
        })
      }
      
      const error = err as { response?: { data?: { message?: string } } }
      setError(error.response?.data?.message || 'Failed to update profile')
    } finally {
      setIsSaving(false)
    }
  }

  const handleResumeUpload = async (file: File) => {
    setIsUploadingResume(true)
    try {
      const uploadResponse = await userService.uploadResume(file)
      
      // Update profile with new resume URL
      if (profile) {
        setProfile({
          ...profile,
          resumeUrl: uploadResponse.url,
        })
      }
      
      setSuccessMessage('Resume uploaded successfully!')
      setTimeout(() => setSuccessMessage(null), 3000)
    } catch (err) {
      throw err // Let FileUpload component handle the error
    } finally {
      setIsUploadingResume(false)
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
        <User className="h-8 w-8 text-primary" />
        <div>
          <h1 className="text-2xl font-bold">Candidate Profile</h1>
          <p className="text-muted-foreground">Manage your profile information and resume</p>
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
              <CardTitle>Personal Information</CardTitle>
              <CardDescription>
                Update your personal details and contact information
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
                          <FormLabel>Full Name</FormLabel>
                          <FormControl>
                            <Input placeholder="Enter your full name" {...field} />
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
                      name="phone"
                      render={({ field }) => (
                        <FormItem>
                          <FormLabel>Phone Number</FormLabel>
                          <FormControl>
                            <div className="relative">
                              <Phone className="absolute left-3 top-3 h-4 w-4 text-gray-400" />
                              <Input className="pl-10" placeholder="Enter your phone number" {...field} />
                            </div>
                          </FormControl>
                          <FormMessage />
                        </FormItem>
                      )}
                    />

                    <FormField
                      control={form.control}
                      name="graduationYear"
                      render={({ field }) => (
                        <FormItem>
                          <FormLabel>Graduation Year</FormLabel>
                          <FormControl>
                            <div className="relative">
                              <Calendar className="absolute left-3 top-3 h-4 w-4 text-gray-400" />
                              <Input
                                type="number"
                                className="pl-10"
                                placeholder="e.g., 2023"
                                {...field}
                                onChange={(e) => field.onChange(parseInt(e.target.value) || 0)}
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
                    name="degree"
                    render={({ field }) => (
                      <FormItem>
                        <FormLabel>Degree</FormLabel>
                        <FormControl>
                          <div className="relative">
                            <GraduationCap className="absolute left-3 top-3 h-4 w-4 text-gray-400" />
                            <Input className="pl-10" placeholder="e.g., Computer Science" {...field} />
                          </div>
                        </FormControl>
                        <FormMessage />
                      </FormItem>
                    )}
                  />

                  <div className="space-y-4">
                    <h3 className="text-lg font-medium">Social Links</h3>
                    
                    <FormField
                      control={form.control}
                      name="linkedinProfile"
                      render={({ field }) => (
                        <FormItem>
                          <FormLabel>LinkedIn Profile</FormLabel>
                          <FormControl>
                            <div className="relative">
                              <LinkIcon className="absolute left-3 top-3 h-4 w-4 text-gray-400" />
                              <Input
                                className="pl-10"
                                placeholder="https://linkedin.com/in/yourprofile"
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
                      name="portfolioUrl"
                      render={({ field }) => (
                        <FormItem>
                          <FormLabel>Portfolio URL</FormLabel>
                          <FormControl>
                            <div className="relative">
                              <LinkIcon className="absolute left-3 top-3 h-4 w-4 text-gray-400" />
                              <Input
                                className="pl-10"
                                placeholder="https://yourportfolio.com"
                                {...field}
                              />
                            </div>
                          </FormControl>
                          <FormMessage />
                        </FormItem>
                      )}
                    />
                  </div>

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

        {/* Resume Upload Sidebar */}
        <div className="space-y-6">
          <Card>
            <CardHeader>
              <CardTitle>Resume</CardTitle>
              <CardDescription>
                Upload your resume to showcase your experience
              </CardDescription>
            </CardHeader>
            <CardContent>
              <FileUpload
                onFileUpload={handleResumeUpload}
                currentFileUrl={profile?.resumeUrl}
                currentFileName={profile?.resumeUrl ? 'Resume.pdf' : undefined}
                acceptedTypes=".pdf,.doc,.docx"
                maxSize={5}
                label="Resume File"
                description="PDF, DOC, or DOCX up to 5MB"
                isLoading={isUploadingResume}
              />
            </CardContent>
          </Card>

          {/* Profile Status */}
          <Card>
            <CardHeader>
              <CardTitle>Profile Status</CardTitle>
            </CardHeader>
            <CardContent>
              <div className="space-y-2">
                <div className="flex justify-between items-center">
                  <span className="text-sm">Profile Complete</span>
                  <span className={`text-sm font-medium ${
                    profile?.resumeUrl ? 'text-green-600' : 'text-yellow-600'
                  }`}>
                    {profile?.resumeUrl ? '100%' : '80%'}
                  </span>
                </div>
                <div className="w-full bg-gray-200 rounded-full h-2">
                  <div
                    className={`h-2 rounded-full ${
                      profile?.resumeUrl ? 'bg-green-500' : 'bg-yellow-500'
                    }`}
                    style={{ width: profile?.resumeUrl ? '100%' : '80%' }}
                  />
                </div>
                {!profile?.resumeUrl && (
                  <p className="text-xs text-muted-foreground">
                    Upload your resume to complete your profile
                  </p>
                )}
              </div>
            </CardContent>
          </Card>
        </div>
      </div>
    </div>
  )
}