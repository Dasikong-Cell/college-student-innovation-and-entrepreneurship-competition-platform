const { request } = require('../../utils/request.js')

Page({
  data: {
    keyword: '',
    category: '',
    categories: [],
    list: [],
    page: 1,
    size: 10,
    total: 0,
    loading: false,
    loadingMore: false,
    noMore: false
  },

  onLoad() {
    this.loadCategories()
    this.refresh()
  },

  onPullDownRefresh() {
    this.refresh().finally(() => wx.stopPullDownRefresh())
  },

  onReachBottom() {
    if (this.data.noMore || this.data.loading || this.data.loadingMore) return
    this.loadMore()
  },

  loadCategories() {
    request({
      url: '/competition/categories',
      method: 'GET',
      auth: false
    }).then(data => {
      this.setData({ categories: data || [] })
    }).catch(() => {})
  },

  refresh() {
    this.setData({
      page: 1,
      list: [],
      noMore: false,
      loading: true
    })
    return this.fetchList()
  },

  loadMore() {
    this.setData({
      page: this.data.page + 1,
      loadingMore: true
    })
    return this.fetchList(true)
  },

  fetchList(isLoadMore) {
    const { page, size, keyword, category } = this.data
    const params = { page, size }
    if (keyword) params.keyword = keyword
    if (category) params.category = category
    return request({
      url: '/competition/page',
      method: 'GET',
      data: params
    }).then(data => {
      const records = (data && (data.records || data.list || data)) || []
      const total = Number(data && (data.total || data.totalCount)) || 0
      const list = isLoadMore ? [...this.data.list, ...records] : records
      const noMore = total > 0 && list.length >= total
      this.setData({ list, total, noMore })
    }).finally(() => {
      this.setData({ loading: false, loadingMore: false })
    })
  },

  onInputKeyword(e) {
    this.setData({ keyword: e.detail.value })
  },

  onConfirmSearch() {
    this.refresh()
  },

  selectCategory(e) {
    const cat = e.currentTarget.dataset.category || ''
    this.setData({ category: cat }, () => {
      this.refresh()
    })
  },

  goDetail(e) {
    const id = e.currentTarget.dataset.id
    wx.navigateTo({ url: '/pages/competition-detail/detail?id=' + id })
  },

  isRegisterOpen(item) {
    if (!item) return false
    const now = Date.now()
    const start = new Date(item.registerStart || item.startTime || item.registerStartTime || 0).getTime()
    const end = new Date(item.registerEnd || item.endTime || item.registerEndTime || 0).getTime()
    if (!start || !end) return true
    return now >= start && now <= end
  }
})
