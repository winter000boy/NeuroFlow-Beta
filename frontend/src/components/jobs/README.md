# Job Detail and Application Components

This directory contains the components implemented for task 11.2: "Implement job detail and application components".

## Components Implemented

### 1. Job Detail Page (`/app/jobs/[id]/page.tsx`)
- **Purpose**: Complete job detail page with company information and application functionality
- **Features**:
  - Responsive layout with job header, description, and sidebar
  - Company information display with logo and website link
  - Job metadata (location, type, salary, posted date)
  - Role-based UI (different views for candidates, employers, and guests)
  - Back navigation and job status indicators
  - Integration with all other components

### 2. Job Application Modal (`job-application-modal.tsx`)
- **Purpose**: Modal for job application submission with confirmation flow
- **Features**:
  - Job information preview before application
  - Application guidelines and requirements
  - Success confirmation with auto-close
  - Error handling with user feedback
  - Loading states during submission
  - Toast notifications for user feedback

### 3. Application Status Component (`application-status.tsx`)
- **Purpose**: Display application status for candidates
- **Features**:
  - Status badges with appropriate colors and icons
  - Status descriptions for user clarity
  - Application date display
  - Employer notes display when available
  - Loading states
  - Support for all application statuses: APPLIED, IN_REVIEW, HIRED, REJECTED

### 4. Job Share Component (`job-share.tsx`)
- **Purpose**: Social media sharing functionality
- **Features**:
  - Copy link to clipboard functionality
  - Social media sharing (Facebook, Twitter, LinkedIn)
  - Share modal with job preview
  - URL generation and validation
  - Error handling for clipboard operations
  - Responsive design for mobile and desktop

### 5. Dialog Component (`/ui/dialog.tsx`)
- **Purpose**: Reusable modal/dialog component
- **Features**:
  - Backdrop with blur effect
  - Keyboard navigation (ESC to close)
  - Click outside to close
  - Accessible design with proper ARIA labels
  - Composable API with header, content, and footer sections

## Requirements Fulfilled

### Requirement 2.2 (Job Detail Display)
- ✅ Job detail pages display job title, description, salary, location, and company information
- ✅ Company information includes logo, name, and website link
- ✅ Job metadata is clearly presented with appropriate icons

### Requirement 2.3 (Job Application)
- ✅ Application button is prominently displayed for active jobs
- ✅ Application modal provides confirmation flow
- ✅ Prevents duplicate applications
- ✅ Success confirmation with email notification mention

### Requirement 2.4 (Application Status Tracking)
- ✅ Candidates can view their application status
- ✅ Status is displayed with appropriate visual indicators
- ✅ Application date and employer notes are shown
- ✅ Different statuses are handled: Applied, In Review, Hired, Rejected

## Technical Implementation

### State Management
- Uses React hooks for local state management
- Integrates with auth context for user role detection
- Handles loading and error states appropriately

### API Integration
- Uses JobService for all job-related API calls
- Implements proper error handling and user feedback
- Supports application status checking and submission

### UI/UX Features
- Responsive design works on desktop, tablet, and mobile
- Dark mode support throughout all components
- Loading states and skeleton screens
- Toast notifications for user feedback
- Accessible design with proper ARIA labels

### Security & Validation
- Role-based access control
- Input validation and sanitization
- Secure social media sharing URLs
- Proper error handling and user feedback

## Usage Examples

### Job Detail Page
```tsx
// Accessed via /jobs/[id] route
// Automatically loads job data and user application status
// Displays appropriate UI based on user role
```

### Job Application Modal
```tsx
<JobApplicationModal
  job={job}
  isOpen={showModal}
  onClose={() => setShowModal(false)}
  onApplicationSubmitted={() => {
    setHasApplied(true)
    setShowModal(false)
  }}
/>
```

### Application Status
```tsx
<ApplicationStatus jobId={job.id} />
```

### Job Share
```tsx
<JobShare job={job} />
```

## Testing

Test files have been created for all major components:
- `__tests__/job-application-modal.test.tsx`
- `__tests__/application-status.test.tsx`
- `__tests__/job-share.test.tsx`

Tests cover:
- Component rendering
- User interactions
- API integration
- Error handling
- Success flows
- Edge cases

## Dependencies

- React 19.1.0
- Next.js 15.5.2
- Lucide React (icons)
- Sonner (toast notifications)
- Tailwind CSS (styling)
- TypeScript (type safety)

## Notes

- The implementation follows the existing code patterns and architecture
- All components are fully typed with TypeScript
- Error handling is comprehensive with user-friendly messages
- The UI is consistent with the existing design system
- Components are reusable and well-documented