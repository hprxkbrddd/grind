export function parseIsoDate(value: string) {
  const match = /^(\d{4})-(\d{2})-(\d{2})$/.exec(value)

  if (!match) {
    return null
  }

  const [, year, month, day] = match
  const parsed = new Date(Date.UTC(
    Number(year),
    Number(month) - 1,
    Number(day),
  ))

  return Number.isNaN(parsed.getTime()) ? null : parsed
}

export function formatIsoDate(value: Date) {
  return value.toISOString().slice(0, 10)
}

export function addUtcDays(value: Date, days: number) {
  return new Date(Date.UTC(
    value.getUTCFullYear(),
    value.getUTCMonth(),
    value.getUTCDate() + days,
  ))
}

export function startOfIsoWeek(value: Date) {
  const day = value.getUTCDay()
  const offset = day === 0 ? -6 : 1 - day

  return addUtcDays(value, offset)
}

export function todayAsUtcDate() {
  const now = new Date()

  return new Date(Date.UTC(
    now.getFullYear(),
    now.getMonth(),
    now.getDate(),
  ))
}
