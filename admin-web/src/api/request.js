import axios from 'axios'
import { ElMessage } from 'element-plus'

const request = axios.create({
  baseURL: '/api',
  timeout: 10000
})

request.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => Promise.reject(error)
)

request.interceptors.response.use(
  response => {
    const data = response.data
    if (data && data.code !== undefined && data.code !== 0 && data.code !== 200) {
      const msg = data.msg || data.message || '请求失败'
      ElMessage.error(msg)
      return Promise.reject(new Error(msg))
    }
    return data.code !== undefined ? data : response
  },
  error => {
    if (error.response) {
      const { status } = error.response
      if (status === 401) {
        localStorage.removeItem('token')
        localStorage.removeItem('user')
        ElMessage.error('登录已过期，请重新登录')
        if (!location.hash.includes('/login')) {
          location.href = '/login'
        }
      } else if (status === 403) {
        ElMessage.error('没有权限访问')
      } else {
        const bd = error.response.data || {}
        const msg = bd.msg || bd.message || `请求错误 (${status})`
        ElMessage.error(msg)
      }
    } else {
      ElMessage.error('网络连接失败')
    }
    return Promise.reject(error)
  }
)

export default request
