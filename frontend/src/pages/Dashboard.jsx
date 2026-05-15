import { useState, useEffect } from 'react'
import { Row, Col, Card, Spin, Typography } from 'antd'
import { TeamOutlined, EnvironmentOutlined, AppstoreOutlined, UserOutlined } from '@ant-design/icons'
import { fetchStats } from '../services/api.js'
import BarChart from '../components/charts/BarChart.jsx'
import PieChart from '../components/charts/PieChart.jsx'

const { Title } = Typography

const GRADIENT_CARDS = [
  { title: '客户总数', icon: <TeamOutlined />, gradient: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)' },
  { title: '覆盖城市', icon: <EnvironmentOutlined />, gradient: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)' },
  { title: '行业分类', icon: <AppstoreOutlined />, gradient: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)' },
  { title: '销售团队', icon: <UserOutlined />, gradient: 'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)' },
]

export default function Dashboard() {
  const [stats, setStats] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)

  useEffect(() => {
    fetchStats()
      .then(setStats)
      .catch(e => setError(e.message))
      .finally(() => setLoading(false))
  }, [])

  if (loading) return <Spin style={{ display: 'block', margin: '100px auto' }} />
  if (error) return <Card>加载失败: {error}</Card>
  if (!stats) return null

  return (
    <div>
      <Title level={4} style={{ marginBottom: 20, color: '#374151' }}>数据概览</Title>
      <Row gutter={[16, 16]} style={{ marginBottom: 24 }}>
        {GRADIENT_CARDS.map((card, i) => {
          const value = [stats.totalCustomers, stats.cityCount, stats.categoryCount, stats.salespersonCount][i]
          return (
            <Col xs={12} sm={12} md={6} key={card.title}>
              <Card style={{ background: card.gradient, borderRadius: 12, border: 'none' }}
                bodyStyle={{ padding: '20px 24px' }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
                  <div>
                    <div style={{ color: 'rgba(255,255,255,0.7)', fontSize: 13, marginBottom: 4 }}>{card.title}</div>
                    <div style={{ color: '#fff', fontSize: 32, fontWeight: 700 }}>{value}</div>
                  </div>
                  <div style={{ fontSize: 28, color: 'rgba(255,255,255,0.4)' }}>{card.icon}</div>
                </div>
              </Card>
            </Col>
          )
        })}
      </Row>
      <Title level={4} style={{ marginBottom: 16, color: '#374151' }}>数据分析</Title>
      <Row gutter={[16, 16]}>
        <Col xs={24} md={12}><Card title="城市分布" style={{ borderRadius: 12 }}><BarChart data={stats.cityDistribution} title="客户数量" /></Card></Col>
        <Col xs={24} md={12}><Card title="行业分布" style={{ borderRadius: 12 }}><PieChart data={stats.categoryDistribution} title="行业占比" /></Card></Col>
        <Col xs={24} md={12}><Card title="销售员排名" style={{ borderRadius: 12 }}><BarChart data={stats.salespersonRanking} title="客户数量" horizontal /></Card></Col>
        <Col xs={24} md={12}><Card title="面积分布" style={{ borderRadius: 12 }}><BarChart data={stats.areaDistribution} title="数量" /></Card></Col>
      </Row>
    </div>
  )
}