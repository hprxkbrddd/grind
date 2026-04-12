import { ListChecks, RefreshCw, Route, Trash2 } from 'lucide-react'
import { useCallback, useEffect, useState } from 'react'
import { useNavigate, useParams } from 'react-router'
import { gatewayApi, getApiErrorMessage } from '../api/gateway'
import { ActionButton } from '../components/app/ActionButton'
import { Field } from '../components/app/Field'
import { Panel } from '../components/app/Panel'
import { SectionLayout } from '../components/dashboard/SectionLayout'
import { FriendlyNote, toNullableId } from '../components/dashboard/Shared'
import { useAuth } from '../hooks/useAuth'
import type { SprintWithCountDTO, TaskDTO } from '../types/gateway'
import { sortSprintsByStartDate } from '../utils/sorting'
import { taskStatusLabel } from '../utils/taskStatus'
import {
  workspaceSprintPath,
  workspaceTrackPath,
} from '../utils/workspaceRoutes'

type Feedback =
  | {
      kind: 'success' | 'error'
      message: string
    }
  | null

type PlanningMode = 'SPRINT' | 'DATE'

function numeric(value: string) {
  return Number.parseInt(value, 10)
}

function taskBackPath(task: TaskDTO | null) {
  if (!task) {
    return '/home/workspace'
  }

  const sprintId = toNullableId(task.sprint_id)
  return sprintId
    ? workspaceSprintPath(task.track_id, sprintId)
    : workspaceTrackPath(task.track_id)
}

function taskBackLabel(task: TaskDTO | null) {
  return toNullableId(task?.sprint_id ?? '') ? 'К спринту' : 'К треку'
}

function sprintDayMax(sprint: SprintWithCountDTO | null) {
  if (!sprint) {
    return null
  }

  const start = Date.parse(sprint.startDate)
  const end = Date.parse(sprint.endDate)

  if (Number.isNaN(start) || Number.isNaN(end)) {
    return null
  }

  const durationDays = Math.round((end - start) / 86_400_000) + 1
  return Math.max(0, durationDays - 1)
}

function dayOfSprintFromTask(
  task: TaskDTO,
  sprint: SprintWithCountDTO | null,
) {
  if (!task.plannedDate || !sprint) {
    return '0'
  }

  const planned = Date.parse(task.plannedDate)
  const sprintStart = Date.parse(sprint.startDate)

  if (Number.isNaN(planned) || Number.isNaN(sprintStart)) {
    return '0'
  }

  const delta = Math.round((planned - sprintStart) / 86_400_000)
  return String(Math.max(0, delta))
}

