import { Route, Routes } from 'react-router'
import { Login } from '../pages/Login'
import { Register } from '../pages/Register'
import { NotFound } from '../pages/NotFound'
import { Home } from '../pages/Home'
import { Layout } from '../layouts/Layout'
import { Welcome } from '../pages/Welcome'
import { RequireAuth } from './RequireAuth'
import { Profile } from '../pages/Profile'
import { Unauthorized } from '../pages/Unauthorized'

export const AppRouter = () => {
  return (
    <Routes>
      <Route path="/" element={<Layout />}>
        <Route index element={<Welcome />} />
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        <Route path="/unauthorized" element={<Unauthorized />} />

        <Route element={<RequireAuth allowedRoles={['1']} />}>
          <Route path="/home" element={<Home />} />
        </Route>

        <Route element={<RequireAuth allowedRoles={['1', '2']} />}>
          <Route path="/profile" element={<Profile />} />
        </Route>

        <Route path="*" element={<NotFound />} />
      </Route>
    </Routes>
  )
}