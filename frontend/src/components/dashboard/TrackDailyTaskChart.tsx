import { Activity, CalendarRange, RefreshCw } from 'lucide-react'
import { useEffect, useState } from 'react'
import {
  Area,
  CartesianGrid,
  Legend,
  Line,
  LineChart,
  ResponsiveContainer,
  Tooltip,
  XAxis,
  YAxis,
} from 'recharts'
import type { DateRangeDTO, DiagramDTO, TaskDTO } from '../../types/gateway'
import {
  addUtcDays,
  formatIsoDate,
  parseIsoDate,
  startOfIsoWeek,
} from '../../utils/date'
import { sortTasksByCreatedAt } from '../../utils/sorting'
import { ActionButton } from '../app/ActionButton'
import { Field } from '../app/Field'
import { Panel } from '../app/Panel'

interface TrackDailyTaskChartProps {
  dayStats: DiagramDTO | null
  weekStats: DiagramDTO | null
  tasks: TaskDTO[]
  loading: boolean
  dayError?: string
  weekError?: string
  rangeStart?: string
  rangeEnd?: string
  onApplyRange: (range: DateRangeDTO) => void | Promise<void>
  onResetRange: () => void | Promise<void>
}

type ChartScale = 'day' | 'week'
type ChartPoint = {
  date: string
  dateLabel: string
  plannedTasks: number
  completedTasks: number
  plannedTitles: string[]
  completedTitles: string[]
}
type BucketTitles = {
  plannedTitles: string[]
  completedTitles: string[]
}

function formatShortDate(value: string) {
  const parsed = parseIsoDate(value)

  if (parsed === null) {
    return value
  }

  return new Intl.DateTimeFormat('ru-RU', {
    day: '2-digit',
    month: '2-digit',
  }).format(parsed)
}

function formatFullDate(value: string) {
  const parsed = parseIsoDate(value)

  if (parsed === null) {
    return value
  }

  return new Intl.DateTimeFormat('ru-RU', {
    day: '2-digit',
    month: 'long',
    year: 'numeric',
  }).format(parsed)
}

function toDateKey(value: string) {
  const parsed = parseIsoDate(value)
  return parsed ? formatIsoDate(parsed) : value
}

function toBucketKey(value: string, scale: ChartScale) {
  const parsed = parseIsoDate(value)

  if (parsed === null) {
    return null
  }

  const bucket = scale === 'day' ? parsed : startOfIsoWeek(parsed)
  return formatIsoDate(bucket)
}

function taskLabel(task: TaskDTO) {
  return task.title.trim() || task.id
}

function buildTitlesByBucket(tasks: TaskDTO[], scale: ChartScale) {
  const titlesByBucket = new Map<string, BucketTitles>()

  for (const task of sortTasksByCreatedAt(tasks)) {
    if (task.plannedDate) {
      const plannedKey = toBucketKey(task.plannedDate, scale)

      if (plannedKey) {
        const entry = titlesByBucket.get(plannedKey) ?? {
          plannedTitles: [],
          completedTitles: [],
        }

        entry.plannedTitles.push(taskLabel(task))
        titlesByBucket.set(plannedKey, entry)
      }
    }

    if (task.actualDate) {
      const completedKey = toBucketKey(task.actualDate, scale)

      if (completedKey) {
        const entry = titlesByBucket.get(completedKey) ?? {
          plannedTitles: [],
          completedTitles: [],
        }

        entry.completedTitles.push(taskLabel(task))
        titlesByBucket.set(completedKey, entry)
      }
    }
  }

  return titlesByBucket
}

interface ChartTooltipProps {
  active?: boolean
  payload?: Array<{
    payload?: ChartPoint
  }>
  label?: string | number
}

