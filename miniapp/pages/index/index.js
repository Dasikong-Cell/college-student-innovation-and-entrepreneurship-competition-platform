const { request } = require('../../utils/request.js')

Page({
  data: {
    loading: true,
    banners: [],
    announcements: [],
    competitions: [],
    unreadCount: 0
  },

  onLoad() {
    this.loadAll()
  },

  onShow() {
    if (!this.data.loading) {
      this.loadUnreadCount()
    }
  },

  onPullDownRefresh() {
    this.loadAll().finally(() => {
      wx.stopPullDownRefresh()
    })
  },

  loadAll() {
    this.setData({ loading: true })
    return Promise.all([
      this.loadAnnouncements(),
      this.loadCompetitions(),
      this.loadUnreadCount()
    ]).finally(() => {
      this.setData({ loading: false })
    })
  },

  loadAnnouncements() {
    return request({
      url: '/announcement/list',
      method: 'GET',
      data: { category: '公告', limit: 5 }
    }).then(data => {
      const list = (data && (data.records || data.list || data)) || []
      this.setData({ announcements: list })
    }).catch(() => {})
  },

  loadCompetitions() {
    return request({
      url: '/competition/page',
      method: 'GET',
      data: { size: 3, status: 'published' }
    }).then(data => {
      const list = (data && (data.records || data.list || data)) || []
      this.setData({ competitions: list })
    }).catch(() => {})
  },

  loadUnreadCount() {
    return request({
      url: '/notification/unread-count',
      method: 'GET'
    }).then(data => {
      this.setData({ unreadCount: Number(data) || 0 })
    }).catch(() => {})
  },

  goCompetition(e) {
    const id = e.currentTarget.dataset.id
    wx.navigateTo({ url: '/pages/competition-detail/detail?id=' + id })
  },

  goCompetitionList() {
    wx.switchTab({ url: '/pages/competition/competition' })
  },

  goLogin() {
    wx.navigateTo({ url: '/pages/login/login' })
  }
})
