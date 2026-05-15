export default function FilterBar({ filters, onChange, onReset }) {
  const handleChange = (key, value) => {
    onChange({ ...filters, [key]: value, page: 0 })
  }

  return (
    <div className="bg-white rounded-lg shadow-sm p-4 mb-4">
      <div className="grid grid-cols-2 md:grid-cols-4 lg:grid-cols-7 gap-3">
        <div>
          <label className="block text-xs text-gray-500 mb-1">城市</label>
          <select value={filters.city || ''} onChange={e => handleChange('city', e.target.value)}
            className="w-full border rounded px-2 py-1.5 text-sm">
            <option value="">全部</option>
            <option value="柳州">柳州</option>
            <option value="鄂尔多斯">鄂尔多斯</option>
          </select>
        </div>
        <div>
          <label className="block text-xs text-gray-500 mb-1">区域</label>
          <input value={filters.area || ''} onChange={e => handleChange('area', e.target.value)}
            placeholder="输入区域" className="w-full border rounded px-2 py-1.5 text-sm" />
        </div>
        <div>
          <label className="block text-xs text-gray-500 mb-1">行业</label>
          <input value={filters.category || ''} onChange={e => handleChange('category', e.target.value)}
            placeholder="输入行业" className="w-full border rounded px-2 py-1.5 text-sm" />
        </div>
        <div>
          <label className="block text-xs text-gray-500 mb-1">最小面积</label>
          <input type="number" value={filters.minSize || ''} onChange={e => handleChange('minSize', e.target.value)}
            placeholder="m²" className="w-full border rounded px-2 py-1.5 text-sm" />
        </div>
        <div>
          <label className="block text-xs text-gray-500 mb-1">最大面积</label>
          <input type="number" value={filters.maxSize || ''} onChange={e => handleChange('maxSize', e.target.value)}
            placeholder="m²" className="w-full border rounded px-2 py-1.5 text-sm" />
        </div>
        <div>
          <label className="block text-xs text-gray-500 mb-1">销售员</label>
          <input value={filters.salesperson || ''} onChange={e => handleChange('salesperson', e.target.value)}
            placeholder="输入姓名" className="w-full border rounded px-2 py-1.5 text-sm" />
        </div>
        <div>
          <label className="block text-xs text-gray-500 mb-1">关键词</label>
          <input value={filters.keyword || ''} onChange={e => handleChange('keyword', e.target.value)}
            placeholder="搜索地址/备注" className="w-full border rounded px-2 py-1.5 text-sm" />
        </div>
      </div>
      <div className="mt-3 flex gap-2">
        <button onClick={onReset} className="text-sm text-gray-500 hover:text-gray-700 px-3 py-1 border rounded">
          重置
        </button>
      </div>
    </div>
  )
}