import http from './http'

function unwrap(response) {
  return response.data.data
}

export async function analyzeCodeSnippet(payload) {
  const response = await http.post('/api/agents/code-analysis', payload)
  return unwrap(response)
}
