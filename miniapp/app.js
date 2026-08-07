const { request } = require('./utils/request.js')
const { wxLogin } = require('./utils/auth.js')

App({
  globalData: {
    token: '',
    user: null,
    API_BASE: 'http://localhost:8090/api'
  },

  onLaunch() {
    const token = wx.getStorageSync('token')
    const user = wx.getStorageSync('user')
    if (token) {
      this.globalData.token = token
      this.globalData.user = user
      this.validateToken()
      // 已有 token → 跳到首页
      wx.switchTab({ url: '/pages/index/index' })
    }
  },

  validateToken() {
    request({
      url: '/auth/me',
      method: 'GET',
      auth: true
    }).then(res => {
      this.globalData.user = res.data || res
      wx.setStorageSync('user', res.data || res)
    }).catch(err => {
      console.warn('validateToken failed', err)
      wx.removeStorageSync('token')
      wx.removeStorageSync('user')
    })
  },

  setToken(token) {
    this.globalData.token = token
    wx.setStorageSync('token', token)
  },

  setUser(user) {
    this.globalData.user = user
    wx.setStorageSync('user', user)
  },

  logout() {
    this.globalData.token = ''
    this.globalData.user = null
    wx.removeStorageSync('token')
    wx.removeStorageSync('user')
  }
})