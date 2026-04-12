interface JsonViewProps {
  data: unknown
  emptyText?: string
}

export function JsonView({
  data,
  emptyText = 'Данные пока не загружены',
}: JsonViewProps) {
  if (data == null) {
    return (
      <div className="rounded-2xl border border-dashed border-primary/15 bg-white/75 px-4 py-5 text-sm leading-6 text-slate-500">
        {emptyText}
      </div>
    )
  }

  return (
    <div className="overflow-hidden rounded-2xl border border-slate-900/10 bg-slate-950 shadow-[0_16px_40px_rgba(15,23,42,0.22)]">
      <div className="flex items-center gap-2 border-b border-white/10 px-4 py-3 text-xs uppercase tracking-[0.24em] text-slate-400">
        <span className="h-2.5 w-2.5 rounded-full bg-emerald-400" />
        Raw API response
      </div>
      <pre className="max-h-96 overflow-auto px-4 py-4 text-xs leading-6 text-slate-100">
        {JSON.stringify(data, null, 2)}
      </pre>
    </div>
  )
}
