# JobApp Frontend

A modern React/Next.js frontend for the Job Application Platform built with TypeScript, Tailwind CSS, and responsive design.

## Features

- **Next.js 15** with App Router and TypeScript support
- **Tailwind CSS v4** with custom design system
- **Dark/Light theme** with system preference detection
- **Responsive design** optimized for all devices
- **Component library** with reusable UI components
- **SEO optimization** with dynamic meta tags
- **Accessibility** compliant components

## Project Structure

```
src/
├── app/                    # Next.js App Router pages
│   ├── layout.tsx         # Root layout with theme provider
│   ├── page.tsx           # Homepage
│   └── globals.css        # Global styles and theme variables
├── components/
│   ├── ui/                # Reusable UI components
│   │   ├── button.tsx     # Button component with variants
│   │   ├── input.tsx      # Input component
│   │   └── card.tsx       # Card components
│   ├── layout/            # Layout components
│   │   ├── header.tsx     # Site header with navigation
│   │   ├── footer.tsx     # Site footer
│   │   └── navigation.tsx # Mobile navigation
│   ├── theme-provider.tsx # Theme context provider
│   └── theme-toggle.tsx   # Dark/light mode toggle
└── lib/
    └── utils.ts           # Utility functions
```

## Design System

### Colors
- **Primary**: Blue (#2563eb / #3b82f6)
- **Secondary**: Slate grays
- **Background**: White / Dark (#0a0a0a)
- **Foreground**: Dark / Light text
- **Muted**: Subtle backgrounds and text
- **Accent**: Interactive elements
- **Destructive**: Error states

### Typography
- **Font**: Inter (system fallback)
- **Sizes**: Responsive text scaling
- **Weights**: 400 (normal), 500 (medium), 600 (semibold), 700 (bold)

### Components
- **Button**: Multiple variants (default, outline, ghost, etc.)
- **Input**: Consistent form styling
- **Card**: Content containers with header/footer
- **Theme Toggle**: Dark/light mode switcher

## Getting Started

1. **Install dependencies**:
   ```bash
   npm install
   ```

2. **Run development server**:
   ```bash
   npm run dev
   ```

3. **Build for production**:
   ```bash
   npm run build
   ```

4. **Start production server**:
   ```bash
   npm start
   ```

## Development

### Adding New Components
1. Create component in `src/components/ui/` or appropriate directory
2. Follow the established patterns for props and styling
3. Use the `cn()` utility for className merging
4. Export from the component file

### Theme Customization
- Modify CSS variables in `src/app/globals.css`
- Update the `@theme inline` section for Tailwind CSS v4
- Add new color variants to components as needed

### Responsive Design
- Mobile-first approach with Tailwind breakpoints
- `sm:` (640px+), `md:` (768px+), `lg:` (1024px+), `xl:` (1280px+)
- Test on multiple screen sizes

## Dependencies

### Core
- **Next.js 15**: React framework with App Router
- **React 19**: UI library
- **TypeScript**: Type safety

### Styling
- **Tailwind CSS v4**: Utility-first CSS framework
- **@tailwindcss/postcss**: PostCSS plugin for Tailwind v4
- **clsx**: Conditional className utility
- **tailwind-merge**: Tailwind class merging

### UI Components
- **@radix-ui/react-slot**: Polymorphic component support

## Browser Support

- Chrome (latest)
- Firefox (latest)
- Safari (latest)
- Edge (latest)

## Performance

- **Static Generation**: Pages pre-rendered at build time
- **Code Splitting**: Automatic route-based splitting
- **Image Optimization**: Next.js built-in optimization
- **Bundle Analysis**: Use `npm run build` to analyze bundle size

## Accessibility

- **ARIA labels**: Proper labeling for screen readers
- **Keyboard navigation**: Full keyboard support
- **Focus management**: Visible focus indicators
- **Color contrast**: WCAG AA compliant colors