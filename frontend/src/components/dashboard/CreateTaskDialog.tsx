import { ClipboardList, Link2, Sparkles, X } from 'lucide-react'
import { useEffect, useState } from 'react'
import type { CreateTaskRequestDTO } from '../../types/gateway'
import { ActionButton } from '../app/ActionButton'
import { Field } from '../app/Field'

function createInitialState(trackId: string): CreateTaskRequestDTO {
  return {
    title: '',
    description: '',
    trackId,
  }
}

interface CreateTaskDialogProps {
  trackId: string
  trackName?: string
  busy?: boolean
  error?: string
  onClose: () => void
  onSubmit: (payload: CreateTaskRequestDTO) => Promise<void>
}

export function CreateTaskDialog({
  trackId,
  trackName = '',
  busy = false,
  error = '',
  onClose,
  onSubmit,
}: CreateTaskDialogProps) {
  const [formState, setFormState] = useState<CreateTaskRequestDTO>(() =>
    createInitialState(trackId),
  )
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

  async function handleSubmit(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault()

    const title = formState.title.trim()
    const description = formState.description.trim()

    if (!title) {
      setValidationError('Укажите название задачи.')
      return
    }

    setValidationError('')

    await onSubmit({
      ...formState,
      title,
      description,
      trackId,
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
        className="w-full max-w-3xl rounded-[32px] border border-white/70 bg-[linear-gradient(180deg,_rgba(247,243,238,0.98),_rgba(255,255,255,0.98))] p-5 shadow-[0_28px_80px_rgba(15,23,42,0.22)] sm:p-6"
        onClick={(event) => event.stopPropagation()}
      >
        <div className="flex items-start justify-between gap-4">
          <div className="space-y-3">
            <div className="inline-flex items-center gap-2 rounded-full border border-primary/15 bg-white/85 px-3 py-1 text-xs font-semibold uppercase tracking-[0.24em] text-primary">
              <Sparkles className="h-3.5 w-3.5" />
              POST /api/core/task
            </div>
            <div>
              <h2 className="text-2xl font-semibold text-slate-900">
                Новая задача
              </h2>
              <p className="mt-2 max-w-2xl text-sm leading-6 text-slate-500">
                Форма собрана по Swagger-контракту создания задачи: `title`,
                `description`, `trackId`.
              </p>
            </div>
          </div>

          <button
            aria-label="Закрыть форму создания задачи"
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
              label={
                <span className="inline-flex items-center gap-2">
                  <ClipboardList className="h-4 w-4 text-primary" />
                  Название задачи
                </span>
              }
              maxLength={255}
              onChange={(event) =>
                setFormState((current) => ({
                  ...current,
                  title: event.target.value,
                }))
              }
              placeholder="Например, Подготовить roadmap"
              required
              value={formState.title}
            />
            <Field
              label={
                <span className="inline-flex items-center gap-2">
                  <Link2 className="h-4 w-4 text-primary" />
                  Track ID
                </span>
              }
              readOnly
              value={trackId}
            />
          </div>

          {trackName ? (
            <div className="rounded-2xl border border-primary/12 bg-white/80 px-4 py-3 text-sm text-slate-600">
              Задача будет создана в треке <span className="font-semibold text-slate-900">{trackName}</span>.
            </div>
          ) : null}

          <Field
            as="textarea"
            label="Описание"
            onChange={(event) =>
              setFormState((current) => ({
                ...current,
                description: event.target.value,
              }))
            }
            placeholder="Кратко опишите ожидаемый результат задачи"
            rows={6}
            value={formState.description}
          />

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
              Создать задачу
            </ActionButton>
          </div>
        </form>
      </div>
    </div>
  )
}
