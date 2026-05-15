import { useState, useEffect, useRef } from 'react'
import { useNavigate } from 'react-router-dom'
import { fetchStats } from '../services/api.js'

export default function DataDashboard() {
  const [stats, setStats] = useState(null)
  const [time, setTime] = useState(new Date())
  const navigate = useNavigate()
  const intervalRef = useRef(null)

  useEffect(() => {
    const load = () => {
      fetchStats().then(setStats).catch(() => {})
      setTime(new Date())
    }
    load()
    intervalRef.current = setInterval(load, 10000) // auto refresh every 10s
    return () => clearInterval(intervalRef.current)
  }, [])

  const toggleFullscreen = () => {
    if (!document.fullscreenElement) {
      document.documentElement.requestFullscreen()
    } else {
      document.exitFullscreen()
    }
  }

  const colorClasses = [
    { bg: 'from-blue-500 to-blue-600', text: 'text-white' },
    { bg: 'from-green-500 to-green-600', text: 'text-white' },
    { bg: 'from-purple-500 to-purple-600', text: 'text-white' },
    { bg: 'from-orange-500 to-orange-600', text: 'text-white' },
  ]

  return (
    <div className="fixed inset-0 bg-gray-900 overflow-hidden z-50">
      {/* Header */}
      <div className="absolute top-0 left-0 right-0 flex items-center justify-between px-8 py-4 z-10">
        <div>
          <h1 className="text-2xl font-bold text-white">博易通数据大屏</h1>
          <p className="text-gray-400 text-sm">{time.toLocaleString('zh-CN')}</p>
        </div>
        <div className="flex gap-3">
          <button onClick={() => navigate('/dashboard')}
            className="px-4 py-2 bg-white/10 text-white rounded hover:bg-white/20 text-sm">
            返回系统
          </button>
          <button onClick={toggleFullscreen}
            className="px-4 py-2 bg-white/10 text-white rounded hover:bg-white/20 text-sm">
            全屏
          </button>
        </div>
      </div>

      {stats && (
        <div className="h-full flex flex-col items-center justify-center px-8 pt-20 pb-8">
          {/* KPI Cards */}
          <div className="grid grid-cols-4 gap-6 w-full mb-8">
            {[
              { title: '客户总数', value: stats.totalCustomers, idx: 0 },
              { title: '覆盖城市', value: stats.cityCount, idx: 1 },
              { title: '行业分类', value: stats.categoryCount, idx: 2 },
              { title: '销售团队', value: stats.salespersonCount, idx: 3 },
            ].map((item, i) => (
              <div key={item.title}
                className={`rounded-2xl p-6 bg-gradient-to-br ${colorClasses[i].bg} ${colorClasses[i].text}`}>
                <div className="text-lg opacity-80">{item.title}</div>
                <div className="text-5xl font-bold mt-2">{item.value}</div>
              </div>
            ))}
          </div>

          {/* Charts Row */}
          <div className="grid grid-cols-4 gap-6 w-full flex-1 min-h-0">
            <ChartCard title="城市分布" type="bar" data={stats.cityDistribution} color="rgba(59, 130, 246, 0.8)" />
            <ChartCard title="行业分布 (Top 10)" type="pie" data={stats.categoryDistribution?.slice(0, 10)} />
            <ChartCard title="销售员排名 (Top 10)" type="bar" data={stats.salespersonRanking?.slice(0, 10)} color="rgba(16, 185, 129, 0.8)" horizontal />
            <ChartCard title="面积分布" type="bar" data={stats.areaDistribution} color="rgba(245, 158, 11, 0.8)" />
          </div>
        </div>
      )}

      {!stats && (
        <div className="h-full flex items-center justify-center">
          <div className="text-gray-400 text-xl">加载中...</div>
        </div>
      )}
    </div>
  )
}

function ChartCard({ title, data, color = 'rgba(59, 130, 246, 0.8)', horizontal = false }) {
  const canvasRef = useRef(null)
  const chartRef = useRef(null)

  useEffect(() => {
    if (!canvasRef.current || !data || data.length === 0) return

    import('chart.js').then(({ Chart, registerables }) => {
      Chart.register(...registerables)
      if (chartRef.current) chartRef.current.destroy()

      const ctx = canvasRef.current.getContext('2d')
      const config = {
        type: 'bar',
        data: {
          labels: data.map(d => d.name),
          datasets: [{
            data: data.map(d => d.value),
            backgroundColor: color,
            borderColor: color.replace('0.8', '1'),
            borderWidth: 1,
          }]
        },
        options: {
          indexAxis: horizontal ? 'y' : 'x',
          responsive: true,
          maintainAspectRatio: false,
          plugins: {
            legend: { display: false },
          },
          scales: {
            x: { ticks: { color: '#9CA3AF' } },
            y: { beginAtZero: true, ticks: { stepSize: 1, color: '#9CA3AF' } },
          },
        }
      }

      chartRef.current = new Chart(ctx, config)
    })

    return () => { if (chartRef.current) chartRef.current.destroy() }
  }, [data, color, horizontal])

  return (
    <div className="bg-gray-800 rounded-2xl p-4 flex flex-col overflow-hidden">
      <h3 className="text-white font-medium mb-3 text-sm">{title}</h3>
      <div className="flex-1 min-h-0">
        <canvas ref={canvasRef} />
      </div>
    </div>
  )
}