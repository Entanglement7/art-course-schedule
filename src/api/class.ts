import request from '@/utils/request'

export const getClasses = (params?: Record<string, unknown>) =>
  request.get('/api/classes', { params })

export const getClass = (id: number) => request.get(`/api/classes/${id}`)

export const getClassStudents = (id: number) =>
  request.get(`/api/classes/${id}/students`)

export const createClass = (data: Record<string, unknown>) =>
  request.post('/api/classes', data)

export const updateClass = (id: number, data: Record<string, unknown>) =>
  request.put(`/api/classes/${id}`, data)

export const deleteClass = (id: number) => request.delete(`/api/classes/${id}`)
