import { useEffect, useRef } from 'react'
import { Chart, registerables } from 'chart.js'

Chart.register(...registerables)

export default function PieChart({ data, title }) {
  const canvasRef = useRef(null)
  const chartRef = useRef(null)

  useEffect(() => {
    if (!canvasRef.current || !data) return

    if (chartRef.current) chartRef.current.destroy()

    const colors = [
      'rgba(59, 130, 246, 0.8)', 'rgba(16, 185, 129, 0.8)',
      'rgba(245, 158, 11, 0.8)', 'rgba(139, 92, 246, 0.8)',
      'rgba(239, 68, 68, 0.8)', 'rgba(236, 72, 153, 0.8)',
      'rgba(14, 165, 233, 0.8)', 'rgba(168, 85, 247, 0.8)',
    ]

    const ctx = canvasRef.current.getContext('2d')
    chartRef.current = new Chart(ctx, {
      type: 'doughnut',
      data: {
        labels: data.map(d => d.name),
        datasets: [{
          data: data.map(d => d.value),
          backgroundColor: colors.slice(0, data.length),
        }]
      },
      options: {
        responsive: true,
        plugins: {
          legend: { position: 'right' },
        }
      }
    })

    return () => { if (chartRef.current) chartRef.current.destroy() }
  }, [data, title])

  return <canvas ref={canvasRef} />
}