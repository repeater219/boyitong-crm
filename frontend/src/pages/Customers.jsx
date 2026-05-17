import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { fetchCustomers } from '../services/api.js'
import api from '../services/api.js'
import FilterBar from '../components/FilterBar.jsx'
import Pagination from '../components/Pagination.jsx'
import { useAuth } from '../services/AuthContext.jsx'

export default function Customers() {
  const navigate = useNavigate()
  const { user, userMap } = useAuth()
  const isAdmin = user?.role === 'ADMIN'
  const [data, setData] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)
  const [editing, setEditing] = useState(null)
  const [editForm, setEditForm] = useState({})
  const [filters, setFilters] = useState({
    city: '', area: '', category: '',
    minSize: '', maxSize: '', salesperson: '', keyword: '',
    page: 0, size: 20,
    sortBy: 'id', sortDir: 'asc',
  })

  const loadData = (params) => {
    setLoading(true)
    setError(null)
    const cleanParams = {}
    for (const [k, v] of Object.entries(params)) {
      if (v !== '' && v !== null && v !== undefined) cleanParams[k] = v
    }
    fetchCustomers(cleanParams)
      .then(setData)
      .catch(e => setError(e.message))
      .finally(() => setLoading(false))
  }

  useEffect(() => { loadData(filters) }, [filters])

  const handleExport = () => {
    const params = new URLSearchParams()
    for (const [k, v] of Object.entries(filters)) {
      if (v !== '' && v !== null && v !== undefined && !['page', 'size', 'sortBy', 'sortDir'].includes(k)) {
        params.append(k, v)
      }
    }
    const token = localStorage.getItem('token')
    window.open(`/api/export/customers?${params.toString()}`, '_blank')
  }

  const handleDelete = async (id) => {
    if (!confirm('确定删除这条记录吗？')) return
    try {
      await api.delete(`/customers/${id}`)
      loadData(filters)
    } catch (e) {
      alert('删除失败: ' + (e.response?.data?.message || e.message))
    }
  }

  const startEdit = (c) => {
    setEditing(c.id)
    setEditForm({
      city: c.city || '', area: c.area || '', address: c.address || '',
      category: c.category || '', size: c.size || '', phone: c.phone || '',
      salesperson: c.salesperson || '', remarks: c.remarks || '',
      date: c.date || '', expiryDate: c.expiryDate || '',
    })
  }

  const saveEdit = async (id) => {
    try {
      await api.put(`/customers/${id}`, editForm)
      setEditing(null)
      loadData(filters)
    } catch (e) {
      alert('保存失败: ' + (e.response?.data?.message || e.message))
    }
  }

  return (
    <div>
      <div className="flex items-center justify-between mb-6">
        <h1 className="text-2xl font-bold">客户列表</h1>
        <button onClick={handleExport}
          className="bg-green-600 text-white px-4 py-2 rounded text-sm hover:bg-green-700">
          导出 Excel
        </button>
      </div>

      <FilterBar filters={filters} onChange={setFilters} onReset={() => setFilters({
        city: '', area: '', category: '', minSize: '', maxSize: '',
        salesperson: '', keyword: '', page: 0, size: 20, sortBy: 'id', sortDir: 'asc',
      })} />

      {error && <div className="bg-red-50 text-red-600 p-4 rounded-lg mb-4">{error}</div>}

      {loading ? (
        <div className="text-center py-12 text-gray-400">加载中...</div>
      ) : data ? (
        <>
          <div className="bg-white rounded-lg shadow-sm overflow-hidden">
            <table className="w-full text-sm">
              <thead>
                <tr className="bg-gray-50 border-b">
                  <th className="text-left px-4 py-3 font-medium text-gray-600">ID</th>
                  <th className="text-left px-4 py-3 font-medium text-gray-600">城市</th>
                  <th className="text-left px-4 py-3 font-medium text-gray-600">区域</th>
                  <th className="text-left px-4 py-3 font-medium text-gray-600">行业</th>
                  <th className="text-right px-4 py-3 font-medium text-gray-600">面积</th>
                  <th className="text-left px-4 py-3 font-medium text-gray-600">分配对象</th>
                  <th className="text-left px-4 py-3 font-medium text-gray-600">日期</th>
                  <th className="text-left px-4 py-3 font-medium text-gray-600">操作</th>
                </tr>
              </thead>
              <tbody>
                {data.content.map((c, idx) => (
                  <tr key={c.id} className={`border-b hover:bg-gray-50 ${idx % 2 === 0 ? 'bg-white' : 'bg-gray-50/50'}`}>
                    {editing === c.id ? (
                      <>
                        <td className="px-4 py-2">{c.id}</td>
                        <td className="px-4 py-2"><input value={editForm.city} onChange={e => setEditForm({...editForm, city: e.target.value})} className="w-16 border rounded px-1 py-0.5 text-xs" /></td>
                        <td className="px-4 py-2"><input value={editForm.area} onChange={e => setEditForm({...editForm, area: e.target.value})} className="w-20 border rounded px-1 py-0.5 text-xs" /></td>
                        <td className="px-4 py-2"><input value={editForm.category} onChange={e => setEditForm({...editForm, category: e.target.value})} className="w-24 border rounded px-1 py-0.5 text-xs" /></td>
                        <td className="px-4 py-2"><input value={editForm.size} onChange={e => setEditForm({...editForm, size: e.target.value})} className="w-16 border rounded px-1 py-0.5 text-xs text-right" /></td>
                        <td className="px-4 py-2"><input value={editForm.salesperson} onChange={e => setEditForm({...editForm, salesperson: e.target.value})} className="w-16 border rounded px-1 py-0.5 text-xs" /></td>
                        <td className="px-4 py-2"><input value={editForm.date} onChange={e => setEditForm({...editForm, date: e.target.value})} className="w-16 border rounded px-1 py-0.5 text-xs" /></td>
                        <td className="px-4 py-2 flex gap-1">
                          <button onClick={() => saveEdit(c.id)} className="text-green-600 text-xs hover:text-green-800">保存</button>
                          <button onClick={() => setEditing(null)} className="text-gray-500 text-xs hover:text-gray-700">取消</button>
                        </td>
                      </>
                    ) : (
                      <>
                        <td className="px-4 py-3 text-gray-500">{c.id}</td>
                        <td className="px-4 py-3">{c.city}</td>
                        <td className="px-4 py-3">{c.area}</td>
                        <td className="px-4 py-3 max-w-[150px] truncate">{c.category}</td>
                        <td className="px-4 py-3 text-right">{c.size ? c.size : '-'}</td>
                        <td className="px-4 py-3">{userMap[c.assignedTo] || c.assignedTo || c.salesperson}</td>
                        <td className="px-4 py-3 text-gray-500">{c.date}</td>
                        <td className="px-4 py-3">
                          <button onClick={() => navigate(`/customers/${c.id}`)} className="text-blue-600 hover:text-blue-800 text-sm mr-2">详情</button>
                          {isAdmin && (
                            <>
                              <button onClick={() => startEdit(c)} className="text-yellow-600 hover:text-yellow-800 text-sm mr-2">编辑</button>
                              <button onClick={() => handleDelete(c.id)} className="text-red-600 hover:text-red-800 text-sm">删除</button>
                            </>
                          )}
                        </td>
                      </>
                    )}
                  </tr>
                ))}
              </tbody>
            </table>
          </div>

          <Pagination
            page={data.page}
            totalPages={data.totalPages}
            totalElements={data.totalElements}
            onPageChange={(page) => setFilters(f => ({ ...f, page }))}
          />
        </>
      ) : null}
    </div>
  )
}