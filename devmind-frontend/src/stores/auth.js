import { defineStore } from 'pinia'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: window.localStorage.getItem('devmind-token') || '',
    username: window.localStorage.getItem('devmind-username') || ''
  }),
  getters: {
    isAuthenticated: (state) => Boolean(state.token)
  },
  actions: {
    login(token, username) {
      this.token = token
      this.username = username
      window.localStorage.setItem('devmind-token', token)
      window.localStorage.setItem('devmind-username', username)
    },
    logout() {
      this.token = ''
      this.username = ''
      window.localStorage.removeItem('devmind-token')
      window.localStorage.removeItem('devmind-username')
    }
  }
})
