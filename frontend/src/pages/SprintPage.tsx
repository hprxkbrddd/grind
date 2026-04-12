import { Activity, ListChecks, RefreshCw, Route } from 'lucide-react'
import { useCallback, useEffect, useState } from 'react'
import { useNavigate, useParams } from 'react-router'
import { gatewayApi, getApiErrorMessage } from '../api/gateway'
import { ActionButton } from '../components/app/ActionButton'
import { Panel } from '../components/app/Panel'
import { SectionLayout } from '../components/dashboard/SectionLayout'
import { StatsSummaryPanel } from '../components/dashboard/StatsSummaryPanel'
import { FriendlyNote, TaskList, metricValue } from '../components/dashboard/Shared'
import { useAuth } from '../hooks/useAuth'
import type {
  SprintStatsDTO,
  SprintWithCountDTO,
  TaskDTO,
} from '../types/gateway'
import {
  workspaceTaskPath,
  workspaceTrackPath,
} from '../utils/workspaceRoutes'

type Feedback =
  | {
      kind: 'success' | 'error'
      message: string
    }
  | null

function normalizeStatsError(error: string) {
  return error === 'sprint id is not in stats db' ? '' : error
}

export function SprintPage() {
  const { trackId, sprintId } = useParams()
  const { auth } = useAuth()
  const accessToken = auth?.accessToken ?? ''
  const navigate = useNavigate()

  const [sprint, setSprint] = useState<SprintWithCountDTO | null>(null)
  const [tasks, setTasks] = useState<TaskDTO[]>([])
  const [sprintStats, setSprintStats] = useState<SprintStatsDTO | null>(null)
  const [feedback, setFeedback] = useState<Feedback>(null)
  const [statsError, setStatsError] = useState('')
  const [loading, setLoading] = useState(false)

  const loadSprintPage = useCallback(async (
    currentTrackId: string,
    currentSprintId: string,
  ) => {
    setLoading(true)
    setFeedback(null)
    setStatsError('')

    try {
      const [sprintPayload, taskPayload, statsResult] = await Promise.all([
        gatewayApi.tracks.getSprints(currentTrackId, accessToken),
        gatewayApi.tasks.getBySprint(currentSprintId, accessToken),
        gatewayApi.statistics.getSprintStats(currentSprintId, accessToken)
          .then((payload) => ({
            payload,
            error: '',
          }))
          .catch((error: unknown) => ({
            payload: null,
            error: normalizeStatsError(getApiErrorMessage(error)),
          })),
      ])

      setTasks(taskPayload)
      setSprintStats(statsResult.payload)
      setStatsError(statsResult.error)

      const matchedSprint =
        sprintPayload.find((item) => item.id === currentSprintId) ?? null

      setSprint(matchedSprint)

      if (!matchedSprint) {
        setFeedback({
          kind: 'error',
          message: 'Спринт не найден в составе выбранного трека.',
        })
      }
    } catch (error) {
      setFeedback({
        kind: 'error',
        message: getApiErrorMessage(error),
      })
    } finally {
      setLoading(false)
    }
  }, [accessToken])

  useEffect(() => {
    if (!accessToken || !trackId || !sprintId) {
      return
    }

    setSprint(null)
    setTasks([])
    setSprintStats(null)
    setFeedback(null)
    setStatsError('')

    void loadSprintPage(trackId, sprintId)
  }, [accessToken, loadSprintPage, sprintId, trackId])

  if (!accessToken || !trackId || !sprintId) {
    return null
  }

  const sprintStatsItems: Array<[string, string]> = sprintStats
    ? [
        ['total_tasks', String(sprintStats.total_tasks)],
        ['completed_tasks', String(sprintStats.completed_tasks)],
        ['remaining_tasks', String(sprintStats.remaining_tasks)],
        ['overdue_tasks', String(sprintStats.overdue_tasks)],
        ['active_wip', String(sprintStats.active_wip)],
        ['completion_percent', `${metricValue(sprintStats.completion_percent)}%`],
        ['overdue_percent', `${metricValue(sprintStats.overdue_percent)}%`],
        [
          'overdue_among_active_percent',
          `${metricValue(sprintStats.overdue_among_active_percent)}%`,
        ],
        ['avg_active_age_days', metricValue(sprintStats.avg_active_age_days)],
      ]
    : []

  return (
    <SectionLayout
      eyebrow="Sprint"
      title={
        sprint
          ? `Спринт ${sprint.startDate} → ${sprint.endDate}`
          : 'Страница спринта'
      }
      description="На странице спринта доступны статистика по спринту и список задач, которые в него запланированы."
      icon={<Route className="h-6 w-6" />}
      backLabel="К треку"
      backTo={workspaceTrackPath(trackId)}
      actions={
        <ActionButton
          type="button"
          variant="secondary"
          busy={loading}
          onClick={() => void loadSprintPage(trackId, sprintId)}
        >
          <RefreshCw className="h-4 w-4" />
          Обновить спринт
        </ActionButton>
      }
    >
      {feedback ? (
        <div
          className={`rounded-2xl px-4 py-3 text-sm ${
            feedback.kind === 'success'
              ? 'border border-emerald-200 bg-emerald-50 text-emerald-800'
              : 'border border-rose-200 bg-rose-50 text-rose-700'
          }`}
        >
          {feedback.message}
        </div>
      ) : null}

      <section className="grid gap-6 xl:grid-cols-[0.9fr_1.1fr]">
        <StatsSummaryPanel
          eyebrow="Sprint State"
          icon={<Activity className="h-5 w-5" />}
          title="Статистика спринта"
          description="Сводная статистика по задачам этого спринта."
          loading={loading}
          error={statsError}
          emptyText="Для этого спринта пока нет агрегированной статистики."
          tone="warm"
          stats={sprintStatsItems}
        />

        <Panel
          eyebrow="Sprint Tasks"
          icon={<ListChecks className="h-5 w-5" />}
          title="Задачи спринта"
          description="На этой странице собраны все задачи, запланированные в выбранный спринт."
          tone="cool"
        >
          {tasks.length === 0 ? (
            <FriendlyNote
              title="Пустой спринт"
              text="На этот спринт задачи не запланированы."
            />
          ) : (
            <TaskList
              title="Задачи"
              tasks={tasks}
              onUseTask={(task) => navigate(workspaceTaskPath(task.id))}
            />
          )}
        </Panel>
      </section>
    </SectionLayout>
  )
}
