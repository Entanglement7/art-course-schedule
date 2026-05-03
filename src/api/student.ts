import request from '@/utils/request'

export const getStudents = (params?: Record<string, unknown>) =>
  request.get('/api/students', { params })

export const getStudent = (id: number) => request.get(`/api/students/${id}`)

export const createStudent = (data: Record<string, unknown>) =>
  request.post('/api/students', data)

export const updateStudent = (id: number, data: Record<string, unknown>) =>
  request.put(`/api/students/${id}`, data)

export const deleteStudent = (id: number) => request.delete(`/api/students/${id}`)
