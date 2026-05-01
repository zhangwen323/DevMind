import http from './http'

function unwrap(response) {
  return response.data.data
}

export async function fetchAdminTraces(params) {
  const response = await http.get('/api/agent-traces', { params })
  return unwrap(response)
}

export async function fetchAdminTraceDetail(traceKey) {
  const response = await http.get(`/api/agent-traces/${traceKey}`)
  return unwrap(response)
}

export async function fetchSessionTraces(sessionId) {
  const response = await http.get(`/api/chat/sessions/${sessionId}/agent-traces`)
  return unwrap(response)
}

export async function fetchSessionTraceDetail(sessionId, traceKey) {
  const response = await http.get(`/api/chat/sessions/${sessionId}/agent-traces/${traceKey}`)
  return unwrap(response)
}
