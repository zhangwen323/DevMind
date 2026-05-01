import http from './http'

function unwrap(response) {
  return response.data.data
}

export async function createReport(payload) {
  const response = await http.post('/api/reports', payload)
  return unwrap(response)
}

export async function fetchReports(params) {
  const response = await http.get('/api/reports', { params })
  return unwrap(response)
}

export async function fetchReportDetail(reportId) {
  const response = await http.get(`/api/reports/${reportId}`)
  return unwrap(response)
}
