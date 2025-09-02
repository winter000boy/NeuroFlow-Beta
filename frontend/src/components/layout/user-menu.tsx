'use client'

import { useState } from 'react'
import Link from 'next/link'
import { User, LogOut, Settings, Briefcase, Users, BarChart3 } from 'lucide-react'
import { Button } from '@/components/ui/button'
import { useAuth } from '@/contexts/auth-context'
import type { User as UserType } from '@/contexts/auth-context'

interface UserMenuProps {
  user: UserType
}

export function UserMenu({ user }: UserMenuProps) {
  const [isOpen, setIsOpen] = useState(false)
  const { logout } = useAuth()

  const handleLogout = () => {
    logout()
    setIsOpen(false)
  }

  const getMenuItems = () => {
    const baseItems = [
      {
        href: '/profile',
        icon: User,
        label: 'Profile',
      },
      {
        href: '/profile/settings',
        icon: Settings,
        label: 'Settings',
      },
    ]

    if (user.role === 'CANDIDATE') {
      return [
        {
          href: '/jobs',
          icon: Briefcase,
          label: 'Browse Jobs',
        },
        {
          href: '/applications',
          icon: BarChart3,
          label: 'My Applications',
        },
        ...baseItems,
      ]
    }

    if (user.role === 'EMPLOYER') {
      return [
        {
          href: '/employer/dashboard',
          icon: BarChart3,
          label: 'Dashboard',
        },
        {
          href: '/employer/jobs',
          icon: Briefcase,
          label: 'My Jobs',
        },
        {
          href: '/employer/applications',
          icon: Users,
          label: 'Applications',
        },
        ...baseItems,
      ]
    }

    if (user.role === 'ADMIN') {
      return [
        {
          href: '/admin/dashboard',
          icon: BarChart3,
          label: 'Admin Dashboard',
        },
        {
          href: '/admin/users',
          icon: Users,
          label: 'User Management',
        },
        {
          href: '/admin/jobs',
          icon: Briefcase,
          label: 'Job Management',
        },
        ...baseItems,
      ]
    }

    return baseItems
  }

  const menuItems = getMenuItems()

  return (
    <div className="relative">
      <Button
        variant="ghost"
        className="flex items-center space-x-2"
        onClick={() => setIsOpen(!isOpen)}
      >
        <div className="h-8 w-8 rounded-full bg-primary flex items-center justify-center">
          <span className="text-primary-foreground font-medium text-sm">
            {user.name.charAt(0).toUpperCase()}
          </span>
        </div>
        <span className="hidden sm:inline-block font-medium">{user.name}</span>
      </Button>

      {isOpen && (
        <>
          {/* Backdrop */}
          <div
            className="fixed inset-0 z-40"
            onClick={() => setIsOpen(false)}
          />
          
          {/* Menu */}
          <div className="absolute right-0 mt-2 w-56 bg-background border rounded-md shadow-lg z-50">
            <div className="p-3 border-b">
              <p className="font-medium">{user.name}</p>
              <p className="text-sm text-muted-foreground">{user.email}</p>
              <p className="text-xs text-muted-foreground capitalize">
                {user.role.toLowerCase()}
              </p>
            </div>
            
            <div className="py-1">
              {menuItems.map((item) => {
                const Icon = item.icon
                return (
                  <Link
                    key={item.href}
                    href={item.href}
                    className="flex items-center px-3 py-2 text-sm hover:bg-muted transition-colors"
                    onClick={() => setIsOpen(false)}
                  >
                    <Icon className="h-4 w-4 mr-3" />
                    {item.label}
                  </Link>
                )
              })}
            </div>
            
            <div className="border-t py-1">
              <button
                onClick={handleLogout}
                className="flex items-center w-full px-3 py-2 text-sm hover:bg-muted transition-colors text-red-600"
              >
                <LogOut className="h-4 w-4 mr-3" />
                Sign Out
              </button>
            </div>
          </div>
        </>
      )}
    </div>
  )
}