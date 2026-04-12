import type { ReactNode } from 'react'
import { Panel } from '../app/Panel'

interface StatsSummaryPanelProps {
  eyebrow: string
  title: string
  description: string
  icon: ReactNode
  loading: boolean
  error?: string
  emptyText?: string
  tone?: 'neutral' | 'warm' | 'cool'
  stats: Array<[string, string]>
  wide?: boolean
}

export function StatsSummaryPanel({
  eyebrow,
  title,
  description,
  icon,
  loading,
  error = '',
  emptyText = 'Статистика пока недоступна.',
  tone = 'neutral',
  stats,
  wide = false,
}: StatsSummaryPanelProps) {
  return (
    <Panel
      eyebrow={eyebrow}
      icon={icon}
      title={title}
      description={description}
      tone={tone}
    >
      {loading ? (
        <div className="rounded-2xl border border-primary/15 bg-white/80 px-4 py-3 text-sm text-slate-600">
          Загружаю статистику...
        </div>
      ) : null}

      {error ? (
        <div className="rounded-2xl border border-rose-200 bg-rose-50 px-4 py-3 text-sm text-rose-700">
          Не удалось загрузить статистику: {error}
        </div>
      ) : null}

      {!loading && !error && stats.length === 0 ? (
        <div className="rounded-2xl border border-dashed border-primary/20 bg-slate-50 px-4 py-5 text-sm text-slate-500">
          {emptyText}
        </div>
      ) : null}

      {!error && stats.length > 0 ? (
        <div
          className={`grid gap-3 ${
            wide
              ? 'sm:grid-cols-2 xl:grid-cols-3'
              : 'sm:grid-cols-2 xl:grid-cols-1'
          }`}
        >
          {stats.map(([label, value]) => (
            <div
              key={`${title}-${label}`}
              className="rounded-2xl border border-primary/15 bg-slate-50 p-4"
            >
              <p className="text-xs uppercase tracking-[0.2em] text-slate-500">
                {label}
              </p>
              <p className="mt-2 text-lg font-semibold text-slate-900">{value}</p>
            </div>
          ))}
        </div>
      ) : null}
    </Panel>
  )
}
