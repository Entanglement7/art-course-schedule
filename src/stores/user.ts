import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useUserStore = defineStore('user', () => {
  const userInfo = ref({
    id: null as number | null,
    name: '',
    role: '',
    username: '',
    teacherId: null as number | null,
    studentId: null as number | null,
  })

  function setUserInfo(info: typeof userInfo.value) {
    userInfo.value = info
    localStorage.setItem('userInfo', JSON.stringify(info))
  }

  function loadUserInfo() {
    const saved = localStorage.getItem('userInfo')
    if (saved) userInfo.value = JSON.parse(saved)
  }

  function logout() {
    userInfo.value = { id: null, name: '', role: '', username: '', teacherId: null, studentId: null }
    localStorage.removeItem('userInfo')
    localStorage.removeItem('token')
  }

  return { userInfo, setUserInfo, loadUserInfo, logout }
})
