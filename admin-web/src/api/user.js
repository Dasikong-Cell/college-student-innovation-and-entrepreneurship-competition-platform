import request from './request'

export function page(params) {
  return request.get('/user/page', { params })
}

export function get(id) {
  return request.get(`/user/${id}`)
}

export function update(data) {
  return request.put('/user', data)
}

export function resetPassword(id, password) {
  return request.put(`/user/${id}/reset-password`, { password })
}
