const { request } = require('../../utils/request.js')

Page({
  data: {
    id: null,
    loading: true,
    detail: null,
    myProjectId: null,
    hasSubmitted: false
  },

  onLoad(options) {
    const id = options.id
    if (!id) {
      wx.showToast({ title: '参数错误', icon: 'none' })
      setTimeout(() => wx.navigateBack(), 800)
      return
    }
    this.setData({ id })
    this.loadDetail()
  },

  onShow() {
    if (this.data.id) {
      this.loadSubmissionStatus()
    }
  },

  loadDetail() {
    this.setData({ loading: true })
    request({
      url: '/competition/' + this.data.id,
      method: 'GET'
    }).then(data => {
      this.setData({ detail: data, loading: false })
      wx.setNavigationBarTitle({ title: (data && (data.title || data.name)) || '大赛详情' })
    }).catch(() => {
      this.setData({ loading: false })
    })
  },

  loadSubmissionStatus() {
    request({
      url: '/project/my/competition/' + this.data.id,
      method: 'GET'
    }).then(data => {
      this.setData({
        myProjectId: data && data.id ? data.id : null,
        hasSubmitted: !!(data && data.id)
      })
    }).catch(() => {})
  },

  onApply() {
    if (!this.data.detail) return
    if (!this.data.hasSubmitted) {
      wx.navigateTo({ url: '/pages/project/submit?cid=' + this.data.id })
    } else {
      wx.navigateTo({ url: '/pages/result/result?id=' + this.data.myProjectId })
    }
  },

  onPreviewImage(e) {
    const url = e.currentTarget.dataset.url
    if (!url) return
    wx.previewImage({ urls: [url] })
  }
})
