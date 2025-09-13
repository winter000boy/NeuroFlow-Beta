import { test, expect } from "@playwright/test";

const mockJobs = [
  {
    id: "1",
    title: "Software Engineer",
    description: "A great job opportunity for a software engineer",
    salary: { min: 80000, max: 120000, currency: "USD" },
    location: "San Francisco, CA",
    jobType: "FULL_TIME",
    isActive: true,
    createdAt: "2024-01-01T00:00:00Z",
    updatedAt: "2024-01-01T00:00:00Z",
    expiresAt: "2024-12-31T00:00:00Z",
    employer: {
      id: "emp1",
      companyName: "Tech Corp",
      logoUrl: "https://example.com/logo.png",
      website: "https://techcorp.com",
    },
  },
  {
    id: "2",
    title: "Frontend Developer",
    description: "React developer needed for exciting projects",
    salary: { min: 70000, max: 100000, currency: "USD" },
    location: "New York, NY",
    jobType: "REMOTE",
    isActive: true,
    createdAt: "2024-01-02T00:00:00Z",
    updatedAt: "2024-01-02T00:00:00Z",
    expiresAt: "2024-12-31T00:00:00Z",
    employer: {
      id: "emp2",
      companyName: "Frontend Inc",
      logoUrl: "https://example.com/logo2.png",
      website: "https://frontend.com",
    },
  },
];

