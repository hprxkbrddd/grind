import axios from 'axios'
import { axiosPrivate, axiosPublic } from '../http/axios'
import type {
  ChangeTaskDTO,
  ChangeTrackDTO,
  CreateTaskRequestDTO,
  CreateTrackRequestDTO,
  DateRangeDTO,
  DiagramDTO,
  PlanTaskDateDTO,
  PlanTaskSprintDTO,
  RegistrationDTO,
  SprintStatsDTO,
  SprintWithCountDTO,
  TaskDTO,
  TokenIntrospectionResponse,
  TokenRequestDTO,
  TokenResponseDTO,
  TrackActualStateStatsDTO,
  TrackDTO,
  TrackRawStatsDTO,
  TrackWithCountDTO,
} from '../types/gateway'

function authHeaders(accessToken: string) {
  return {
    headers: {
      Authorization: `Bearer ${accessToken}`,
    },
  }
}

async function getAuthorized<T>(path: string, accessToken: string) {
  const response = await axiosPrivate.get<T>(path, authHeaders(accessToken))
  return response.data
}

async function getAuthorizedWithBody<T, B>(
  path: string,
  body: B,
  accessToken: string,
) {
  const response = await axiosPrivate.request<T>({
    url: path,
    method: 'GET',
    ...authHeaders(accessToken),
    data: body,
  })

  return response.data
}

async function postAuthorized<T, B>(path: string, body: B, accessToken: string) {
  const response = await axiosPrivate.post<T>(path, body, authHeaders(accessToken))
  return response.data
}

async function putAuthorized<T, B>(path: string, body: B, accessToken: string) {
  const response = await axiosPrivate.put<T>(path, body, authHeaders(accessToken))
  return response.data
}

async function putAuthorizedWithoutBody<T>(path: string, accessToken: string) {
  const response = await axiosPrivate.put<T>(path, null, authHeaders(accessToken))
  return response.data
}

async function deleteAuthorized<T>(path: string, accessToken: string) {
  const response = await axiosPrivate.delete<T>(path, authHeaders(accessToken))
  return response.data
}

export function getApiErrorMessage(error: unknown) {
  if (axios.isAxiosError(error)) {
    const payload = error.response?.data

    if (typeof payload === 'string') {
      return payload
    }

    if (payload && typeof payload === 'object') {
      if ('message' in payload && typeof payload.message === 'string') {
        return payload.message
      }

      return JSON.stringify(payload, null, 2)
    }

    return error.message
  }

  if (error instanceof Error) {
    return error.message
  }

  return 'Unexpected error'
}

