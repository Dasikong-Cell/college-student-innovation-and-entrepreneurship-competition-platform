const { request } = require('../../utils/request.js')

Page({
  data: {
    loading: true,
    list: []
  },

  onLoad() {
    this.loadList()
  },

  onShow() {
    if (!this.data.loading) {
      this.loadList()
    }
  },

  onPullDownRefresh() {
    this.loadList().finally(() => wx.stopPullDownRefresh())
  },

  loadList() {
    this.setData({ loading: true })
    return request({
      url: '/project/my',
      method: 'GET'
    }).then(data => {
      const list = (data && (data.records || data.list || data)) || []
      this.setData({ list })
    }).catch(() => {
      this.setData({ list: [] })
    }).finally(() => {
      this.setData({ loading: false })
    })
  },

  getStatusTag(status) {
    const map = {
      draft: { label: '草稿', cls: 'tag-gray' },
      submitted: { label: '已提交', cls: 'tag-blue' },
      reviewing: { label: '评审中', cls: 'tag-orange' },
      approved: { label: '已通过', cls: 'tag-green' },
      rejected: { label: '未通过', cls: 'tag-red' },
      '已通过': { label: '已通过', cls: 'tag-green' },
      '未通过': { label: '未通过', cls: 'tag-red' },
      '评审中': { label: '评审中', cls: 'tag-orange' },
      '已提交': { label: '已提交', cls: 'tag-gray' }
    }
    return map[status] || { label: status || '未知', cls: 'tag-gray' }
  },

  goDetail(e) {
    const id = e.currentTarget.dataset.id
    wx.navigateTo({ url: '/pages/result/result?id=' + id })
  }
})
