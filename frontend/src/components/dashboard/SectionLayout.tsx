import { ArrowLeft } from 'lucide-react'
import type { ReactNode } from 'react'
import { Link } from 'react-router'

interface SectionLayoutProps {
  eyebrow: string
  title: string
  description: string
  icon: ReactNode
  backLabel?: string
  backTo?: string
  actions?: ReactNode
  children: ReactNode
}

export function SectionLayout({
  eyebrow,
  title,
  description,
  icon,
  backLabel = 'К workspace',
  backTo = '/home/workspace',
  actions,
  children,
}: SectionLayoutProps) {
  return (
    <div className="space-y-6">
      <section className="relative overflow-hidden rounded-[34px] bg-[linear-gradient(145deg,_#1f363d,_#40798c_62%,_#6aa0a8)] px-6 py-8 text-white shadow-[0_28px_80px_rgba(31,54,61,0.18)] sm:px-8">
        <div className="absolute -right-8 top-4 h-28 w-28 rounded-full bg-white/10 blur-2xl" />
        <div className="absolute bottom-0 left-1/3 h-24 w-24 rounded-full bg-[#fff8ef]/18 blur-2xl" />
        <div className="flex flex-wrap items-center justify-between gap-4">
          <Link
            className="inline-flex items-center gap-2 rounded-full border border-white/15 bg-white/8 px-4 py-2 text-sm text-white/90 backdrop-blur transition hover:bg-white/12"
            to={backTo}
          >
            <ArrowLeft className="h-4 w-4" />
            {backLabel}
          </Link>
          {actions ? <div className="flex flex-wrap gap-3">{actions}</div> : null}
        </div>
        <div className="mt-8 flex items-start gap-4">
          <div className="flex h-14 w-14 shrink-0 items-center justify-center rounded-2xl border border-white/15 bg-white/10 text-white shadow-[0_16px_30px_rgba(0,0,0,0.08)]">
            {icon}
          </div>
          <div className="max-w-3xl">
            <p className="text-sm uppercase tracking-[0.32em] text-cyan-200">
              {eyebrow}
            </p>
            <h1 className="mt-4 text-3xl font-semibold leading-tight sm:text-4xl">
              {title}
            </h1>
            <p className="mt-4 text-sm leading-7 text-slate-200">{description}</p>
          </div>
        </div>
      </section>

      {children}
    </div>
  )
}
