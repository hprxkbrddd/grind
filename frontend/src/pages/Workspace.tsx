import { KanbanSquare, ListChecks, RefreshCw } from 'lucide-react'
import { useCallback, useEffect, useState } from 'react'
import { useNavigate } from 'react-router'
import { gatewayApi, getApiErrorMessage } from '../api/gateway'
import { ActionButton } from '../components/app/ActionButton'
import { Field } from '../components/app/Field'
import { JsonView } from '../components/app/JsonView'
import { Panel } from '../components/app/Panel'
import { TrackHierarchy } from '../components/dashboard/TrackHierarchy'
import { SectionLayout } from '../components/dashboard/SectionLayout'
import {
  ContextValue,
  FormTitle,
  FriendlyNote,
  TaskList,
  TrackList,
  toNullableId,
} from '../components/dashboard/Shared'
import { useAuth } from '../hooks/useAuth'
import type {
  SprintWithCountDTO,
  TaskDTO,
  TrackDTO,
  TrackStatus,
  TrackWithCountDTO,
} from '../types/gateway'
import {
  workspaceSprintPath,
  workspaceTaskPath,
  workspaceTrackPath,
} from '../utils/workspaceRoutes'

const trackStatusOptions: Array<{ label: string; value: TrackStatus }> = [
  { label: 'ACTIVE', value: 'ACTIVE' },
  { label: 'COMPLETED', value: 'COMPLETED' },
  { label: 'ARCHIVED', value: 'ARCHIVED' },
]

type Feedback =
  | {
      kind: 'success' | 'error'
      message: string
    }
  | null

const initialTrackForm = {
  name: '',
  description: '',
  petId: '',
  sprintLength: '14',
  startDate: '',
  targetDate: '',
  messagePolicy: 'NONE',
  status: 'ACTIVE' as TrackStatus,
}

const initialTaskForm = {
  title: '',
  description: '',
  trackId: '',
}

function numeric(value: string) {
  return Number.parseInt(value, 10)
}

