export function workspaceTrackPath(trackId: string) {
  return `/home/workspace/tracks/${trackId}`
}

export function workspaceSprintPath(trackId: string, sprintId: string) {
  return `/home/workspace/tracks/${trackId}/sprints/${sprintId}`
}

export function workspaceTaskPath(taskId: string) {
  return `/home/workspace/tasks/${taskId}`
}
