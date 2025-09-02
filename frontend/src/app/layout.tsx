import type { Metadata } from "next";
import { Inter } from "next/font/google";
import "./globals.css";
import { ThemeProvider } from "@/components/theme-provider";
import { AuthProvider } from "@/contexts/auth-context";
import { Header } from "@/components/layout/header";
import { Footer } from "@/components/layout/footer";
import { Toaster } from "sonner";

const inter = Inter({
  subsets: ["latin"],
  variable: "--font-sans",
});

export const metadata: Metadata = {
  title: "JobApp - Find Your Dream Job",
  description: "Connect with top employers and find your perfect job opportunity. Post jobs, search candidates, and build your career with JobApp.",
  keywords: ["jobs", "careers", "employment", "hiring", "recruitment"],
  authors: [{ name: "JobApp Team" }],
  creator: "JobApp",
  publisher: "JobApp",
  formatDetection: {
    email: false,
    address: false,
    telephone: false,
  },
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en" suppressHydrationWarning>
      <body className={`${inter.variable} font-sans antialiased`}>
        <ThemeProvider
          defaultTheme="system"
          storageKey="jobapp-theme"
        >
          <AuthProvider>
            <div className="relative flex min-h-screen flex-col">
              <Header />
              <main className="flex-1">
                {children}
              </main>
              <Footer />
              <Toaster richColors position="top-right" />
            </div>
          </AuthProvider>
        </ThemeProvider>
      </body>
    </html>
  );
}
