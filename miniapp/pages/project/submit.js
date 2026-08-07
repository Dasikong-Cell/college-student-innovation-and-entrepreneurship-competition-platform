const { request, uploadFile } = require('../../utils/request.js')
const app = getApp()

Page({
  data: {
    cid: null,
    competitionTitle: '',
    loading: true,
    submitting: false,
    form: {
      teamName: '',
      leaderName: '',
      leaderPhone: '',
      teamMembers: '',
      college: '',
      category: '',
      abstract: '',
      planContent: '',
      innovationPoints: '',
      expectedResults: ''
    },
    attachments: [],
    categories: []
  },

  onLoad(options) {
    const cid = options.cid
    if (!cid) {
      wx.showToast({ title: '参数错误', icon: 'none' })
      setTimeout(() => wx.navigateBack(), 800)
      return
    }
    this.setData({ cid })
    this.loadCompetition()
    this.loadUser()
  },

  loadCompetition() {
    request({
      url: '/competition/' + this.data.cid,
      method: 'GET'
    }).then(data => {
      this.setData({
        competitionTitle: (data && (data.title || data.name)) || ''
      })
      wx.setNavigationBarTitle({ title: '项目申报 - ' + ((data && (data.title || data.name)) || '') })
    }).catch(() => {}).finally(() => {
      this.setData({ loading: false })
    })
  },

  loadUser() {
    const user = app && app.globalData && app.globalData.user
    if (user) {
      this.setData({
        'form.leaderName': user.name || user.nickname || '',
        'form.leaderPhone': user.phone || user.mobile || '',
        'form.college': user.college || user.school || '',
        'form.category': user.major || ''
      })
    }
  },

  onInput(e) {
    const field = e.currentTarget.dataset.field
    this.setData({ ['form.' + field]: e.detail.value })
  },

  onChooseFile() {
    wx.chooseMedia({
      count: 3,
      mediaType: ['image', 'file', 'video'],
      sourceType: ['album', 'camera'],
      success: (res) => {
        wx.showLoading({ title: '上传中...' })
        const files = res.tempFiles || []
        Promise.all(files.map(f => this.uploadSingleFile(f.tempFilePath))).then(uploaded => {
          const valid = uploaded.filter(u => u)
          this.setData({ attachments: [...this.data.attachments, ...valid] })
          wx.hideLoading()
          wx.showToast({ title: '上传成功', icon: 'success' })
        }).catch(() => {
          wx.hideLoading()
        })
      }
    })
  },

  uploadSingleFile(filePath) {
    return uploadFile({
      filePath,
      name: 'file',
      formData: { bizType: 'project', fileName: filePath.split('/').pop() }
    }).then(data => {
      if (typeof data === 'string') {
        try { data = JSON.parse(data) } catch (e) {}
      }
      return {
        url: (data && (data.url || data.fileUrl || data)) || filePath,
        name: filePath.split('/').pop()
      }
    }).catch(() => null)
  },

  onRemoveAttachment(e) {
    const idx = e.currentTarget.dataset.idx
    const attachments = [...this.data.attachments]
    attachments.splice(idx, 1)
    this.setData({ attachments })
  },

  validate() {
    const f = this.data.form
    if (!f.teamName) { wx.showToast({ title: '请填写团队名称', icon: 'none' }); return false }
    if (!f.leaderName) { wx.showToast({ title: '请填写负责人姓名', icon: 'none' }); return false }
    if (!f.leaderPhone) { wx.showToast({ title: '请填写负责人电话', icon: 'none' }); return false }
    if (!f.abstract) { wx.showToast({ title: '请填写项目简介', icon: 'none' }); return false }
    return true
  },

  buildPayload(draft) {
    const f = this.data.form
    return {
      competitionId: this.data.cid,
      teamName: f.teamName,
      leaderName: f.leaderName,
      leaderPhone: f.leaderPhone,
      teamMembers: f.teamMembers,
      college: f.college,
      category: f.category,
      abstract: f.abstract,
      planContent: f.planContent,
      innovationPoints: f.innovationPoints,
      expectedResults: f.expectedResults,
      status: draft ? 'draft' : 'submitted',
      attachments: this.data.attachments
    }
  },

  onSubmit() {
    if (!this.validate()) return
    if (this.data.submitting) return
    this.setData({ submitting: true })
    request({
      url: '/project',
      method: 'POST',
      data: this.buildPayload(false)
    }).then(res => {
      wx.showToast({ title: '申报成功', icon: 'success' })
      setTimeout(() => {
        wx.redirectTo({ url: '/pages/project/list' })
      }, 800)
    }).catch(() => {}).finally(() => {
      this.setData({ submitting: false })
    })
  },

  onSaveDraft() {
    if (!this.data.form.teamName) {
      wx.showToast({ title: '请至少填写团队名称', icon: 'none' })
      return
    }
    if (this.data.submitting) return
    this.setData({ submitting: true })
    request({
      url: '/project',
      method: 'POST',
      data: this.buildPayload(true)
    }).then(res => {
      wx.showToast({ title: '草稿已保存', icon: 'success' })
      setTimeout(() => wx.navigateBack(), 800)
    }).catch(() => {}).finally(() => {
      this.setData({ submitting: false })
    })
  }
})
