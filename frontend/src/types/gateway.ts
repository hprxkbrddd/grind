export type TrackStatus = 'ACTIVE' | 'COMPLETED' | 'ARCHIVED'

export type TaskStatus = 'CREATED' | 'PLANNED' | 'COMPLETED' | 'OVERDUE' | 'DELETED'

export interface TokenRequestDTO {
  username: string
  password: string
}

export interface TokenResponseDTO {
  access_token: string
  expires_in: number
  refresh_expires_in: number
  refresh_token: string
  token_type: string
  id_token: string
  'not-before-policy': number
  session_state: string
  scope: string
}

export interface RegistrationDTO {
  username: string
  password: string
  email: string
  firstName: string
  lastName: string
  isEnabled: boolean
}

export interface TokenIntrospectionRequestDTO {
  token: string
}

export interface TokenIntrospectionResponse {
  active: boolean
  sub: string
  username: string
  email: string
  exp: number
  iat: number
  scope: string
  token_type: string
  client_id: string
}

export interface CreateTrackRequestDTO {
  name: string
  description: string
  petId: string
  sprintLength: number
  startDate: string
  targetDate: string
  messagePolicy: string
  status: TrackStatus
}

export interface ChangeTrackDTO extends CreateTrackRequestDTO {}

export interface TrackDTO {
  id: string
  name: string
  description: string
  petId: string
  durationDays: number
  startDate: string
  targetDate: string
  createdAt: string
  messagePolicy: string
  status: TrackStatus
  userId: string
}

export interface TrackWithCountDTO extends TrackDTO {
  tasks: number
}

export interface SprintWithCountDTO {
  id: string
  startDate: string
  endDate: string
  track_id: string
  tasks: number
}

export interface CreateTaskRequestDTO {
  title: string
  description: string
  trackId: string
}

export interface ChangeTaskDTO {
  title: string
  description: string
}

export interface PlanTaskSprintDTO {
  sprintId: string
  dayOfSprint: number
}

export interface PlanTaskDateDTO {
  plannedDate: string
}

export interface TaskDTO {
  id: string
  title: string
  sprint_id: string
  track_id: string
  plannedDate: string | null
  actualDate: string | null
  description: string
  status: TaskStatus
  createdAt: string
  version: number
}

export interface TrackActualStateStatsDTO {
  track_id: string
  total_tasks: number
  completed_tasks: number
  remaining_tasks: number
  overdue_tasks: number
  active_wip: number
  completion_percent: number
  overdue_percent: number
  overdue_among_active_percent: number
  avg_active_age_days: number
}

export interface TrackRawStatsDTO {
  track_id: string
  completed_last_30d: number
  completed_last_7d: number
}

export interface SprintStatsDTO {
  sprint_id: string
  total_tasks: number
  completed_tasks: number
  remaining_tasks: number
  overdue_tasks: number
  active_wip: number
  completion_percent: number
  overdue_percent: number
  overdue_among_active_percent: number
  avg_active_age_days: number
}

export interface DiagramUnitDTO {
  day: string
  completed_tasks: number
  planned_tasks: number
}

export interface DiagramDTO {
  diagram: DiagramUnitDTO[]
}

export interface DateRangeDTO {
  startDate: string
  endDate: string
}

export interface AuthSession {
  username: string
  accessToken: string
  refreshToken: string
  expiresAt: number | null
  roles: string[]
}
