import request from './request'

export function page(params) {
  return request.get('/project/page', { params })
}

export function get(id) {
  return request.get(`/project/${id}`)
}

export function create(data) {
  return request.post('/project', data)
}

export function updateDraft(id, data) {
  return request.put(`/project/${id}/draft`, data)
}

export function submit(id) {
  return request.post(`/project/${id}/submit`)
}

export function reviewDecision(id, data) {
  return request.put(`/project/${id}/review`, data)
}

export function myProjects(params) {
  return request.get('/project/my', { params })
}

export function byCompetition(competitionId) {
  return request.get(`/project/by-competition/${competitionId}`)
}
