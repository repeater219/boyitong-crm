import axios from 'axios'

const api = axios.create({
  baseURL: '/api',
  timeout: 10000,
})

export async function fetchCustomers(params = {}) {
  const res = await api.get('/customers', { params })
  return res.data.data
}

export async function fetchCustomerById(id) {
  const res = await api.get(`/customers/${id}`)
  return res.data.data
}

export async function fetchStats() {
  const res = await api.get('/stats')
  return res.data.data
}

export default api