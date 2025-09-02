import { z } from 'zod'

// URL validation helper
const urlSchema = z
  .string()
  .optional()
  .refine((val) => {
    if (!val || val === '') return true
    try {
      new URL(val)
      return true
    } catch {
      return false
    }
  }, 'Please enter a valid URL')

// LinkedIn profile validation
const linkedinSchema = z
  .string()
  .optional()
  .refine((val) => {
    if (!val || val === '') return true
    return val.includes('linkedin.com/in/') || val.includes('linkedin.com/pub/')
  }, 'Please enter a valid LinkedIn profile URL')

export const candidateProfileSchema = z.object({
  name: z
    .string()
    .min(1, 'Name is required')
    .min(2, 'Name must be at least 2 characters'),
  email: z
    .string()
    .min(1, 'Email is required')
    .email('Please enter a valid email address'),
  phone: z
    .string()
    .min(1, 'Phone number is required')
    .regex(/^\+?[\d\s-()]+$/, 'Please enter a valid phone number'),
  degree: z
    .string()
    .min(1, 'Degree is required')
    .min(2, 'Degree must be at least 2 characters'),
  graduationYear: z
    .number()
    .min(1950, 'Graduation year must be after 1950')
    .max(new Date().getFullYear() + 10, 'Graduation year cannot be more than 10 years in the future'),
  linkedinProfile: linkedinSchema,
  portfolioUrl: urlSchema,
})

export const employerProfileSchema = z.object({
  name: z
    .string()
    .min(1, 'Contact name is required')
    .min(2, 'Name must be at least 2 characters'),
  email: z
    .string()
    .min(1, 'Email is required')
    .email('Please enter a valid email address'),
  companyName: z
    .string()
    .min(1, 'Company name is required')
    .min(2, 'Company name must be at least 2 characters'),
  website: urlSchema,
  description: z
    .string()
    .min(1, 'Company description is required')
    .min(10, 'Description must be at least 10 characters')
    .max(1000, 'Description cannot exceed 1000 characters'),
  address: z
    .string()
    .optional(),
})

export const passwordChangeSchema = z.object({
  currentPassword: z
    .string()
    .min(1, 'Current password is required'),
  newPassword: z
    .string()
    .min(1, 'New password is required')
    .min(6, 'Password must be at least 6 characters')
    .regex(
      /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)/,
      'Password must contain at least one uppercase letter, one lowercase letter, and one number'
    ),
  confirmPassword: z.string().min(1, 'Please confirm your new password'),
}).refine((data) => data.newPassword === data.confirmPassword, {
  message: "Passwords don't match",
  path: ["confirmPassword"],
})

export type CandidateProfileFormData = z.infer<typeof candidateProfileSchema>
export type EmployerProfileFormData = z.infer<typeof employerProfileSchema>
export type PasswordChangeFormData = z.infer<typeof passwordChangeSchema>