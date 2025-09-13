import { test, expect } from '@playwright/test'

test.describe('Authentication Flow', () => {
  test.beforeEach(async ({ page }) => {
    // Mock API responses
    await page.route('**/api/auth/login', async route => {
      if (route.request().method() === 'POST') {
        const postData = route.request().postDataJSON()
        if (postData.email === 'test@example.com' && postData.password === 'password123') {
          await route.fulfill({
            status: 200,
            contentType: 'application/json',
            body: JSON.stringify({
              token: 'mock-token',
              user: {
                id: '1',
                email: 'test@example.com',
                role: 'CANDIDATE',
              },
            }),
          })
        } else {
          await route.fulfill({
            status: 401,
            contentType: 'application/json',
            body: JSON.stringify({ message: 'Invalid credentials' }),
          })
        }
      }
    })

    await page.route('**/api/auth/register/candidate', async route => {
      if (route.request().method() === 'POST') {
        await route.fulfill({
          status: 200,
          contentType: 'application/json',
          body: JSON.stringify({ message: 'Registration successful' }),
        })
      }
    })
  })

  test('should login successfully with valid credentials', async ({ page }) => {
    await page.goto('/login')

    // Fill login form
    await page.fill('[data-testid="email-input"]', 'test@example.com')
    await page.fill('[data-testid="password-input"]', 'password123')
    
    // Submit form
    await page.click('[data-testid="login-button"]')

    // Should redirect to home page
    await expect(page).toHaveURL('/')
    
    // Should show success message
    await expect(page.locator('[data-testid="toast"]')).toContainText('Login successful!')
  })

  test('should show error with invalid credentials', async ({ page }) => {
    await page.goto('/login')

    // Fill login form with invalid credentials
    await page.fill('[data-testid="email-input"]', 'test@example.com')
    await page.fill('[data-testid="password-input"]', 'wrongpassword')
    
    // Submit form
    await page.click('[data-testid="login-button"]')

    // Should show error message
    await expect(page.locator('[data-testid="toast"]')).toContainText('Invalid credentials')
    
    // Should remain on login page
    await expect(page).toHaveURL('/login')
  })

  test('should validate required fields', async ({ page }) => {
    await page.goto('/login')

    // Try to submit without filling fields
    await page.click('[data-testid="login-button"]')

    // Should show validation errors
    await expect(page.locator('[data-testid="email-error"]')).toContainText('Email is required')
    await expect(page.locator('[data-testid="password-error"]')).toContainText('Password is required')
  })

  test('should register new candidate successfully', async ({ page }) => {
    await page.goto('/register')

    // Fill registration form
    await page.fill('[data-testid="name-input"]', 'John Doe')
    await page.fill('[data-testid="email-input"]', 'john@example.com')
    await page.fill('[data-testid="phone-input"]', '+1234567890')
    await page.fill('[data-testid="degree-input"]', 'Computer Science')
    await page.fill('[data-testid="graduation-year-input"]', '2022')
    await page.fill('[data-testid="password-input"]', 'password123')

    // Submit form
    await page.click('[data-testid="register-button"]')

    // Should redirect to login page
    await expect(page).toHaveURL('/login')
    
    // Should show success message
    await expect(page.locator('[data-testid="toast"]')).toContainText('Registration successful!')
  })

  test('should switch between candidate and employer registration', async ({ page }) => {
    await page.goto('/register')

    // Should show candidate form by default
    await expect(page.locator('[data-testid="name-input"]')).toBeVisible()
    await expect(page.locator('[data-testid="degree-input"]')).toBeVisible()

    // Switch to employer tab
    await page.click('[data-testid="employer-tab"]')

    // Should show employer form
    await expect(page.locator('[data-testid="company-name-input"]')).toBeVisible()
    await expect(page.locator('[data-testid="website-input"]')).toBeVisible()
    await expect(page.locator('[data-testid="name-input"]')).not.toBeVisible()
  })

  test('should navigate between login and register pages', async ({ page }) => {
    await page.goto('/login')

    // Click register link
    await page.click('[data-testid="register-link"]')
    await expect(page).toHaveURL('/register')

    // Click login link
    await page.click('[data-testid="login-link"]')
    await expect(page).toHaveURL('/login')
  })

  test('should show loading state during login', async ({ page }) => {
    // Add delay to login API
    await page.route('**/api/auth/login', async route => {
      await new Promise(resolve => setTimeout(resolve, 1000))
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify({
          token: 'mock-token',
          user: { id: '1', email: 'test@example.com', role: 'CANDIDATE' },
        }),
      })
    })

    await page.goto('/login')

    await page.fill('[data-testid="email-input"]', 'test@example.com')
    await page.fill('[data-testid="password-input"]', 'password123')
    
    // Click login and check loading state
    await page.click('[data-testid="login-button"]')
    
    // Should show loading state
    await expect(page.locator('[data-testid="login-button"]')).toContainText('Signing in...')
    await expect(page.locator('[data-testid="login-button"]')).toBeDisabled()
  })
})