export function Workspace() {
  const { auth } = useAuth()
  const accessToken = auth?.accessToken ?? ''
  const navigate = useNavigate()

  const [feedback, setFeedback] = useState<Feedback>(null)
  const [busyKey, setBusyKey] = useState<string | null>(null)

  const [selectedTrackId, setSelectedTrackId] = useState('')
  const [selectedSprintId, setSelectedSprintId] = useState('')
  const [selectedTaskId, setSelectedTaskId] = useState('')

  const [trackLookupId, setTrackLookupId] = useState('')
  const [taskLookupId, setTaskLookupId] = useState('')
  const [tasksBySprintLookupId, setTasksBySprintLookupId] = useState('')

  const [trackForm, setTrackForm] = useState(initialTrackForm)
  const [taskForm, setTaskForm] = useState(initialTaskForm)

  const [myTracks, setMyTracks] = useState<TrackWithCountDTO[]>([])
  const [allTracks, setAllTracks] = useState<TrackWithCountDTO[]>([])
  const [trackDetail, setTrackDetail] = useState<TrackWithCountDTO | null>(null)
  const [trackSprints, setTrackSprints] = useState<SprintWithCountDTO[]>([])
  const [allTasks, setAllTasks] = useState<TaskDTO[]>([])
  const [trackTasks, setTrackTasks] = useState<TaskDTO[]>([])
  const [sprintTasks, setSprintTasks] = useState<TaskDTO[]>([])
  const [taskDetail, setTaskDetail] = useState<TaskDTO | null>(null)
  const [createdTrack, setCreatedTrack] = useState<TrackDTO | null>(null)
  const [mutatedTask, setMutatedTask] = useState<TaskDTO | null>(null)
  const [expandedTrackId, setExpandedTrackId] = useState<string | null>(null)
  const [expandedSprintId, setExpandedSprintId] = useState<string | null>(null)
  const [trackSprintsById, setTrackSprintsById] = useState<
    Record<string, SprintWithCountDTO[] | undefined>
  >({})
  const [sprintTasksById, setSprintTasksById] = useState<
    Record<string, TaskDTO[] | undefined>
  >({})
  const [loadingHierarchyTrackId, setLoadingHierarchyTrackId] = useState<
    string | null
  >(null)
  const [loadingHierarchySprintId, setLoadingHierarchySprintId] = useState<
    string | null
  >(null)

  const runAction = useCallback(async <T,>(
    key: string,
    action: () => Promise<T>,
    onSuccess: (payload: T) => void,
    successMessage: string,
  ) => {
    setBusyKey(key)
    setFeedback(null)

    try {
      const payload = await action()
      onSuccess(payload)
      setFeedback({
        kind: 'success',
        message: successMessage,
      })
    } catch (error) {
      setFeedback({
        kind: 'error',
        message: getApiErrorMessage(error),
      })
    } finally {
      setBusyKey(null)
    }
  }, [])

  const captureTrack = useCallback((
    track: TrackWithCountDTO | TrackDTO,
    sprintLengthHint?: string,
  ) => {
    setSelectedTrackId(track.id)
    setTrackLookupId(track.id)
    void sprintLengthHint
    setTaskForm((current) => ({
      ...current,
      trackId: track.id,
    }))
  }, [])

  const captureSprint = useCallback((sprint: SprintWithCountDTO) => {
    setSelectedSprintId(sprint.id)
    setTasksBySprintLookupId(sprint.id)
  }, [])

  const captureTask = useCallback((task: TaskDTO) => {
    setSelectedTaskId(task.id)
    setTaskLookupId(task.id)
    setTaskForm((current) => ({
      ...current,
      trackId: task.track_id,
    }))
    setSelectedTrackId(task.track_id)
    setTrackLookupId(task.track_id)

    const sprintId = toNullableId(task.sprint_id)
    if (sprintId) {
      setSelectedSprintId(sprintId)
      setTasksBySprintLookupId(sprintId)
    }
  }, [])

  const clearSelectedTaskContext = useCallback(() => {
    setSelectedTaskId('')
    setTaskLookupId('')
    setTaskDetail(null)
  }, [])

  const loadTrackBundle = useCallback(async (
    trackId: string,
    track?: TrackWithCountDTO,
  ) => {
    if (!trackId) {
      return
    }

    setBusyKey('workspace-load')

    try {
      const [detail, sprints, tasks] = await Promise.all([
        gatewayApi.tracks.getById(trackId, accessToken),
        gatewayApi.tracks.getSprints(trackId, accessToken),
        gatewayApi.tasks.getByTrack(trackId, accessToken),
      ])

      if (track) {
        captureTrack(track)
      } else {
        captureTrack(detail)
      }

      setTrackDetail(detail)
      setTrackSprints(sprints)
      setTrackTasks(tasks)
      setExpandedTrackId(trackId)
      setExpandedSprintId(null)
      setTrackSprintsById((current) => ({
        ...current,
        [trackId]: sprints,
      }))

      if (sprints.length > 0) {
        captureSprint(sprints[0])
        setSprintTasks([])
      } else {
        setSelectedSprintId('')
        setTasksBySprintLookupId('')
        setSprintTasks([])
        setExpandedSprintId(null)
      }

      if (tasks.length > 0) {
        captureTask(tasks[0])
        setTaskDetail(tasks[0])
      } else {
        clearSelectedTaskContext()
      }
    } catch (error) {
      setFeedback({
        kind: 'error',
        message: getApiErrorMessage(error),
      })
    } finally {
      setBusyKey(null)
    }
  }, [accessToken, captureSprint, captureTask, captureTrack, clearSelectedTaskContext])

  async function loadMine() {
    await runAction(
      'tracks-mine',
      () => gatewayApi.tracks.getMine(accessToken),
      (tracks) => {
        setMyTracks(tracks)
        setTrackSprintsById({})
        setSprintTasksById({})
        setExpandedTrackId(null)
        setExpandedSprintId(null)
        if (tracks.length > 0) {
          void loadTrackBundle(tracks[0].id, tracks[0])
        } else {
          setSelectedTrackId('')
          setSelectedSprintId('')
          clearSelectedTaskContext()
          setTrackDetail(null)
          setTrackSprints([])
          setTrackTasks([])
          setSprintTasks([])
        }
      },
      'Мои треки обновлены',
    )
  }

  async function toggleTrackHierarchy(track: TrackWithCountDTO) {
    if (expandedTrackId === track.id) {
      setExpandedTrackId(null)
      setExpandedSprintId(null)
      captureTrack(track)
      return
    }

    setLoadingHierarchyTrackId(track.id)

    try {
      await loadTrackBundle(track.id, track)
    } finally {
      setLoadingHierarchyTrackId(null)
    }
  }

  async function toggleSprintHierarchy(
    track: TrackWithCountDTO,
    sprint: SprintWithCountDTO,
  ) {
    captureTrack(track)
    captureSprint(sprint)
    setExpandedTrackId(track.id)

    if (expandedSprintId === sprint.id) {
      setExpandedSprintId(null)
      return
    }

    setExpandedSprintId(sprint.id)

    const cachedTasks = sprintTasksById[sprint.id]
    if (cachedTasks) {
      setSprintTasks(cachedTasks)
      if (cachedTasks.length > 0) {
        captureTask(cachedTasks[0])
        setTaskDetail(cachedTasks[0])
      } else {
        clearSelectedTaskContext()
      }
      return
    }

    if (sprint.tasks === 0) {
      setSprintTasks([])
      setSprintTasksById((current) => ({
        ...current,
        [sprint.id]: [],
      }))
      clearSelectedTaskContext()
      return
    }

    setLoadingHierarchySprintId(sprint.id)
    setFeedback(null)

    try {
      const tasks = await gatewayApi.tasks.getBySprint(sprint.id, accessToken)
      setSprintTasks(tasks)
      setSprintTasksById((current) => ({
        ...current,
        [sprint.id]: tasks,
      }))

      if (tasks.length > 0) {
        captureTask(tasks[0])
        setTaskDetail(tasks[0])
      } else {
        clearSelectedTaskContext()
      }
    } catch (error) {
      setFeedback({
        kind: 'error',
        message: getApiErrorMessage(error),
      })
    } finally {
      setLoadingHierarchySprintId(null)
    }
  }

  function resetTrackCreateForm() {
    setTrackForm(initialTrackForm)
  }

  function resetTaskCreateForm() {
    setTaskForm((current) => ({
      ...initialTaskForm,
      trackId: current.trackId,
    }))
  }

  useEffect(() => {
    if (!accessToken) {
      return
    }

    void runAction(
      'tracks-mine',
      () => gatewayApi.tracks.getMine(accessToken),
      (tracks) => {
        setMyTracks(tracks)
        setTrackSprintsById({})
        setSprintTasksById({})
        setExpandedTrackId(null)
        setExpandedSprintId(null)
        if (tracks.length > 0) {
          void loadTrackBundle(tracks[0].id, tracks[0])
        } else {
          setSelectedTrackId('')
          setSelectedSprintId('')
          clearSelectedTaskContext()
          setTrackDetail(null)
          setTrackSprints([])
          setTrackTasks([])
          setSprintTasks([])
        }
      },
      'Мои треки обновлены',
    )
  }, [accessToken, clearSelectedTaskContext, loadTrackBundle, runAction])

  if (!accessToken) {
    return null
  }

  return (
    <SectionLayout
      eyebrow="Workspace"
      title="Мои треки и задачи"
      description="Отдельный layout для повседневной работы. Теперь блок «Мои треки» разворачивается по цепочке трек → спринт → задача и ведёт на отдельные страницы сущностей."
      icon={<KanbanSquare className="h-6 w-6" />}
      actions={
        <ActionButton
          type="button"
          variant="secondary"
          busy={busyKey === 'tracks-mine' || busyKey === 'workspace-load'}
          onClick={() => void loadMine()}
        >
          <RefreshCw className="h-4 w-4" />
          Обновить данные
        </ActionButton>
      }
    >
      <section className="grid gap-6 xl:grid-cols-[0.95fr_1.05fr]">
        <Panel
          eyebrow="Context"
          icon={<KanbanSquare className="h-5 w-5" />}
          title="Текущий контекст"
          description="Выбор трека, спринта или задачи продолжает подставляться в формы автоматически."
          tone="warm"
        >
          <div className="grid gap-4 sm:grid-cols-3">
            <ContextValue label="Track" value={selectedTrackId || '—'} />
            <ContextValue label="Sprint" value={selectedSprintId || '—'} />
            <ContextValue label="Task" value={selectedTaskId || '—'} />
          </div>
          <div className="grid gap-3 lg:grid-cols-2">
            <FriendlyNote
              title="Что происходит автоматически"
              text="После открытия layout загружаются ваши треки. При выборе трека сразу подтягиваются его детали, спринты и задачи."
            />
            <FriendlyNote
              title="Что осталось вручную"
              text="CRUD, lookup по ID и планирование остаются явными действиями, чтобы вы управляли изменениями осознанно."
            />
          </div>
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
        </Panel>

        <Panel
          eyebrow="Quick Data"
          icon={<ListChecks className="h-5 w-5" />}
          title="Мои треки"
          description="Клик по треку открывает его спринты, клик по спринту открывает задачи. Для трека, спринта и задачи доступны отдельные страницы."
          tone="cool"
        >
          <TrackHierarchy
            title="Мои треки"
            tracks={myTracks}
            expandedTrackId={expandedTrackId}
            expandedSprintId={expandedSprintId}
            selectedTrackId={selectedTrackId}
            selectedSprintId={selectedSprintId}
            selectedTaskId={selectedTaskId}
            sprintsByTrackId={trackSprintsById}
            tasksBySprintId={sprintTasksById}
            loadingTrackId={loadingHierarchyTrackId}
            loadingSprintId={loadingHierarchySprintId}
            onToggleTrack={(track) => {
              void toggleTrackHierarchy(track)
            }}
            onToggleSprint={(track, sprint) => {
              void toggleSprintHierarchy(track, sprint)
            }}
            onOpenTrackPage={(trackId) => navigate(workspaceTrackPath(trackId))}
            onOpenSprintPage={(trackId, sprintId) =>
              navigate(workspaceSprintPath(trackId, sprintId))
            }
            onOpenTask={(task) => {
              captureTask(task)
              setTaskDetail(task)
              navigate(workspaceTaskPath(task.id))
            }}
          />
          <div className="grid gap-4 sm:grid-cols-3">
            <ContextValue
              label="Спринтов в track"
              value={selectedTrackId ? String(trackSprints.length) : '—'}
            />
            <ContextValue
              label="Задач в track"
              value={selectedTrackId ? String(trackTasks.length) : '—'}
            />
            <ContextValue
              label="Задач в sprint"
              value={selectedSprintId ? String(sprintTasks.length) : '—'}
            />
          </div>
          <FriendlyNote
            title="Страницы сущностей"
            text="У каждого трека и спринта теперь есть своя страница. На странице трека можно отдельно загрузить полный список задач этого трека."
          />
        </Panel>
      </section>

      <section className="grid gap-6 xl:grid-cols-2">
        <Panel
          eyebrow="Track API"
          icon={<KanbanSquare className="h-5 w-5" />}
          title="Треки"
          description="В dashboard остаются создание треков, lookup и обзор всех треков. Редактирование и удаление перенесены на страницу конкретного трека."
          tone="warm"
        >
          <div className="grid gap-3 md:grid-cols-2">
            <ActionButton
              type="button"
              busy={busyKey === 'tracks-all'}
              onClick={() =>
                void runAction(
                  'tracks-all',
                  () => gatewayApi.tracks.getAll(accessToken),
                  setAllTracks,
                  'Загружены все треки',
                )
              }
            >
              Все треки
            </ActionButton>
            <ActionButton
              type="button"
              variant="secondary"
              busy={busyKey === 'track-detail'}
              onClick={() =>
                void runAction(
                  'track-detail',
                  () => gatewayApi.tracks.getById(trackLookupId, accessToken),
                  (payload) => {
                    captureTrack(payload)
                    setTrackDetail(payload)
                  },
                  'Детали трека загружены',
                )
              }
              disabled={!trackLookupId}
            >
              Track by id
            </ActionButton>
          </div>

          <Field
            label="Track id"
            hint="Для ручного lookup по конкретному id"
            value={trackLookupId}
            onChange={(event) => setTrackLookupId(event.target.value)}
            placeholder="track-1"
          />

          <FormTitle title="Создать track" />
          <div className="grid gap-3">
            <Field
              label="Name"
              value={trackForm.name}
              onChange={(event) =>
                setTrackForm((current) => ({
                  ...current,
                  name: event.target.value,
                }))
              }
              required
            />
            <Field
              as="textarea"
              label="Description"
              value={trackForm.description}
              onChange={(event) =>
                setTrackForm((current) => ({
                  ...current,
                  description: event.target.value,
                }))
              }
              rows={3}
            />
            <div className="grid gap-3 md:grid-cols-2">
              <Field
                label="Pet id"
                value={trackForm.petId}
                onChange={(event) =>
                  setTrackForm((current) => ({
                    ...current,
                    petId: event.target.value,
                  }))
                }
              />
              <Field
                label="Sprint length"
                type="number"
                min="1"
                value={trackForm.sprintLength}
                onChange={(event) =>
                  setTrackForm((current) => ({
                    ...current,
                    sprintLength: event.target.value,
                  }))
                }
              />
              <Field
                label="Start date"
                type="date"
                value={trackForm.startDate}
                onChange={(event) =>
                  setTrackForm((current) => ({
                    ...current,
                    startDate: event.target.value,
                  }))
                }
              />
              <Field
                label="Target date"
                type="date"
                value={trackForm.targetDate}
                onChange={(event) =>
                  setTrackForm((current) => ({
                    ...current,
                    targetDate: event.target.value,
                  }))
                }
              />
              <Field
                label="Message policy"
                value={trackForm.messagePolicy}
                onChange={(event) =>
                  setTrackForm((current) => ({
                    ...current,
                    messagePolicy: event.target.value,
                  }))
                }
              />
              <Field
                as="select"
                label="Status"
                value={trackForm.status}
                onChange={(event) =>
                  setTrackForm((current) => ({
                    ...current,
                    status: event.target.value as TrackStatus,
                  }))
                }
                options={trackStatusOptions}
              />
            </div>
            <ActionButton
              type="button"
              busy={busyKey === 'track-create'}
              onClick={() =>
                void runAction(
                  'track-create',
                  () =>
                    gatewayApi.tracks.create(
                      {
                        ...trackForm,
                        sprintLength: numeric(trackForm.sprintLength),
                      },
                      accessToken,
                    ),
                  (payload) => {
                    setCreatedTrack(payload)
                    captureTrack(payload, trackForm.sprintLength)
                    resetTrackCreateForm()
                    void loadMine()
                  },
                  'Трек создан',
                )
              }
            >
              Создать track
            </ActionButton>
          </div>

          <TrackList
            title="Все треки"
            tracks={allTracks}
            onUseTrack={(track) => {
              captureTrack(track)
              void loadTrackBundle(track.id, track)
            }}
          />

          <JsonView data={trackDetail} emptyText="Track detail ещё не запрошен." />
          <JsonView
            data={createdTrack}
            emptyText="Последний результат создания track появится здесь."
          />
        </Panel>

        <Panel
          eyebrow="Task API"
          icon={<ListChecks className="h-5 w-5" />}
          title="Задачи"
          description="В dashboard остаются создание задач, lookup и общий список. Редактирование, планирование и быстрые действия перенесены на страницу конкретной задачи."
          tone="cool"
        >
          <div className="grid gap-3 md:grid-cols-2">
            <ActionButton
              type="button"
              busy={busyKey === 'tasks-all'}
              onClick={() =>
                void runAction(
                  'tasks-all',
                  () => gatewayApi.tasks.getAll(accessToken),
                  setAllTasks,
                  'Загружены все задачи',
                )
              }
            >
              Все задачи
            </ActionButton>
            <ActionButton
              type="button"
              variant="secondary"
              busy={busyKey === 'task-detail'}
              onClick={() =>
                void runAction(
                  'task-detail',
                  () => gatewayApi.tasks.getById(taskLookupId, accessToken),
                  (payload) => {
                    captureTask(payload)
                    setTaskDetail(payload)
                  },
                  'Детали задачи загружены',
                )
              }
              disabled={!taskLookupId}
            >
              Task by id
            </ActionButton>
          </div>

          <div className="grid gap-3 md:grid-cols-2">
            <Field
              label="Task id"
              value={taskLookupId}
              onChange={(event) => setTaskLookupId(event.target.value)}
              placeholder="task-1"
            />
            <Field
              label="Sprint id"
              value={tasksBySprintLookupId}
              onChange={(event) => setTasksBySprintLookupId(event.target.value)}
              placeholder="sprint-1"
            />
            <ActionButton
              type="button"
              variant="secondary"
              busy={busyKey === 'tasks-by-sprint'}
              onClick={() =>
                void runAction(
                  'tasks-by-sprint',
                  () => gatewayApi.tasks.getBySprint(tasksBySprintLookupId, accessToken),
                  setSprintTasks,
                  'Задачи спринта загружены',
                )
              }
              disabled={!tasksBySprintLookupId}
            >
              Tasks of sprint
            </ActionButton>
          </div>

          <FormTitle title="Создать task" />
          <div className="grid gap-3">
            <Field
              label="Title"
              value={taskForm.title}
              onChange={(event) =>
                setTaskForm((current) => ({
                  ...current,
                  title: event.target.value,
                }))
              }
            />
            <Field
              as="textarea"
              label="Description"
              value={taskForm.description}
              onChange={(event) =>
                setTaskForm((current) => ({
                  ...current,
                  description: event.target.value,
                }))
              }
              rows={3}
            />
            <Field
              label="Track id"
              value={taskForm.trackId}
              onChange={(event) =>
                setTaskForm((current) => ({
                  ...current,
                  trackId: event.target.value,
                }))
              }
            />
            <ActionButton
              type="button"
              busy={busyKey === 'task-create'}
              onClick={() =>
                void runAction(
                  'task-create',
                  () => gatewayApi.tasks.create(taskForm, accessToken),
                  (payload) => {
                    setMutatedTask(payload)
                    captureTask(payload)
                    resetTaskCreateForm()
                    void loadTrackBundle(payload.track_id)
                  },
                  'Задача создана',
                )
              }
            >
              Создать task
            </ActionButton>
          </div>

          <TaskList title="Все задачи" tasks={allTasks} onUseTask={captureTask} />

          <JsonView data={taskDetail} emptyText="Task detail ещё не запрошен." />
          <JsonView
            data={mutatedTask}
            emptyText="Последний результат создания task появится здесь."
          />
        </Panel>
      </section>
    </SectionLayout>
  )
}
