import { useState, useEffect } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { fetchCustomerById } from '../services/api.js'
import api from '../services/api.js'
import { useAuth } from '../services/AuthContext.jsx'

const STATUS_LABELS = {
  'NEW': '新线索', 'FOLLOWING': '跟进中', 'NEGOTIATING': '洽谈中',
  'WON': '已成交', 'LOST': '已流失',
}
const STATUS_COLORS = {
  'NEW': 'bg-blue-100 text-blue-700', 'FOLLOWING': 'bg-yellow-100 text-yellow-700',
  'NEGOTIATING': 'bg-purple-100 text-purple-700', 'WON': 'bg-green-100 text-green-700',
  'LOST': 'bg-gray-200 text-gray-600',
}

const METHOD_LABELS = { 'PHONE': '电话', 'VISIT': '拜访', 'CHAT': '微信/聊天', 'OTHER': '其他' }

export default function CustomerDetail() {
  const { id } = useParams()
  const navigate = useNavigate()
  const { user } = useAuth()
  const isAdmin = user?.role === 'ADMIN'
  const [customer, setCustomer] = useState(null)
  const [followUps, setFollowUps] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)
  const [newFollow, setNewFollow] = useState({ method: 'PHONE', content: '', nextFollowDate: '' })

  const load = async () => {
    try {
      const c = await fetchCustomerById(id)
      setCustomer(c)
      const res = await api.get(`/follow-ups/customer/${id}`)
      setFollowUps(res.data.data || [])
    } catch (e) {
      setError(e.message)
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => { load() }, [id])

  const changeStatus = async (status) => {
    await api.put(`/customers/${id}/status`, { status })
    load()
  }

  const assignTo = async (username) => {
    await api.put(`/customers/${id}/assign`, { assignedTo: username })
    load()
  }

  const addFollowUp = async () => {
    if (!newFollow.content.trim()) return
    await api.post('/follow-ups', { customerId: parseInt(id), ...newFollow })
    setNewFollow({ method: 'PHONE', content: '', nextFollowDate: '' })
    load()
  }

  if (loading) return <div className="text-center py-12 text-gray-400">加载中...</div>
  if (error) return <div className="text-center py-12 text-red-500">加载失败: {error}</div>
  if (!customer) return <div className="text-center py-12 text-gray-400">未找到客户</div>

  const fields = [
    { label: 'ID', value: customer.id },
    { label: '城市', value: customer.city },
    { label: '日期', value: customer.date },
    { label: '区域', value: customer.area },
    { label: '地址', value: customer.address },
    { label: '行业/板块', value: customer.category },
    { label: '面积', value: customer.size ? `${customer.size} m²` : '-' },
    { label: '电话', value: customer.phone },
    { label: '到期日期', value: customer.expiryDate || '-' },
    { label: '原始销售员', value: customer.salesperson || '-' },
    { label: '分配对象', value: customer.assignedTo || '未分配' },
    { label: '备注', value: customer.remarks || '-' },
  ]

  return (
    <div>
      <button onClick={() => navigate('/customers')} className="text-blue-600 hover:text-blue-800 mb-4 inline-block">&larr; 返回列表</button>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Customer Info */}
        <div className="lg:col-span-2 bg-white rounded-lg shadow-sm p-6">
          <div className="flex items-center justify-between mb-4">
            <h1 className="text-xl font-bold">客户详情 #{customer.id}</h1>
            <span className={`px-3 py-1 rounded text-sm ${STATUS_COLORS[customer.status] || 'bg-gray-100'}`}>
              {STATUS_LABELS[customer.status] || customer.status}
            </span>
          </div>

          {isAdmin && (
            <div className="flex gap-2 mb-4">
              <select onChange={e => assignTo(e.target.value)} value={customer.assignedTo || ''} className="border rounded px-2 py-1 text-sm">
                <option value="">分配客户...</option>
                <option value="zhangrui">张睿</option>
                <option value="wangxian">王鲜</option>
              </select>
              <select onChange={e => changeStatus(e.target.value)} value={customer.status} className="border rounded px-2 py-1 text-sm">
                <option value="NEW">设为：新线索</option>
                <option value="FOLLOWING">设为：跟进中</option>
                <option value="NEGOTIATING">设为：洽谈中</option>
                <option value="WON">设为：已成交</option>
                <option value="LOST">设为：已流失</option>
              </select>
            </div>
          )}

          <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
            {fields.map(f => (
              <div key={f.label} className="border-b pb-2">
                <span className="text-sm text-gray-500">{f.label}</span>
                <div className="text-gray-800 mt-0.5">{f.value}</div>
              </div>
            ))}
          </div>
        </div>

        {/* Follow Up Panel */}
        <div className="bg-white rounded-lg shadow-sm p-4">
          <h2 className="font-semibold mb-3">跟进记录</h2>

          <div className="space-y-2 mb-4">
            {followUps.map(f => (
              <div key={f.id} className="text-sm border-b pb-2">
                <div className="flex items-center gap-2 text-xs text-gray-400">
                  <span>{METHOD_LABELS[f.method] || f.method}</span>
                  <span>{f.createdAt?.replace('T', ' ')}</span>
                  <span className="font-medium text-gray-500">{f.salesperson}</span>
                </div>
                <div className="mt-0.5">{f.content}</div>
                {f.nextFollowDate && <div className="text-xs text-blue-500 mt-0.5">下次跟进: {f.nextFollowDate}</div>}
              </div>
            ))}
            {followUps.length === 0 && <div className="text-sm text-gray-400 text-center py-4">暂无跟进记录</div>}
          </div>

          <div className="space-y-2">
            <select value={newFollow.method} onChange={e => setNewFollow({...newFollow, method: e.target.value})}
              className="w-full border rounded px-2 py-1 text-sm">
              <option value="PHONE">电话</option>
              <option value="VISIT">拜访</option>
              <option value="CHAT">微信/聊天</option>
              <option value="OTHER">其他</option>
            </select>
            <textarea value={newFollow.content} onChange={e => setNewFollow({...newFollow, content: e.target.value})}
              placeholder="记录跟进内容..." rows={3} className="w-full border rounded px-2 py-1 text-sm" />
            <div className="flex gap-2">
              <input value={newFollow.nextFollowDate} onChange={e => setNewFollow({...newFollow, nextFollowDate: e.target.value})}
                placeholder="下次跟进日期（可选）" className="flex-1 border rounded px-2 py-1 text-sm" />
              <button onClick={async () => {
                if (!customer) return
                const res = await api.post('/ai/generate-followup', { keywords: `${customer.city || ''} ${customer.category || ''} ${customer.salesperson || ''}` })
                setNewFollow(prev => ({ ...prev, content: res.data.data.content }))
              }} className="bg-purple-600 text-white px-3 py-1 rounded text-sm hover:bg-purple-700 whitespace-nowrap">
                AI 生成
              </button>
            </div>
            <button onClick={addFollowUp} disabled={!newFollow.content.trim()}
              className="w-full bg-blue-600 text-white py-1.5 rounded text-sm hover:bg-blue-700 disabled:opacity-50">
              添加跟进
            </button>
          </div>
        </div>
      </div>
    </div>
  )
}