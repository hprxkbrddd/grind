import { Link } from 'react-router'
import { Panel } from '../components/app/Panel'

export function NotFound() {
  return (
    <Panel
      title="Страница не найдена"
      description="Путь не зарегистрирован во frontend router."
    >
      <Link
        className="rounded-2xl bg-primary px-4 py-2 text-sm font-semibold text-white"
        to="/"
      >
        Вернуться на welcome
      </Link>
    </Panel>
  )
}