test.describe("Job Search Flow", () => {
  test.beforeEach(async ({ page }) => {
    // Mock authentication
    await page.addInitScript(() => {
      localStorage.setItem("token", "mock-token");
    });

    // Mock API responses
    await page.route("**/api/jobs**", async (route) => {
      const url = new URL(route.request().url());
      const search = url.searchParams.get("search");
      const jobType = url.searchParams.get("jobType");

      let filteredJobs = mockJobs;

      if (search) {
        filteredJobs = mockJobs.filter(
          (job) =>
            job.title.toLowerCase().includes(search.toLowerCase()) ||
            job.description.toLowerCase().includes(search.toLowerCase())
        );
      }

      if (jobType) {
        filteredJobs = filteredJobs.filter((job) => job.jobType === jobType);
      }

      await route.fulfill({
        status: 200,
        contentType: "application/json",
        body: JSON.stringify({
          content: filteredJobs,
          totalPages: 1,
          totalElements: filteredJobs.length,
          number: 0,
          size: 10,
        }),
      });
    });

    await page.route("**/api/jobs/*/apply", async (route) => {
      if (route.request().method() === "POST") {
        await route.fulfill({
          status: 200,
          contentType: "application/json",
          body: JSON.stringify({
            id: "app1",
            jobId: route.request().url().split("/")[5],
            candidateId: "1",
            status: "APPLIED",
            appliedAt: "2024-01-01T00:00:00Z",
            updatedAt: "2024-01-01T00:00:00Z",
          }),
        });
      }
    });

    await page.route("**/api/auth/me", async (route) => {
      await route.fulfill({
        status: 200,
        contentType: "application/json",
        body: JSON.stringify({
          id: "1",
          email: "candidate@example.com",
          role: "CANDIDATE",
        }),
      });
    });
  });

  test("should display job listings", async ({ page }) => {
    await page.goto("/jobs");

    // Should show job listings
    await expect(page.locator('[data-testid="job-card"]')).toHaveCount(2);

    // Should show job details
    await expect(page.locator("text=Software Engineer")).toBeVisible();
    await expect(page.locator("text=Frontend Developer")).toBeVisible();
    await expect(page.locator("text=Tech Corp")).toBeVisible();
    await expect(page.locator("text=Frontend Inc")).toBeVisible();
  });

  test("should search jobs by keyword", async ({ page }) => {
    await page.goto("/jobs");

    // Wait for initial jobs to load
    await expect(page.locator('[data-testid="job-card"]')).toHaveCount(2);

    // Search for "frontend"
    await page.fill('[data-testid="search-input"]', "frontend");
    await page.click('[data-testid="search-button"]');

    // Should show only frontend job
    await expect(page.locator('[data-testid="job-card"]')).toHaveCount(1);
    await expect(page.locator("text=Frontend Developer")).toBeVisible();
    await expect(page.locator("text=Software Engineer")).not.toBeVisible();
  });

  test("should filter jobs by type", async ({ page }) => {
    await page.goto("/jobs");

    // Wait for initial jobs to load
    await expect(page.locator('[data-testid="job-card"]')).toHaveCount(2);

    // Filter by remote jobs
    await page.selectOption('[data-testid="job-type-select"]', "REMOTE");
    await page.click('[data-testid="search-button"]');

    // Should show only remote job
    await expect(page.locator('[data-testid="job-card"]')).toHaveCount(1);
    await expect(page.locator("text=Frontend Developer")).toBeVisible();
    await expect(page.locator("text=Remote")).toBeVisible();
  });

  test("should apply for a job", async ({ page }) => {
    await page.goto("/jobs");

    // Wait for jobs to load
    await expect(page.locator('[data-testid="job-card"]')).toHaveCount(2);

    // Click apply on first job
    await page.click('[data-testid="apply-button"]:first-child');

    // Should open application modal
    await expect(
      page.locator('[data-testid="application-modal"]')
    ).toBeVisible();
    await expect(page.locator("text=Apply for Position")).toBeVisible();
    await expect(page.locator("text=Software Engineer")).toBeVisible();

    // Submit application
    await page.click('[data-testid="submit-application-button"]');

    // Should show success message
    await expect(page.locator('[data-testid="toast"]')).toContainText(
      "Application submitted successfully!"
    );

    // Modal should close
    await expect(
      page.locator('[data-testid="application-modal"]')
    ).not.toBeVisible();
  });

  test("should view job details", async ({ page }) => {
    await page.goto("/jobs");

    // Wait for jobs to load
    await expect(page.locator('[data-testid="job-card"]')).toHaveCount(2);

    // Click on job title to view details
    await page.click("text=Software Engineer");

    // Should navigate to job detail page
    await expect(page).toHaveURL("/jobs/1");

    // Should show job details
    await expect(page.locator("text=Software Engineer")).toBeVisible();
    await expect(page.locator("text=Tech Corp")).toBeVisible();
    await expect(page.locator("text=San Francisco, CA")).toBeVisible();
    await expect(page.locator("text=$80,000 - $120,000")).toBeVisible();
  });

  test("should clear search filters", async ({ page }) => {
    await page.goto("/jobs");

    // Apply filters
    await page.fill('[data-testid="search-input"]', "frontend");
    await page.fill('[data-testid="location-input"]', "New York");
    await page.selectOption('[data-testid="job-type-select"]', "REMOTE");
    await page.click('[data-testid="search-button"]');

    // Should show filtered results
    await expect(page.locator('[data-testid="job-card"]')).toHaveCount(1);

    // Clear filters
    await page.click('[data-testid="clear-filters-button"]');

    // Should show all jobs again
    await expect(page.locator('[data-testid="job-card"]')).toHaveCount(2);

    // Form should be cleared
    await expect(page.locator('[data-testid="search-input"]')).toHaveValue("");
    await expect(page.locator('[data-testid="location-input"]')).toHaveValue(
      ""
    );
  });

  test("should handle empty search results", async ({ page }) => {
    await page.goto("/jobs");

    // Search for non-existent job
    await page.fill('[data-testid="search-input"]', "nonexistent");
    await page.click('[data-testid="search-button"]');

    // Should show empty state
    await expect(page.locator('[data-testid="empty-state"]')).toBeVisible();
    await expect(page.locator("text=No jobs found")).toBeVisible();
    await expect(
      page.locator("text=Try adjusting your search criteria")
    ).toBeVisible();
  });

  test("should be responsive on mobile", async ({ page }) => {
    // Set mobile viewport
    await page.setViewportSize({ width: 375, height: 667 });
    await page.goto("/jobs");

    // Should show mobile layout
    await expect(
      page.locator('[data-testid="mobile-search-toggle"]')
    ).toBeVisible();

    // Toggle search filters
    await page.click('[data-testid="mobile-search-toggle"]');
    await expect(page.locator('[data-testid="search-form"]')).toBeVisible();

    // Job cards should stack vertically
    const jobCards = page.locator('[data-testid="job-card"]');
    await expect(jobCards).toHaveCount(2);

    // Check that cards are stacked (not side by side)
    const firstCard = jobCards.first();
    const secondCard = jobCards.nth(1);

    const firstCardBox = await firstCard.boundingBox();
    const secondCardBox = await secondCard.boundingBox();

    expect(secondCardBox!.y).toBeGreaterThan(
      firstCardBox!.y + firstCardBox!.height - 10
    );
  });

  test("should show job application status", async ({ page }) => {
    // Mock applied jobs
    await page.route("**/api/applications/my", async (route) => {
      await route.fulfill({
        status: 200,
        contentType: "application/json",
        body: JSON.stringify([
          {
            id: "app1",
            jobId: "1",
            status: "APPLIED",
            appliedAt: "2024-01-01T00:00:00Z",
          },
        ]),
      });
    });

    await page.goto("/jobs");

    // Should show applied status for job 1
    const firstJobCard = page.locator('[data-testid="job-card"]').first();
    await expect(
      firstJobCard.locator('[data-testid="application-status"]')
    ).toContainText("Applied");

    // Apply button should be disabled or show different text
    await expect(
      firstJobCard.locator('[data-testid="apply-button"]')
    ).toContainText("Applied");
  });
});
