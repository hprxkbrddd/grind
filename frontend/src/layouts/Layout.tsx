import { Compass, LayoutDashboard, LogIn, UserRoundPlus } from 'lucide-react'
import { Link, NavLink, Outlet, useNavigate } from 'react-router'
import { useAuth } from '../hooks/useAuth'
import { ActionButton } from '../components/app/ActionButton'

const navLinkClass = ({ isActive }: { isActive: boolean }) =>
  `inline-flex items-center gap-2 rounded-full px-4 py-2 text-sm font-medium transition ${
    isActive
      ? 'bg-white text-primary-dark shadow-[0_8px_22px_rgba(31,54,61,0.08)]'
      : 'text-slate-600 hover:bg-white/70 hover:text-slate-900'
  }`

export function Layout() {
  const { auth, logout } = useAuth()
  const navigate = useNavigate()

  function handleLogout() {
    logout()
    navigate('/')
  }

  return (
    <div className="min-h-screen">
      <header className="sticky top-0 z-20 border-b border-white/60 bg-white/75 backdrop-blur">
        <div className="mx-auto flex max-w-7xl items-center justify-between gap-4 px-4 py-4 sm:px-6 lg:px-8">
          <Link to="/" className="flex items-center gap-3">
            <div className="flex h-11 w-11 items-center justify-center rounded-2xl bg-[linear-gradient(135deg,_#1f363d,_#40798c)] text-lg font-bold text-white shadow-[0_10px_24px_rgba(31,54,61,0.2)]">
              G
            </div>
            <div>
              <p className="text-sm uppercase tracking-[0.3em] text-primary">
                Grind
              </p>
              <p className="text-xs text-slate-500">
                дружелюбный трекер поверх `gateway`
              </p>
            </div>
          </Link>

          <nav className="flex items-center gap-2 rounded-full border border-white/70 bg-slate-100/80 p-1.5 shadow-[0_10px_24px_rgba(31,54,61,0.05)]">
            <NavLink to="/" className={navLinkClass} end>
              <Compass className="h-4 w-4" />
              Welcome
            </NavLink>
            {auth?.accessToken ? (
              <>
                <NavLink to="/home" className={navLinkClass}>
                  <LayoutDashboard className="h-4 w-4" />
                  Dashboard
                </NavLink>
                <NavLink to="/profile" className={navLinkClass}>
                  <Compass className="h-4 w-4" />
                  Profile
                </NavLink>
              </>
            ) : (
              <>
                <NavLink to="/login" className={navLinkClass}>
                  <LogIn className="h-4 w-4" />
                  Login
                </NavLink>
                <NavLink to="/register" className={navLinkClass}>
                  <UserRoundPlus className="h-4 w-4" />
                  Register
                </NavLink>
              </>
            )}
          </nav>

          {auth?.accessToken ? (
            <div className="flex items-center gap-3">
              <div className="hidden text-right sm:block">
                <p className="text-sm font-semibold text-slate-900">Привет, {auth.username}</p>
                <p className="text-xs text-slate-500">
                  {auth.roles.length > 0 ? auth.roles.join(', ') : 'user'}
                </p>
              </div>
              <ActionButton type="button" onClick={handleLogout} variant="secondary">
                Logout
              </ActionButton>
            </div>
          ) : null}
        </div>
      </header>

      <main className="mx-auto max-w-7xl px-4 py-8 sm:px-6 lg:px-8">
        <Outlet />
      </main>
    </div>
  )
}
