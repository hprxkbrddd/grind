import { jwtDecode, type JwtPayload } from 'jwt-decode'
import type { AuthSession, TokenResponseDTO } from '../types/gateway'

interface KeycloakJwtPayload extends JwtPayload {
  preferred_username?: string
  resource_access?: Record<string, { roles?: string[] }>
}

export function buildAuthSession(
  tokenResponse: TokenResponseDTO,
  fallbackUsername: string,
): AuthSession {
  const payload = jwtDecode<KeycloakJwtPayload>(tokenResponse.access_token)
  const roles = Array.from(
    new Set(
      Object.values(payload.resource_access ?? {}).flatMap(
        (resource) => resource.roles ?? [],
      ),
    ),
  )

  return {
    username: payload.preferred_username ?? fallbackUsername,
    accessToken: tokenResponse.access_token,
    refreshToken: tokenResponse.refresh_token,
    expiresAt: payload.exp ? payload.exp * 1000 : null,
    roles,
  }
}
