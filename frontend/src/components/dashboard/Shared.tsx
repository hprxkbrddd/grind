import { useState, type ReactNode } from 'react'
import type {
  DiagramDTO,
  SprintWithCountDTO,
  TaskDTO,
  TrackWithCountDTO,
} from '../../types/gateway'
import {
  sortSprintsByStartDate,
  sortTasksByCreatedAt,
} from '../../utils/sorting'
import {
  filterTasksByStatus,
  taskStatusBadgeClassName,
  taskStatusCardClassName,
  taskStatusFilterOptions,
  taskStatusLabel,
  type TaskStatusFilterValue,
} from '../../utils/taskStatus'

const BACKLOG_SPRINT_ID = '00000000-0000-0000-0000-000000000000'

export function toNullableId(value: string) {
  return value && value !== BACKLOG_SPRINT_ID ? value : null
}

export function metricValue(value: number) {
  return Number.isFinite(value) ? value.toFixed(2) : '0.00'
}

function labelDate(value: string | null) {
  return value ?? '—'
}

export function ContextValue({
  label,
  value,
}: {
  label: string
  value: string
}) {
  return (
    <div className="rounded-2xl border border-primary/15 bg-slate-50 p-4">
      <p className="text-xs uppercase tracking-[0.3em] text-primary">{label}</p>
      <p className="mt-2 break-all text-sm font-semibold text-slate-900">
        {value}
      </p>
    </div>
  )
}

export function FriendlyNote({
  title,
  text,
}: {
  title: string
  text: string
}) {
  return (
    <div className="rounded-2xl border border-primary/12 bg-white/75 p-4 text-sm text-slate-600 shadow-[0_10px_22px_rgba(31,54,61,0.04)]">
      <p className="font-semibold text-slate-800">{title}</p>
      <p className="mt-2 leading-6">{text}</p>
    </div>
  )
}

export function FormTitle({ title }: { title: string }) {
  return (
    <div className="border-t border-primary/10 pt-6">
      <h3 className="text-sm font-semibold uppercase tracking-[0.24em] text-primary">
        {title}
      </h3>
    </div>
  )
}

export function HeroPill({
  icon,
  children,
}: {
  icon: ReactNode
  children: ReactNode
}) {
  return (
    <div className="flex items-center gap-2 rounded-2xl border border-white/15 bg-white/8 px-4 py-3 text-sm text-slate-100 backdrop-blur">
      {icon}
      <span>{children}</span>
    </div>
  )
}

export function TrackList({
  title,
  tracks,
  onUseTrack,
  emptyText = 'Список пуст',
}: {
  title: string
  tracks: TrackWithCountDTO[]
  onUseTrack: (track: TrackWithCountDTO) => void
  emptyText?: string
}) {
  return (
    <div className="space-y-3">
      <h3 className="text-sm font-semibold uppercase tracking-[0.24em] text-primary">
        {title}
      </h3>
      {tracks.length === 0 ? (
        <div className="rounded-2xl border border-dashed border-primary/20 bg-slate-50 px-4 py-4 text-sm text-slate-500">
          {emptyText}
        </div>
      ) : (
        tracks.map((track) => (
          <button
            key={track.id}
            className="block w-full rounded-2xl border border-primary/15 bg-slate-50 p-4 text-left transition hover:border-primary/40 hover:bg-white"
            onClick={() => onUseTrack(track)}
            type="button"
          >
            <div className="flex items-start justify-between gap-3">
              <div>
                <p className="font-semibold text-slate-900">{track.name}</p>
                <p className="mt-1 text-xs text-slate-500">{track.id}</p>
              </div>
              <span className="rounded-full bg-primary/10 px-3 py-1 text-xs font-semibold text-primary-dark">
                {track.tasks} tasks
              </span>
            </div>
            <p className="mt-3 text-sm text-slate-600">{track.description}</p>
          </button>
        ))
      )}
    </div>
  )
}

