import request from '@/utils/request'

export const getAdjustments = (params?: Record<string, unknown>) =>
  request.get('/api/adjustments', { params })

export const createAdjustment = (data: Record<string, unknown>) =>
  request.post('/api/adjustments', data)

export const approveAdjustment = (id: number) =>
  request.put(`/api/adjustments/${id}/approve`)

export const rejectAdjustment = (id: number, rejectReason: string) =>
  request.put(`/api/adjustments/${id}/reject`, { rejectReason })

export const deleteAdjustment = (id: number) =>
  request.delete(`/api/adjustments/${id}`)
