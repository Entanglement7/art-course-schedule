import request from '@/utils/request'

export interface LoginRequest {
  username: string
  password: string
}

export interface RegisterRequest {
  username: string
  password: string
  name: string
  role: string
}

export const login = (data: LoginRequest) => request.post('/api/auth/login', data)
export const register = (data: RegisterRequest) => request.post('/api/auth/register', data)
export const logout = () => request.post('/api/auth/logout')
export const getMe = () => request.get('/api/auth/me')
