import {
  createContext,
  useEffect,
  useMemo,
  useState,
  type Dispatch,
  type ReactNode,
  type SetStateAction,
} from 'react'
import { setUnauthorizedHandler } from '../http/axios'
import type { AuthSession } from '../types/gateway'

interface AuthProviderProps {
  children: ReactNode
}

interface AuthContextType {
  auth: AuthSession | null
  setAuth: Dispatch<SetStateAction<AuthSession | null>>
  logout: () => void
}

const STORAGE_KEY = 'grind.auth'

function readStoredAuth() {
  const stored = localStorage.getItem(STORAGE_KEY)

  if (!stored) {
    return null
  }

  try {
    return JSON.parse(stored) as AuthSession
  } catch {
    localStorage.removeItem(STORAGE_KEY)
    return null
  }
}

export const AuthContext = createContext<AuthContextType | null>(null)

export function AuthProvider({ children }: AuthProviderProps) {
  const [auth, setAuth] = useState<AuthSession | null>(() => readStoredAuth())

  useEffect(() => {
    setUnauthorizedHandler(() => {
      setAuth(null)
    })

    return () => {
      setUnauthorizedHandler(null)
    }
  }, [])

  useEffect(() => {
    if (auth) {
      localStorage.setItem(STORAGE_KEY, JSON.stringify(auth))
      return
    }

    localStorage.removeItem(STORAGE_KEY)
  }, [auth])

  const value = useMemo(
    () => ({
      auth,
      setAuth,
      logout: () => setAuth(null),
    }),
    [auth],
  )

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}
