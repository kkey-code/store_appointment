import http, { PageResult } from './http'

export interface Employee {
  id?: number
  name: string
  phone: string
  gender?: number
  position?: string
  status?: number
  remark?: string
  createTime?: string
}

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

export interface ServiceItem {
  id?: number
  name: string
  price?: number
  duration?: number
  description?: string
  status?: number
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

export interface Appointment {
  id?: number
  customerId?: number
  customerName?: string
  customerPhone?: string
  employeeId?: number
  employeeName?: string
  serviceItemId?: number
  serviceItemName?: string
  serviceItemPrice?: number
  appointmentTime?: string
  status?: number
  remark?: string
  createTime?: string
}

export interface OrderInfo {
  id?: number
  orderNo?: string
  appointmentId?: number
  customerId?: number
  customerName?: string
  serviceItemId?: number
  serviceItemName?: string
  cardType?: string
  originalAmount?: number
  discountAmount?: number
  amount?: number
  paidAmount?: number
  debtAmount?: number
  monthlyPayment?: number
  paymentMethod?: string
  debtStatus?: number
  payStatus?: number
  orderStatus?: number
  payTime?: string
  createTime?: string
  remark?: string
}

export interface Overview {
  customerCount: number
  todayAppointmentCount: number
  todayOrderCount: number
  todayAmount: number
}

export interface AmountStatistics {
  dateList: string[]
  amountList: number[]
}

export const login = (username: string, password: string) =>
  http.get<{ token: string; userName?: string; username?: string; role?: string }>('/api/login', {
    params: { username, password }
  })

export const employeeApi = {
  page: (params: Record<string, unknown>) => http.get<PageResult<Employee>>('/api/employees', { params }),
  save: (data: Employee) => http.post('/api/employees', data),
  update: (data: Employee) => http.put('/api/employees', data),
  remove: (id: number) => http.delete(`/api/employees/${id}`)
}

export const customerApi = {
  page: (params: Record<string, unknown>) => http.get<PageResult<Customer>>('/admin/customer/page', { params }),
  exportCustomers: (params: Record<string, unknown>) => http.get<Blob>('/admin/export/customers', { params, responseType: 'blob' }),
  save: (data: Customer) => http.post('/admin/customer', data),
  update: (data: Customer) => http.put('/admin/customer', data),
  remove: (id: number) => http.delete(`/admin/customer/${id}`)
}

export const serviceItemApi = {
  page: (params: Record<string, unknown>) => http.get<PageResult<ServiceItem>>('/admin/serviceItem/page', { params }),
  save: (data: ServiceItem) => http.post('/admin/serviceItem', data),
  update: (data: ServiceItem) => http.put('/admin/serviceItem', data),
  remove: (id: number) => http.delete(`/admin/serviceItem/${id}`)
}

export const inventoryApi = {
  page: (params: Record<string, unknown>) => http.get<PageResult<InventoryItem>>('/admin/inventory/page', { params }),
  save: (data: InventoryItem) => http.post('/admin/inventory', data),
  update: (data: InventoryItem) => http.put('/admin/inventory', data),
  remove: (id: number) => http.delete(`/admin/inventory/${id}`)
}

export const appointmentApi = {
  page: (params: Record<string, unknown>) => http.get<PageResult<Appointment>>('/admin/appointment/page', { params }),
  save: (data: Appointment) => http.post('/admin/appointment', data),
  update: (data: Appointment) => http.put('/admin/appointment', data),
  cancel: (id: number) => http.put(`/admin/appointment/${id}/cancel`),
  complete: (id: number) => http.put(`/admin/appointment/${id}/complete`)
}

export const orderApi = {
  page: (params: Record<string, unknown>) => http.get<PageResult<OrderInfo>>('/admin/order/page', { params }),
  exportOrders: (params: Record<string, unknown>) => http.get<Blob>('/admin/export/orders', { params, responseType: 'blob' }),
  save: (data: OrderInfo) => http.post('/admin/order', data),
  pay: (id: number) => http.put(`/admin/order/${id}/pay`),
  updatePayment: (id: number, data: Pick<OrderInfo, 'paidAmount' | 'monthlyPayment' | 'paymentMethod'>) =>
    http.put(`/admin/order/${id}/payment`, data),
  complete: (id: number) => http.put(`/admin/order/${id}/complete`),
  cancel: (id: number) => http.put(`/admin/order/${id}/cancel`)
}

export const statisticsApi = {
  overview: () => http.get<Overview>('/admin/statistics/overview'),
  orderAmount: () => http.get<AmountStatistics>('/admin/statistics/orderAmount')
}
