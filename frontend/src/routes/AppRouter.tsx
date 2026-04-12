import { Route, Routes } from 'react-router'
import { Login } from '../pages/Login'
import { Register } from '../pages/Register'
import { NotFound } from '../pages/NotFound'
import { Home } from '../pages/Home'
import { Layout } from '../layouts/Layout'
import { Welcome } from '../pages/Welcome'
import { Profile } from '../pages/Profile'
import { Unauthorized } from '../pages/Unauthorized'
import { RequireAuth } from './RequireAuth'
import { SprintPage } from '../pages/SprintPage'
import { Workspace } from '../pages/Workspace'
import { Statistics } from '../pages/Statistics'
import { TaskPage } from '../pages/TaskPage'
import { TrackPage } from '../pages/TrackPage'

export function AppRouter() {
  return (
    <Routes>
      <Route element={<Layout />}>
        <Route index element={<Welcome />} />
        <Route path="login" element={<Login />} />
        <Route path="register" element={<Register />} />
        <Route path="unauthorized" element={<Unauthorized />} />
        <Route element={<RequireAuth />}>
          <Route path="home" element={<Home />} />
          <Route path="home/workspace" element={<Workspace />} />
          <Route path="home/workspace/tracks/:trackId" element={<TrackPage />} />
          <Route
            path="home/workspace/tracks/:trackId/sprints/:sprintId"
            element={<SprintPage />}
          />
          <Route path="home/workspace/tasks/:taskId" element={<TaskPage />} />
          <Route path="home/statistics" element={<Statistics />} />
          <Route path="profile" element={<Profile />} />
        </Route>
        <Route path="*" element={<NotFound />} />
      </Route>
    </Routes>
  )
}
