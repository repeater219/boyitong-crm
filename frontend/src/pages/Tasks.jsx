import { useState, useEffect } from 'react'
import api from '../services/api.js'

export default function TasksPage() {
  const [tasks, setTasks] = useState([])
  const [newTitle, setNewTitle] = useState('')
  const [newDesc, setNewDesc] = useState('')
  const [newDue, setNewDue] = useState('')
  const [loading, setLoading] = useState(true)
  const [pendingCount, setPendingCount] = useState(0)

  const load = async () => {
    try {
      const [tasksRes, countRes] = await Promise.all([
        api.get('/tasks'),
        api.get('/tasks/pending-count')
      ])
      setTasks(tasksRes.data.data || [])
      setPendingCount(countRes.data.data || 0)
    } catch (_) {}
    finally { setLoading(false) }
  }

  useEffect(() => { load() }, [])

  const createTask = async () => {
    if (!newTitle.trim()) return
    await api.post('/tasks', { title: newTitle.trim(), description: newDesc.trim(), dueDate: newDue })
    setNewTitle(''); setNewDesc(''); setNewDue('')
    load()
  }

  const completeTask = async (id) => {
    await api.post(`/tasks/${id}/complete`)
    load()
  }

  const deleteTask = async (id) => {
    if (!confirm('确定删除此任务？')) return
    await api.delete(`/tasks/${id}`)
    load()
  }

  if (loading) return <div className="text-center py-12 text-gray-400">加载中...</div>

  const activeTasks = tasks.filter(t => !t.completed)
  const doneTasks = tasks.filter(t => t.completed)

  return (
    <div>
      <div className="flex items-center justify-between mb-6">
        <h1 className="text-2xl font-bold">我的任务</h1>
        <span className="text-sm text-gray-500">待完成: {pendingCount}</span>
      </div>

      {/* Create Task */}
      <div className="bg-white rounded-lg shadow-sm p-4 mb-6">
        <h2 className="font-semibold mb-3">新建任务</h2>
        <div className="flex gap-2">
          <input value={newTitle} onChange={e => setNewTitle(e.target.value)}
            placeholder="任务标题" className="flex-1 border rounded px-3 py-2 text-sm" />
          <input value={newDue} onChange={e => setNewDue(e.target.value)}
            placeholder="截止日期" className="w-32 border rounded px-3 py-2 text-sm" />
          <button onClick={createTask} disabled={!newTitle.trim()}
            className="bg-blue-600 text-white px-4 py-2 rounded text-sm hover:bg-blue-700 disabled:opacity-50">
            创建
          </button>
        </div>
        <textarea value={newDesc} onChange={e => setNewDesc(e.target.value)}
          placeholder="任务描述（可选）" rows={2} className="w-full border rounded px-3 py-2 text-sm mt-2" />
      </div>

      {/* Active Tasks */}
      <div className="space-y-2 mb-6">
        <h2 className="font-semibold text-gray-600">待完成 ({activeTasks.length})</h2>
        {activeTasks.map(t => (
          <div key={t.id} className="bg-white rounded-lg shadow-sm p-4 flex items-start justify-between">
            <div className="flex-1">
              <div className="font-medium">{t.title}</div>
              {t.description && <div className="text-sm text-gray-500 mt-0.5">{t.description}</div>}
              <div className="text-xs text-gray-400 mt-1">
                {t.dueDate && <span className="mr-3">截止: {t.dueDate}</span>}
                {t.customerId && <span>关联客户: #{t.customerId}</span>}
              </div>
            </div>
            <div className="flex gap-2 ml-4">
              <button onClick={() => completeTask(t.id)} className="bg-green-600 text-white px-3 py-1 rounded text-xs hover:bg-green-700">完成</button>
              <button onClick={() => deleteTask(t.id)} className="text-red-500 text-xs hover:text-red-700">删除</button>
            </div>
          </div>
        ))}
        {activeTasks.length === 0 && <div className="text-center py-8 text-gray-400">暂无待办任务</div>}
      </div>

      {/* Completed Tasks */}
      {doneTasks.length > 0 && (
        <div>
          <h2 className="font-semibold text-gray-400 mb-2">已完成 ({doneTasks.length})</h2>
          <div className="space-y-1">
            {doneTasks.map(t => (
              <div key={t.id} className="bg-white rounded shadow-sm p-3 flex items-center gap-3 opacity-60">
                <span className="text-green-600 text-sm">✓</span>
                <span className="text-sm line-through">{t.title}</span>
                <span className="text-xs text-gray-400 ml-auto">{t.completedAt?.replace('T', ' ')}</span>
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  )
}