export const gatewayApi = {
  auth: {
    async login(payload: TokenRequestDTO) {
      const response = await axiosPublic.post<TokenResponseDTO>(
        '/grind/keycloak/token',
        payload,
      )

      return response.data
    },
    async register(payload: RegistrationDTO) {
      const response = await axiosPublic.post<string>(
        '/grind/keycloak/register',
        payload,
      )

      return response.data
    },
    async introspect(token: string) {
      const response = await axiosPublic.post<TokenIntrospectionResponse>(
        '/grind/keycloak/token/introspect',
        { token },
      )

      return response.data
    },
  },
  tracks: {
    getMine(accessToken: string) {
      return getAuthorized<TrackWithCountDTO[]>('/api/core/track', accessToken)
    },
    getAll(accessToken: string) {
      return getAuthorized<TrackWithCountDTO[]>('/api/core/track/all', accessToken)
    },
    getById(trackId: string, accessToken: string) {
      return getAuthorized<TrackWithCountDTO>(`/api/core/track/${trackId}`, accessToken)
    },
    getSprints(trackId: string, accessToken: string) {
      return getAuthorized<SprintWithCountDTO[]>(
        `/api/core/track/sprints/${trackId}`,
        accessToken,
      )
    },
    create(payload: CreateTrackRequestDTO, accessToken: string) {
      return postAuthorized<TrackDTO, CreateTrackRequestDTO>(
        '/api/core/track',
        payload,
        accessToken,
      )
    },
    update(trackId: string, payload: ChangeTrackDTO, accessToken: string) {
      return putAuthorized<TrackDTO, ChangeTrackDTO>(
        `/api/core/track/${trackId}`,
        payload,
        accessToken,
      )
    },
    remove(trackId: string, accessToken: string) {
      return deleteAuthorized<TrackDTO>(`/api/core/track/${trackId}`, accessToken)
    },
  },
  tasks: {
    getAll(accessToken: string) {
      return getAuthorized<TaskDTO[]>('/api/core/task/all', accessToken)
    },
    getById(taskId: string, accessToken: string) {
      return getAuthorized<TaskDTO>(`/api/core/task/${taskId}`, accessToken)
    },
    getBySprint(sprintId: string, accessToken: string) {
      return getAuthorized<TaskDTO[]>(
        `/api/core/task/sprint/${sprintId}`,
        accessToken,
      )
    },
    getByTrack(trackId: string, accessToken: string) {
      return getAuthorized<TaskDTO[]>(
        `/api/core/task/track/${trackId}`,
        accessToken,
      )
    },
    create(payload: CreateTaskRequestDTO, accessToken: string) {
      return postAuthorized<TaskDTO, CreateTaskRequestDTO>(
        '/api/core/task',
        payload,
        accessToken,
      )
    },
    update(taskId: string, payload: ChangeTaskDTO, accessToken: string) {
      return putAuthorized<TaskDTO, ChangeTaskDTO>(
        `/api/core/task/${taskId}`,
        payload,
        accessToken,
      )
    },
    planSprint(taskId: string, payload: PlanTaskSprintDTO, accessToken: string) {
      return putAuthorized<TaskDTO, PlanTaskSprintDTO>(
        `/api/core/task/${taskId}/plan/sprint`,
        payload,
        accessToken,
      )
    },
    planDate(taskId: string, payload: PlanTaskDateDTO, accessToken: string) {
      return putAuthorized<TaskDTO, PlanTaskDateDTO>(
        `/api/core/task/${taskId}/plan/date`,
        payload,
        accessToken,
      )
    },
    complete(taskId: string, accessToken: string) {
      return putAuthorizedWithoutBody<TaskDTO>(
        `/api/core/task/${taskId}/complete`,
        accessToken,
      )
    },
    backlog(taskId: string, accessToken: string) {
      return putAuthorizedWithoutBody<TaskDTO>(
        `/api/core/task/${taskId}/backlog`,
        accessToken,
      )
    },
    remove(taskId: string, accessToken: string) {
      return deleteAuthorized<TaskDTO>(`/api/core/task/${taskId}`, accessToken)
    },
  },
  statistics: {
    getTrackState(trackId: string, accessToken: string) {
      return getAuthorized<TrackActualStateStatsDTO>(
        `/api/statistics/track/${trackId}/state`,
        accessToken,
      )
    },
    getTrackRaw(trackId: string, accessToken: string) {
      return getAuthorized<TrackRawStatsDTO>(
        `/api/statistics/track/${trackId}/raw`,
        accessToken,
      )
    },
    getSprintStats(sprintId: string, accessToken: string) {
      return getAuthorized<SprintStatsDTO>(
        `/api/statistics/sprint/${sprintId}`,
        accessToken,
      )
    },
    getPerDay(trackId: string, accessToken: string) {
      return getAuthorized<DiagramDTO>(
        `/api/statistics/track/${trackId}/per-day`,
        accessToken,
      )
    },
    getPerWeek(trackId: string, accessToken: string) {
      return getAuthorized<DiagramDTO>(
        `/api/statistics/track/${trackId}/per-week`,
        accessToken,
      )
    },
    getPerDayInRange(trackId: string, range: DateRangeDTO, accessToken: string) {
      return getAuthorizedWithBody<DiagramDTO, DateRangeDTO>(
        `/api/statistics/track/${trackId}/per-day/range`,
        range,
        accessToken,
      )
    },
    getPerWeekInRange(trackId: string, range: DateRangeDTO, accessToken: string) {
      return getAuthorizedWithBody<DiagramDTO, DateRangeDTO>(
        `/api/statistics/track/${trackId}/per-week/range`,
        range,
        accessToken,
      )
    },
  },
}
