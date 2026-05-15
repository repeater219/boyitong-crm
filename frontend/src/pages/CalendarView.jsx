import { useState, useEffect } from 'react'
import { Card, Badge, Calendar, Typography, Spin } from 'antd'
import api from '../services/api.js'

const { Text } = Typography

export default function CalendarView() {
  const [tasks, setTasks] = useState([])
  const [followUps, setFollowUps] = useState([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    Promise.all([
      api.get('/tasks').then(r => setTasks(r.data.data || [])),
      api.get('/follow-ups/customer/0').catch(() => {}),
    ]).finally(() => setLoading(false))
  }, [])

  const dateCellRender = (value) => {
    const dateStr = value.format('YYYY-MM-DD')
    const dayTasks = tasks.filter(t => t.dueDate === dateStr && !t.completed)
    return (
      <div>
        {dayTasks.map(t => <Badge key={t.id} status="processing" text={<Text style={{ fontSize: 12 }}>{t.title}</Text>} />)}
      </div>
    )
  }

  if (loading) return <Spin style={{ display: 'block', margin: '100px auto' }} />
  return (
    <Card title="日程视图">
      <Calendar cellRender={dateCellRender} />
    </Card>
  )
}