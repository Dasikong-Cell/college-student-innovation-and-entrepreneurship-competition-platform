import request from './request'

export function list(params) {
  return request.get('/announcement/list', { params })
}

export function page(params) {
  return request.get('/announcement/page', { params })
}

export function create(data) {
  return request.post('/announcement', data)
}

export function update(data) {
  return request.put('/announcement', data)
}

export function remove(id) {
  return request.delete(`/announcement/${id}`)
}
