export const ADMIN_TOKEN_KEY = 'store_admin_token'
export const ADMIN_NAME_KEY = 'store_admin_name'
export const ADMIN_ROLE_KEY = 'store_admin_role'

export interface LoginUser {
  token: string
  userName?: string
  username?: string
  role?: string
}

export const saveAdminSession = (user: LoginUser, fallbackName: string) => {
  localStorage.setItem(ADMIN_TOKEN_KEY, user.token)
  localStorage.setItem(ADMIN_NAME_KEY, user.userName || user.username || fallbackName)
  localStorage.setItem(ADMIN_ROLE_KEY, user.role || '')
}

export const clearAdminSession = () => {
  localStorage.removeItem(ADMIN_TOKEN_KEY)
  localStorage.removeItem(ADMIN_NAME_KEY)
  localStorage.removeItem(ADMIN_ROLE_KEY)
}

export const getAdminToken = () => localStorage.getItem(ADMIN_TOKEN_KEY)

export const getAdminName = () => localStorage.getItem(ADMIN_NAME_KEY) || '管理员'

export const getAdminRole = () => localStorage.getItem(ADMIN_ROLE_KEY) || ''

export const isReadOnlyAdmin = () => getAdminRole().toLowerCase() === 'readonly'
