import { Link } from 'react-router'
import { Panel } from '../components/app/Panel'

export function Unauthorized() {
  return (
    <Panel
      title="Недостаточно прав"
      description="Текущий frontend не использует admin-only endpoint’ы. Вернитесь в dashboard или выполните вход под другим пользователем."
    >
      <div className="flex flex-wrap gap-3">
        <Link
          className="rounded-2xl bg-primary px-4 py-2 text-sm font-semibold text-white"
          to="/home"
        >
          На главную
        </Link>
        <Link
          className="rounded-2xl border border-primary/30 px-4 py-2 text-sm font-semibold text-primary-dark"
          to="/login"
        >
          Войти заново
        </Link>
      </div>
    </Panel>
  )
}
