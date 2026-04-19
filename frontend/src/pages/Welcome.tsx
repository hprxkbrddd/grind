import { CloudSun, HeartHandshake, Sparkles } from 'lucide-react'
import type { ReactNode } from 'react'
import { Link } from 'react-router'
import { ActionButton } from '../components/app/ActionButton'
import { Panel } from '../components/app/Panel'
import { useAuth } from '../hooks/useAuth'

const userEndpoints = [
  'POST /grind/keycloak/token',
  'POST /grind/keycloak/register',
  'POST /grind/keycloak/token/introspect',
  'GET/POST/PUT/DELETE /api/core/track/**',
  'GET/POST/PUT/DELETE /api/core/task/**',
  'GET /api/statistics/track/**',
  'GET /api/statistics/sprint/{sprintId}',
]

export function Welcome() {
  const { auth } = useAuth()

  return (
    <div className="grid gap-6 lg:grid-cols-[1.15fr_0.85fr]">
      <section className="relative overflow-hidden rounded-[34px] bg-[linear-gradient(145deg,_#1f363d,_#40798c_58%,_#5e9ba2)] px-6 py-8 text-white shadow-[0_28px_80px_rgba(31,54,61,0.2)] sm:px-8 sm:py-10">
        <div className="absolute -right-10 -top-10 h-36 w-36 rounded-full bg-white/10 blur-2xl" />
        <div className="absolute -bottom-12 left-10 h-32 w-32 rounded-full bg-[#f6efe6]/20 blur-2xl" />
        <p className="text-sm uppercase tracking-[0.32em] text-cyan-200">
          Friendly React Frontend
        </p>
        <h1 className="mt-4 max-w-3xl text-4xl font-semibold leading-tight sm:text-5xl">
          Минимальный, но уже тёплый интерфейс для треков, задач и статистики
        </h1>
        <p className="mt-5 max-w-2xl text-base leading-7 text-slate-200">
          Интерфейс по-прежнему завязан на DTO и контроллеры `gateway`, но теперь
          выглядит менее как консоль для тестов и больше как рабочее место, в
          котором приятно ориентироваться.
        </p>
        <div className="mt-8 grid gap-3 sm:grid-cols-3">
          <WelcomeChip icon={<HeartHandshake className="h-4 w-4" />}>
            быстрый вход и регистрация
          </WelcomeChip>
          <WelcomeChip icon={<Sparkles className="h-4 w-4" />}>
            живой workspace по всем user endpoint’ам
          </WelcomeChip>
          <WelcomeChip icon={<CloudSun className="h-4 w-4" />}>
            мягкий визуальный слой вместо сухой панели
          </WelcomeChip>
        </div>
        <div className="mt-8 flex flex-wrap gap-3">
          <Link to={auth?.accessToken ? '/home/workspace' : '/login'}>
            <ActionButton>
              {auth?.accessToken ? 'Открыть workspace' : 'Войти'}
              </ActionButton>
            </Link>
          {!auth?.accessToken ? (
            <Link to="/register">
              <ActionButton variant="secondary">
                Зарегистрироваться
              </ActionButton>
            </Link>
          ) : null}
        </div>
      </section>

      <Panel
        eyebrow="Coverage"
        icon={<Sparkles className="h-5 w-5" />}
        title="Что уже подключено"
        description="Каждую пользовательскую ручку можно вызвать из UI. Для сложных операций оставлены понятные формы и контекстные ID."
        tone="warm"
      >
        <div className="space-y-3">
          {userEndpoints.map((endpoint) => (
            <div
              key={endpoint}
              className="rounded-2xl border border-primary/12 bg-white/80 px-4 py-3 text-sm text-slate-700 shadow-[0_10px_18px_rgba(31,54,61,0.04)]"
            >
              {endpoint}
            </div>
          ))}
        </div>
      </Panel>
    </div>
  )
}

function WelcomeChip({
  icon,
  children,
}: {
  icon: ReactNode
  children: ReactNode
}) {
  return (
    <div className="flex items-center gap-2 rounded-2xl border border-white/15 bg-white/8 px-4 py-3 text-sm text-slate-100 backdrop-blur">
      {icon}
      <span>{children}</span>
    </div>
  )
}
