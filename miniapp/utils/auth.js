const { request } = require('./request.js')
const app = getApp()

function wxLogin() {
  return new Promise((resolve, reject) => {
    wx.login({
      success(res) {
        if (!res.code) {
          reject(new Error('获取微信登录凭证失败'))
          return
        }
        request({
          url: '/miniapp/auth/wx-login',
          method: 'POST',
          data: { code: res.code },
          auth: false
        }).then(data => {
          const token = (data && (data.token || data.accessToken)) || ''
          const user = (data && (data.user || data.userInfo || data)) || null
          if (app) {
            app.setToken(token)
            app.setUser(user)
          } else {
            wx.setStorageSync('token', token)
            wx.setStorageSync('user', user)
          }
          resolve({ token, user })
        }).catch(err => {
          reject(err)
        })
      },
      fail(err) {
        reject(err)
      }
    })
  })
}

function accountLogin(username, password) {
  return request({
    url: '/auth/login',
    method: 'POST',
    data: { username, password },
    auth: false
  }).then(data => {
    const token = (data && (data.token || data.accessToken)) || ''
    const user = (data && (data.user || data.userInfo || data)) || null
    if (app) {
      app.setToken(token)
      app.setUser(user)
    } else {
      wx.setStorageSync('token', token)
      wx.setStorageSync('user', user)
    }
    return { token, user }
  })
}

function logout() {
  wx.removeStorageSync('token')
  wx.removeStorageSync('user')
  if (app) {
    app.globalData.token = ''
    app.globalData.user = null
  }
}

module.exports = {
  wxLogin,
  accountLogin,
  logout
}
