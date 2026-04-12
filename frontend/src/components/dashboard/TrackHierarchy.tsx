import {
  ChevronDown,
  ChevronRight,
  KanbanSquare,
  ListChecks,
  Route,
} from 'lucide-react'
import type {
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
import { ActionButton } from '../app/ActionButton'
import { useState } from 'react'

interface TrackHierarchyProps {
  title: string
  tracks: TrackWithCountDTO[]
  expandedTrackId: string | null
  expandedSprintId: string | null
  selectedTrackId: string
  selectedSprintId: string
  selectedTaskId: string
  sprintsByTrackId: Record<string, SprintWithCountDTO[] | undefined>
  tasksBySprintId: Record<string, TaskDTO[] | undefined>
  loadingTrackId: string | null
  loadingSprintId: string | null
  onToggleTrack: (track: TrackWithCountDTO) => void
  onToggleSprint: (track: TrackWithCountDTO, sprint: SprintWithCountDTO) => void
  onOpenTrackPage: (trackId: string) => void
  onOpenSprintPage: (trackId: string, sprintId: string) => void
  onOpenTask: (task: TaskDTO) => void
}

function EmptyMessage({ text }: { text: string }) {
  return (
    <div className="rounded-2xl border border-dashed border-primary/20 bg-slate-50 px-4 py-4 text-sm text-slate-500">
      {text}
    </div>
  )
}

function toggleIcon(open: boolean) {
  return open ? (
    <ChevronDown className="h-4 w-4 text-primary" />
  ) : (
    <ChevronRight className="h-4 w-4 text-slate-400" />
  )
}

export function TrackHierarchy({
  title,
  tracks,
  expandedTrackId,
  expandedSprintId,
  selectedTrackId,
  selectedSprintId,
  selectedTaskId,
  sprintsByTrackId,
  tasksBySprintId,
  loadingTrackId,
  loadingSprintId,
  onToggleTrack,
  onToggleSprint,
  onOpenTrackPage,
  onOpenSprintPage,
  onOpenTask,
}: TrackHierarchyProps) {
  const [statusFilter, setStatusFilter] = useState<TaskStatusFilterValue>('ALL')

  return (
    <div className="space-y-3">
      <h3 className="text-sm font-semibold uppercase tracking-[0.24em] text-primary">
        {title}
      </h3>
      {tracks.length === 0 ? (
        <EmptyMessage text="У вас пока нет треков." />
      ) : (
        tracks.map((track) => {
          const trackOpen = expandedTrackId === track.id
          const trackActive = selectedTrackId === track.id
          const sprints = sprintsByTrackId[track.id]
          const orderedSprints = sprints ? sortSprintsByStartDate(sprints) : []

          return (
            <div key={track.id} className="space-y-3">
              <button
                className={`block w-full rounded-2xl border p-4 text-left transition ${
                  trackActive || trackOpen
                    ? 'border-primary/45 bg-white shadow-[0_14px_28px_rgba(31,54,61,0.08)]'
                    : 'border-primary/15 bg-slate-50 hover:border-primary/40 hover:bg-white'
                }`}
                onClick={() => onToggleTrack(track)}
                type="button"
              >
                <div className="flex items-start justify-between gap-3">
                  <div className="flex items-start gap-3">
                    <div className="mt-0.5 rounded-full bg-primary/8 p-2">
                      {toggleIcon(trackOpen)}
                    </div>
                    <div>
                      <p className="font-semibold text-slate-900">{track.name}</p>
                      <p className="mt-1 text-xs text-slate-500">{track.id}</p>
                    </div>
                  </div>
                  <span className="rounded-full bg-primary/10 px-3 py-1 text-xs font-semibold text-primary-dark">
                    {track.tasks} задач
                  </span>
                </div>
                <p className="mt-3 text-sm text-slate-600">
                  {track.description || 'Описание трека пока не заполнено.'}
                </p>
              </button>

              {trackOpen ? (
                <div className="ml-4 space-y-3 border-l border-primary/12 pl-4">
                  <div className="flex flex-wrap gap-3">
                    <ActionButton
                      type="button"
                      variant="secondary"
                      className="gap-2 text-xs"
                      onClick={() => onOpenTrackPage(track.id)}
                    >
                      <KanbanSquare className="h-4 w-4" />
                      Страница трека
                    </ActionButton>
                  </div>

                  {loadingTrackId === track.id ? (
                    <EmptyMessage text="Загружаю спринты трека..." />
                  ) : !sprints ? (
                    <EmptyMessage text="Спринты этого трека ещё не загружены." />
                  ) : orderedSprints.length === 0 ? (
                    <EmptyMessage text="У этого трека пока нет спринтов." />
                  ) : (
                    orderedSprints.map((sprint) => {
                      const sprintOpen = expandedSprintId === sprint.id
                      const sprintActive = selectedSprintId === sprint.id
                      const tasks = tasksBySprintId[sprint.id]
                      const orderedTasks = tasks ? sortTasksByCreatedAt(tasks) : []

                      return (
                        <div key={sprint.id} className="space-y-3">
                          <button
                            className={`block w-full rounded-2xl border p-4 text-left transition ${
                              sprintActive || sprintOpen
                                ? 'border-primary/45 bg-white'
                                : 'border-primary/15 bg-slate-50 hover:border-primary/40 hover:bg-white'
                            }`}
                            onClick={() => onToggleSprint(track, sprint)}
                            type="button"
                          >
                            <div className="flex items-start justify-between gap-3">
                              <div className="flex items-start gap-3">
                                <div className="mt-0.5 rounded-full bg-primary/8 p-2">
                                  {toggleIcon(sprintOpen)}
                                </div>
                                <div>
                                  <p className="font-semibold text-slate-900">
                                    {sprint.startDate} → {sprint.endDate}
                                  </p>
                                  <p className="mt-1 text-xs text-slate-500">
                                    {sprint.id}
                                  </p>
                                </div>
                              </div>
                              <span className="rounded-full bg-primary/10 px-3 py-1 text-xs font-semibold text-primary-dark">
                                {sprint.tasks} задач
                              </span>
                            </div>
                          </button>

                          {sprintOpen ? (
                            <div className="ml-4 space-y-3 border-l border-primary/12 pl-4">
                              <div className="flex flex-wrap gap-3">
                                <ActionButton
                                  type="button"
                                  variant="ghost"
                                  className="gap-2 text-xs"
                                  onClick={() =>
                                    onOpenSprintPage(track.id, sprint.id)
                                  }
                                >
                                  <Route className="h-4 w-4" />
                                  Страница спринта
                                </ActionButton>
                              </div>

                              {loadingSprintId === sprint.id ? (
                                <EmptyMessage text="Загружаю задачи спринта..." />
                              ) : !tasks ? (
                                <EmptyMessage text="Нажмите на спринт, чтобы загрузить его задачи." />
                              ) : filterTasksByStatus(orderedTasks, statusFilter)
                                  .length === 0 ? (
                                tasks.length === 0 ? (
                                  <EmptyMessage text="На этот спринт задачи не запланированы." />
                                ) : (
                                  <div className="space-y-3">
                                    <div className="flex flex-wrap gap-2">
                                      {taskStatusFilterOptions.map((option) => (
                                        <button
                                          key={`${sprint.id}-${option.value}`}
                                          className={`rounded-full border px-3 py-1.5 text-xs font-semibold transition ${
                                            statusFilter === option.value
                                              ? 'border-primary bg-primary text-white'
                                              : 'border-primary/15 bg-white text-slate-600 hover:border-primary/35 hover:text-slate-900'
                                          }`}
                                          onClick={() =>
                                            setStatusFilter(option.value)
                                          }
                                          type="button"
                                        >
                                          {option.label}
                                        </button>
                                      ))}
                                    </div>
                                    <EmptyMessage text="По выбранному статусу задач нет." />
                                  </div>
                                )
                              ) : (
                                <div className="space-y-3">
                                  <div className="flex flex-wrap gap-2">
                                    {taskStatusFilterOptions.map((option) => (
                                      <button
                                        key={`${sprint.id}-${option.value}`}
                                        className={`rounded-full border px-3 py-1.5 text-xs font-semibold transition ${
                                          statusFilter === option.value
                                            ? 'border-primary bg-primary text-white'
                                            : 'border-primary/15 bg-white text-slate-600 hover:border-primary/35 hover:text-slate-900'
                                        }`}
                                        onClick={() =>
                                          setStatusFilter(option.value)
                                        }
                                        type="button"
                                      >
                                        {option.label}
                                      </button>
                                    ))}
                                  </div>
                                  {filterTasksByStatus(
                                    orderedTasks,
                                    statusFilter,
                                  ).map((task) => {
                                    const taskActive = selectedTaskId === task.id

                                    return (
                                      <button
                                        key={task.id}
                                        className={`block w-full rounded-2xl border p-4 text-left transition ${taskStatusCardClassName(task.status)} ${
                                          taskActive
                                            ? 'ring-2 ring-primary/35'
                                            : ''
                                        }`}
                                        onClick={() => onOpenTask(task)}
                                        type="button"
                                      >
                                        <div className="flex items-start justify-between gap-3">
                                          <div className="flex items-start gap-3">
                                            <div className="mt-0.5 rounded-full bg-primary/8 p-2">
                                              <ListChecks className="h-4 w-4 text-primary" />
                                            </div>
                                            <div>
                                              <p className="font-semibold text-slate-900">
                                                {task.title}
                                              </p>
                                              <p className="mt-1 text-xs text-slate-500">
                                                {task.id}
                                              </p>
                                            </div>
                                          </div>
                                          <span
                                            className={`rounded-full border border-black/5 px-3 py-1 text-xs font-semibold shadow-[inset_0_1px_0_rgba(255,255,255,0.45)] ${taskStatusBadgeClassName(task.status)}`}
                                          >
                                            {taskStatusLabel(task.status)}
                                          </span>
                                        </div>
                                        <p className="mt-3 text-sm text-slate-600">
                                          {task.description ||
                                            'Описание задачи пока не заполнено.'}
                                        </p>
                                      </button>
                                    )
                                  })}
                                </div>
                              )}
                            </div>
                          ) : null}
                        </div>
                      )
                    })
                  )}
                </div>
              ) : null}
            </div>
          )
        })
      )}
    </div>
  )
}
