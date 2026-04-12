import { Activity, ArrowRight, KanbanSquare, Route } from 'lucide-react'
import type { ReactNode } from 'react'
import { Link } from 'react-router'
import { ActionButton } from '../components/app/ActionButton'
import { Panel } from '../components/app/Panel'
import { HeroPill } from '../components/dashboard/Shared'

export function Home() {
  return (
    <div className="space-y-6">
      <section className="relative overflow-hidden rounded-[34px] bg-[linear-gradient(145deg,_#1f363d,_#40798c_62%,_#6aa0a8)] px-6 py-8 text-white shadow-[0_28px_80px_rgba(31,54,61,0.18)] sm:px-8">
        <div className="absolute -right-8 top-4 h-28 w-28 rounded-full bg-white/10 blur-2xl" />
        <div className="absolute bottom-0 left-1/3 h-24 w-24 rounded-full bg-[#fff8ef]/18 blur-2xl" />
        <p className="text-sm uppercase tracking-[0.32em] text-cyan-200">
          Dashboard Hub
        </p>
        <h1 className="mt-4 max-w-3xl text-3xl font-semibold leading-tight sm:text-4xl">
          Выберите рабочий сценарий, а не одну перегруженную страницу
        </h1>
        <p className="mt-4 max-w-3xl text-sm leading-7 text-slate-200">
          `gateway` остаётся тем же, но интерфейс разделён на отдельные layouts:
          один для треков и задач, второй для статистики. Так проще двигаться по
          своему сценарию без лишнего шума.
        </p>
        <div className="mt-7 grid gap-3 sm:grid-cols-3">
          <HeroPill icon={<KanbanSquare className="h-4 w-4" />}>
            треки и задачи в одном рабочем пространстве
          </HeroPill>
          <HeroPill icon={<Route className="h-4 w-4" />}>
            связанные ID подставляются автоматически
          </HeroPill>
          <HeroPill icon={<Activity className="h-4 w-4" />}>
            статистика загружается целиком по выбранному треку
          </HeroPill>
        </div>
      </section>

      <section className="grid gap-6 lg:grid-cols-2">
        <HubCard
          eyebrow="Workspace"
          title="Мои треки и задачи"
          description="Отдельный layout для повседневной работы: список ваших треков, задачи выбранного трека, формы создания, редактирования и планирования."
          to="/home/workspace"
          icon={<KanbanSquare className="h-5 w-5" />}
          actionLabel="Открыть workspace"
        />
        <HubCard
          eyebrow="Statistics"
          title="Статистика"
          description="Отдельный layout для аналитики: по выбранному треку автоматически показываются state, raw, per-day, per-week и статистика всех его спринтов."
          to="/home/statistics"
          icon={<Activity className="h-5 w-5" />}
          actionLabel="Открыть statistics"
        />
      </section>
    </div>
  )
}

function HubCard({
  eyebrow,
  title,
  description,
  icon,
  to,
  actionLabel,
}: {
  eyebrow: string
  title: string
  description: string
  icon: ReactNode
  to: string
  actionLabel: string
}) {
  return (
    <Panel
      eyebrow={eyebrow}
      icon={icon}
      title={title}
      description={description}
      tone={eyebrow === 'Statistics' ? 'cool' : 'warm'}
    >
      <div className="rounded-2xl border border-primary/12 bg-white/75 p-4 text-sm leading-6 text-slate-600">
        Переход открывает отдельный экран со своей структурой и не смешивает CRUD
        и аналитику в одной длинной странице.
      </div>
      <Link to={to}>
        <ActionButton className="w-full justify-between">
          <span>{actionLabel}</span>
          <ArrowRight className="h-4 w-4" />
        </ActionButton>
      </Link>
    </Panel>
  )
}
