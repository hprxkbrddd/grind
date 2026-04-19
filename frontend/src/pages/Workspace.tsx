import { KanbanSquare, RefreshCw } from 'lucide-react'
import { useCallback, useEffect, useState } from 'react'
import { useNavigate } from 'react-router'
import { gatewayApi, getApiErrorMessage } from '../api/gateway'
import { ActionButton } from '../components/app/ActionButton'
import { CreateTrackDialog } from '../components/dashboard/CreateTrackDialog'
import { Panel } from '../components/app/Panel'
import { SectionLayout } from '../components/dashboard/SectionLayout'
import { FriendlyNote } from '../components/dashboard/Shared'
import {
  TrackOverviewGrid,
  type TrackOverviewCardItem,
} from '../components/dashboard/TrackOverviewGrid'
import { useAuth } from '../hooks/useAuth'
import type { CreateTrackRequestDTO } from '../types/gateway'
import type { TrackWithCountDTO } from '../types/gateway'
import { workspaceTrackPath } from '../utils/workspaceRoutes'

interface TrackCardStats {
  sprintCount: number | null
  completionPercent: number | null
}

function normalizeTrackStatsError(error: unknown) {
  const message = getApiErrorMessage(error)

  return message === 'track id is not in stats db' ? '' : message
}

export function Workspace() {
  const { auth } = useAuth()
  const accessToken = auth?.accessToken ?? ''
  const navigate = useNavigate()

  const [error, setError] = useState('')
  const [createTrackError, setCreateTrackError] = useState('')
  const [createTrackOpen, setCreateTrackOpen] = useState(false)
  const [creatingTrack, setCreatingTrack] = useState(false)
  const [metricsNotice, setMetricsNotice] = useState('')
  const [refreshing, setRefreshing] = useState(false)
  const [myTracks, setMyTracks] = useState<TrackWithCountDTO[]>([])
  const [trackStatsById, setTrackStatsById] = useState<
    Record<string, TrackCardStats>
  >({})

  const loadTrackCards = useCallback(async (tracks: TrackWithCountDTO[]) => {
    if (!accessToken) {
      return
    }

    if (tracks.length === 0) {
      setTrackStatsById({})
      setMetricsNotice('')
      return
    }

    const cardResults = await Promise.all(
      tracks.map(async (track) => {
        const [sprintsResult, stateResult] = await Promise.all([
          gatewayApi.tracks.getSprints(track.id, accessToken)
            .then((sprints) => ({
              sprintCount: sprints.length,
              error: '',
            }))
            .catch((requestError: unknown) => ({
              sprintCount: null,
              error: getApiErrorMessage(requestError),
            })),
          gatewayApi.statistics.getTrackState(track.id, accessToken)
            .then((state) => ({
              completionPercent: state.completion_percent,
              error: '',
            }))
            .catch((requestError: unknown) => {
              const normalizedError = normalizeTrackStatsError(requestError)

              return {
                completionPercent: normalizedError ? null : 0,
                error: normalizedError,
              }
            }),
        ])

        return {
          trackId: track.id,
          stats: {
            sprintCount: sprintsResult.sprintCount,
            completionPercent: stateResult.completionPercent,
          },
          hasMissingMetrics:
            sprintsResult.error.length > 0 || stateResult.error.length > 0,
        }
      }),
    )

    setTrackStatsById(
      Object.fromEntries(
        cardResults.map(({ trackId, stats }) => [trackId, stats]),
      ),
    )
    setMetricsNotice(
      cardResults.some(({ hasMissingMetrics }) => hasMissingMetrics)
        ? 'Для части треков не удалось загрузить все метрики. Недоступные значения помечены тире.'
        : '',
    )
  }, [accessToken])

  const loadMyTracks = useCallback(async () => {
    if (!accessToken) {
      return
    }

    setRefreshing(true)
    setError('')
    setMetricsNotice('')
    setTrackStatsById({})

    try {
      const tracks = await gatewayApi.tracks.getMine(accessToken)
      setMyTracks(tracks)

      await loadTrackCards(tracks)
    } catch (requestError) {
      setError(getApiErrorMessage(requestError))
      setMyTracks([])
      setTrackStatsById({})
      setMetricsNotice('')
    } finally {
      setRefreshing(false)
    }
  }, [accessToken, loadTrackCards])

  useEffect(() => {
    if (!accessToken) {
      return
    }

    void loadMyTracks()
  }, [accessToken, loadMyTracks])

  const handleCreateTrack = useCallback(async (payload: CreateTrackRequestDTO) => {
    if (!accessToken) {
      return
    }

    setCreatingTrack(true)
    setCreateTrackError('')

    try {
      await gatewayApi.tracks.create(payload, accessToken)
      setCreateTrackOpen(false)
      await loadMyTracks()
    } catch (requestError) {
      setCreateTrackError(getApiErrorMessage(requestError))
    } finally {
      setCreatingTrack(false)
    }
  }, [accessToken, loadMyTracks])

  if (!accessToken) {
    return null
  }

  const trackCards: TrackOverviewCardItem[] = myTracks.map((track) => ({
    track,
    sprintCount: trackStatsById[track.id]?.sprintCount ?? null,
    completionPercent: trackStatsById[track.id]?.completionPercent ?? null,
  }))

  return (
    <SectionLayout
      eyebrow="Workspace"
      title="Быстрый вход в треки"
      description="На экране остался только список ваших треков с ключевыми метриками. Клик по карточке открывает страницу выбранного трека."
      icon={<KanbanSquare className="h-6 w-6" />}
      actions={
        <ActionButton
          type="button"
          variant="secondary"
          busy={refreshing}
          onClick={() => void loadMyTracks()}
        >
          <RefreshCw className="h-4 w-4" />
          Обновить
        </ActionButton>
      }
    >
      <section className="mx-auto max-w-5xl">
        <Panel
          eyebrow="Tracks"
          icon={<KanbanSquare className="h-5 w-5" />}
          title="Список треков"
          description="Каждая карточка показывает количество спринтов, количество задач и процент завершения трека."
          tone="cool"
        >
          <TrackOverviewGrid
            title="Мои треки"
            tracks={trackCards}
            onCreateTrack={() => {
              setCreateTrackError('')
              setCreateTrackOpen(true)
            }}
            onOpenTrack={(trackId) => navigate(workspaceTrackPath(trackId))}
          />

          <FriendlyNote
            title="Как теперь устроен workspace"
            text="Страница больше не раскрывает древо сущностей. Она показывает обзор по трекам, а детальная работа со спринтами и задачами открывается уже внутри выбранного трека."
          />

          {error ? (
            <div className="rounded-2xl border border-rose-200 bg-rose-50 px-4 py-3 text-sm text-rose-700">
              {error}
            </div>
          ) : null}

          {metricsNotice ? (
            <div className="rounded-2xl border border-amber-200 bg-amber-50 px-4 py-3 text-sm text-amber-800">
              {metricsNotice}
            </div>
          ) : null}
        </Panel>
      </section>

      {createTrackOpen ? (
        <CreateTrackDialog
          busy={creatingTrack}
          error={createTrackError}
          onClose={() => {
            if (creatingTrack) {
              return
            }

            setCreateTrackError('')
            setCreateTrackOpen(false)
          }}
          onSubmit={handleCreateTrack}
        />
      ) : null}
    </SectionLayout>
  )
}
