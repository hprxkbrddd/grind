import { CalendarRange, Route, Sparkles, Tag, X } from 'lucide-react'
import { useEffect, useState } from 'react'
import type { CreateTrackRequestDTO, TrackStatus } from '../../types/gateway'
import { ActionButton } from '../app/ActionButton'
import { Field } from '../app/Field'

const DEFAULT_SPRINT_LENGTH = 14

const TRACK_STATUS_OPTIONS: Array<{ label: string; value: TrackStatus }> = [
  { label: 'Активный', value: 'ACTIVE' },
  { label: 'Завершённый', value: 'COMPLETED' },
  { label: 'Архивный', value: 'ARCHIVED' },
]

function formatDateInput(value: Date) {
  const year = value.getFullYear()
  const month = String(value.getMonth() + 1).padStart(2, '0')
  const day = String(value.getDate()).padStart(2, '0')

  return `${year}-${month}-${day}`
}

function createInitialState(): CreateTrackRequestDTO {
  const startDate = new Date()
  const targetDate = new Date(startDate)

  targetDate.setDate(targetDate.getDate() + DEFAULT_SPRINT_LENGTH - 1)

  return {
    name: '',
    description: '',
    petId: '',
    sprintLength: DEFAULT_SPRINT_LENGTH,
    startDate: formatDateInput(startDate),
    targetDate: formatDateInput(targetDate),
    messagePolicy: '',
    status: 'ACTIVE',
  }
}

interface CreateTrackDialogProps {
  busy?: boolean
  error?: string
  onClose: () => void
  onSubmit: (payload: CreateTrackRequestDTO) => Promise<void>
}

