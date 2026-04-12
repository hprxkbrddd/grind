import type { ReactNode } from 'react'

interface PanelProps {
  title: string
  description?: string
  eyebrow?: string
  icon?: ReactNode
  tone?: 'neutral' | 'warm' | 'cool'
  children: ReactNode
}

export function Panel({
  title,
  description,
  eyebrow,
  icon,
  tone = 'neutral',
  children,
}: PanelProps) {
  const toneClassName =
    tone === 'warm'
      ? 'bg-[linear-gradient(180deg,_rgba(255,248,239,0.96),_rgba(255,255,255,0.9))]'
      : tone === 'cool'
        ? 'bg-[linear-gradient(180deg,_rgba(239,248,250,0.96),_rgba(255,255,255,0.92))]'
        : 'bg-[linear-gradient(180deg,_rgba(255,255,255,0.96),_rgba(249,251,251,0.92))]'

  return (
    <section
      className={`rounded-[30px] border border-white/70 p-5 shadow-[0_20px_70px_rgba(31,54,61,0.08)] backdrop-blur sm:p-6 ${toneClassName}`}
    >
      <div className="mb-5 flex items-start gap-4">
        {icon ? (
          <div className="flex h-12 w-12 shrink-0 items-center justify-center rounded-2xl bg-white text-primary shadow-[0_10px_24px_rgba(31,54,61,0.08)]">
            {icon}
          </div>
        ) : null}
        <div>
          {eyebrow ? (
            <p className="text-xs font-semibold uppercase tracking-[0.28em] text-primary/80">
              {eyebrow}
            </p>
          ) : null}
          <h2 className="mt-1 text-xl font-semibold text-slate-900">{title}</h2>
          {description ? (
            <p className="mt-2 max-w-2xl text-sm leading-6 text-slate-500">
              {description}
            </p>
          ) : null}
        </div>
      </div>
      <div className="space-y-4">
        {children}
      </div>
    </section>
  )
}
