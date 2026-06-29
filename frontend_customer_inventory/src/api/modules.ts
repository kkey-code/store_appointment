import http, { PageResult } from './http'

export interface Customer {
  id?: number
  name: string
  phone: string
  gender?: number
  birthday?: string
  level?: string
  source?: string
  remark?: string
  createTime?: string
}

export interface InventoryItem {
  id?: number
  name: string
  category?: string
  unit?: string
  quantity?: number
  safetyStock?: number
  costPrice?: number
  supplier?: string
  status?: number
  remark?: string
  createTime?: string
  updateTime?: string
}

export const login = (username: string, password: string) =>
  http.get<{ token: string; userName?: string; username?: string; role?: string }>('/api/login', {
    params: { username, password }
  })

export const customerApi = {
  page: (params: Record<string, unknown>) => http.get<PageResult<Customer>>('/admin/customer/page', { params }),
  exportCustomers: (params: Record<string, unknown>) => http.get<Blob>('/admin/export/customers', { params, responseType: 'blob' }),
  save: (data: Customer) => http.post('/admin/customer', data),
  update: (data: Customer) => http.put('/admin/customer', data),
  remove: (id: number) => http.delete(`/admin/customer/${id}`)
}

export const inventoryApi = {
  page: (params: Record<string, unknown>) => http.get<PageResult<InventoryItem>>('/admin/inventory/page', { params }),
  save: (data: InventoryItem) => http.post('/admin/inventory', data),
  update: (data: InventoryItem) => http.put('/admin/inventory', data),
  remove: (id: number) => http.delete(`/admin/inventory/${id}`)
}
