export default function StatCard({ title, value, color = 'blue' }) {
  const colors = {
    blue: 'bg-blue-50 text-blue-600 border-blue-200',
    green: 'bg-green-50 text-green-600 border-green-200',
    purple: 'bg-purple-50 text-purple-600 border-purple-200',
    orange: 'bg-orange-50 text-orange-600 border-orange-200',
  }

  return (
    <div className={`rounded-lg border p-4 ${colors[color] || colors.blue}`}>
      <div className="text-sm opacity-70">{title}</div>
      <div className="text-2xl font-bold mt-1">{value}</div>
    </div>
  )
}