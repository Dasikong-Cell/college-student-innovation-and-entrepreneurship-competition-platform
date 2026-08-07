const { wxLogin, accountLogin } = require('../../utils/auth.js')

Page({
  data: {
    loading: false,
    showTestLogin: false,
    username: '',
    password: ''
  },

  onLoad() {
    const token = wx.getStorageSync('token')
    if (token) {
      wx.switchTab({ url: '/pages/index/index' })
      return
    }
    // ===== 自动填充 + 登录（调试用） =====
    this.setData({ username: 'student01', password: '123456', showTestLogin: true })
    console.log('[login] 自动填充 student01/123456，500ms 后自动登录')
    setTimeout(() => {
      console.log('[login] 触发 onAccountLogin')
      this.onAccountLogin()
    }, 600)
  },

  onWxLogin() {
    if (this.data.loading) return
    this.setData({ loading: true })
    wxLogin().then(() => {
      wx.showToast({ title: '登录成功', icon: 'success' })
      setTimeout(() => {
        wx.switchTab({ url: '/pages/index/index' })
      }, 500)
    }).catch(err => {
      console.error('wxLogin error', err)
      wx.showToast({ title: '微信登录失败，请使用测试账号登录', icon: 'none' })
    }).finally(() => {
      this.setData({ loading: false })
    })
  },

  onInputUsername(e) {
    this.setData({ username: e.detail.value })
  },

  onInputPassword(e) {
    this.setData({ password: e.detail.value })
  },

  onAccountLogin() {
    if (this.data.loading) return
    const { username, password } = this.data
    if (!username || !password) {
      wx.showToast({ title: '请输入账号和密码', icon: 'none' })
      return
    }
    this.setData({ loading: true })
    accountLogin(username, password).then(() => {
      wx.showToast({ title: '登录成功', icon: 'success' })
      setTimeout(() => {
        wx.switchTab({ url: '/pages/index/index' })
      }, 500)
    }).catch(() => {}).finally(() => {
      this.setData({ loading: false })
    })
  },

  toggleTestLogin() {
    this.setData({ showTestLogin: !this.data.showTestLogin })
  }
})

