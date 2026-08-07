const app = getApp()

function getApiBase() {
  return (app && app.globalData && app.globalData.API_BASE) || 'http://localhost:8090/api'
}

function getToken() {
  if (app && app.globalData && app.globalData.token) {
    return app.globalData.token
  }
  return wx.getStorageSync('token') || ''
}

function request({ url, method = 'GET', data = {}, auth = true, header = {} }) {
  return new Promise((resolve, reject) => {
    const token = getToken()
    const finalHeader = {
      'Content-Type': 'application/json',
      ...header
    }
    if (auth && token) {
      finalHeader['Authorization'] = 'Bearer ' + token
    }

    wx.request({
      url: getApiBase() + url,
      method,
      data,
      header: finalHeader,
      success(res) {
        const statusCode = res.statusCode
        const body = res.data

        if (statusCode === 401) {
          const hadToken = !!token
          if (hadToken) {
            wx.removeStorageSync('token')
            wx.removeStorageSync('user')
            if (app) {
              app.globalData.token = ''
              app.globalData.user = null
            }
            wx.showToast({ title: '登录已过期', icon: 'none' })
            setTimeout(() => {
              wx.reLaunch({ url: '/pages/login/login' })
            }, 800)
          }
          reject(new Error('Unauthorized'))
          return
        }

        if (statusCode >= 200 && statusCode < 300) {
          if (body && (body.code === 0 || body.code === 200 || body.success === true)) {
            resolve(body.data !== undefined ? body.data : body)
          } else if (body && body.code !== undefined) {
            const msg = body.msg || body.message || '请求失败'
            wx.showToast({ title: msg, icon: 'none' })
            reject(new Error(msg))
          } else {
            resolve(body)
          }
        } else {
          const msg = (body && (body.msg || body.message)) || ('请求失败 (' + statusCode + ')')
          wx.showToast({ title: msg, icon: 'none' })
          reject(new Error(msg))
        }
      },
      fail(err) {
        wx.showToast({ title: '网络错误，请检查网络', icon: 'none' })
        reject(err)
      }
    })
  })
}

function uploadFile({ filePath, name = 'file', formData = {}, header = {} }) {
  return new Promise((resolve, reject) => {
    const token = getToken()
    const finalHeader = {
      ...header
    }
    if (token) {
      finalHeader['Authorization'] = 'Bearer ' + token
    }

    wx.uploadFile({
      url: getApiBase() + '/file/upload',
      filePath,
      name,
      formData,
      header: finalHeader,
      success(res) {
        try {
          const body = JSON.parse(res.data)
          if (body && (body.code === 0 || body.code === 200)) {
            resolve(body.data !== undefined ? body.data : body)
          } else {
            const msg = body.msg || '上传失败'
            wx.showToast({ title: msg, icon: 'none' })
            reject(new Error(msg))
          }
        } catch (e) {
          resolve(res.data)
        }
      },
      fail(err) {
        wx.showToast({ title: '上传失败', icon: 'none' })
        reject(err)
      }
    })
  })
}

module.exports = {
  request,
  uploadFile
}