export function TaskList({
  title,
  tasks,
  onUseTask,
  emptyText = 'Список пуст',
}: {
  title: string
  tasks: TaskDTO[]
  onUseTask: (task: TaskDTO) => void
  emptyText?: string
}) {
  const [statusFilter, setStatusFilter] = useState<TaskStatusFilterValue>('ALL')
  const orderedTasks = sortTasksByCreatedAt(tasks)
  const filteredTasks = filterTasksByStatus(orderedTasks, statusFilter)

  return (
    <div className="space-y-3">
      <h3 className="text-sm font-semibold uppercase tracking-[0.24em] text-primary">
        {title}
      </h3>
      <div className="flex flex-wrap gap-2">
        {taskStatusFilterOptions.map((option) => (
          <button
            key={`${title}-${option.value}`}
            className={`rounded-full border px-3 py-1.5 text-xs font-semibold transition ${
              statusFilter === option.value
                ? 'border-primary bg-primary text-white'
                : 'border-primary/15 bg-white text-slate-600 hover:border-primary/35 hover:text-slate-900'
            }`}
            onClick={() => setStatusFilter(option.value)}
            type="button"
          >
            {option.label}
          </button>
        ))}
      </div>
      {filteredTasks.length === 0 ? (
        <div className="rounded-2xl border border-dashed border-primary/20 bg-slate-50 px-4 py-4 text-sm text-slate-500">
          {tasks.length === 0
            ? emptyText
            : 'По выбранному статусу задач нет.'}
        </div>
      ) : (
        filteredTasks.map((task) => (
          <button
            key={task.id}
            className={`block w-full rounded-2xl border p-4 text-left transition ${taskStatusCardClassName(task.status)}`}
            onClick={() => onUseTask(task)}
            type="button"
          >
            <div className="flex items-start justify-between gap-3">
              <div>
                <p className="font-semibold text-slate-900">{task.title}</p>
                <p className="mt-1 text-xs text-slate-500">{task.id}</p>
              </div>
              <span
                className={`rounded-full border border-black/5 px-3 py-1 text-xs font-semibold shadow-[inset_0_1px_0_rgba(255,255,255,0.45)] ${taskStatusBadgeClassName(task.status)}`}
              >
                {taskStatusLabel(task.status)}
              </span>
            </div>
            <div className="mt-3 grid gap-2 text-xs text-slate-500 sm:grid-cols-2">
              <p>track: {task.track_id}</p>
              <p>sprint: {toNullableId(task.sprint_id) ?? 'BACKLOG'}</p>
              <p>planned: {labelDate(task.plannedDate)}</p>
              <p>actual: {labelDate(task.actualDate)}</p>
            </div>
          </button>
        ))
      )}
    </div>
  )
}

export function SprintList({
  title,
  sprints,
  onUseSprint,
  emptyText = 'Список пуст',
}: {
  title: string
  sprints: SprintWithCountDTO[]
  onUseSprint: (sprint: SprintWithCountDTO) => void
  emptyText?: string
}) {
  const orderedSprints = sortSprintsByStartDate(sprints)

  return (
    <div className="space-y-3">
      <h3 className="text-sm font-semibold uppercase tracking-[0.24em] text-primary">
        {title}
      </h3>
      {orderedSprints.length === 0 ? (
        <div className="rounded-2xl border border-dashed border-primary/20 bg-slate-50 px-4 py-4 text-sm text-slate-500">
          {emptyText}
        </div>
      ) : (
        orderedSprints.map((sprint) => (
          <button
            key={sprint.id}
            className="block w-full rounded-2xl border border-primary/15 bg-slate-50 p-4 text-left transition hover:border-primary/40 hover:bg-white"
            onClick={() => onUseSprint(sprint)}
            type="button"
          >
            <div className="flex items-start justify-between gap-3">
              <div>
                <p className="font-semibold text-slate-900">{sprint.id}</p>
                <p className="mt-1 text-xs text-slate-500">
                  {sprint.startDate} → {sprint.endDate}
                </p>
              </div>
              <span className="rounded-full bg-primary/10 px-3 py-1 text-xs font-semibold text-primary-dark">
                {sprint.tasks} tasks
              </span>
            </div>
          </button>
        ))
      )}
    </div>
  )
}

export function StatsGrid({
  title,
  stats,
}: {
  title: string
  stats: Array<[string, string]>
}) {
  return (
    <div className="space-y-3">
      <h3 className="text-sm font-semibold uppercase tracking-[0.24em] text-primary">
        {title}
      </h3>
      <div className="grid gap-3 sm:grid-cols-2">
        {stats.map(([label, value]) => (
          <div
            key={label}
            className="rounded-2xl border border-primary/15 bg-slate-50 p-4"
          >
            <p className="text-xs uppercase tracking-[0.2em] text-slate-500">
              {label}
            </p>
            <p className="mt-2 text-lg font-semibold text-slate-900">{value}</p>
          </div>
        ))}
      </div>
    </div>
  )
}

export function DiagramTable({
  title,
  data,
}: {
  title: string
  data: DiagramDTO | null
}) {
  return (
    <div className="space-y-3">
      <h3 className="text-sm font-semibold uppercase tracking-[0.24em] text-primary">
        {title}
      </h3>
      {!data || data.diagram.length === 0 ? (
        <div className="rounded-2xl border border-dashed border-primary/20 bg-slate-50 px-4 py-4 text-sm text-slate-500">
          Диаграмма ещё не загружена
        </div>
      ) : (
        <div className="overflow-hidden rounded-2xl border border-primary/15">
          <table className="min-w-full divide-y divide-primary/10 bg-white text-left text-sm">
            <thead className="bg-slate-50 text-slate-500">
              <tr>
                <th className="px-4 py-3">day</th>
                <th className="px-4 py-3">planned_tasks</th>
                <th className="px-4 py-3">completed_tasks</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-primary/10">
              {data.diagram.map((item) => (
                <tr key={`${title}-${item.day}`}>
                  <td className="px-4 py-3 text-slate-700">{item.day}</td>
                  <td className="px-4 py-3 text-slate-700">
                    {item.planned_tasks}
                  </td>
                  <td className="px-4 py-3 text-slate-700">
                    {item.completed_tasks}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  )
}
