import request from '@/utils/request'

export const getWeekSchedule = (date?: string) =>
  request.get('/api/schedules/week', { params: { date } })

export const getTeacherSchedule = (teacherId: number, date?: string) =>
  request.get(`/api/schedules/teacher/${teacherId}`, { params: { date } })

export const getStudentSchedule = (studentId: number, date?: string) =>
  request.get(`/api/schedules/student/${studentId}`, { params: { date } })

export const getClassroomSchedule = (classroomId: number, date?: string) =>
  request.get(`/api/schedules/classroom/${classroomId}`, { params: { date } })

export const createManualSchedule = (data: Record<string, unknown>) =>
  request.post('/api/schedules/manual', data)
