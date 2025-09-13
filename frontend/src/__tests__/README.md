# Frontend Testing Suite

This directory contains comprehensive tests for the Job Application Platform frontend.

## Test Structure

### Unit Tests
- **Components**: Tests for all React components including auth, jobs, profile, admin, and UI components
- **Hooks**: Tests for custom React hooks like `useAuth`, `useJobs`, `useProfile`
- **Services**: Tests for API service functions
- **Utils**: Tests for utility functions and helpers

### Integration Tests
- **Auth Flow**: Complete authentication workflows including login, registration, and logout
- **Job Search Flow**: End-to-end job search, filtering, and application processes
- **Profile Management**: User profile creation, editing, and file upload workflows

### End-to-End Tests (E2E)
- **Authentication**: Complete user authentication flows across different user roles
- **Job Search**: Job discovery, filtering, and application submission
- **Profile Management**: Profile editing, resume upload, and settings management

## Test Configuration

### Jest Configuration
- **Framework**: Jest with React Testing Library
- **Environment**: jsdom for DOM simulation
- **Coverage**: 80% threshold for branches, functions, lines, and statements
- **Mocking**: MSW (Mock Service Worker) for API mocking

### Playwright Configuration
- **Browsers**: Chrome, Firefox, Safari, Mobile Chrome, Mobile Safari
- **Reporters**: HTML reports with screenshots and traces
- **Parallel Execution**: Full parallel test execution

## Running Tests

### Unit and Integration Tests
```bash
# Run all tests
npm run test

# Run tests in watch mode
npm run test:watch

# Run tests with coverage
npm run test:coverage

# Run tests for CI
npm run test:ci
```

### End-to-End Tests
```bash
# Run E2E tests
npm run e2e

# Run E2E tests with UI
npm run e2e:ui

# Install Playwright browsers
npx playwright install
```

## Test Coverage

The test suite covers:

### Components (100% coverage target)
- ✅ Authentication components (login, register, protected routes)
- ✅ Job components (cards, listings, search, application modal)
- ✅ Profile components (candidate/employer profiles, file upload)
- ✅ Admin components (dashboard, user management, analytics)
- ✅ Layout components (header, footer, navigation)
- ✅ UI components (buttons, forms, modals, etc.)

### Hooks (100% coverage target)
- ✅ `useAuth` - Authentication state management
- ✅ `useJobs` - Job search and application functionality
- ✅ `useProfile` - User profile management
- ✅ `useEmployerJobs` - Employer job management
- ✅ `usePerformance` - Performance monitoring

### Integration Flows (100% coverage target)
- ✅ Complete authentication workflows
- ✅ Job search and application processes
- ✅ Profile management workflows
- ✅ Admin management flows
- ✅ Error handling and edge cases

### E2E Scenarios (Critical user journeys)
- ✅ User registration and login
- ✅ Job search with filters
- ✅ Job application submission
- ✅ Profile editing and file uploads
- ✅ Responsive design on mobile devices
- ✅ Error states and recovery

## Test Utilities

### Custom Render Function
Located in `src/__tests__/utils/test-utils.tsx`, provides:
- Pre-configured providers (Auth, Theme)
- Mock data generators
- Helper functions for common test scenarios

### Mock Service Worker (MSW)
Located in `src/__tests__/mocks/`, provides:
- API endpoint mocking
- Realistic response simulation
- Error scenario testing

### Test Data
Standardized mock data for:
- User profiles (candidates, employers, admins)
- Job listings and applications
- API responses and error states

## Best Practices

### Test Organization
- Group related tests in describe blocks
- Use descriptive test names that explain the expected behavior
- Follow AAA pattern (Arrange, Act, Assert)

### Mocking Strategy
- Mock external dependencies (APIs, services)
- Use MSW for HTTP request mocking
- Mock Next.js specific components and hooks

### Accessibility Testing
- Test keyboard navigation
- Verify ARIA labels and roles
- Ensure screen reader compatibility

### Performance Testing
- Test component rendering performance
- Verify lazy loading functionality
- Monitor bundle size impact

## Continuous Integration

The test suite is integrated with GitHub Actions:
- Runs on every push and pull request
- Tests across multiple Node.js versions
- Generates coverage reports
- Uploads test artifacts

## Troubleshooting

### Common Issues
1. **Module resolution**: Ensure path aliases are configured correctly
2. **MSW setup**: Verify MSW handlers are properly configured
3. **Async testing**: Use proper async/await patterns with waitFor
4. **Component mocking**: Mock complex dependencies appropriately

### Debug Tips
- Use `screen.debug()` to inspect rendered DOM
- Add `--verbose` flag for detailed test output
- Use `--detectOpenHandles` to find async issues
- Check browser console in E2E tests for errors