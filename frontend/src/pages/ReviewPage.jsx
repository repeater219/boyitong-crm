import { useState, useEffect } from 'react'
import { useAuth } from '../services/AuthContext.jsx'
import api from '../services/api.js'

export default function ReviewPage() {
  const { user } = useAuth()
  const [uploads, setUploads] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)

  const loadUploads = () => {
    setLoading(true)
    api.get('/uploads/pending')
      .then(res => setUploads(res.data.data))
      .catch(e => setError(e.message))
      .finally(() => setLoading(false))
  }

  useEffect(() => { loadUploads() }, [])

  const handleApprove = async (id, e) => {
    const comment = e?.target?.dataset?.comment || ''
    await api.post(`/uploads/${id}/approve`, { comment })
    loadUploads()
  }

  const handleReject = async (id) => {
    const comment = prompt('请输入拒绝原因（可选）：')
    if (comment === null) return
    await api.post(`/uploads/${id}/reject`, { comment: comment || '' })
    loadUploads()
  }

  if (loading) return <div className="text-center py-12 text-gray-400">加载中...</div>
  if (error) return <div className="text-center py-12 text-red-500">{error}</div>

  return (
    <div>
      <h1 className="text-2xl font-bold mb-6">审核管理</h1>

      {uploads.length === 0 ? (
        <div className="bg-white rounded-lg shadow-sm p-8 text-center text-gray-400">
          暂无待审核数据
        </div>
      ) : (
        <div className="space-y-4">
          {uploads.map(u => (
            <div key={u.id} className="bg-white rounded-lg shadow-sm p-4">
              <div className="flex items-start justify-between">
                <div>
                  <p><span className="text-gray-500">上传者：</span>{u.uploader}</p>
                  <p><span className="text-gray-500">城市：</span>{u.city}</p>
                  <p><span className="text-gray-500">文件：</span>{u.fileName}</p>
                  <p><span className="text-gray-500">记录数：</span>{u.recordCount} 条</p>
                  <p><span className="text-gray-500">时间：</span>{u.createdAt}</p>
                </div>
                <div className="flex gap-2">
                  <button onClick={() => handleApprove(u.id)}
                    className="bg-green-600 text-white px-4 py-1.5 rounded text-sm hover:bg-green-700">
                    通过
                  </button>
                  <button onClick={() => handleReject(u.id)}
                    className="bg-red-600 text-white px-4 py-1.5 rounded text-sm hover:bg-red-700">
                    拒绝
                  </button>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}

      <div className="mt-8">
        <h2 className="text-lg font-semibold mb-3">全部上传记录</h2>
        <UploadHistory />
      </div>
    </div>
  )
}

function UploadHistory() {
  const [records, setRecords] = useState([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    api.get('/uploads/all')
      .then(res => setRecords(res.data.data))
      .catch(() => {})
      .finally(() => setLoading(false))
  }, [])

  if (loading) return null

  return (
    <div className="bg-white rounded-lg shadow-sm overflow-hidden">
      <table className="w-full text-sm">
        <thead>
          <tr className="bg-gray-50 border-b">
            <th className="text-left px-4 py-2">上传者</th>
            <th className="text-left px-4 py-2">城市</th>
            <th className="text-left px-4 py-2">记录数</th>
            <th className="text-left px-4 py-2">状态</th>
            <th className="text-left px-4 py-2">审核人</th>
            <th className="text-left px-4 py-2">时间</th>
          </tr>
        </thead>
        <tbody>
          {records.map(r => (
            <tr key={r.id} className="border-b">
              <td className="px-4 py-2">{r.uploader}</td>
              <td className="px-4 py-2">{r.city}</td>
              <td className="px-4 py-2">{r.recordCount}</td>
              <td className="px-4 py-2">
                <span className={`px-2 py-0.5 rounded text-xs ${
                  r.status === 'APPROVED' ? 'bg-green-100 text-green-700' :
                  r.status === 'REJECTED' ? 'bg-red-100 text-red-700' :
                  'bg-yellow-100 text-yellow-700'
                }`}>
                  {r.status === 'APPROVED' ? '已通过' : r.status === 'REJECTED' ? '已拒绝' : '待审核'}
                </span>
              </td>
              <td className="px-4 py-2">{r.reviewer || '-'}</td>
              <td className="px-4 py-2 text-gray-500">{r.createdAt?.replace('T', ' ')}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}