import { useState } from 'react'
import api from '../services/api.js'

export default function ImportPage() {
  const [file, setFile] = useState(null)
  const [city, setCity] = useState('')
  const [uploading, setUploading] = useState(false)
  const [result, setResult] = useState(null)
  const [error, setError] = useState(null)

  const handleFileChange = (e) => {
    const f = e.target.files[0]
    setFile(f)
    // Auto-detect city from file name
    if (f) {
      const name = f.name.replace(/\.[^.]+$/, '')
      if (name.includes('柳州')) setCity('柳州')
      else if (name.includes('鄂尔多斯')) setCity('鄂尔多斯')
      else setCity(name)
    }
  }

  const handleUpload = async () => {
    if (!file || !city.trim()) return
    setUploading(true)
    setError(null)
    setResult(null)
    try {
      const formData = new FormData()
      formData.append('file', file)
      formData.append('city', city.trim())
      const res = await api.post('/uploads/submit', formData, {
        headers: { 'Content-Type': 'multipart/form-data' }
      })
      setResult(res.data.data)
      setFile(null)
    } catch (e) {
      setError(e.response?.data?.message || e.message)
    } finally {
      setUploading(false)
    }
  }

  return (
    <div>
      <h1 className="text-2xl font-bold mb-6">导入数据</h1>

      <div className="bg-white rounded-lg shadow-sm p-6 max-w-xl">
        <div className="mb-4">
          <label className="block text-sm text-gray-500 mb-1">上传说明</label>
          <p className="text-xs text-gray-400">
            上传后数据将进入待审核状态，需管理员审核通过后才会更新到系统数据中。
            选择文件后系统会自动识别城市名，你也可以手动修改。
          </p>
        </div>

        <div className="mb-4">
          <label className="block text-sm text-gray-500 mb-1">选择 Excel 文件</label>
          <input type="file" accept=".xlsx" onChange={handleFileChange}
            className="border rounded px-3 py-2 w-full text-sm" />
        </div>

        <div className="mb-4">
          <label className="block text-sm text-gray-500 mb-1">城市名称</label>
          <input value={city} onChange={e => setCity(e.target.value)}
            placeholder="自动从文件名识别"
            className="border rounded px-3 py-2 w-full text-sm" />
        </div>

        <button onClick={handleUpload} disabled={!file || !city.trim() || uploading}
          className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700 disabled:opacity-50">
          {uploading ? '上传中...' : '提交审核'}
        </button>

        {result && (
          <div className="mt-4 p-3 bg-green-50 text-green-700 rounded text-sm">{result}</div>
        )}
        {error && (
          <div className="mt-4 p-3 bg-red-50 text-red-600 rounded text-sm">{error}</div>
        )}
      </div>
    </div>
  )
}