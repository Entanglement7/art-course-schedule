import request from '@/utils/request'

export const getTeachers = (params?: Record<string, unknown>) =>
  request.get('/api/teachers', { params })

export const getTeacherOptions = () => request.get('/api/teachers/options')

export const getTeacher = (id: number) => request.get(`/api/teachers/${id}`)

export const createTeacher = (data: Record<string, unknown>) =>
  request.post('/api/teachers', data)

export const updateTeacher = (id: number, data: Record<string, unknown>) =>
  request.put(`/api/teachers/${id}`, data)

export const deleteTeacher = (id: number) => request.delete(`/api/teachers/${id}`)
