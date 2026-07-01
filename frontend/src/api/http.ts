import axios, { AxiosRequestConfig } from 'axios'
import { ElMessage } from 'element-plus'
import { getAdminToken } from '@/utils/auth'

export interface ApiResult<T> {
  code: number
  msg: string
  data: T
}

export interface PageResult<T> {
  total: number
  records: T[]
}

const service = axios.create({
  timeout: 20000
})

service.interceptors.request.use((config) => {
  const token = getAdminToken()
  if (token) {
    config.headers.token = token
  }
  return config
})

service.interceptors.response.use(
  (response) => {
    const body = response.data as ApiResult<unknown>
    if (body && typeof body.code !== 'undefined' && body.code !== 1) {
      ElMessage.error(body.msg || '请求失败')
      return Promise.reject(new Error(body.msg || '请求失败'))
    }
    return body && typeof body.code !== 'undefined' ? body.data : response.data
  },
  (error) => {
    ElMessage.error(error.response?.data?.msg || error.message || '网络异常')
    return Promise.reject(error)
  }
)

const http = service as unknown as {
  get<T>(url: string, config?: AxiosRequestConfig): Promise<T>
  post<T>(url: string, data?: unknown, config?: AxiosRequestConfig): Promise<T>
  put<T>(url: string, data?: unknown, config?: AxiosRequestConfig): Promise<T>
  delete<T>(url: string, config?: AxiosRequestConfig): Promise<T>
}

export default http
