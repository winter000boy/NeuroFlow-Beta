import { test, expect } from '@playwright/test'

test.describe('Profile Management Flow', () => {
  test.beforeEach(async ({ page }) => {
    // Mock authentication
    await page.addInitScript(() => {
      localStorage.setItem('token', 'mock-token')
    })

    // Mock API responses
    await page.route('**/api/auth/me', async route => {
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify({
          id: '1',
          email: 'candidate@example.com',
          role: 'CANDIDATE',
          name: 'John Doe',
          phone: '+1234567890',
          degree: 'Computer Science',
          graduationYear: 2022,
          resumeUrl: 'https://example.com/resume.pdf',
          linkedinProfile: 'https://linkedin.com/in/johndoe',
          portfolioUrl: 'https://johndoe.dev',
        }),
      })
    })

    await page.route('**/api/users/profile', async route => {
      if (route.request().method() === 'GET') {
        await route.fulfill({
          status: 200,
          contentType: 'application/json',
          body: JSON.stringify({
            id: '1',
            email: 'candidate@example.com',
            name: 'John Doe',
            phone: '+1234567890',
            degree: 'Computer Science',
            graduationYear: 2022,
            resumeUrl: 'https://example.com/resume.pdf',
            linkedinProfile: 'https://linkedin.com/in/johndoe',
            portfolioUrl: 'https://johndoe.dev',
          }),
        })
      } else if (route.request().method() === 'PUT') {
        const postData = route.request().postDataJSON()
        await route.fulfill({
          status: 200,
          contentType: 'application/json',
          body: JSON.stringify({
            ...postData,
            id: '1',
            email: 'candidate@example.com',
          }),
        })
      }
    })

    await page.route('**/api/users/upload/resume', async route => {
      if (route.request().method() === 'POST') {
        await route.fulfill({
          status: 200,
          contentType: 'application/json',
          body: JSON.stringify({
            url: 'https://example.com/new-resume.pdf',
            filename: 'resume.pdf',
          }),
        })
      }
    })
  })

  test('should display candidate profile information', async ({ page }) => {
    await page.goto('/profile')

    // Should show profile information
    await expect(page.locator('[data-testid="profile-name"]')).toContainText('John Doe')
    await expect(page.locator('[data-testid="profile-email"]')).toContainText('candidate@example.com')
    await expect(page.locator('[data-testid="profile-phone"]')).toContainText('+1234567890')
    await expect(page.locator('[data-testid="profile-degree"]')).toContainText('Computer Science')
    await expect(page.locator('[data-testid="profile-graduation-year"]')).toContainText('2022')
  })

  test('should edit profile information', async ({ page }) => {
    await page.goto('/profile')

    // Click edit button
    await page.click('[data-testid="edit-profile-button"]')

    // Should show edit form
    await expect(page.locator('[data-testid="profile-form"]')).toBeVisible()

    // Update profile information
    await page.fill('[data-testid="name-input"]', 'Jane Doe')
    await page.fill('[data-testid="phone-input"]', '+0987654321')
    await page.fill('[data-testid="degree-input"]', 'Software Engineering')
    await page.fill('[data-testid="graduation-year-input"]', '2023')
    await page.fill('[data-testid="linkedin-input"]', 'https://linkedin.com/in/janedoe')
    await page.fill('[data-testid="portfolio-input"]', 'https://janedoe.dev')

    // Save changes
    await page.click('[data-testid="save-profile-button"]')

    // Should show success message
    await expect(page.locator('[data-testid="toast"]')).toContainText('Profile updated successfully!')

    // Should show updated information
    await expect(page.locator('[data-testid="profile-name"]')).toContainText('Jane Doe')
    await expect(page.locator('[data-testid="profile-phone"]')).toContainText('+0987654321')
  })

  test('should upload resume', async ({ page }) => {
    await page.goto('/profile')

    // Should show current resume
    await expect(page.locator('[data-testid="current-resume"]')).toBeVisible()
    await expect(page.locator('[data-testid="resume-download-link"]')).toHaveAttribute('href', 'https://example.com/resume.pdf')

    // Click upload new resume
    await page.click('[data-testid="upload-resume-button"]')

    // Should show file upload dialog
    await expect(page.locator('[data-testid="file-upload-modal"]')).toBeVisible()

    // Mock file upload
    const fileChooserPromise = page.waitForEvent('filechooser')
    await page.click('[data-testid="file-input"]')
    const fileChooser = await fileChooserPromise
    
    // Create a mock file
    await fileChooser.setFiles({
      name: 'new-resume.pdf',
      mimeType: 'application/pdf',
      buffer: Buffer.from('mock pdf content'),
    })

    // Upload file
    await page.click('[data-testid="upload-file-button"]')

    // Should show success message
    await expect(page.locator('[data-testid="toast"]')).toContainText('Resume uploaded successfully!')

    // Should update resume link
    await expect(page.locator('[data-testid="resume-download-link"]')).toHaveAttribute('href', 'https://example.com/new-resume.pdf')
  })

  test('should validate profile form', async ({ page }) => {
    await page.goto('/profile')

    // Click edit button
    await page.click('[data-testid="edit-profile-button"]')

    // Clear required fields
    await page.fill('[data-testid="name-input"]', '')
    await page.fill('[data-testid="phone-input"]', '')

    // Try to save
    await page.click('[data-testid="save-profile-button"]')

    // Should show validation errors
    await expect(page.locator('[data-testid="name-error"]')).toContainText('Name is required')
    await expect(page.locator('[data-testid="phone-error"]')).toContainText('Phone is required')
  })

  test('should validate social links format', async ({ page }) => {
    await page.goto('/profile')

    // Click edit button
    await page.click('[data-testid="edit-profile-button"]')

    // Enter invalid URLs
    await page.fill('[data-testid="linkedin-input"]', 'invalid-url')
    await page.fill('[data-testid="portfolio-input"]', 'not-a-url')

    // Try to save
    await page.click('[data-testid="save-profile-button"]')

    // Should show validation errors
    await expect(page.locator('[data-testid="linkedin-error"]')).toContainText('Invalid LinkedIn URL')
    await expect(page.locator('[data-testid="portfolio-error"]')).toContainText('Invalid URL format')
  })

  test('should cancel profile editing', async ({ page }) => {
    await page.goto('/profile')

    const originalName = await page.locator('[data-testid="profile-name"]').textContent()

    // Click edit button
    await page.click('[data-testid="edit-profile-button"]')

    // Make changes
    await page.fill('[data-testid="name-input"]', 'Changed Name')

    // Cancel editing
    await page.click('[data-testid="cancel-edit-button"]')

    // Should revert to original values
    await expect(page.locator('[data-testid="profile-name"]')).toContainText(originalName!)
    await expect(page.locator('[data-testid="profile-form"]')).not.toBeVisible()
  })

  test('should change password', async ({ page }) => {
    // Mock password change API
    await page.route('**/api/users/change-password', async route => {
      if (route.request().method() === 'POST') {
        const postData = route.request().postDataJSON()
        if (postData.currentPassword === 'oldpassword' && postData.newPassword === 'newpassword123') {
          await route.fulfill({
            status: 200,
            contentType: 'application/json',
            body: JSON.stringify({ message: 'Password changed successfully' }),
          })
        } else {
          await route.fulfill({
            status: 400,
            contentType: 'application/json',
            body: JSON.stringify({ message: 'Current password is incorrect' }),
          })
        }
      }
    })

    await page.goto('/profile/settings')

    // Fill password change form
    await page.fill('[data-testid="current-password-input"]', 'oldpassword')
    await page.fill('[data-testid="new-password-input"]', 'newpassword123')
    await page.fill('[data-testid="confirm-password-input"]', 'newpassword123')

    // Submit form
    await page.click('[data-testid="change-password-button"]')

    // Should show success message
    await expect(page.locator('[data-testid="toast"]')).toContainText('Password changed successfully')

    // Form should be cleared
    await expect(page.locator('[data-testid="current-password-input"]')).toHaveValue('')
    await expect(page.locator('[data-testid="new-password-input"]')).toHaveValue('')
    await expect(page.locator('[data-testid="confirm-password-input"]')).toHaveValue('')
  })

  test('should validate password change form', async ({ page }) => {
    await page.goto('/profile/settings')

    // Try to submit without filling fields
    await page.click('[data-testid="change-password-button"]')

    // Should show validation errors
    await expect(page.locator('[data-testid="current-password-error"]')).toContainText('Current password is required')
    await expect(page.locator('[data-testid="new-password-error"]')).toContainText('New password is required')

    // Test password mismatch
    await page.fill('[data-testid="new-password-input"]', 'newpassword123')
    await page.fill('[data-testid="confirm-password-input"]', 'differentpassword')
    await page.click('[data-testid="change-password-button"]')

    await expect(page.locator('[data-testid="confirm-password-error"]')).toContainText('Passwords do not match')
  })

  test('should handle file upload errors', async ({ page }) => {
    // Mock file upload error
    await page.route('**/api/users/upload/resume', async route => {
      await route.fulfill({
        status: 400,
        contentType: 'application/json',
        body: JSON.stringify({ message: 'File too large' }),
      })
    })

    await page.goto('/profile')

    // Try to upload file
    await page.click('[data-testid="upload-resume-button"]')

    const fileChooserPromise = page.waitForEvent('filechooser')
    await page.click('[data-testid="file-input"]')
    const fileChooser = await fileChooserPromise
    
    await fileChooser.setFiles({
      name: 'large-resume.pdf',
      mimeType: 'application/pdf',
      buffer: Buffer.from('mock large pdf content'),
    })

    await page.click('[data-testid="upload-file-button"]')

    // Should show error message
    await expect(page.locator('[data-testid="toast"]')).toContainText('File too large')
  })

  test('should be responsive on mobile', async ({ page }) => {
    // Set mobile viewport
    await page.setViewportSize({ width: 375, height: 667 })
    await page.goto('/profile')

    // Should show mobile layout
    await expect(page.locator('[data-testid="profile-container"]')).toBeVisible()

    // Profile sections should stack vertically
    const profileSections = page.locator('[data-testid="profile-section"]')
    const firstSection = profileSections.first()
    const secondSection = profileSections.nth(1)
    
    const firstSectionBox = await firstSection.boundingBox()
    const secondSectionBox = await secondSection.boundingBox()
    
    expect(secondSectionBox!.y).toBeGreaterThan(firstSectionBox!.y + firstSectionBox!.height - 10)
  })
})