import type { SprintWithCountDTO, TaskDTO } from '../types/gateway'

function compareDateValues(left: string, right: string) {
  const leftTime = Date.parse(left)
  const rightTime = Date.parse(right)

  if (Number.isNaN(leftTime) || Number.isNaN(rightTime)) {
    return left.localeCompare(right)
  }

  return leftTime - rightTime
}

export function sortTasksByCreatedAt(tasks: TaskDTO[]) {
  return [...tasks].sort((left, right) => {
    const diff = compareDateValues(left.createdAt, right.createdAt)
    return diff !== 0 ? diff : left.id.localeCompare(right.id)
  })
}

export function sortSprintsByStartDate(sprints: SprintWithCountDTO[]) {
  return [...sprints].sort((left, right) => {
    const diff = compareDateValues(left.startDate, right.startDate)
    return diff !== 0 ? diff : left.id.localeCompare(right.id)
  })
}
