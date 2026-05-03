import request from '@/utils/request'

export const getClassrooms = (params?: Record<string, unknown>) =>
  request.get('/api/classrooms', { params })

export const getClassroom = (id: number) => request.get(`/api/classrooms/${id}`)

export const createClassroom = (data: Record<string, unknown>) =>
  request.post('/api/classrooms', data)

export const updateClassroom = (id: number, data: Record<string, unknown>) =>
  request.put(`/api/classrooms/${id}`, data)

export const deleteClassroom = (id: number) => request.delete(`/api/classrooms/${id}`)