function ChartTooltip({ active, payload, label }: ChartTooltipProps) {
  if (!active || !payload || payload.length === 0) {
    return null
  }

  const point = payload[0]?.payload as ChartPoint | undefined

  if (!point) {
    return null
  }

  const rows: Array<{
    label: string
    value: number
    titles: string[]
    tone: string
  }> = [
    {
      label: 'Запланировано',
      value: point.plannedTasks,
      titles: point.plannedTitles,
      tone: 'bg-amber-500',
    },
    {
      label: 'Выполнено',
      value: point.completedTasks,
      titles: point.completedTitles,
      tone: 'bg-emerald-500',
    },
  ]

  return (
    <div className="w-80 rounded-2xl border border-primary/15 bg-white px-4 py-3 shadow-[0_18px_40px_rgba(31,54,61,0.14)]">
      <p className="text-sm font-semibold text-slate-900">{label}</p>
      <div className="mt-3 space-y-3">
        {rows.map((row) => (
          <div key={row.label} className="rounded-xl bg-slate-50 p-3">
            <div className="flex items-center justify-between gap-3">
              <div className="flex items-center gap-2">
                <span className={`h-2.5 w-2.5 rounded-full ${row.tone}`} />
                <span className="text-sm font-medium text-slate-700">
                  {row.label}
                </span>
              </div>
              <span className="text-sm font-semibold text-slate-900">
                {row.value}
              </span>
            </div>
            <div className="mt-2 space-y-1">
              {row.titles.length > 0 ? (
                row.titles.map((title, index) => (
                  <p
                    key={`${row.label}-${index}-${title}`}
                    className="text-xs text-slate-600"
                  >
                    {title}
                  </p>
                ))
              ) : (
                <p className="text-xs text-slate-400">Нет задач</p>
              )}
            </div>
          </div>
        ))}
      </div>
    </div>
  )
}