export function CreateTrackDialog({
  busy = false,
  error = '',
  onClose,
  onSubmit,
}: CreateTrackDialogProps) {
  const [formState, setFormState] = useState<CreateTrackRequestDTO>(createInitialState)
  const [validationError, setValidationError] = useState('')

  useEffect(() => {
    const previousOverflow = document.body.style.overflow

    document.body.style.overflow = 'hidden'

    function handleKeyDown(event: KeyboardEvent) {
      if (event.key === 'Escape' && !busy) {
        onClose()
      }
    }

    window.addEventListener('keydown', handleKeyDown)

    return () => {
      document.body.style.overflow = previousOverflow
      window.removeEventListener('keydown', handleKeyDown)
    }
  }, [busy, onClose])

  function updateField<K extends keyof CreateTrackRequestDTO>(
    key: K,
    value: CreateTrackRequestDTO[K],
  ) {
    setFormState((current) => ({
      ...current,
      [key]: value,
    }))
  }

  async function handleSubmit(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault()

    const name = formState.name.trim()
    const description = formState.description.trim()
    const petId = formState.petId.trim()
    const messagePolicy = formState.messagePolicy.trim()

    if (!name) {
      setValidationError('Укажите название трека.')
      return
    }

    if (!Number.isInteger(formState.sprintLength) || formState.sprintLength < 1) {
      setValidationError('Длина спринта должна быть целым числом больше нуля.')
      return
    }

    if (formState.targetDate < formState.startDate) {
      setValidationError('Дата завершения не может быть раньше даты старта.')
      return
    }

    setValidationError('')

    await onSubmit({
      ...formState,
      name,
      description,
      petId,
      messagePolicy,
    })
  }

  return (
    <div
      aria-modal="true"
      className="fixed inset-0 z-50 flex items-center justify-center bg-slate-950/45 px-4 py-6 backdrop-blur-sm"
      role="dialog"
      onClick={busy ? undefined : onClose}
    >
      <div
        className="w-full max-w-4xl rounded-[32px] border border-white/70 bg-[linear-gradient(180deg,_rgba(247,243,238,0.98),_rgba(255,255,255,0.98))] p-5 shadow-[0_28px_80px_rgba(15,23,42,0.22)] sm:p-6"
        onClick={(event) => event.stopPropagation()}
      >
        <div className="flex items-start justify-between gap-4">
          <div className="space-y-3">
            <div className="inline-flex items-center gap-2 rounded-full border border-primary/15 bg-white/85 px-3 py-1 text-xs font-semibold uppercase tracking-[0.24em] text-primary">
              <Sparkles className="h-3.5 w-3.5" />
              POST /api/core/track
            </div>
            <div>
              <h2 className="text-2xl font-semibold text-slate-900">
                Новый трек
              </h2>
              <p className="mt-2 max-w-2xl text-sm leading-6 text-slate-500">
                Форма собрана по Swagger-контракту создания трека: `name`,
                `description`, `petId`, `sprintLength`, `startDate`,
                `targetDate`, `messagePolicy`, `status`.
              </p>
            </div>
          </div>

          <button
            aria-label="Закрыть форму создания трека"
            className="inline-flex h-11 w-11 items-center justify-center rounded-2xl border border-primary/12 bg-white/90 text-slate-500 transition hover:border-primary/30 hover:text-primary disabled:cursor-not-allowed disabled:opacity-60"
            disabled={busy}
            onClick={onClose}
            type="button"
          >
            <X className="h-5 w-5" />
          </button>
        </div>

        <form className="mt-6 space-y-5" onSubmit={handleSubmit}>
          <div className="grid gap-4 sm:grid-cols-2">
            <Field
              autoFocus
              label="Название"
              maxLength={255}
              onChange={(event) => updateField('name', event.target.value)}
              placeholder="Например, Fitness Sprint"
              required
              value={formState.name}
            />
            <Field
              label="Pet ID"
              maxLength={255}
              onChange={(event) => updateField('petId', event.target.value)}
              placeholder="pet-1"
              value={formState.petId}
            />
            <Field
              as="input"
              label="Длина спринта, дней"
              min={1}
              onChange={(event) =>
                updateField('sprintLength', Number(event.target.value))
              }
              required
              type="number"
              value={formState.sprintLength}
            />
            <Field
              as="select"
              label="Статус"
              onChange={(event) =>
                updateField('status', event.target.value as TrackStatus)
              }
              options={TRACK_STATUS_OPTIONS}
              value={formState.status}
            />
            <Field
              as="input"
              label={
                <span className="inline-flex items-center gap-2">
                  <CalendarRange className="h-4 w-4 text-primary" />
                  Дата старта
                </span>
              }
              onChange={(event) => {
                const nextStartDate = event.target.value

                setFormState((current) => ({
                  ...current,
                  startDate: nextStartDate,
                  targetDate:
                    current.targetDate < nextStartDate
                      ? nextStartDate
                      : current.targetDate,
                }))
              }}
              required
              type="date"
              value={formState.startDate}
            />
            <Field
              as="input"
              label={
                <span className="inline-flex items-center gap-2">
                  <Route className="h-4 w-4 text-primary" />
                  Дата завершения
                </span>
              }
              min={formState.startDate}
              onChange={(event) => updateField('targetDate', event.target.value)}
              required
              type="date"
              value={formState.targetDate}
            />
          </div>

          <div className="grid gap-4 sm:grid-cols-2">
            <Field
              as="textarea"
              label="Описание"
              onChange={(event) => updateField('description', event.target.value)}
              placeholder="Кратко опишите цель и содержимое трека"
              rows={5}
              value={formState.description}
            />
            <Field
              as="textarea"
              label={
                <span className="inline-flex items-center gap-2">
                  <Tag className="h-4 w-4 text-primary" />
                  Message policy
                </span>
              }
              onChange={(event) => updateField('messagePolicy', event.target.value)}
              placeholder="Например, daily-summary"
              rows={5}
              value={formState.messagePolicy}
            />
          </div>

          {validationError ? (
            <div className="rounded-2xl border border-amber-200 bg-amber-50 px-4 py-3 text-sm text-amber-800">
              {validationError}
            </div>
          ) : null}

          {error ? (
            <div className="rounded-2xl border border-rose-200 bg-rose-50 px-4 py-3 text-sm text-rose-700">
              {error}
            </div>
          ) : null}

          <div className="flex flex-wrap justify-end gap-3">
            <ActionButton
              disabled={busy}
              onClick={onClose}
              type="button"
              variant="ghost"
            >
              Отмена
            </ActionButton>
            <ActionButton busy={busy} type="submit">
              Создать трек
            </ActionButton>
          </div>
        </form>
      </div>
    </div>
  )
}
