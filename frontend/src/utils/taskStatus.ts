import type { TaskDTO, TaskStatus } from '../types/gateway'

export type TaskStatusFilterValue =
  | 'ALL'
  | 'CREATED'
  | 'PLANNED'
  | 'COMPLETED'
  | 'OVERDUE'

export const taskStatusFilterOptions: Array<{
  label: string
  value: TaskStatusFilterValue
}> = [
  { label: 'Все', value: 'ALL' },
  { label: 'BACKLOG', value: 'CREATED' },
  { label: 'PLANNED', value: 'PLANNED' },
  { label: 'COMPLETED', value: 'COMPLETED' },
  { label: 'OVERDUE', value: 'OVERDUE' },
]

export function taskStatusLabel(status: TaskStatus) {
  switch (status) {
    case 'CREATED':
      return 'BACKLOG'
    default:
      return status
  }
}

export function filterTasksByStatus(
  tasks: TaskDTO[],
  filter: TaskStatusFilterValue,
) {
  if (filter === 'ALL') {
    return tasks
  }

  return tasks.filter((task) => task.status === filter)
}

export function taskStatusBadgeClassName(status: TaskStatus) {
  switch (status) {
    case 'PLANNED':
      return 'bg-amber-200 text-amber-950'
    case 'COMPLETED':
      return 'bg-emerald-200 text-emerald-950'
    case 'OVERDUE':
      return 'bg-rose-200 text-rose-950'
    case 'CREATED':
      return 'bg-white text-slate-800'
    default:
      return 'bg-slate-200 text-slate-800'
  }
}

export function taskStatusCardClassName(status: TaskStatus) {
  switch (status) {
    case 'PLANNED':
      return 'border-amber-300/90 bg-amber-50 hover:border-amber-400 hover:bg-amber-100/70'
    case 'COMPLETED':
      return 'border-emerald-300/90 bg-emerald-50 hover:border-emerald-400 hover:bg-emerald-100/70'
    case 'OVERDUE':
      return 'border-rose-300/90 bg-rose-50 hover:border-rose-400 hover:bg-rose-100/70'
    case 'CREATED':
      return 'border-slate-200 bg-white hover:border-slate-300 hover:bg-slate-50'
    default:
      return 'border-slate-200 bg-slate-50 hover:border-slate-300 hover:bg-slate-100'
  }
}
