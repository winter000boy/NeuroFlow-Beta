import type { Metadata } from "next";
import { Inter } from "next/font/google";
import "./globals.css";
import { ThemeProvider } from "@/components/theme-provider";
import { AuthProvider } from "@/contexts/auth-context";
import { Header } from "@/components/layout/header";
import { Footer } from "@/components/layout/footer";
import { ServiceWorkerProvider } from "@/components/service-worker-provider";
import { DevPerformanceOverlay } from "@/components/performance-monitor";
import { Toaster } from "sonner";

const inter = Inter({
  subsets: ["latin"],
  variable: "--font-sans",
});

export const metadata: Metadata = {
  metadataBase: new URL(process.env.NEXT_PUBLIC_SITE_URL || 'https://jobapp.com'),
  title: {
    default: "JobApp - Find Your Dream Job",
    template: "%s | JobApp"
  },
  description: "Connect with top employers and find your perfect job opportunity. Post jobs, search candidates, and build your career with JobApp.",
  keywords: ["jobs", "careers", "employment", "hiring", "recruitment", "job board", "job search", "career opportunities"],
  authors: [{ name: "JobApp Team" }],
  creator: "JobApp",
  publisher: "JobApp",
  formatDetection: {
    email: false,
    address: false,
    telephone: false,
  },
  robots: {
    index: true,
    follow: true,
    googleBot: {
      index: true,
      follow: true,
      'max-video-preview': -1,
      'max-image-preview': 'large',
      'max-snippet': -1,
    },
  },
  openGraph: {
    type: 'website',
    locale: 'en_US',
    url: '/',
    siteName: 'JobApp',
    title: 'JobApp - Find Your Dream Job',
    description: 'Connect with top employers and find your perfect job opportunity.',
    images: [
      {
        url: '/og-default.jpg',
        width: 1200,
        height: 630,
        alt: 'JobApp - Job Search Platform',
      },
    ],
  },
  twitter: {
    card: 'summary_large_image',
    title: 'JobApp - Find Your Dream Job',
    description: 'Connect with top employers and find your perfect job opportunity.',
    images: ['/og-default.jpg'],
    creator: '@jobapp',
  },
  verification: {
    google: process.env.GOOGLE_SITE_VERIFICATION,
    yandex: process.env.YANDEX_VERIFICATION,
    yahoo: process.env.YAHOO_VERIFICATION,
  },
  alternates: {
    canonical: '/',
  },
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en" suppressHydrationWarning>
      <head>
        <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=5" />
        <meta name="theme-color" content="#000000" />
        <link rel="icon" href="/favicon.ico" />
        <link rel="apple-touch-icon" href="/apple-touch-icon.png" />
        <link rel="manifest" href="/manifest.json" />
      </head>
      <body className={`${inter.variable} font-sans antialiased`}>
        <ThemeProvider
          defaultTheme="system"
          storageKey="jobapp-theme"
        >
          <AuthProvider>
            <ServiceWorkerProvider />
            <div className="relative flex min-h-screen flex-col">
              <Header />
              <main className="flex-1">
                {children}
              </main>
              <Footer />
              <Toaster richColors position="top-right" />
              <DevPerformanceOverlay />
            </div>
          </AuthProvider>
        </ThemeProvider>
      </body>
    </html>
  );
}
