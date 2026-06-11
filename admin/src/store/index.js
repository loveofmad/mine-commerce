import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('admin_token') || '')
  const userInfo = ref(JSON.parse(localStorage.getItem('admin_user') || '{}'))

  function setToken(val) {
    token.value = val
    localStorage.setItem('admin_token', val)
  }

  function setUserInfo(val) {
    userInfo.value = val
    localStorage.setItem('admin_user', JSON.stringify(val))
  }

  function logout() {
    token.value = ''
    userInfo.value = {}
    localStorage.removeItem('admin_token')
    localStorage.removeItem('admin_user')
  }

  return { token, userInfo, setToken, setUserInfo, logout }
})
