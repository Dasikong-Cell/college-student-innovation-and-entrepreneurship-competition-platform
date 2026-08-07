const { request } = require('../../utils/request.js')

Page({
  data: {
    id: null,
    loading: true,
    project: null,
    reviews: []
  },

  onLoad(options) {
    const id = options.id
    if (!id) {
      wx.showToast({ title: '参数错误', icon: 'none' })
      setTimeout(() => wx.navigateBack(), 800)
      return
    }
    this.setData({ id })
    this.loadAll()
  },

  loadAll() {
    this.setData({ loading: true })
    Promise.all([
      request({ url: '/project/' + this.data.id, method: 'GET' }).catch(() => null),
      request({ url: '/review/project/' + this.data.id, method: 'GET' }).catch(() => null)
    ]).then(([project, reviews]) => {
      const list = Array.isArray(reviews) ? reviews : ((reviews && (reviews.records || reviews.list)) || [])
      const enriched = list.map(r => this.enrichReview(r))
      this.setData({
        project: project || null,
        reviews: enriched
      })
      const title = (project && (project.teamName || project.projectName)) || '评审结果'
      wx.setNavigationBarTitle({ title })
    }).finally(() => {
      this.setData({ loading: false })
    })
  },

  enrichReview(r) {
    const total = Number(r.scoreTotal) || 0
    const max = 100
    return {
      ...r,
      barScoreInnovation: this.percent(r.scoreInnovation, 25),
      barScoreFeasibility: this.percent(r.scoreFeasibility, 25),
      barScoreTeam: this.percent(r.scoreTeam, 25),
      barScorePresentation: this.percent(r.scorePresentation, 25),
      barTotal: Math.min(100, (total / max) * 100)
    }
  },

  percent(val, max) {
    if (!val && val !== 0) return 0
    return Math.min(100, (Number(val) / max) * 100)
  },

  previewAttachment(e) {
    const url = e.currentTarget.dataset.url
    if (!url) return
    if (/\.(png|jpe?g|gif|webp)/i.test(url)) {
      wx.previewImage({ urls: [url] })
    } else {
      wx.setClipboardData({ data: url, success: () => wx.showToast({ title: '链接已复制', icon: 'none' }) })
    }
  }
})
