"use client"

import Link from "next/link"
import { Button } from "@/components/ui/button"
import { ThemeToggle } from "@/components/theme-toggle"
import { Navigation } from "./navigation"
import { useAuth } from "@/contexts/auth-context"
import { UserMenu } from "./user-menu"

export function Header() {
  const { user, isLoading } = useAuth()

  return (
    <header className="sticky top-0 z-50 w-full border-b bg-background/95 backdrop-blur supports-[backdrop-filter]:bg-background/60">
      <div className="container mx-auto flex h-16 items-center justify-between px-4">
        {/* Logo */}
        <Link href="/" className="flex items-center space-x-2">
          <div className="h-8 w-8 rounded-lg bg-primary flex items-center justify-center">
            <span className="text-primary-foreground font-bold text-lg">J</span>
          </div>
          <span className="font-bold text-xl">JobApp</span>
        </Link>

        {/* Desktop Navigation */}
        <div className="hidden md:flex items-center space-x-6">
          <Link 
            href="/jobs" 
            className="text-sm font-medium transition-colors hover:text-primary"
          >
            Find Jobs
          </Link>
          <Link 
            href="/companies" 
            className="text-sm font-medium transition-colors hover:text-primary"
          >
            Companies
          </Link>
          <Link 
            href="/about" 
            className="text-sm font-medium transition-colors hover:text-primary"
          >
            About
          </Link>
        </div>

        {/* Actions */}
        <div className="flex items-center space-x-4">
          <ThemeToggle />
          
          {/* Authentication-aware content */}
          {!isLoading && (
            <>
              {user ? (
                <UserMenu user={user} />
              ) : (
                <div className="hidden sm:flex items-center space-x-2">
                  <Button variant="ghost" asChild>
                    <Link href="/login">Sign In</Link>
                  </Button>
                  <Button asChild>
                    <Link href="/register">Get Started</Link>
                  </Button>
                </div>
              )}
            </>
          )}

          {/* Mobile Navigation */}
          <Navigation className="md:hidden" />
        </div>
      </div>
    </header>
  )
}