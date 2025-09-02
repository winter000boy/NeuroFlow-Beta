'use client'

import { useState, useEffect } from 'react'
import { useParams, useRouter } from 'next/navigation'
import { toast } from 'sonner'
import {
    ArrowLeft,
    MapPin,
    DollarSign,
    Building2,
    ExternalLink,
    Calendar,
    Users,
    Briefcase
} from 'lucide-react'
import { Job } from '@/types/job'
import { JobService } from '@/services/job.service'
import { useAuth } from '@/contexts/auth-context'
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Badge } from '@/components/ui/badge'
import { JobApplicationModal } from '@/components/jobs/job-application-modal'
import { ApplicationStatus } from '@/components/jobs/application-status'
import { JobShare } from '@/components/jobs/job-share'

export default function JobDetailPage() {
    const params = useParams()
    const router = useRouter()
    const { user } = useAuth()
    const [job, setJob] = useState<Job | null>(null)
    const [loading, setLoading] = useState(true)
    const [showApplicationModal, setShowApplicationModal] = useState(false)
    const [hasApplied, setHasApplied] = useState(false)

    const jobId = params.id as string

    useEffect(() => {
        const fetchJob = async () => {
            try {
                const jobData = await JobService.getJobById(jobId)
                setJob(jobData)

                // Check if user has already applied (only for candidates)
                if (user?.role === 'CANDIDATE') {
                    const applicationStatus = await JobService.getApplicationStatus(jobId)
                    setHasApplied(!!applicationStatus)
                }
            } catch (error) {
                console.error('Error fetching job:', error)
                toast.error('Failed to load job details')
                router.push('/jobs')
            } finally {
                setLoading(false)
            }
        }

        if (jobId) {
            fetchJob()
        }
    }, [jobId, user, router])

    const handleApplicationSubmitted = () => {
        setHasApplied(true)
        setShowApplicationModal(false)
    }

    const formatJobType = (jobType: string) => {
        return jobType.replace('_', ' ').toLowerCase().replace(/\b\w/g, l => l.toUpperCase())
    }

    const formatSalary = (salary: Job['salary']) => {
        return `$${salary.min.toLocaleString()} - $${salary.max.toLocaleString()} ${salary.currency}`
    }

    if (loading) {
        return (
            <div className="container mx-auto px-4 py-8">
                <div className="animate-pulse space-y-6">
                    <div className="h-8 bg-gray-200 dark:bg-gray-700 rounded w-1/4"></div>
                    <div className="h-12 bg-gray-200 dark:bg-gray-700 rounded w-3/4"></div>
                    <div className="h-64 bg-gray-200 dark:bg-gray-700 rounded"></div>
                </div>
            </div>
        )
    }

    if (!job) {
        return (
            <div className="container mx-auto px-4 py-8">
                <div className="text-center">
                    <h1 className="text-2xl font-bold text-gray-900 dark:text-white">Job Not Found</h1>
                    <p className="text-gray-600 dark:text-gray-400 mt-2">
                        The job you're looking for doesn't exist or has been removed.
                    </p>
                    <Button onClick={() => router.push('/jobs')} className="mt-4">
                        Back to Jobs
                    </Button>
                </div>
            </div>
        )
    }

    return (
        <div className="container mx-auto px-4 py-8">
            {/* Back button */}
            <Button
                variant="ghost"
                onClick={() => router.back()}
                className="mb-6 flex items-center space-x-2"
            >
                <ArrowLeft className="h-4 w-4" />
                <span>Back</span>
            </Button>

            <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
                {/* Main content */}
                <div className="lg:col-span-2 space-y-6">
                    {/* Job header */}
                    <Card>
                        <CardHeader>
                            <div className="flex items-start justify-between">
                                <div className="flex-1">
                                    <CardTitle className="text-2xl font-bold text-gray-900 dark:text-white">
                                        {job.title}
                                    </CardTitle>
                                    <div className="flex items-center space-x-2 mt-2">
                                        <Building2 className="h-4 w-4 text-gray-500" />
                                        <span className="text-lg font-medium text-gray-700 dark:text-gray-300">
                                            {job.employer.companyName}
                                        </span>
                                        {job.employer.website && (
                                            <a
                                                href={job.employer.website}
                                                target="_blank"
                                                rel="noopener noreferrer"
                                                className="text-blue-600 hover:text-blue-700 dark:text-blue-400"
                                            >
                                                <ExternalLink className="h-4 w-4" />
                                            </a>
                                        )}
                                    </div>
                                </div>
                                {job.employer.logoUrl && (
                                    <img
                                        src={job.employer.logoUrl}
                                        alt={`${job.employer.companyName} logo`}
                                        className="w-16 h-16 rounded-lg object-cover"
                                    />
                                )}
                            </div>

                            {/* Job meta info */}
                            <div className="flex flex-wrap items-center gap-4 mt-4 text-sm text-gray-600 dark:text-gray-400">
                                <div className="flex items-center space-x-1">
                                    <MapPin className="h-4 w-4" />
                                    <span>{job.location}</span>
                                </div>
                                <div className="flex items-center space-x-1">
                                    <Briefcase className="h-4 w-4" />
                                    <span>{formatJobType(job.jobType)}</span>
                                </div>
                                <div className="flex items-center space-x-1">
                                    <DollarSign className="h-4 w-4" />
                                    <span>{formatSalary(job.salary)}</span>
                                </div>
                                <div className="flex items-center space-x-1">
                                    <Calendar className="h-4 w-4" />
                                    <span>Posted {new Date(job.createdAt).toLocaleDateString()}</span>
                                </div>
                            </div>

                            {/* Job status badge */}
                            <div className="mt-4">
                                <Badge variant={job.isActive ? "default" : "secondary"}>
                                    {job.isActive ? "Active" : "Inactive"}
                                </Badge>
                            </div>
                        </CardHeader>
                    </Card>

                    {/* Job description */}
                    <Card>
                        <CardHeader>
                            <CardTitle>Job Description</CardTitle>
                        </CardHeader>
                        <CardContent>
                            <div className="prose dark:prose-invert max-w-none">
                                <p className="whitespace-pre-wrap">{job.description}</p>
                            </div>
                        </CardContent>
                    </Card>
                </div>

                {/* Sidebar */}
                <div className="space-y-6">
                    {/* Application section */}
                    <Card>
                        <CardHeader>
                            <CardTitle className="flex items-center space-x-2">
                                <Users className="h-5 w-5" />
                                <span>Application</span>
                            </CardTitle>
                        </CardHeader>
                        <CardContent className="space-y-4">
                            {user?.role === 'CANDIDATE' ? (
                                <>
                                    {hasApplied ? (
                                        <ApplicationStatus jobId={job.id} />
                                    ) : (
                                        <>
                                            {job.isActive ? (
                                                <Button
                                                    onClick={() => setShowApplicationModal(true)}
                                                    className="w-full"
                                                    size="lg"
                                                >
                                                    Apply Now
                                                </Button>
                                            ) : (
                                                <div className="text-center p-4 bg-gray-50 dark:bg-gray-800 rounded-lg">
                                                    <p className="text-sm text-gray-600 dark:text-gray-400">
                                                        This job is no longer accepting applications
                                                    </p>
                                                </div>
                                            )}
                                        </>
                                    )}
                                </>
                            ) : user?.role === 'EMPLOYER' ? (
                                <div className="text-center p-4 bg-blue-50 dark:bg-blue-900/20 rounded-lg">
                                    <p className="text-sm text-blue-800 dark:text-blue-200">
                                        You are viewing this job as an employer
                                    </p>
                                </div>
                            ) : (
                                <div className="text-center p-4 bg-gray-50 dark:bg-gray-800 rounded-lg">
                                    <p className="text-sm text-gray-600 dark:text-gray-400 mb-3">
                                        Sign in as a candidate to apply for this job
                                    </p>
                                    <Button
                                        variant="outline"
                                        onClick={() => router.push('/login')}
                                        className="w-full"
                                    >
                                        Sign In
                                    </Button>
                                </div>
                            )}
                        </CardContent>
                    </Card>

                    {/* Share job */}
                    <Card>
                        <CardHeader>
                            <CardTitle>Share this Job</CardTitle>
                        </CardHeader>
                        <CardContent>
                            <JobShare job={job} />
                        </CardContent>
                    </Card>

                    {/* Job details */}
                    <Card>
                        <CardHeader>
                            <CardTitle>Job Details</CardTitle>
                        </CardHeader>
                        <CardContent className="space-y-3">
                            <div className="flex justify-between">
                                <span className="text-sm text-gray-600 dark:text-gray-400">Job Type</span>
                                <span className="text-sm font-medium">{formatJobType(job.jobType)}</span>
                            </div>
                            <div className="flex justify-between">
                                <span className="text-sm text-gray-600 dark:text-gray-400">Location</span>
                                <span className="text-sm font-medium">{job.location}</span>
                            </div>
                            <div className="flex justify-between">
                                <span className="text-sm text-gray-600 dark:text-gray-400">Salary Range</span>
                                <span className="text-sm font-medium">{formatSalary(job.salary)}</span>
                            </div>
                            <div className="flex justify-between">
                                <span className="text-sm text-gray-600 dark:text-gray-400">Posted</span>
                                <span className="text-sm font-medium">
                                    {new Date(job.createdAt).toLocaleDateString()}
                                </span>
                            </div>
                            {job.expiresAt && (
                                <div className="flex justify-between">
                                    <span className="text-sm text-gray-600 dark:text-gray-400">Expires</span>
                                    <span className="text-sm font-medium">
                                        {new Date(job.expiresAt).toLocaleDateString()}
                                    </span>
                                </div>
                            )}
                        </CardContent>
                    </Card>
                </div>
            </div>

            {/* Application Modal */}
            {user?.role === 'CANDIDATE' && job && (
                <JobApplicationModal
                    job={job}
                    isOpen={showApplicationModal}
                    onClose={() => setShowApplicationModal(false)}
                    onApplicationSubmitted={handleApplicationSubmitted}
                />
            )}
        </div>
    )
}