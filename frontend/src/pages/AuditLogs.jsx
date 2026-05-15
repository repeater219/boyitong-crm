import { useState, useEffect } from 'react'
import api from '../services/api.js'

export default function AuditLogs() {
  const [logs, setLogs] = useState([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    api.get('/audit-logs')
      .then(res => setLogs(res.data.data))
      .catch(() => {})
      .finally(() => setLoading(false))
  }, [])

  const actionLabels = {
    'UPLOAD': '上传数据',
    'APPROVE_UPLOAD': '审核通过',
    'REJECT_UPLOAD': '审核拒绝',
    'UPDATE': '编辑数据',
    'DELETE': '删除数据',
  }

  if (loading) return <div className="text-center py-12 text-gray-400">加载中...</div>

  return (
    <div>
      <h1 className="text-2xl font-bold mb-6">操作日志</h1>

      {logs.length === 0 ? (
        <div className="bg-white rounded-lg shadow-sm p-8 text-center text-gray-400">暂无日志</div>
      ) : (
        <div className="bg-white rounded-lg shadow-sm overflow-hidden">
          <table className="w-full text-sm">
            <thead>
              <tr className="bg-gray-50 border-b">
                <th className="text-left px-4 py-3">时间</th>
                <th className="text-left px-4 py-3">用户</th>
                <th className="text-left px-4 py-3">操作</th>
                <th className="text-left px-4 py-3">对象</th>
                <th className="text-left px-4 py-3">详情</th>
              </tr>
            </thead>
            <tbody>
              {logs.map(log => (
                <tr key={log.id} className="border-b hover:bg-gray-50">
                  <td className="px-4 py-3 text-gray-500 whitespace-nowrap">{log.createdAt?.replace('T', ' ')}</td>
                  <td className="px-4 py-3">{log.username}</td>
                  <td className="px-4 py-3">
                    <span className={`px-2 py-0.5 rounded text-xs ${
                      log.action?.includes('REJECT') ? 'bg-red-100 text-red-700' :
                      log.action?.includes('DELETE') ? 'bg-red-100 text-red-700' :
                      log.action?.includes('APPROVE') ? 'bg-green-100 text-green-700' :
                      'bg-blue-100 text-blue-700'
                    }`}>
                      {actionLabels[log.action] || log.action}
                    </span>
                  </td>
                  <td className="px-4 py-3 text-gray-500">{log.targetType}#{log.targetId}</td>
                  <td className="px-4 py-3 max-w-md truncate">{log.detail}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  )
}