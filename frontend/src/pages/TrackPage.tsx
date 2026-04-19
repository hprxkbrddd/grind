import {
  Activity,
  KanbanSquare,
  ListChecks,
  Pencil,
  RefreshCw,
  Route,
  Trash2,
} from 'lucide-react'
import { useCallback, useEffect, useRef, useState } from 'react'
import { useNavigate, useParams } from 'react-router'
import { gatewayApi, getApiErrorMessage } from '../api/gateway'
import { ActionButton } from '../components/app/ActionButton'
import { Field } from '../components/app/Field'
import { Panel } from '../components/app/Panel'
import { CreateTaskDialog } from '../components/dashboard/CreateTaskDialog'
import { SectionLayout } from '../components/dashboard/SectionLayout'
import { StatsSummaryPanel } from '../components/dashboard/StatsSummaryPanel'
import { TrackDailyTaskChart } from '../components/dashboard/TrackDailyTaskChart'
import {
  FriendlyNote,
  SprintList,
  TaskList,
  metricValue,
} from '../components/dashboard/Shared'
import { useAuth } from '../hooks/useAuth'
import type {
  DiagramDTO,
  CreateTaskRequestDTO,
  DateRangeDTO,
  SprintWithCountDTO,
  TaskDTO,
  TrackActualStateStatsDTO,
  TrackStatus,
  TrackWithCountDTO,
} from '../types/gateway'
import {
  addUtcDays,
  formatIsoDate,
  parseIsoDate,
  startOfIsoWeek,
  todayAsUtcDate,
} from '../utils/date'
import {
  workspaceSprintPath,
  workspaceTaskPath,
} from '../utils/workspaceRoutes'

type Feedback =
  | {
      kind: 'success' | 'error'
      message: string
    }
  | null

const trackStatusOptions: Array<{ label: string; value: TrackStatus }> = [
  { label: 'ACTIVE', value: 'ACTIVE' },
  { label: 'COMPLETED', value: 'COMPLETED' },
  { label: 'ARCHIVED', value: 'ARCHIVED' },
]

const initialEditForm = {
  name: '',
  description: '',
  petId: '',
  sprintLength: '14',
  startDate: '',
  targetDate: '',
  messagePolicy: 'NONE',
  status: 'ACTIVE' as TrackStatus,
}

function numeric(value: string) {
  return Number.parseInt(value, 10)
}

function normalizeStatsError(error: string) {
  return error === 'track id is not in stats db' ? '' : error
}

function inferSprintLength(sprints: SprintWithCountDTO[]) {
  if (sprints.length === 0) {
    return '14'
  }

  const [firstSprint] = sprints
  const start = Date.parse(firstSprint.startDate)
  const end = Date.parse(firstSprint.endDate)

  if (Number.isNaN(start) || Number.isNaN(end)) {
    return '14'
  }

  return String(Math.max(1, Math.round((end - start) / 86_400_000) + 1))
}

function buildDefaultChartRange(track: TrackWithCountDTO): DateRangeDTO {
  const trackStart = parseIsoDate(track.startDate)
  const today = todayAsUtcDate()
  const spanDays = Math.max(track.durationDays - 1, 0)

  if (trackStart === null) {
    const date = formatIsoDate(today)
    return {
      startDate: date,
      endDate: date,
    }
  }

  const visibleStart =
    trackStart.getTime() > addUtcDays(today, -spanDays).getTime()
      ? trackStart
      : addUtcDays(today, -spanDays)

  return {
    startDate: formatIsoDate(visibleStart),
    endDate: formatIsoDate(addUtcDays(visibleStart, spanDays)),
  }
}

function buildQueryRange(range: DateRangeDTO, scale: 'day' | 'week'): DateRangeDTO {
  const startDate = parseIsoDate(range.startDate)
  const endDate = parseIsoDate(range.endDate)

  if (startDate === null || endDate === null) {
    return range
  }

  const from = scale === 'day' ? startDate : startOfIsoWeek(startDate)
  const to = scale === 'day' ? endDate : startOfIsoWeek(endDate)

  return {
    startDate: formatIsoDate(addUtcDays(from, -1)),
    endDate: formatIsoDate(addUtcDays(to, 1)),
  }
}

