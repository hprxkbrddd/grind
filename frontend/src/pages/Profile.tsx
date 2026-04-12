import { Fingerprint, UserCircle2 } from 'lucide-react'
import { useEffect, useEffectEvent, useState } from 'react'
import { gatewayApi, getApiErrorMessage } from '../api/gateway'
import { ActionButton } from '../components/app/ActionButton'
import { JsonView } from '../components/app/JsonView'
import { Panel } from '../components/app/Panel'
import { useAuth } from '../hooks/useAuth'
import type { TokenIntrospectionResponse } from '../types/gateway'

function formatEpoch(epochSeconds: number | null) {
  if (!epochSeconds) {
    return 'unknown'
  }

  return new Date(epochSeconds * 1000).toLocaleString()
}

export function Profile() {
  const { auth } = useAuth()
  const [introspection, setIntrospection] =
    useState<TokenIntrospectionResponse | null>(null)
  const [error, setError] = useState('')
  const [busy, setBusy] = useState(false)

  const loadIntrospection = useEffectEvent(async () => {
    if (!auth?.accessToken) {
      return
    }

    setBusy(true)
    setError('')

    try {
      const payload = await gatewayApi.auth.introspect(auth.accessToken)
      setIntrospection(payload)
    } catch (requestError) {
      setError(getApiErrorMessage(requestError))
    } finally {
      setBusy(false)
    }
  })

  useEffect(() => {
    void loadIntrospection()
  }, [auth?.accessToken])

  return (
    <div className="grid gap-6 xl:grid-cols-[0.85fr_1.15fr]">
      <Panel
        eyebrow="Session"
        icon={<UserCircle2 className="h-5 w-5" />}
        title="Текущая сессия"
        description="Здесь можно спокойно проверить состояние текущего JWT и убедиться, что сессия жива, не читая сырые ответы вручную."
        tone="cool"
      >
        <div className="space-y-4">
          <div className="rounded-2xl border border-primary/15 bg-slate-50 p-4">
            <p className="text-xs uppercase tracking-[0.3em] text-primary">
              Username
            </p>
            <p className="mt-2 text-lg font-semibold text-slate-900">
              {auth?.username}
            </p>
          </div>

          <div className="grid gap-4 sm:grid-cols-2">
            <div className="rounded-2xl border border-primary/15 bg-slate-50 p-4">
              <p className="text-xs uppercase tracking-[0.3em] text-primary">
                Roles
              </p>
              <p className="mt-2 text-sm text-slate-700">
                {auth?.roles.length ? auth.roles.join(', ') : 'No mapped roles'}
              </p>
            </div>
            <div className="rounded-2xl border border-primary/15 bg-slate-50 p-4">
              <p className="text-xs uppercase tracking-[0.3em] text-primary">
                Access token expiry
              </p>
              <p className="mt-2 text-sm text-slate-700">
                {auth?.expiresAt
                  ? new Date(auth.expiresAt).toLocaleString()
                  : 'Unknown'}
              </p>
            </div>
          </div>

          {introspection ? (
            <div className="grid gap-4 sm:grid-cols-2">
              <div className="rounded-2xl border border-emerald-200 bg-emerald-50 p-4">
                <p className="text-xs uppercase tracking-[0.3em] text-emerald-700">
                  Active
                </p>
                <p className="mt-2 text-lg font-semibold text-emerald-900">
                  {introspection.active ? 'true' : 'false'}
                </p>
              </div>
              <div className="rounded-2xl border border-primary/15 bg-slate-50 p-4">
                <p className="text-xs uppercase tracking-[0.3em] text-primary">
                  Token window
                </p>
                <p className="mt-2 text-sm text-slate-700">
                  `iat`: {formatEpoch(introspection.iat)}
                </p>
                <p className="mt-1 text-sm text-slate-700">
                  `exp`: {formatEpoch(introspection.exp)}
                </p>
              </div>
            </div>
          ) : null}

          {error ? (
            <div className="rounded-2xl border border-rose-200 bg-rose-50 px-4 py-3 text-sm text-rose-700">
              {error}
            </div>
          ) : null}

          <ActionButton type="button" onClick={loadIntrospection} busy={busy}>
            Обновить introspection
          </ActionButton>
        </div>
      </Panel>

      <Panel
        eyebrow="Debug"
        icon={<Fingerprint className="h-5 w-5" />}
        title="Introspection payload"
        description="Если нужно сверить фактическую схему `TokenIntrospectionResponse`, технический JSON остаётся рядом."
      >
        <JsonView
          data={introspection}
          emptyText="Интроспекция ещё не загружена или завершилась ошибкой."
        />
      </Panel>
    </div>
  )
}
