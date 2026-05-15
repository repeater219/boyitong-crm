import { createContext, useContext, useState, useEffect } from 'react'
import api from './api.js'

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    const token = localStorage.getItem('token')
    if (token) {
      api.defaults.headers.common['Authorization'] = 'Bearer ' + token
      api.get('/auth/me')
        .then(res => setUser(res.data.data))
        .catch(() => { localStorage.removeItem('token'); delete api.defaults.headers.common['Authorization'] })
        .finally(() => setLoading(false))
    } else {
      setLoading(false)
    }
  }, [])

  const login = async (username, password) => {
    const res = await api.post('/auth/login', { username, password })
    const data = res.data.data
    localStorage.setItem('token', data.token)
    api.defaults.headers.common['Authorization'] = 'Bearer ' + data.token
    setUser(data)
    return data
  }

  const logout = () => {
    localStorage.removeItem('token')
    delete api.defaults.headers.common['Authorization']
    setUser(null)
  }

  const refreshUser = async () => {
    try {
      const res = await api.get('/profile')
      setUser(prev => ({ ...prev, ...res.data.data }))
    } catch (_) {}
  }

  return (
    <AuthContext.Provider value={{ user, login, logout, loading, refreshUser }}>
      {children}
    </AuthContext.Provider>
  )
}

export const useAuth = () => useContext(AuthContext)