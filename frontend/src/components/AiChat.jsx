import { useState, useRef, useEffect } from 'react'
import { Button, Input, Space, Typography, Spin, message } from 'antd'
import { SendOutlined, CloseOutlined, RobotOutlined } from '@ant-design/icons'
import api from '../services/api.js'

const { Text } = Typography

export default function AiChat() {
  const [open, setOpen] = useState(false)
  const [messages, setMessages] = useState([{ role: 'ai', content: '你好！我是小博，你的客户数据管理助手，可以问我关于系统数据的问题，或者帮你生成跟进文案。' }])
  const [input, setInput] = useState('')
  const [loading, setLoading] = useState(false)
  const bottomRef = useRef(null)

  useEffect(() => { if (open) bottomRef.current?.scrollIntoView({ behavior: 'smooth' }) }, [messages, open])

  const send = async () => {
    if (!input.trim()) return
    const userMsg = input.trim()
    setMessages(prev => [...prev, { role: 'user', content: userMsg }])
    setInput('')
    setLoading(true)
    try {
      const res = await api.post('/ai/chat', { message: userMsg })
      setMessages(prev => [...prev, { role: 'ai', content: res.data.data.reply }])
    } catch (e) {
      setMessages(prev => [...prev, { role: 'ai', content: '抱歉，AI服务暂时不可用。' }])
    } finally {
      setLoading(false)
    }
  }

  return (
    <>
      {open && (
        <div style={{
          position: 'fixed', bottom: 80, right: 24, width: 380, height: 500,
          background: '#fff', borderRadius: 12, boxShadow: '0 4px 24px rgba(0,0,0,0.15)',
          display: 'flex', flexDirection: 'column', zIndex: 1000, overflow: 'hidden'
        }}>
          <div style={{ padding: '12px 16px', borderBottom: '1px solid #f0f0f0', display: 'flex', justifyContent: 'space-between', alignItems: 'center', background: '#1677ff', color: '#fff' }}>
            <Space><RobotOutlined /><Text strong style={{ color: '#fff' }}>小博</Text></Space>
            <Button type="text" size="small" icon={<CloseOutlined />} onClick={() => setOpen(false)} style={{ color: '#fff' }} />
          </div>
          <div style={{ flex: 1, overflow: 'auto', padding: 12, display: 'flex', flexDirection: 'column', gap: 8 }}>
            {messages.map((m, i) => (
              <div key={i} style={{ display: 'flex', justifyContent: m.role === 'user' ? 'flex-end' : 'flex-start' }}>
                <div style={{
                  maxWidth: '80%', padding: '8px 12px', borderRadius: 8, fontSize: 13, lineHeight: 1.5,
                  background: m.role === 'user' ? '#1677ff' : '#f0f2f5',
                  color: m.role === 'user' ? '#fff' : '#333',
                  whiteSpace: 'pre-wrap'
                }}>{m.content}</div>
              </div>
            ))}
            {loading && <div style={{ textAlign: 'center' }}><Spin size="small" /></div>}
            <div ref={bottomRef} />
          </div>
          <div style={{ padding: 12, borderTop: '1px solid #f0f0f0' }}>
            <Input.Search placeholder="问关于数据的问题..." enterButton={<SendOutlined />} value={input}
              onChange={e => setInput(e.target.value)} onSearch={send} loading={loading} />
          </div>
        </div>
      )}
      <Button type="primary" shape="circle" size="large" icon={<RobotOutlined />}
        onClick={() => setOpen(!open)}
        style={{ position: 'fixed', bottom: 24, right: 24, width: 48, height: 48, zIndex: 1000, boxShadow: '0 4px 12px rgba(22,119,255,0.4)' }} />
    </>
  )
}