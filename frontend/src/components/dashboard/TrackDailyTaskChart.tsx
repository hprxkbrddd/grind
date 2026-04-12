import { Activity } from 'lucide-react'
import { useState } from 'react'
import {
  CartesianGrid,
  Legend,
  Line,
  LineChart,
  ResponsiveContainer,
  Tooltip,
  XAxis,
  YAxis,
} from 'recharts'
import type { DiagramDTO } from '../../types/gateway'
import { Panel } from '../app/Panel'

interface TrackDailyTaskChartProps {
  dayStats: DiagramDTO | null
  weekStats: DiagramDTO | null
  loading: boolean
  dayError?: string
  weekError?: string
  rangeStart?: string
  rangeEnd?: string
}

type ChartScale = 'day' | 'week'

function parseDate(value: string) {
  const parsed = new Date(value)
  return Number.isNaN(parsed.getTime())
    ? null
    : new Date(Date.UTC(
      parsed.getUTCFullYear(),
      parsed.getUTCMonth(),
      parsed.getUTCDate(),
    ))
}

function formatShortDate(value: string) {
  const parsed = parseDate(value)

  if (parsed === null) {
    return value
  }

  return new Intl.DateTimeFormat('ru-RU', {
    day: '2-digit',
    month: '2-digit',
  }).format(parsed)
}

function formatFullDate(value: string) {
  const parsed = parseDate(value)

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
  const parsed = parseDate(value)
  return parsed ? parsed.toISOString().slice(0, 10) : value
}

function addDays(date: Date, days: number) {
  return new Date(Date.UTC(
    date.getUTCFullYear(),
    date.getUTCMonth(),
    date.getUTCDate() + days,
  ))
}

function startOfWeek(date: Date) {
  const day = date.getUTCDay()
  return addDays(date, -day)
}

export function TrackDailyTaskChart({
  dayStats,
  weekStats,
  loading,
  dayError = '',
  weekError = '',
  rangeStart = '',
  rangeEnd = '',
}: TrackDailyTaskChartProps) {
  const [scale, setScale] = useState<ChartScale>('day')

  const isDaily = scale === 'day'
  const stats = isDaily ? dayStats : weekStats
  const error = isDaily ? dayError : weekError
  const normalizedStats = (stats?.diagram ?? [])
    .slice()
    .sort((left, right) => {
      const leftTime = parseDate(left.day)?.getTime() ?? Number.MAX_SAFE_INTEGER
      const rightTime = parseDate(right.day)?.getTime() ?? Number.MAX_SAFE_INTEGER
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

  const rangeStartDate = parseDate(rangeStart)
  const rangeEndDate = parseDate(rangeEnd)
  const hasExplicitRange =
    rangeStartDate !== null &&
    rangeEndDate !== null &&
    rangeStartDate.getTime() <= rangeEndDate.getTime()

  const chartData = []

  if (hasExplicitRange) {
    const start = isDaily ? rangeStartDate : startOfWeek(rangeStartDate)
    const end = isDaily ? rangeEndDate : startOfWeek(rangeEndDate)
    const step = isDaily ? 1 : 7

    for (
      let current = start;
      current.getTime() <= end.getTime();
      current = addDays(current, step)
    ) {
      const key = current.toISOString().slice(0, 10)
      const metric = statsByDate.get(key)

      chartData.push({
        date: key,
        dateLabel: formatShortDate(key),
        plannedTasks: metric?.plannedTasks ?? 0,
        completedTasks: metric?.completedTasks ?? 0,
      })
    }
  } else {
    chartData.push(
      ...normalizedStats.map((item) => ({
        date: toDateKey(item.day),
        dateLabel: formatShortDate(item.day),
        plannedTasks: item.planned_tasks,
        completedTasks: item.completed_tasks,
      })),
    )
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
                dataKey="plannedTasks"
                name="plannedTasks"
                stroke="#d3a027"
                strokeWidth={3}
                dot={{ r: 3 }}
                activeDot={{ r: 5 }}
              />
              <Line
                type="monotone"
                dataKey="completedTasks"
                name="completedTasks"
                stroke="#1d8f4d"
                strokeWidth={3}
                dot={{ r: 3 }}
                activeDot={{ r: 5 }}
              />
            </LineChart>
          </ResponsiveContainer>
        </div>
      ) : null}
    </Panel>
  )
}