export function TrackPage() {
  const { trackId } = useParams()
  const { auth } = useAuth()
  const accessToken = auth?.accessToken ?? ''
  const navigate = useNavigate()

  const [track, setTrack] = useState<TrackWithCountDTO | null>(null)
  const [sprints, setSprints] = useState<SprintWithCountDTO[]>([])
  const [tasks, setTasks] = useState<TaskDTO[]>([])
  const [perDayStats, setPerDayStats] = useState<DiagramDTO | null>(null)
  const [perWeekStats, setPerWeekStats] = useState<DiagramDTO | null>(null)
  const [trackStats, setTrackStats] = useState<TrackActualStateStatsDTO | null>(null)
  const [feedback, setFeedback] = useState<Feedback>(null)
  const [dayChartError, setDayChartError] = useState('')
  const [weekChartError, setWeekChartError] = useState('')
  const [trackStatsError, setTrackStatsError] = useState('')
  const [createTaskError, setCreateTaskError] = useState('')
  const [createTaskOpen, setCreateTaskOpen] = useState(false)
  const [creatingTask, setCreatingTask] = useState(false)
  const [diagramLoading, setDiagramLoading] = useState(false)
  const [chartRange, setChartRange] = useState<DateRangeDTO | null>(null)
  const [trackLoading, setTrackLoading] = useState(false)
  const [actionBusy, setActionBusy] = useState<string | null>(null)
  const [editForm, setEditForm] = useState(initialEditForm)
  const [isEditOpen, setIsEditOpen] = useState(false)
  const chartRangeRef = useRef<DateRangeDTO | null>(null)

  const applyChartRange = useCallback((range: DateRangeDTO | null) => {
    chartRangeRef.current = range
    setChartRange(range)
  }, [])

  const loadDiagramStats = useCallback(async (id: string, range: DateRangeDTO) => {
    setDiagramLoading(true)
    setDayChartError('')
    setWeekChartError('')

    try {
      const [dayStatsResult, weekStatsResult] = await Promise.all([
        gatewayApi.statistics.getPerDayInRange(
          id,
          buildQueryRange(range, 'day'),
          accessToken,
        )
          .then((payload) => ({
            payload,
            error: '',
          }))
          .catch((error: unknown) => ({
            payload: null,
            error: normalizeStatsError(getApiErrorMessage(error)),
          })),
        gatewayApi.statistics.getPerWeekInRange(
          id,
          buildQueryRange(range, 'week'),
          accessToken,
        )
          .then((payload) => ({
            payload,
            error: '',
          }))
          .catch((error: unknown) => ({
            payload: null,
            error: normalizeStatsError(getApiErrorMessage(error)),
          })),
      ])

      applyChartRange(range)
      setPerDayStats(dayStatsResult.payload)
      setPerWeekStats(weekStatsResult.payload)
      setDayChartError(dayStatsResult.error)
      setWeekChartError(weekStatsResult.error)
    } finally {
      setDiagramLoading(false)
    }
  }, [accessToken, applyChartRange])

  const loadTrackPage = useCallback(async (id: string, requestedRange?: DateRangeDTO | null) => {
    setTrackLoading(true)
    setFeedback(null)
    setTrackStatsError('')

    try {
      const [
        trackPayload,
        sprintPayload,
        taskPayload,
        trackStatsResult,
      ] = await Promise.all([
        gatewayApi.tracks.getById(id, accessToken),
        gatewayApi.tracks.getSprints(id, accessToken),
        gatewayApi.tasks.getByTrack(id, accessToken),
        gatewayApi.statistics.getTrackState(id, accessToken)
          .then((payload) => ({
            payload,
            error: '',
          }))
          .catch((error: unknown) => ({
            payload: null,
            error: normalizeStatsError(getApiErrorMessage(error)),
          })),
      ])

      setTrack(trackPayload)
      setSprints(sprintPayload)
      setTasks(taskPayload)
      setTrackStats(trackStatsResult.payload)
      setTrackStatsError(trackStatsResult.error)

      const nextRange =
        requestedRange !== undefined
          ? requestedRange ?? buildDefaultChartRange(trackPayload)
          : chartRangeRef.current ?? buildDefaultChartRange(trackPayload)
      await loadDiagramStats(id, nextRange)
    } catch (error) {
      setTrackStats(null)
      setPerDayStats(null)
      setPerWeekStats(null)
      applyChartRange(null)
      setFeedback({
        kind: 'error',
        message: getApiErrorMessage(error),
      })
    } finally {
      setTrackLoading(false)
    }
  }, [accessToken, applyChartRange, loadDiagramStats])

  const handleApplyChartRange = useCallback(async (range: DateRangeDTO) => {
    if (!trackId) {
      return
    }

    await loadDiagramStats(trackId, range)
  }, [loadDiagramStats, trackId])

  const handleResetChartRange = useCallback(async () => {
    if (!trackId || !track) {
      return
    }

    await loadDiagramStats(trackId, buildDefaultChartRange(track))
  }, [loadDiagramStats, track, trackId])

  useEffect(() => {
    if (!accessToken || !trackId) {
      return
    }

    setTrack(null)
    setSprints([])
    setTasks([])
    setTrackStats(null)
    setPerDayStats(null)
    setPerWeekStats(null)
    setFeedback(null)
    setTrackStatsError('')
    setDayChartError('')
    setWeekChartError('')
    applyChartRange(null)

    void loadTrackPage(trackId, null)
  }, [accessToken, applyChartRange, loadTrackPage, trackId])

  useEffect(() => {
    if (!track) {
      return
    }

    setEditForm({
      name: track.name,
      description: track.description,
      petId: track.petId,
      sprintLength: inferSprintLength(sprints),
      startDate: track.startDate,
      targetDate: track.targetDate,
      messagePolicy: track.messagePolicy ?? '',
      status: track.status,
    })
  }, [sprints, track])

  async function handleUpdateTrack() {
    if (!trackId) {
      return
    }

    setActionBusy('track-update')
    setFeedback(null)

    try {
      await gatewayApi.tracks.update(
        trackId,
        {
          name: editForm.name,
          description: editForm.description,
          petId: editForm.petId,
          sprintLength: numeric(editForm.sprintLength),
          startDate: editForm.startDate,
          targetDate: editForm.targetDate,
          messagePolicy: editForm.messagePolicy,
          status: editForm.status,
        },
        accessToken,
      )

      await loadTrackPage(trackId)
      setIsEditOpen(false)
      setFeedback({
        kind: 'success',
        message: 'Трек обновлён',
      })
    } catch (error) {
      setFeedback({
        kind: 'error',
        message: getApiErrorMessage(error),
      })
    } finally {
      setActionBusy(null)
    }
  }

  async function handleDeleteTrack() {
    if (!trackId) {
      return
    }

    setActionBusy('track-delete')
    setFeedback(null)

    try {
      await gatewayApi.tracks.remove(trackId, accessToken)
      navigate('/home/workspace', { replace: true })
    } catch (error) {
      setFeedback({
        kind: 'error',
        message: getApiErrorMessage(error),
      })
      setActionBusy(null)
    }
  }

  const handleCreateTask = useCallback(async (payload: CreateTaskRequestDTO) => {
    if (!trackId) {
      return
    }

    setCreatingTask(true)
    setCreateTaskError('')
    setFeedback(null)

    try {
      await gatewayApi.tasks.create(payload, accessToken)
      setCreateTaskOpen(false)
      await loadTrackPage(trackId)
      setFeedback({
        kind: 'success',
        message: 'Задача создана',
      })
    } catch (error) {
      setCreateTaskError(getApiErrorMessage(error))
    } finally {
      setCreatingTask(false)
    }
  }, [accessToken, loadTrackPage, trackId])

  if (!accessToken || !trackId) {
    return null
  }

  const trackStatsItems: Array<[string, string]> = trackStats
    ? [
        ['total_tasks', String(trackStats.total_tasks)],
        ['completed_tasks', String(trackStats.completed_tasks)],
        ['remaining_tasks', String(trackStats.remaining_tasks)],
        ['overdue_tasks', String(trackStats.overdue_tasks)],
        ['active_wip', String(trackStats.active_wip)],
        ['completion_percent', `${metricValue(trackStats.completion_percent)}%`],
        ['overdue_percent', `${metricValue(trackStats.overdue_percent)}%`],
        [
          'overdue_among_active_percent',
          `${metricValue(trackStats.overdue_among_active_percent)}%`,
        ],
        ['avg_active_age_days', metricValue(trackStats.avg_active_age_days)],
      ]
    : []

  return (
    <SectionLayout
      eyebrow="Track"
      title={track?.name ?? 'Страница трека'}
      description={track?.description || 'Описание трека пока не заполнено.'}
      icon={<KanbanSquare className="h-6 w-6" />}
      backLabel="К workspace"
      backTo="/home/workspace"
      actions={
        <>
          <ActionButton
            type="button"
            variant={isEditOpen ? 'primary' : 'secondary'}
            onClick={() => setIsEditOpen((current) => !current)}
          >
            <Pencil className="h-4 w-4" />
            {isEditOpen ? 'Скрыть редактирование' : 'Редактировать'}
          </ActionButton>
          <ActionButton
            type="button"
            variant="secondary"
            busy={trackLoading}
            onClick={() => void loadTrackPage(trackId)}
          >
            <RefreshCw className="h-4 w-4" />
            Обновить трек
          </ActionButton>
        </>
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

      <TrackDailyTaskChart
        dayStats={perDayStats}
        weekStats={perWeekStats}
        tasks={tasks}
        loading={trackLoading || diagramLoading}
        dayError={dayChartError}
        weekError={weekChartError}
        rangeStart={chartRange?.startDate}
        rangeEnd={chartRange?.endDate}
        onApplyRange={handleApplyChartRange}
        onResetRange={handleResetChartRange}
      />

      <StatsSummaryPanel
        eyebrow="Track State"
        icon={<Activity className="h-5 w-5" />}
        title="Статистика трека"
        description="Текущее состояние задач этого трека в агрегированном виде."
        loading={trackLoading}
        error={trackStatsError}
        emptyText="Для этого трека пока нет агрегированной статистики."
        tone="warm"
        stats={trackStatsItems}
        wide
      />

      {!isEditOpen ? (
        <section className="grid gap-6 xl:grid-cols-2">
          <Panel
            eyebrow="Sprints"
            icon={<Route className="h-5 w-5" />}
            title="Список спринтов"
            description="Все спринты этого трека доступны сразу на странице."
            tone="warm"
          >
            <SprintList
              title="Спринты"
              sprints={sprints}
              emptyText="У этого трека пока нет спринтов."
              onUseSprint={(sprint) =>
                navigate(workspaceSprintPath(trackId, sprint.id))
              }
            />
          </Panel>

          <Panel
            eyebrow="Tasks"
            icon={<ListChecks className="h-5 w-5" />}
            title="Список задач трека"
            description="Все задачи, привязанные к этому треку, отображаются здесь."
            tone="cool"
          >
            <TaskList
              title="Задачи"
              tasks={tasks}
              emptyText="В этом треке пока нет задач."
              onCreateTask={() => {
                setCreateTaskError('')
                setCreateTaskOpen(true)
              }}
              showCreateCard
              onUseTask={(task) => navigate(workspaceTaskPath(task.id))}
            />
          </Panel>
        </section>
      ) : null}

      {createTaskOpen ? (
        <CreateTaskDialog
          busy={creatingTask}
          error={createTaskError}
          onClose={() => {
            if (creatingTask) {
              return
            }

            setCreateTaskError('')
            setCreateTaskOpen(false)
          }}
          onSubmit={handleCreateTask}
          trackId={trackId}
          trackName={track?.name}
        />
      ) : null}

      {isEditOpen ? (
        <Panel
          eyebrow="Track Edit"
          icon={<KanbanSquare className="h-5 w-5" />}
          title="Редактирование трека"
          description="Это тот же компонент редактирования трека, но он открывается только по кнопке."
          tone="neutral"
        >
          <FriendlyNote
            title="Что важно"
            text="Если изменить даты трека или длину спринта, состав и границы спринтов будут перестроены."
          />

          <div className="grid gap-3">
            <Field
              label="Name"
              value={editForm.name}
              onChange={(event) =>
                setEditForm((current) => ({
                  ...current,
                  name: event.target.value,
                }))
              }
            />
            <Field
              as="textarea"
              label="Description"
              value={editForm.description}
              onChange={(event) =>
                setEditForm((current) => ({
                  ...current,
                  description: event.target.value,
                }))
              }
              rows={3}
            />
            <div className="grid gap-3 md:grid-cols-2">
              <Field
                label="Pet id"
                value={editForm.petId}
                onChange={(event) =>
                  setEditForm((current) => ({
                    ...current,
                    petId: event.target.value,
                  }))
                }
              />
              <Field
                label="Sprint length"
                type="number"
                min="1"
                value={editForm.sprintLength}
                onChange={(event) =>
                  setEditForm((current) => ({
                    ...current,
                    sprintLength: event.target.value,
                  }))
                }
              />
              <Field
                label="Start date"
                type="date"
                value={editForm.startDate}
                onChange={(event) =>
                  setEditForm((current) => ({
                    ...current,
                    startDate: event.target.value,
                  }))
                }
              />
              <Field
                label="Target date"
                type="date"
                value={editForm.targetDate}
                onChange={(event) =>
                  setEditForm((current) => ({
                    ...current,
                    targetDate: event.target.value,
                  }))
                }
              />
              <Field
                label="Message policy"
                value={editForm.messagePolicy}
                onChange={(event) =>
                  setEditForm((current) => ({
                    ...current,
                    messagePolicy: event.target.value,
                  }))
                }
              />
              <Field
                as="select"
                label="Status"
                value={editForm.status}
                onChange={(event) =>
                  setEditForm((current) => ({
                    ...current,
                    status: event.target.value as TrackStatus,
                  }))
                }
                options={trackStatusOptions}
              />
            </div>
            <div className="grid gap-3 md:grid-cols-2">
              <ActionButton
                type="button"
                busy={actionBusy === 'track-update'}
                onClick={() => void handleUpdateTrack()}
                disabled={!track}
              >
                Сохранить изменения
              </ActionButton>
              <ActionButton
                type="button"
                variant="secondary"
                busy={actionBusy === 'track-delete'}
                onClick={() => void handleDeleteTrack()}
                disabled={!track}
              >
                <Trash2 className="h-4 w-4" />
                Удалить трек
              </ActionButton>
            </div>
          </div>
        </Panel>
      ) : null}
    </SectionLayout>
  )
}