export function TrackDailyTaskChart({
  dayStats,
  weekStats,
  tasks,
  loading,
  dayError = '',
  weekError = '',
  rangeStart = '',
  rangeEnd = '',
  onApplyRange,
  onResetRange,
}: TrackDailyTaskChartProps) {
  const [scale, setScale] = useState<ChartScale>('day')
  const [draftRangeStart, setDraftRangeStart] = useState(rangeStart)
  const [draftRangeEnd, setDraftRangeEnd] = useState(rangeEnd)
  const [rangeInputError, setRangeInputError] = useState('')

  useEffect(() => {
    setDraftRangeStart(rangeStart)
    setDraftRangeEnd(rangeEnd)
    setRangeInputError('')
  }, [rangeEnd, rangeStart])

  const isDaily = scale === 'day'
  const stats = isDaily ? dayStats : weekStats
  const error = isDaily ? dayError : weekError
  const titlesByBucket = buildTitlesByBucket(tasks, scale)
  const normalizedStats = (stats?.diagram ?? [])
    .slice()
    .sort((left, right) => {
      const leftTime = parseIsoDate(left.day)?.getTime() ?? Number.MAX_SAFE_INTEGER
      const rightTime = parseIsoDate(right.day)?.getTime() ?? Number.MAX_SAFE_INTEGER
      return leftTime - rightTime
    })

  const statsByDate = new Map(
    normalizedStats.map((item) => [
      toDateKey(item.day),
      {
        plannedTasks: item.planned_tasks,
        completedTasks: item.completed_tasks,
      },
    ]),
  )

  const rangeStartDate = parseIsoDate(rangeStart)
  const rangeEndDate = parseIsoDate(rangeEnd)
  const hasExplicitRange =
    rangeStartDate !== null &&
    rangeEndDate !== null &&
    rangeStartDate.getTime() <= rangeEndDate.getTime()
  const hasPendingRangeChange =
    draftRangeStart !== rangeStart || draftRangeEnd !== rangeEnd

  const chartData: ChartPoint[] = []

  if (hasExplicitRange) {
    const start = isDaily ? rangeStartDate : startOfIsoWeek(rangeStartDate)
    const end = isDaily ? rangeEndDate : startOfIsoWeek(rangeEndDate)
    const step = isDaily ? 1 : 7

    for (
      let current = start;
      current.getTime() <= end.getTime();
      current = addUtcDays(current, step)
    ) {
      const key = formatIsoDate(current)
      const metric = statsByDate.get(key)
      const titles = titlesByBucket.get(key)

      chartData.push({
        date: key,
        dateLabel: formatShortDate(key),
        plannedTasks: metric?.plannedTasks ?? 0,
        completedTasks: metric?.completedTasks ?? 0,
        plannedTitles: titles?.plannedTitles ?? [],
        completedTitles: titles?.completedTitles ?? [],
      })
    }
  } else {
    chartData.push(
      ...normalizedStats.map((item) => ({
        date: toDateKey(item.day),
        dateLabel: formatShortDate(item.day),
        plannedTasks: item.planned_tasks,
        completedTasks: item.completed_tasks,
        plannedTitles: titlesByBucket.get(toDateKey(item.day))?.plannedTitles ?? [],
        completedTitles: titlesByBucket.get(toDateKey(item.day))?.completedTitles ?? [],
      })),
    )
  }

  function handleApplyRange() {
    if (!draftRangeStart || !draftRangeEnd) {
      setRangeInputError('Укажите обе границы диапазона.')
      return
    }

    if (draftRangeStart > draftRangeEnd) {
      setRangeInputError('Правая граница не может быть раньше левой.')
      return
    }

    setRangeInputError('')
    void onApplyRange({
      startDate: draftRangeStart,
      endDate: draftRangeEnd,
    })
  }

  return (
    <Panel
      eyebrow="Track Stats"
      icon={<Activity className="h-5 w-5" />}
      title={isDaily ? 'Динамика задач по дням' : 'Динамика задач по неделям'}
      description={
        isDaily
          ? 'На одном графике показано, сколько задач было запланировано и выполнено по датам этого трека.'
          : 'На одном графике показано, сколько задач было запланировано и выполнено по неделям этого трека.'
      }
      tone="cool"
    >
      <div className="flex flex-wrap gap-2">
        <button
          className={`rounded-full border px-3 py-1.5 text-xs font-semibold transition ${
            isDaily
              ? 'border-primary bg-primary text-white'
              : 'border-primary/15 bg-white text-slate-600 hover:border-primary/35 hover:text-slate-900'
          }`}
          onClick={() => setScale('day')}
          type="button"
        >
          По дням
        </button>
        <button
          className={`rounded-full border px-3 py-1.5 text-xs font-semibold transition ${
            !isDaily
              ? 'border-primary bg-primary text-white'
              : 'border-primary/15 bg-white text-slate-600 hover:border-primary/35 hover:text-slate-900'
          }`}
          onClick={() => setScale('week')}
          type="button"
        >
          По неделям
        </button>
      </div>

      <div className="grid gap-3 rounded-[24px] border border-primary/12 bg-white/80 p-4">
        <div className="flex items-center gap-2 text-xs font-semibold uppercase tracking-[0.2em] text-primary">
          <CalendarRange className="h-4 w-4" />
          Видимый диапазон графика
        </div>
        <div className="grid gap-3 md:grid-cols-2 xl:grid-cols-[1fr_1fr_auto_auto] xl:items-end">
          <Field
            as="input"
            disabled={loading}
            label="От"
            onChange={(event) => setDraftRangeStart(event.target.value)}
            type="date"
            value={draftRangeStart}
          />
          <Field
            as="input"
            disabled={loading}
            label="До"
            onChange={(event) => setDraftRangeEnd(event.target.value)}
            type="date"
            value={draftRangeEnd}
          />
          <ActionButton
            className="w-full xl:w-auto"
            disabled={loading || !hasPendingRangeChange}
            onClick={handleApplyRange}
            type="button"
            variant="primary"
          >
            Применить
          </ActionButton>
          <ActionButton
            className="w-full xl:w-auto"
            disabled={loading}
            onClick={() => {
              setRangeInputError('')
              void onResetRange()
            }}
            type="button"
            variant="secondary"
          >
            <RefreshCw className="h-4 w-4" />
            Сбросить
          </ActionButton>
        </div>
        <p className="text-xs text-slate-500">
          {hasExplicitRange
            ? `Сейчас на графике показан интервал ${formatFullDate(rangeStart)} - ${formatFullDate(rangeEnd)}.`
            : 'Диапазон будет рассчитан после загрузки данных трека.'}
        </p>
        {rangeInputError ? (
          <div className="rounded-2xl border border-rose-200 bg-rose-50 px-4 py-3 text-sm text-rose-700">
            {rangeInputError}
          </div>
        ) : null}
      </div>

      {loading ? (
        <div className="rounded-2xl border border-primary/15 bg-white/80 px-4 py-3 text-sm text-slate-600">
          {isDaily
            ? 'Загружаю статистику по дням...'
            : 'Загружаю статистику по неделям...'}
        </div>
      ) : null}

      {error ? (
        <div className="rounded-2xl border border-rose-200 bg-rose-50 px-4 py-3 text-sm text-rose-700">
          Не удалось загрузить график: {error}
        </div>
      ) : null}

      {!loading && !error && chartData.length === 0 ? (
        <div className="rounded-2xl border border-dashed border-primary/20 bg-slate-50 px-4 py-5 text-sm text-slate-500">
          {isDaily
            ? 'Для этого трека пока нет данных по дням.'
            : 'Для этого трека пока нет данных по неделям.'}
        </div>
      ) : null}

      {!error && chartData.length > 0 ? (
        <div className="h-80 w-full rounded-[24px] border border-primary/10 bg-white/70 p-3 sm:p-4">
          <ResponsiveContainer width="100%" height="100%">
            <LineChart
              data={chartData}
              margin={{ top: 8, right: 16, left: 0, bottom: 8 }}
            >
              <CartesianGrid stroke="rgba(64,121,140,0.12)" strokeDasharray="4 4" />
              <Area
                type="monotone"
                dataKey="plannedTasks"
                name="plannedTasks"
                fill="rgba(211,160,39,0.14)"
                stroke="none"
                legendType="none"
              />
              <Area
                type="monotone"
                dataKey="completedTasks"
                name="completedTasks"
                fill="rgba(29,143,77,0.12)"
                stroke="none"
                legendType="none"
              />
              <XAxis
                dataKey="dateLabel"
                minTickGap={24}
                stroke="#60707a"
                tickLine={false}
                axisLine={false}
              />
              <YAxis
                allowDecimals={false}
                stroke="#60707a"
                tickLine={false}
                axisLine={false}
              />
              <Tooltip
                content={<ChartTooltip />}
                labelFormatter={(label, payload) => {
                  if (payload.length === 0) {
                    return label
                  }

                  const source = payload[0]?.payload
                  return source?.date ? formatFullDate(source.date) : label
                }}
                formatter={(value, name) => [
                  value,
                  name === 'plannedTasks' ? 'Запланировано' : 'Выполнено',
                ]}
                contentStyle={{
                  borderRadius: '16px',
                  borderColor: 'rgba(64,121,140,0.16)',
                  boxShadow: '0 18px 40px rgba(31,54,61,0.10)',
                }}
              />
              <Legend
                formatter={(value) =>
                  value === 'plannedTasks' ? 'Запланировано' : 'Выполнено'
                }
              />
              <Line
                type="monotone"
                dataKey="completedTasks"
                name="completedTasks"
                stroke="#1d8f4d"
                strokeOpacity={0.9}
                strokeWidth={3}
                strokeLinecap="round"
                strokeLinejoin="round"
                dot={{ r: 3, fill: '#fff', stroke: '#1d8f4d', strokeWidth: 2 }}
                activeDot={{ r: 5, fill: '#fff', stroke: '#1d8f4d', strokeWidth: 2 }}
              />
              <Line
                type="monotone"
                dataKey="plannedTasks"
                name="plannedTasks"
                stroke="#c9971f"
                strokeOpacity={0.7}
                strokeWidth={2}
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeDasharray="6 4"
                dot={{ r: 3, fill: '#fff', stroke: '#c9971f', strokeWidth: 2 }}
                activeDot={{ r: 5, fill: '#fff', stroke: '#c9971f', strokeWidth: 2 }}
              />
            </LineChart>
          </ResponsiveContainer>
        </div>
      ) : null}
    </Panel>
  )
}
