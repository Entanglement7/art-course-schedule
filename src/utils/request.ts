import axios from 'axios'
import { useUserStore } from '@/stores/user'
import router from '@/router'

// 自动获取后端地址：如果是通过 IP 访问，则使用相同 IP；否则使用 localhost
const getBaseURL = () => {
  const hostname = window.location.hostname
  // 如果是 IP 地址访问，使用相同 IP
  if (hostname !== 'localhost' && hostname !== '127.0.0.1') {
    return `http://${hostname}:8080`
  }
  // 否则使用 localhost
  return 'http://localhost:8080'
}

const request = axios.create({
  baseURL: getBaseURL(),
  timeout: 10000,
})

request.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})

request.interceptors.response.use(
  (res) => {
    if (res.data.code === 200) return res.data.data
    return Promise.reject(new Error(res.data.message || '请求失败'))
  },
  (err) => {
    if (err.response?.status === 401) {
      localStorage.removeItem('token')
      useUserStore().logout()
      router.push('/login')
    }
    return Promise.reject(err)
  },
)

export default request
