import type {
  InputHTMLAttributes,
  ReactNode,
  SelectHTMLAttributes,
  TextareaHTMLAttributes,
} from 'react'

type Option = {
  label: string
  value: string
}

type FieldProps =
  | ({
      label: ReactNode
      hint?: ReactNode
      as?: 'input'
      options?: never
    } & InputHTMLAttributes<HTMLInputElement>)
  | ({
      label: ReactNode
      hint?: ReactNode
      as: 'textarea'
      options?: never
    } & TextareaHTMLAttributes<HTMLTextAreaElement>)
  | ({
      label: ReactNode
      hint?: ReactNode
      as: 'select'
      options: Option[]
    } & SelectHTMLAttributes<HTMLSelectElement>)

export function Field(props: FieldProps) {
  const sharedClassName =
    'mt-2 w-full rounded-2xl border border-primary/12 bg-white/95 px-3.5 py-2.5 text-sm text-slate-900 shadow-[inset_0_1px_0_rgba(255,255,255,0.7)] outline-none transition placeholder:text-slate-400 focus:border-primary/45 focus:ring-4 focus:ring-primary/12'
  if (props.as === 'textarea') {
    const { label, hint, as: _as, options: _options, ...elementProps } = props
    void _as
    void _options

    return (
      <label className="block text-sm font-medium text-slate-700">
        <span>{label}</span>
        {hint ? <span className="mt-1 block text-xs text-slate-500">{hint}</span> : null}
        <textarea className={sharedClassName} {...elementProps} />
      </label>
    )
  }

  if (props.as === 'select') {
    const { label, hint, as: _as, options, ...elementProps } = props
    void _as

    return (
      <label className="block text-sm font-medium text-slate-700">
        <span>{label}</span>
        {hint ? <span className="mt-1 block text-xs text-slate-500">{hint}</span> : null}
        <select className={sharedClassName} {...elementProps}>
          {options.map((option) => (
            <option key={option.value} value={option.value}>
              {option.label}
            </option>
          ))}
        </select>
      </label>
    )
  }

  const { label, hint, as: _as, options: _options, ...elementProps } = props
  void _as
  void _options

  return (
    <label className="block text-sm font-medium text-slate-700">
      <span>{label}</span>
      {hint ? <span className="mt-1 block text-xs text-slate-500">{hint}</span> : null}
      <input className={sharedClassName} {...elementProps} />
    </label>
  )
}
