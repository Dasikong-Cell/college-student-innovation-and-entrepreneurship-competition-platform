const { request } = require('../../utils/request.js')
const { logout } = require('../../utils/auth.js')
const app = getApp()

Page({
  data: {
    user: null,
    stats: {
      total: 0,
      approved: 0,
      reviewing: 0
    }
  },

  onLoad() {
    this.refresh()
  },

  onShow() {
    this.refresh()
  },

  onPullDownRefresh() {
    this.refresh().finally(() => wx.stopPullDownRefresh())
  },

  refresh() {
    const user = (app && app.globalData && app.globalData.user) || wx.getStorageSync('user') || null
    this.setData({ user })
    return this.loadStats()
  },

  loadStats() {
    return request({
      url: '/project/my/stats',
      method: 'GET'
    }).then(data => {
      this.setData({
        stats: {
          total: Number(data && (data.total || data.totalCount || data.totalProject)) || 0,
          approved: Number(data && (data.approved || data.passCount)) || 0,
          reviewing: Number(data && (data.reviewing || data.reviewCount)) || 0
        }
      })
    }).catch(() => {})
  },

  goProjectList() {
    wx.switchTab({ url: '/pages/project/list' })
  },

  goReviewList() {
    wx.showToast({ title: '评审功能开发中', icon: 'none' })
  },

  showAbout() {
    wx.showModal({
      title: '关于我们',
      content: '大学生创新创业大赛平台 v1.0.0\n激发创新潜能，放飞创业梦想。',
      showCancel: false,
      confirmText: '我知道了'
    })
  },

  confirmLogout() {
    wx.showModal({
      title: '提示',
      content: '确定要退出登录吗？',
      success: (res) => {
        if (res.confirm) {
          this.doLogout()
        }
      }
    })
  },

  doLogout() {
    logout()
    if (app) {
      app.globalData.token = ''
      app.globalData.user = null
    }
    wx.showToast({ title: '已退出登录', icon: 'none' })
    setTimeout(() => {
      wx.reLaunch({ url: '/pages/login/login' })
    }, 600)
  }
})
