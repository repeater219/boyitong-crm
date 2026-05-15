export default function Pagination({ page, totalPages, totalElements, onPageChange }) {
  if (totalPages <= 1) return null

  const pages = []
  const start = Math.max(0, page - 2)
  const end = Math.min(totalPages, page + 3)
  for (let i = start; i < end; i++) {
    pages.push(i)
  }

  return (
    <div className="flex items-center justify-between mt-4">
      <span className="text-sm text-gray-500">共 {totalElements} 条</span>
      <div className="flex gap-1">
        <button disabled={page === 0} onClick={() => onPageChange(page - 1)}
          className="px-3 py-1 text-sm border rounded disabled:opacity-30">上一页</button>
        {pages.map(p => (
          <button key={p} onClick={() => onPageChange(p)}
            className={`px-3 py-1 text-sm border rounded ${p === page ? 'bg-blue-600 text-white' : ''}`}>
            {p + 1}
          </button>
        ))}
        <button disabled={page >= totalPages - 1} onClick={() => onPageChange(page + 1)}
          className="px-3 py-1 text-sm border rounded disabled:opacity-30">下一页</button>
      </div>
    </div>
  )
}