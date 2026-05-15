import { useState, useEffect } from 'react'
import api from '../services/api.js'

export default function MyUploads() {
  const [uploads, setUploads] = useState([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    api.get('/uploads/my')
      .then(res => setUploads(res.data.data))
      .catch(() => {})
      .finally(() => setLoading(false))
  }, [])

  if (loading) return <div className="text-center py-12 text-gray-400">加载中...</div>

  return (
    <div>
      <h1 className="text-2xl font-bold mb-6">我的上传记录</h1>

      {uploads.length === 0 ? (
        <div className="bg-white rounded-lg shadow-sm p-8 text-center text-gray-400">
          暂无上传记录
        </div>
      ) : (
        <div className="bg-white rounded-lg shadow-sm overflow-hidden">
          <table className="w-full text-sm">
            <thead>
              <tr className="bg-gray-50 border-b">
                <th className="text-left px-4 py-3">城市</th>
                <th className="text-left px-4 py-3">文件名</th>
                <th className="text-right px-4 py-3">记录数</th>
                <th className="text-left px-4 py-3">状态</th>
                <th className="text-left px-4 py-3">审核人</th>
                <th className="text-left px-4 py-3">备注</th>
                <th className="text-left px-4 py-3">时间</th>
              </tr>
            </thead>
            <tbody>
              {uploads.map(u => (
                <tr key={u.id} className="border-b hover:bg-gray-50">
                  <td className="px-4 py-3">{u.city}</td>
                  <td className="px-4 py-3 max-w-[200px] truncate">{u.fileName}</td>
                  <td className="px-4 py-3 text-right">{u.recordCount}</td>
                  <td className="px-4 py-3">
                    <span className={`px-2 py-0.5 rounded text-xs ${
                      u.status === 'APPROVED' ? 'bg-green-100 text-green-700' :
                      u.status === 'REJECTED' ? 'bg-red-100 text-red-700' :
                      'bg-yellow-100 text-yellow-700'
                    }`}>
                      {u.status === 'APPROVED' ? '已通过' : u.status === 'REJECTED' ? '已拒绝' : '待审核'}
                    </span>
                  </td>
                  <td className="px-4 py-3">{u.reviewer || '-'}</td>
                  <td className="px-4 py-3 max-w-[200px] truncate text-gray-500">{u.reviewComment || '-'}</td>
                  <td className="px-4 py-3 text-gray-500">{u.createdAt?.replace('T', ' ')}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  )
}