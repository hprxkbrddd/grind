import { KeyRound, ShieldCheck, Sparkles } from 'lucide-react'
import { useState, type FormEvent } from 'react'
import { Link, useLocation, useNavigate } from 'react-router'
import { gatewayApi, getApiErrorMessage } from '../api/gateway'
import { ActionButton } from '../components/app/ActionButton'
import { Field } from '../components/app/Field'
import { Panel } from '../components/app/Panel'
import { useAuth } from '../hooks/useAuth'
import { buildAuthSession } from '../utils/auth'

interface NavigationState {
  from?: string
}

export function Login() {
  const { setAuth } = useAuth()
  const navigate = useNavigate()
  const location = useLocation()
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const [busy, setBusy] = useState(false)

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    setBusy(true)
    setError('')

    try {
      const tokenResponse = await gatewayApi.auth.login({ username, password })
      const session = buildAuthSession(tokenResponse, username)
      const state = location.state as NavigationState | null

      setAuth(session)
      navigate(state?.from ?? '/home', { replace: true })
    } catch (submissionError) {
      setError(getApiErrorMessage(submissionError))
    } finally {
      setBusy(false)
    }
  }

  return (
    <div className="mx-auto grid max-w-5xl gap-6 lg:grid-cols-[0.92fr_1.08fr]">
      <Panel
        eyebrow="Sign In"
        icon={<KeyRound className="h-5 w-5" />}
        title="Вход в Grind"
        description="Здесь начинается рабочая сессия: логин получает `access_token`, сохраняет его локально и открывает защищённый dashboard."
        tone="warm"
      >
        <form className="space-y-4" onSubmit={handleSubmit}>
          <Field
            label="Username"
            hint="Обычный username из Keycloak"
            name="username"
            value={username}
            onChange={(event) => setUsername(event.target.value)}
            placeholder="testicula_user"
            autoComplete="username"
            required
          />
          <Field
            label="Password"
            hint="Пароль не отправляется никуда кроме `POST /grind/keycloak/token`"
            name="password"
            type="password"
            value={password}
            onChange={(event) => setPassword(event.target.value)}
            autoComplete="current-password"
            required
          />

          {error ? (
            <div className="rounded-2xl border border-rose-200 bg-rose-50 px-4 py-3 text-sm text-rose-700">
              {error}
            </div>
          ) : null}

          <ActionButton className="w-full" type="submit" busy={busy}>
            Получить токен
          </ActionButton>
        </form>
      </Panel>

      <section className="rounded-[32px] border border-white/60 bg-[linear-gradient(180deg,_rgba(239,248,250,0.9),_rgba(255,255,255,0.86))] p-6 shadow-[0_18px_70px_rgba(31,54,61,0.08)] backdrop-blur">
        <div className="flex items-center gap-3">
          <div className="flex h-11 w-11 items-center justify-center rounded-2xl bg-white text-primary shadow-[0_10px_20px_rgba(31,54,61,0.08)]">
            <ShieldCheck className="h-5 w-5" />
          </div>
          <p className="text-sm uppercase tracking-[0.32em] text-primary">
            Auth Flow
          </p>
        </div>
        <h2 className="mt-4 text-3xl font-semibold text-slate-900">
          После входа всё важное уже под рукой
        </h2>
        <div className="mt-6 space-y-4 text-sm leading-7 text-slate-600">
          <p>
            `gateway` защищает все `api/**` маршруты JWT-токеном. Этот frontend
            подставляет `Authorization: Bearer ...` для всех защищённых вызовов.
          </p>
          <p>
            Если пользователя ещё нет, сначала зарегистрируйте его через
            отдельную форму регистрации.
          </p>
        </div>
        <div className="mt-8 rounded-2xl border border-primary/12 bg-white/80 p-4 text-sm text-slate-600">
          <div className="flex items-center gap-2 font-semibold text-slate-800">
            <Sparkles className="h-4 w-4 text-primary" />
            Что дальше после логина
          </div>
          <p className="mt-2 leading-6">
            Вы попадёте в dashboard, где можно загружать треки, создавать задачи,
            планировать их по спринтам и смотреть статистику без переключения между
            разными тестовыми страницами.
          </p>
        </div>
        <div className="mt-8 flex flex-wrap gap-3">
          <Link
            className="rounded-2xl border border-primary/30 px-4 py-2 text-sm font-semibold text-primary-dark"
            to="/register"
          >
            Перейти к регистрации
          </Link>
          <Link
            className="rounded-2xl border border-primary/30 px-4 py-2 text-sm font-semibold text-primary-dark"
            to="/"
          >
            На welcome
          </Link>
        </div>
      </section>
    </div>
  )
}
