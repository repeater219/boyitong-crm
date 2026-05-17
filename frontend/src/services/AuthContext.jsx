import { createContext, useContext, useState, useEffect } from 'react'
import api from './api.js'

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null)
  const [loading, setLoading] = useState(true)
  const [userMap, setUserMap] = useState({})

  useEffect(() => {
    const token = localStorage.getItem('token')
    if (token) {
      api.defaults.headers.common['Authorization'] = 'Bearer ' + token
      const loadInitial = async () => {
        try {
          const res = await api.get('/auth/me')
          setUser(res.data.data)
          // Load user map
          const usersRes = await api.get('/users')
          const map = {}
          usersRes.data.data.forEach(u => { map[u.username] = u.displayName })
          setUserMap(map)
        } catch (e) {
          localStorage.removeItem('token')
          delete api.defaults.headers.common['Authorization']
        } finally {
          setLoading(false)
        }
      }
      loadInitial()
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
    // Load user map after login
    try {
      const usersRes = await api.get('/users')
      const map = {}
      usersRes.data.data.forEach(u => { map[u.username] = u.displayName })
      setUserMap(map)
    } catch (_) {}
    return data
  }

  const logout = () => {
    localStorage.removeItem('token')
    delete api.defaults.headers.common['Authorization']
    setUser(null)
    setUserMap({})
  }

  const refreshUser = async () => {
    try {
      const res = await api.get('/profile')
      setUser(prev => ({ ...prev, ...res.data.data }))
    } catch (_) {}
  }

  return (
    <AuthContext.Provider value={{ user, login, logout, loading, refreshUser, userMap }}>
      {children}
    </AuthContext.Provider>
  )
}

export const useAuth = () => useContext(AuthContext)