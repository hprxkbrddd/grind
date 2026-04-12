import { Navigate, Outlet, useLocation } from 'react-router'
import { useAuth } from '../hooks/useAuth'

export function RequireAuth() {
  const { auth } = useAuth()
  const location = useLocation()

  if (auth?.accessToken) {
    return <Outlet />
  }

  return <Navigate to="/login" state={{ from: location.pathname }} replace />
}
