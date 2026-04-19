import { ArrowRight, KanbanSquare, ListChecks, Route } from 'lucide-react'
import type { ReactNode } from 'react'
import type { TrackWithCountDTO } from '../../types/gateway'
import { metricValue } from './Shared'

export interface TrackOverviewCardItem {
  track: TrackWithCountDTO
  sprintCount: number | null
  completionPercent: number | null
}

interface TrackOverviewGridProps {
  title: string
  tracks: TrackOverviewCardItem[]
  onOpenTrack: (trackId: string) => void
  onCreateTrack?: () => void
  emptyText?: string
}

function formatMetricValue(value: number | null) {
  return value === null ? '—' : String(value)
}

function formatCompletionValue(value: number | null) {
  return value === null ? '—' : `${metricValue(value)}%`
}

function clampPercent(value: number | null) {
  if (value === null) {
    return 0
  }

  return Math.min(100, Math.max(0, value))
}

function TrackCardButton({
  title,
  status,
  sprintValue,
  taskValue,
  completionValue,
  progress,
  onClick,
}: {
  title: string
  status: string
  sprintValue: string
  taskValue: string
  completionValue: string
  progress: number
  onClick?: () => void
}) {
  return (
    <button
      className="group aspect-square rounded-[28px] border border-primary/15 bg-[linear-gradient(180deg,_rgba(255,255,255,0.98),_rgba(239,245,247,0.96))] p-5 text-left shadow-[0_18px_40px_rgba(31,54,61,0.08)] transition hover:-translate-y-0.5 hover:border-primary/35 hover:shadow-[0_24px_50px_rgba(31,54,61,0.12)]"
      onClick={onClick}
      type="button"
    >
      <div className="flex h-full flex-col justify-between gap-5">
        <div className="space-y-4">
          <div className="flex items-start justify-between gap-3">
            <div className="rounded-2xl bg-primary/10 p-3 text-primary">
              <KanbanSquare className="h-5 w-5" />
            </div>
            <ArrowRight className="h-5 w-5 text-slate-400 transition group-hover:translate-x-0.5 group-hover:text-primary" />
          </div>
          <div>
            <p className="text-lg font-semibold leading-tight text-slate-900">
              {title}
            </p>
            <p className="mt-2 text-xs uppercase tracking-[0.24em] text-slate-500">
              {status}
            </p>
          </div>
        </div>

        <div className="space-y-3">
          <div className="grid grid-cols-2 gap-3">
            <MetricCard
              label="Спринты"
              value={sprintValue}
              icon={<Route className="h-3.5 w-3.5" />}
            />
            <MetricCard
              label="Задачи"
              value={taskValue}
              icon={<ListChecks className="h-3.5 w-3.5" />}
            />
          </div>

          <div className="rounded-2xl border border-primary/10 bg-primary/8 px-4 py-3">
            <div className="flex items-center justify-between gap-3 text-[11px] uppercase tracking-[0.24em] text-primary-dark">
              <span>Завершение</span>
              <span>{completionValue}</span>
            </div>
            <div className="mt-3 h-2.5 rounded-full bg-white/80">
              <div
                className="h-full rounded-full bg-primary transition-[width]"
                style={{ width: `${progress}%` }}
              />
            </div>
          </div>
        </div>
      </div>
    </button>
  )
}

function NewTrackCardButton({ onClick }: { onClick?: () => void }) {
  return (
    <button
      className="group aspect-square rounded-[28px] border border-primary/15 bg-[linear-gradient(180deg,_rgba(255,255,255,0.98),_rgba(239,245,247,0.96))] p-5 text-left shadow-[0_18px_40px_rgba(31,54,61,0.08)] transition hover:-translate-y-0.5 hover:border-primary/35 hover:shadow-[0_24px_50px_rgba(31,54,61,0.12)]"
      onClick={onClick}
      type="button"
    >
      <div className="flex h-full flex-col items-center justify-center gap-6 text-center">
        <p className="text-lg font-semibold leading-tight text-slate-900">
          Новый трек
        </p>
        <span className="text-8xl leading-none font-light text-primary">+</span>
      </div>
    </button>
  )
}

function MetricCard({
  label,
  value,
  icon,
}: {
  label: string
  value: string
  icon: ReactNode
}) {
  return (
    <div className="rounded-2xl border border-primary/10 bg-white/80 p-3">
      <div className="flex items-center gap-2 text-[11px] uppercase tracking-[0.24em] text-slate-500">
        <span className="text-primary">{icon}</span>
        <span>{label}</span>
      </div>
      <p className="mt-3 text-2xl font-semibold leading-none text-slate-900">
        {value}
      </p>
    </div>
  )
}

export function TrackOverviewGrid({
  title,
  tracks,
  onOpenTrack,
  onCreateTrack,
  emptyText = 'У вас пока нет треков.',
}: TrackOverviewGridProps) {
  return (
    <div className="space-y-3">
      <h3 className="text-sm font-semibold uppercase tracking-[0.24em] text-primary">
        {title}
      </h3>
      {tracks.length === 0 ? (
        <div className="rounded-2xl border border-dashed border-primary/20 bg-slate-50 px-4 py-4 text-sm text-slate-500">
          {emptyText}
        </div>
      ) : null}
      <div className="grid gap-4 sm:grid-cols-2 xl:grid-cols-3">
        {tracks.map(({ track, sprintCount, completionPercent }) => (
          <TrackCardButton
            key={track.id}
            title={track.name}
            status={track.status}
            sprintValue={formatMetricValue(sprintCount)}
            taskValue={String(track.tasks)}
            completionValue={formatCompletionValue(completionPercent)}
            progress={clampPercent(completionPercent)}
            onClick={() => onOpenTrack(track.id)}
          />
        ))}
        <NewTrackCardButton onClick={onCreateTrack} />
      </div>
    </div>
  )
}
