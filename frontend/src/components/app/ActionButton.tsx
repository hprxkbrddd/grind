import type { ButtonHTMLAttributes, ReactNode } from 'react'

interface ActionButtonProps extends ButtonHTMLAttributes<HTMLButtonElement> {
  busy?: boolean
  children: ReactNode
  variant?: 'primary' | 'secondary' | 'ghost'
}

export function ActionButton({
  busy = false,
  children,
  variant = 'primary',
  className = '',
  ...props
}: ActionButtonProps) {
  const variantClassName =
    variant === 'secondary'
      ? 'border-primary/15 bg-white/90 text-primary-dark shadow-[0_10px_24px_rgba(31,54,61,0.06)] hover:-translate-y-0.5 hover:bg-white'
      : variant === 'ghost'
        ? 'border-primary/10 bg-sand text-slate-700 hover:-translate-y-0.5 hover:bg-warm'
        : 'border-primary/30 bg-[linear-gradient(135deg,_#40798c,_#2e6f73)] text-white shadow-[0_14px_30px_rgba(64,121,140,0.28)] hover:-translate-y-0.5 hover:shadow-[0_18px_34px_rgba(64,121,140,0.34)]'

  return (
    <button
      className={`inline-flex items-center justify-center rounded-2xl border px-4 py-2.5 text-sm font-semibold transition duration-200 disabled:cursor-not-allowed disabled:opacity-60 ${variantClassName} ${className}`}
      disabled={busy || props.disabled}
      {...props}
    >
      {busy ? 'Выполняется...' : children}
    </button>
  )
}
