import request from '@/utils/request'

export const getCourses = (params?: Record<string, unknown>) =>
  request.get('/api/courses', { params })

export const getCourseOptions = () => request.get('/api/courses/options')

export const getCourse = (id: number) => request.get(`/api/courses/${id}`)

export const createCourse = (data: Record<string, unknown>) =>
  request.post('/api/courses', data)

export const updateCourse = (id: number, data: Record<string, unknown>) =>
  request.put(`/api/courses/${id}`, data)

export const deleteCourse = (id: number) => request.delete(`/api/courses/${id}`)
