import request from './request'

export function submitReview(data) {
  return request.post('/review', data)
}

export function byProject(projectId) {
  return request.get(`/review/project/${projectId}`)
}

export function byExpert() {
  return request.get('/review/expert')
}

export function remove(id) {
  return request.delete(`/review/${id}`)
}
