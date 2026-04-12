import axios, { type AxiosError, type AxiosInstance } from 'axios'

const API_URL = import.meta.env.VITE_API_URL ?? 'http://localhost:8080'

const baseConfig = {
  baseURL: API_URL,
  headers: {
    'Content-Type': 'application/json',
  },
}

export const axiosPublic = axios.create(baseConfig)

export const axiosPrivate = axios.create(baseConfig)

let unauthorizedHandler: (() => void) | null = null
const configuredInstances = new WeakSet<AxiosInstance>()

function attachUnauthorizedInterceptor(instance: AxiosInstance) {
  if (configuredInstances.has(instance)) {
    return
  }

  instance.interceptors.response.use(
    (response) => response,
    (error: AxiosError) => {
      if (error.response?.status === 401) {
        unauthorizedHandler?.()
      }

      return Promise.reject(error)
    },
  )

  configuredInstances.add(instance)
}

export function setUnauthorizedHandler(handler: (() => void) | null) {
  unauthorizedHandler = handler
}

attachUnauthorizedInterceptor(axiosPublic)
attachUnauthorizedInterceptor(axiosPrivate)
