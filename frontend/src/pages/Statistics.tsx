import { Activity, BarChart3, RefreshCw, Rows4 } from 'lucide-react'
import { useEffect, useState } from 'react'
import { gatewayApi, getApiErrorMessage } from '../api/gateway'
import { ActionButton } from '../components/app/ActionButton'
import { JsonView } from '../components/app/JsonView'
import { Panel } from '../components/app/Panel'
import { SectionLayout } from '../components/dashboard/SectionLayout'
import {
  DiagramTable,
  FriendlyNote,
  HeroPill,
  SprintList,
  StatsGrid,
  TrackList,
  metricValue,
} from '../components/dashboard/Shared'
import { useAuth } from '../hooks/useAuth'
import type {
  DiagramDTO,
  SprintStatsDTO,
  SprintWithCountDTO,
  TrackActualStateStatsDTO,
  TrackRawStatsDTO,
  TrackWithCountDTO,
} from '../types/gateway'

interface SprintStatsCardData {
  sprint: SprintWithCountDTO
  stats: SprintStatsDTO | null
  error?: string
}

export function Statistics() {
  const { auth } = useAuth()
  const accessToken = auth?.accessToken ?? ''

  const [busy, setBusy] = useState(false)
  const [error, setError] = useState('')
  const [myTracks, setMyTracks] = useState<TrackWithCountDTO[]>([])
  const [selectedTrackId, setSelectedTrackId] = useState('')

  const [selectedTrack, setSelectedTrack] = useState<TrackWithCountDTO | null>(null)
  const [trackSprints, setTrackSprints] = useState<SprintWithCountDTO[]>([])
  const [trackState, setTrackState] =
    useState<TrackActualStateStatsDTO | null>(null)
  const [trackRaw, setTrackRaw] = useState<TrackRawStatsDTO | null>(null)
  const [perDay, setPerDay] = useState<DiagramDTO | null>(null)
  const [perWeek, setPerWeek] = useState<DiagramDTO | null>(null)
  const [sprintStatsCards, setSprintStatsCards] = useState<SprintStatsCardData[]>(
    [],
  )

  async function loadMyTracks() {
    if (!accessToken) {
      return
    }

    setBusy(true)
    setError('')

    try {
      const tracks = await gatewayApi.tracks.getMine(accessToken)
      setMyTracks(tracks)

      if (tracks.length > 0) {
        setSelectedTrackId((current) => current || tracks[0].id)
      }
    } catch (requestError) {
      setError(getApiErrorMessage(requestError))
    } finally {
      setBusy(false)
    }
  }

  async function loadAllStatistics(trackId: string) {
    if (!trackId) {
      return
    }

    setBusy(true)
    setError('')

    try {
      const trackFromList = myTracks.find((track) => track.id === trackId) ?? null
      setSelectedTrack(trackFromList)

      const [state, raw, day, week, sprints] = await Promise.all([
        gatewayApi.statistics.getTrackState(trackId, accessToken),
        gatewayApi.statistics.getTrackRaw(trackId, accessToken),
        gatewayApi.statistics.getPerDay(trackId, accessToken),
        gatewayApi.statistics.getPerWeek(trackId, accessToken),
        gatewayApi.tracks.getSprints(trackId, accessToken),
      ])

      setTrackState(state)
      setTrackRaw(raw)
      setPerDay(day)
      setPerWeek(week)
      setTrackSprints(sprints)

      const sprintResults = await Promise.allSettled(
        sprints.map(async (sprint) => ({
          sprint,
          stats: await gatewayApi.statistics.getSprintStats(sprint.id, accessToken),
        })),
      )

      setSprintStatsCards(
        sprintResults.map((result, index) => {
          if (result.status === 'fulfilled') {
            return result.value
          }

          return {
            sprint: sprints[index],
            stats: null,
            error: getApiErrorMessage(result.reason),
          }
        }),
      )
    } catch (requestError) {
      setError(getApiErrorMessage(requestError))
      setTrackState(null)
      setTrackRaw(null)
      setPerDay(null)
      setPerWeek(null)
      setTrackSprints([])
      setSprintStatsCards([])
    } finally {
      setBusy(false)
    }
  }

  useEffect(() => {
    if (!accessToken) {
      return
    }

    void loadMyTracks()
  }, [accessToken])

  useEffect(() => {
    if (!accessToken || !selectedTrackId) {
      return
    }

    void loadAllStatistics(selectedTrackId)
  }, [accessToken, selectedTrackId])

  if (!accessToken) {
    return null
  }

  return (
    <SectionLayout
      eyebrow="Statistics"
      title="Статистика по треку без ручного протыкивания"
      description="Выбираете трек один раз, а страница автоматически загружает current state, raw, диаграммы по дням и неделям, список спринтов и статистику каждого спринта."
      icon={<Activity className="h-6 w-6" />}
      actions={
        <ActionButton
          type="button"
          variant="secondary"
          busy={busy}
          onClick={() =>
            void (selectedTrackId ? loadAllStatistics(selectedTrackId) : loadMyTracks())
          }
        >
          <RefreshCw className="h-4 w-4" />
          Обновить
        </ActionButton>
      }
    >
      <section className="grid gap-6 xl:grid-cols-[0.8fr_1.2fr]">
        <Panel
          eyebrow="Track Selection"
          icon={<Rows4 className="h-5 w-5" />}
          title="Ваши треки"
          description="Клик по треку сразу перерисовывает весь статистический layout."
          tone="warm"
        >
          <div className="grid gap-3 sm:grid-cols-3">
            <HeroPill icon={<Activity className="h-4 w-4" />}>
              auto-load state и raw
            </HeroPill>
            <HeroPill icon={<BarChart3 className="h-4 w-4" />}>
              auto-load per-day и per-week
            </HeroPill>
            <HeroPill icon={<Rows4 className="h-4 w-4" />}>
              auto-load всех sprint stats
            </HeroPill>
          </div>
          <TrackList
            title="Мои треки"
            tracks={myTracks}
            onUseTrack={(track) => {
              setSelectedTrackId(track.id)
              setSelectedTrack(track)
            }}
          />
          {error ? (
            <div className="rounded-2xl border border-rose-200 bg-rose-50 px-4 py-3 text-sm text-rose-700">
              {error}
            </div>
          ) : null}
          <FriendlyNote
            title="Как читать экран"
            text="Слева выбор трека, справа полная статистическая выкладка. Отдельных кнопок для state/raw/diagram больше нет."
          />
        </Panel>

        <Panel
          eyebrow="Full View"
          icon={<BarChart3 className="h-5 w-5" />}
          title={selectedTrack ? selectedTrack.name : 'Статистика'}
          description={
            selectedTrack
              ? `Выбран track ${selectedTrack.id}. Ниже собран весь доступный статистический набор по этому треку.`
              : 'Выберите трек, чтобы увидеть статистику.'
          }
          tone="cool"
        >
          {busy ? (
            <div className="rounded-2xl border border-primary/15 bg-white/75 px-4 py-5 text-sm text-slate-600">
              Загружаю всю статистику по треку и его спринтам...
            </div>
          ) : null}

          {selectedTrack ? (
            <div className="grid gap-4 md:grid-cols-3">
              <QuickMetric
                label="Track id"
                value={selectedTrack.id}
              />
              <QuickMetric
                label="Tasks"
                value={String(selectedTrack.tasks)}
              />
              <QuickMetric
                label="Status"
                value={selectedTrack.status}
              />
            </div>
          ) : null}

          {trackState ? (
            <StatsGrid
              title="Track actual state"
              stats={[
                ['total_tasks', String(trackState.total_tasks)],
                ['completed_tasks', String(trackState.completed_tasks)],
                ['remaining_tasks', String(trackState.remaining_tasks)],
                ['overdue_tasks', String(trackState.overdue_tasks)],
                ['active_wip', String(trackState.active_wip)],
                ['completion_percent', `${metricValue(trackState.completion_percent)}%`],
                ['overdue_percent', `${metricValue(trackState.overdue_percent)}%`],
                [
                  'overdue_among_active_percent',
                  `${metricValue(trackState.overdue_among_active_percent)}%`,
                ],
                ['avg_active_age_days', metricValue(trackState.avg_active_age_days)],
              ]}
            />
          ) : null}

          {trackRaw ? (
            <StatsGrid
              title="Track raw"
              stats={[
                ['completed_last_30d', String(trackRaw.completed_last_30d)],
                ['completed_last_7d', String(trackRaw.completed_last_7d)],
              ]}
            />
          ) : null}

          <DiagramTable title="Per day diagram" data={perDay} />
          <DiagramTable title="Per week diagram" data={perWeek} />

          <SprintList
            title="Спринты выбранного трека"
            sprints={trackSprints}
            onUseSprint={(sprint) => {
              const sprintCard = document.getElementById(`sprint-${sprint.id}`)
              sprintCard?.scrollIntoView({ behavior: 'smooth', block: 'start' })
            }}
          />

          <div className="space-y-4">
            {sprintStatsCards.map((item) => (
              <div id={`sprint-${item.sprint.id}`} key={item.sprint.id}>
                {item.stats ? (
                  <StatsGrid
                    title={`Sprint ${item.sprint.id}`}
                    stats={[
                      ['range', `${item.sprint.startDate} → ${item.sprint.endDate}`],
                      ['total_tasks', String(item.stats.total_tasks)],
                      ['completed_tasks', String(item.stats.completed_tasks)],
                      ['remaining_tasks', String(item.stats.remaining_tasks)],
                      ['overdue_tasks', String(item.stats.overdue_tasks)],
                      ['active_wip', String(item.stats.active_wip)],
                      [
                        'completion_percent',
                        `${metricValue(item.stats.completion_percent)}%`,
                      ],
                      ['overdue_percent', `${metricValue(item.stats.overdue_percent)}%`],
                      [
                        'overdue_among_active_percent',
                        `${metricValue(item.stats.overdue_among_active_percent)}%`,
                      ],
                      [
                        'avg_active_age_days',
                        metricValue(item.stats.avg_active_age_days),
                      ],
                    ]}
                  />
                ) : (
                  <div className="rounded-2xl border border-rose-200 bg-rose-50 px-4 py-4 text-sm text-rose-700">
                    Не удалось загрузить статистику спринта {item.sprint.id}: {item.error}
                  </div>
                )}
              </div>
            ))}
          </div>

          <JsonView
            data={{
              trackState,
              trackRaw,
              perDay,
              perWeek,
              sprints: trackSprints,
              sprintStats: sprintStatsCards,
            }}
            emptyText="Статистика ещё не загружена."
          />
        </Panel>
      </section>
    </SectionLayout>
  )
}

function QuickMetric({
  label,
  value,
}: {
  label: string
  value: string
}) {
  return (
    <div className="rounded-2xl border border-primary/15 bg-slate-50 p-4">
      <p className="text-xs uppercase tracking-[0.2em] text-slate-500">{label}</p>
      <p className="mt-2 break-all text-lg font-semibold text-slate-900">{value}</p>
    </div>
  )
}
