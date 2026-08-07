import { defineStore } from 'pinia'
import { login as loginApi, getMe } from '../api/auth'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: localStorage.getItem('token') || '',
    user: JSON.parse(localStorage.getItem('user') || 'null')
  }),
  getters: {
    isLoggedIn: (state) => !!state.token,
    role: (state) => state.user?.role || '',
    userName: (state) => state.user?.name || ''
  },
  actions: {
    async login(form) {
      const res = await loginApi(form)
      const d = res.data || {}
      const token = d.token
      const user = { id: d.userId, username: d.username, role: d.role, name: d.name }
      if (!token) throw new Error('登录响应缺少 token')
      this.token = token
      this.user = user
      localStorage.setItem('token', token)
      localStorage.setItem('user', JSON.stringify(user))
      return user
    },
    async fetchMe() {
      const res = await getMe()
      const user = res.data || res
      this.user = user
      localStorage.setItem('user', JSON.stringify(user))
      return user
    },
    logout() {
      this.token = ''
      this.user = null
      localStorage.removeItem('token')
      localStorage.removeItem('user')
    }
  }
})
