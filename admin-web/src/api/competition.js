import request from './request'

export function page(params) {
  return request.get('/competition/page', { params })
}

export function get(id) {
  return request.get(`/competition/${id}`)
}

export function create(data) {
  return request.post('/competition', data)
}

export function update(data) {
  return request.put('/competition', data)
}

export function remove(id) {
  return request.delete(`/competition/${id}`)
}

export function publish(id) {
  return request.put(`/competition/${id}/publish`)
}

export function takeDown(id) {
  return request.put(`/competition/${id}/take-down`)
}

export function stats() {
  return request.get('/competition/stats')
}
