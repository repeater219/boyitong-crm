import { useState, useEffect } from 'react'
import { Card, Form, Input, Button, Typography, message, Space } from 'antd'
import { UserOutlined, LockOutlined } from '@ant-design/icons'
import { useAuth } from '../services/AuthContext.jsx'
import Logo from '../components/Logo.jsx'

const { Title, Text } = Typography

const FEATURES = [
  '客户管理 · 商机跟踪 · 合同回款',
  '销售漏斗 · 数据大屏 · AI 助手',
  '上传审核 · 权限管理 · 操作日志',
]

export default function LoginPage() {
  const { login } = useAuth()
  const [loading, setLoading] = useState(false)
  const [featureIdx, setFeatureIdx] = useState(0)
  const [fade, setFade] = useState(true)

  // Rotating feature text
  useEffect(() => {
    const interval = setInterval(() => {
      setFade(false)
      setTimeout(() => {
        setFeatureIdx(prev => (prev + 1) % FEATURES.length)
        setFade(true)
      }, 400)
    }, 3000)
    return () => clearInterval(interval)
  }, [])

  const handleSubmit = async (values) => {
    setLoading(true)
    try {
      await login(values.username, values.password)
    } catch (err) {
      message.error(err.response?.data?.message || '登录失败')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div style={{ minHeight: '100vh', display: 'flex', background: '#0f0f23' }}>
      {/* Left: Brand with dynamic background */}
      <div style={{
        flex: '0 0 480px',
        background: 'linear-gradient(135deg, #1e1b4b 0%, #312e81 50%, #4338ca 100%)',
        display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center',
        padding: 48, position: 'relative', overflow: 'hidden',
      }}>
        {/* Animated gradient orbs */}
        <div className="orb orb-1" />
        <div className="orb orb-2" />
        <div className="orb orb-3" />

        {/* Floating particles */}
        {[...Array(12)].map((_, i) => (
          <div key={i} className="particle" style={{
            left: `${10 + Math.random() * 80}%`,
            top: `${10 + Math.random() * 80}%`,
            width: `${4 + Math.random() * 6}px`,
            height: `${4 + Math.random() * 6}px`,
            animationDelay: `${Math.random() * 8}s`,
            animationDuration: `${6 + Math.random() * 6}s`,
          }} />
        ))}

        <div style={{ position: 'relative', zIndex: 1, textAlign: 'center' }}>
          <Logo size={64} />
          <Title level={2} style={{ color: '#fff', marginTop: 24, marginBottom: 8, letterSpacing: 2 }}>博易通 CRM</Title>
          <Text style={{ color: 'rgba(255,255,255,0.6)', fontSize: 15, display: 'block', letterSpacing: 4 }}>客户数据管理系统</Text>

          {/* Rotating feature text */}
          <div style={{ marginTop: 64, height: 48, display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
            <div className={`feature-fade ${fade ? 'visible' : ''}`} style={{ fontSize: 16 }}>
              <Text style={{ color: 'rgba(255,255,255,0.8)', fontSize: 16, letterSpacing: 1 }}>{FEATURES[featureIdx]}</Text>
            </div>
          </div>
        </div>

        <style>{`
          @keyframes float {
            0%, 100% { transform: translate(0, 0) scale(1); opacity: 0.15; }
            33% { transform: translate(30px, -20px) scale(1.1); opacity: 0.2; }
            66% { transform: translate(-20px, 10px) scale(0.9); opacity: 0.1; }
          }
          .orb-1 {
            position: absolute; top: -150px; right: -80px;
            width: 400px; height: 400px; border-radius: 50%;
            background: radial-gradient(circle, rgba(99,102,241,0.25) 0%, transparent 70%);
            animation: float 12s ease-in-out infinite;
          }
          .orb-2 {
            position: absolute; bottom: -100px; left: -100px;
            width: 350px; height: 350px; border-radius: 50%;
            background: radial-gradient(circle, rgba(139,92,246,0.2) 0%, transparent 70%);
            animation: float 15s ease-in-out infinite reverse;
          }
          .orb-3 {
            position: absolute; top: 40%; left: 50%;
            width: 250px; height: 250px; border-radius: 50%;
            background: radial-gradient(circle, rgba(168,85,247,0.15) 0%, transparent 70%);
            animation: float 10s ease-in-out infinite 2s;
          }
          @keyframes drift {
            0%, 100% { transform: translateY(0) translateX(0); opacity: 0; }
            20% { opacity: 0.6; }
            80% { opacity: 0.6; }
            100% { transform: translateY(-100px) translateX(20px); opacity: 0; }
          }
          .particle {
            position: absolute; border-radius: 50%;
            background: rgba(255,255,255,0.3);
            animation: drift 12s ease-in-out infinite;
            pointer-events: none;
          }
          .feature-fade {
            transition: opacity 0.4s ease, transform 0.4s ease;
            opacity: 0; transform: translateY(8px);
          }
          .feature-fade.visible {
            opacity: 1; transform: translateY(0);
          }
        `}</style>
      </div>

      {/* Right: Form with entrance animation */}
      <div style={{
        flex: 1, display: 'flex', alignItems: 'center', justifyContent: 'center',
        background: 'linear-gradient(135deg, #f8fafc 0%, #eef2ff 100%)',
      }}>
        <Card className="login-card" style={{
          width: 380,
          boxShadow: '0 8px 32px rgba(0,0,0,0.08)',
          borderRadius: 16, border: 'none',
        }}>
          <div style={{ textAlign: 'center', marginBottom: 32 }}>
            <Title level={3} style={{ margin: 0, color: '#1e1b4b' }}>欢迎回来</Title>
            <Text type="secondary" style={{ fontSize: 14 }}>请输入账号密码登录系统</Text>
          </div>
          <Form onFinish={handleSubmit} size="large" layout="vertical">
            <Form.Item name="username" rules={[{ required: true, message: '请输入用户名' }]}>
              <Input
                prefix={<UserOutlined style={{ color: '#9ca3af' }} />}
                placeholder="用户名"
                className="login-input"
                style={{ borderRadius: 10, height: 48 }}
              />
            </Form.Item>
            <Form.Item name="password" rules={[{ required: true, message: '请输入密码' }]}>
              <Input.Password
                prefix={<LockOutlined style={{ color: '#9ca3af' }} />}
                placeholder="密码"
                className="login-input"
                style={{ borderRadius: 10, height: 48 }}
              />
            </Form.Item>
            <Form.Item>
              <Button type="primary" htmlType="submit" loading={loading} block size="large"
                style={{ borderRadius: 10, height: 48, fontSize: 16 }}>登 录</Button>
            </Form.Item>
          </Form>
        </Card>

        <style>{`
          @keyframes slideUp {
            from { opacity: 0; transform: translateY(24px); }
            to { opacity: 1; transform: translateY(0); }
          }
          .login-card { animation: slideUp 0.6s ease-out; }
          .login-input { transition: all 0.3s ease; }
          .login-input:hover, .login-input:focus {
            box-shadow: 0 0 0 3px rgba(99,102,241,0.15);
          }
        `}</style>
      </div>
    </div>
  )
}

function FeatureItem({ text }) {
  return (
    <div style={{ display: 'flex', alignItems: 'center', gap: 12, marginBottom: 16 }}>
      <div style={{ width: 8, height: 8, borderRadius: '50%', background: '#818cf8', flexShrink: 0 }} />
      <Text style={{ color: 'rgba(255,255,255,0.7)', fontSize: 14 }}>{text}</Text>
    </div>
  )
}