export function TaskPage() {
  const { taskId } = useParams()
  const { auth } = useAuth()
  const accessToken = auth?.accessToken ?? ''
  const navigate = useNavigate()

  const [task, setTask] = useState<TaskDTO | null>(null)
  const [trackSprints, setTrackSprints] = useState<SprintWithCountDTO[]>([])
  const [feedback, setFeedback] = useState<Feedback>(null)
  const [loading, setLoading] = useState(false)
  const [actionBusy, setActionBusy] = useState<string | null>(null)
  const [planningMode, setPlanningMode] = useState<PlanningMode>('SPRINT')
  const [editForm, setEditForm] = useState({
    title: '',
    description: '',
  })
  const [planningForm, setPlanningForm] = useState({
    sprintId: '',
    dayOfSprint: '0',
    plannedDate: '',
  })

  const applyTaskPayload = useCallback((
    payload: TaskDTO,
    sprints: SprintWithCountDTO[],
  ) => {
    setTask(payload)
    setTrackSprints(sprints)
    setEditForm({
      title: payload.title,
      description: payload.description,
    })

    const currentSprintId = toNullableId(payload.sprint_id) ?? ''
    const currentSprint =
      sprints.find((item) => item.id === currentSprintId) ?? null

    setPlanningForm({
      sprintId: currentSprintId,
      dayOfSprint: dayOfSprintFromTask(payload, currentSprint),
      plannedDate: payload.plannedDate ?? '',
    })

    setPlanningMode(currentSprintId ? 'SPRINT' : 'DATE')
  }, [])

  const loadTask = useCallback(async (id: string) => {
    setLoading(true)
    setFeedback(null)

    try {
      const taskPayload = await gatewayApi.tasks.getById(id, accessToken)
      const sprintPayload = await gatewayApi.tracks.getSprints(
        taskPayload.track_id,
        accessToken,
      )
      applyTaskPayload(taskPayload, sprintPayload)
    } catch (error) {
      setFeedback({
        kind: 'error',
        message: getApiErrorMessage(error),
      })
    } finally {
      setLoading(false)
    }
  }, [accessToken, applyTaskPayload])

  useEffect(() => {
    if (!accessToken || !taskId) {
      return
    }

    setTask(null)
    setTrackSprints([])
    setFeedback(null)

    void loadTask(taskId)
  }, [accessToken, loadTask, taskId])

  async function mutateTask(
    key: string,
    action: () => Promise<TaskDTO>,
    successMessage: string,
  ) {
    setActionBusy(key)
    setFeedback(null)

    try {
      const payload = await action()
      const sprintPayload = await gatewayApi.tracks.getSprints(
        payload.track_id,
        accessToken,
      )

      applyTaskPayload(payload, sprintPayload)
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
      setActionBusy(null)
    }
  }

  async function handleDeleteTask() {
    if (!taskId || !task) {
      return
    }

    setActionBusy('task-delete')
    setFeedback(null)

    try {
      const payload = await gatewayApi.tasks.remove(taskId, accessToken)
      navigate(taskBackPath(payload), { replace: true })
    } catch (error) {
      setFeedback({
        kind: 'error',
        message: getApiErrorMessage(error),
      })
      setActionBusy(null)
    }
  }

  async function handleSavePlanning() {
    if (!taskId || !task) {
      return
    }

    if (planningMode === 'SPRINT') {
      await mutateTask(
        'task-plan-sprint',
        () =>
          gatewayApi.tasks.planSprint(
            taskId,
            {
              sprintId: planningForm.sprintId,
              dayOfSprint: numeric(planningForm.dayOfSprint),
            },
            accessToken,
          ),
        'Задача прикреплена к спринту',
      )
      return
    }

    await mutateTask(
      'task-plan-date',
      () =>
        gatewayApi.tasks.planDate(
          taskId,
          {
            plannedDate: planningForm.plannedDate,
          },
          accessToken,
        ),
      'Задача прикреплена к дню',
    )
  }

  if (!accessToken || !taskId) {
    return null
  }

  const orderedSprints = sortSprintsByStartDate(trackSprints)
  const selectedPlanningSprint =
    orderedSprints.find((item) => item.id === planningForm.sprintId) ?? null
  const selectedSprintDayMax = sprintDayMax(selectedPlanningSprint)
  const sprintDayHint = selectedPlanningSprint
    ? `выберите день спринта от 0 до ${selectedSprintDayMax ?? 'n'}`
    : 'сначала выберите спринт'

  return (
    <SectionLayout
      eyebrow="Task"
      title={task?.title ?? 'Страница задачи'}
      description="Страница задачи теперь отвечает за её редактирование, планирование, смену статуса и удаление."
      icon={<ListChecks className="h-6 w-6" />}
      backLabel={taskBackLabel(task)}
      backTo={taskBackPath(task)}
      actions={
        <ActionButton
          type="button"
          variant="secondary"
          busy={loading}
          onClick={() => void loadTask(taskId)}
        >
          <RefreshCw className="h-4 w-4" />
          Обновить задачу
        </ActionButton>
      }
    >
      <section className="grid gap-6 xl:grid-cols-[1.05fr_0.95fr]">
        <Panel
          eyebrow="Task Edit"
          icon={<ListChecks className="h-5 w-5" />}
          title="Редактирование задачи"
          description="Здесь находятся редактирование текста задачи и быстрые действия над её статусом."
          tone="warm"
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

          <FriendlyNote
            title="Текущий статус"
            text={
              task
                ? `Статус задачи сейчас: ${taskStatusLabel(task.status)}`
                : 'После загрузки здесь появится текущий статус задачи.'
            }
          />

          <div className="grid gap-3">
            <Field
              label="Title"
              value={editForm.title}
              onChange={(event) =>
                setEditForm((current) => ({
                  ...current,
                  title: event.target.value,
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
            <ActionButton
              type="button"
              busy={actionBusy === 'task-update'}
              onClick={() =>
                void mutateTask(
                  'task-update',
                  () =>
                    gatewayApi.tasks.update(
                      taskId,
                      {
                        title: editForm.title,
                        description: editForm.description,
                      },
                      accessToken,
                    ),
                  'Задача обновлена',
                )
              }
              disabled={!task}
            >
              Сохранить изменения
            </ActionButton>
          </div>

          <div className="grid gap-3 border-t border-primary/10 pt-4 md:grid-cols-3">
            <ActionButton
              type="button"
              variant="secondary"
              busy={actionBusy === 'task-backlog'}
              onClick={() =>
                void mutateTask(
                  'task-backlog',
                  () => gatewayApi.tasks.backlog(taskId, accessToken),
                  'Задача перемещена в backlog',
                )
              }
              disabled={!task}
            >
              В backlog
            </ActionButton>
            <ActionButton
              type="button"
              busy={actionBusy === 'task-complete'}
              onClick={() =>
                void mutateTask(
                  'task-complete',
                  () => gatewayApi.tasks.complete(taskId, accessToken),
                  'Задача отмечена как completed',
                )
              }
              disabled={!task}
            >
              Завершить
            </ActionButton>
            <ActionButton
              type="button"
              variant="ghost"
              busy={actionBusy === 'task-delete'}
              onClick={() => void handleDeleteTask()}
              disabled={!task}
            >
              <Trash2 className="h-4 w-4" />
              Удалить
            </ActionButton>
          </div>
        </Panel>

        <Panel
          eyebrow="Task Planning"
          icon={<Route className="h-5 w-5" />}
          title="Планирование задачи"
          description="Выберите режим планирования: прикрепить задачу к спринту или сразу к конкретному дню."
          tone="cool"
        >
          <div className="flex flex-wrap gap-2">
            <button
              className={`rounded-full border px-4 py-2 text-sm font-semibold transition ${
                planningMode === 'SPRINT'
                  ? 'border-primary bg-primary text-white'
                  : 'border-primary/15 bg-white text-slate-600 hover:border-primary/35 hover:text-slate-900'
              }`}
              onClick={() => setPlanningMode('SPRINT')}
              type="button"
            >
              К спринту
            </button>
            <button
              className={`rounded-full border px-4 py-2 text-sm font-semibold transition ${
                planningMode === 'DATE'
                  ? 'border-primary bg-primary text-white'
                  : 'border-primary/15 bg-white text-slate-600 hover:border-primary/35 hover:text-slate-900'
              }`}
              onClick={() => setPlanningMode('DATE')}
              type="button"
            >
              К дню
            </button>
          </div>

          {planningMode === 'SPRINT' ? (
            orderedSprints.length === 0 ? (
              <FriendlyNote
                title="Нет спринтов"
                text="У родительского трека пока нет спринтов. Сначала настройте трек."
              />
            ) : (
              <div className="space-y-3">
                <div className="space-y-3">
                  {orderedSprints.map((sprint) => {
                    const selected = planningForm.sprintId === sprint.id

                    return (
                      <button
                        key={sprint.id}
                        className={`block w-full rounded-2xl border p-4 text-left transition ${
                          selected
                            ? 'border-primary bg-white shadow-[0_12px_28px_rgba(31,54,61,0.08)]'
                            : 'border-primary/15 bg-slate-50 hover:border-primary/40 hover:bg-white'
                        }`}
                        onClick={() =>
                          setPlanningForm((current) => ({
                            ...current,
                            sprintId: sprint.id,
                            dayOfSprint:
                              current.sprintId === sprint.id
                                ? current.dayOfSprint
                                : '0',
                          }))
                        }
                        type="button"
                      >
                        <div className="flex items-start justify-between gap-3">
                          <div>
                            <p className="font-semibold text-slate-900">
                              {sprint.startDate} → {sprint.endDate}
                            </p>
                            <p className="mt-1 text-xs text-slate-500">
                              {sprint.id}
                            </p>
                          </div>
                          <span className="rounded-full bg-primary/10 px-3 py-1 text-xs font-semibold text-primary-dark">
                            {sprint.tasks} задач
                          </span>
                        </div>
                      </button>
                    )
                  })}
                </div>

                <Field
                  label="Day of sprint"
                  type="number"
                  min="0"
                  max={
                    selectedSprintDayMax == null
                      ? undefined
                      : String(selectedSprintDayMax)
                  }
                  value={planningForm.dayOfSprint}
                  onChange={(event) =>
                    setPlanningForm((current) => ({
                      ...current,
                      dayOfSprint: event.target.value,
                    }))
                  }
                  hint={sprintDayHint}
                  placeholder={
                    selectedSprintDayMax == null ? '0' : `0-${selectedSprintDayMax}`
                  }
                />
              </div>
            )
          ) : (
            <div className="grid gap-3">
              <Field
                label="Плановый день"
                type="date"
                value={planningForm.plannedDate}
                onChange={(event) =>
                  setPlanningForm((current) => ({
                    ...current,
                    plannedDate: event.target.value,
                  }))
                }
              />
              <FriendlyNote
                title="Режим по дню"
                text="В этом режиме задача прикрепляется к конкретной календарной дате."
              />
            </div>
          )}

          <ActionButton
            type="button"
            busy={
              actionBusy === 'task-plan-sprint' || actionBusy === 'task-plan-date'
            }
            onClick={() => void handleSavePlanning()}
            disabled={
              !task ||
              (planningMode === 'SPRINT'
                ? !planningForm.sprintId || planningForm.dayOfSprint === ''
                : !planningForm.plannedDate)
            }
          >
            Сохранить
          </ActionButton>
        </Panel>
      </section>
    </SectionLayout>
  )
}
