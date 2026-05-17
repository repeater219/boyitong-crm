import { useState, useEffect } from 'react'
import { useNavigate, useLocation } from 'react-router-dom'
import { Layout, Menu, Button, Avatar, Badge, Dropdown, Space, Typography, theme } from 'antd'
import {
  DashboardOutlined, TeamOutlined, DatabaseOutlined, CheckSquareOutlined,
  UploadOutlined, AuditOutlined, FileTextOutlined, ShoppingCartOutlined,
  DollarOutlined, BellOutlined, BulbOutlined,
  CalendarOutlined, LogoutOutlined, MenuFoldOutlined, MenuUnfoldOutlined,
  UserOutlined, SettingOutlined, SafetyOutlined
} from '@ant-design/icons'
import { useAuth } from '../services/AuthContext.jsx'
import api from '../services/api.js'
import AiChat from './AiChat.jsx'
import Logo from './Logo.jsx'

const { Header, Sider, Content } = Layout
const { Text } = Typography

const SIDEBAR_WIDTH = 240
const SIDEBAR_COLLAPSED = 64

export default function AppLayout({ children }) {
  const { user, logout } = useAuth()
  const navigate = useNavigate()
  const location = useLocation()
  const isAdmin = user?.role === 'ADMIN'
  const [collapsed, setCollapsed] = useState(false)
  const [notifications, setNotifications] = useState([])
  const [unreadCount, setUnreadCount] = useState(0)

  useEffect(() => {
    if (!user) return
    const fetchNotifs = () => {
      api.get('/notifications/unread').then(r => setNotifications(r.data.data || [])).catch(() => {})
      api.get('/notifications/unread-count').then(r => setUnreadCount(r.data.data || 0)).catch(() => {})
    }
    fetchNotifs()
    const interval = setInterval(fetchNotifs, 30000)
    return () => clearInterval(interval)
  }, [user])

  const menuItems = [
    { key: '/dashboard', icon: <DashboardOutlined />, label: '仪表盘' },
    { key: '/customers', icon: <TeamOutlined />, label: '客户管理' },
    { key: '/opportunities', icon: <BulbOutlined />, label: '商机管理' },
    { key: '/contracts', icon: <FileTextOutlined />, label: '合同管理' },
    { key: '/products', icon: <ShoppingCartOutlined />, label: '产品管理' },
    { key: '/tasks', icon: <CheckSquareOutlined />, label: '任务中心' },
    { key: '/calendar', icon: <CalendarOutlined />, label: '日程视图' },
    { type: 'divider' },
    { key: '/import', icon: <UploadOutlined />, label: '导入数据' },
    ...(isAdmin ? [
      { key: '/review', icon: <AuditOutlined />, label: '审核管理' },
      { key: '/audit-logs', icon: <DatabaseOutlined />, label: '操作日志' },
      { key: '/users', icon: <SafetyOutlined />, label: '用户管理' },
    ] : []),
    { key: '/announcements', icon: <BellOutlined />, label: '公告中心' },
  ]

  const notifItems = notifications.length === 0
    ? [{ key: 'empty', label: '暂无通知', disabled: true }]
    : [
        ...notifications.slice(0, 5).map(n => ({
          key: n.id, label: (
            <div style={{ maxWidth: 280 }}>
              <div style={{ fontWeight: 600, fontSize: 13 }}>{n.title}</div>
              <Text type="secondary" style={{ fontSize: 12 }}>{n.content}</Text>
            </div>
          )
        })),
        { type: 'divider' },
        { key: 'clear', label: <Button type="link" size="small" onClick={() => api.post('/notifications/read-all').then(() => { setNotifications([]); setUnreadCount(0) })} block>全部已读</Button> }
      ]

  const userMenuItems = [
    { key: 'profile', icon: <UserOutlined />, label: '个人中心', onClick: () => navigate('/profile') },
    { type: 'divider' },
    { key: 'logout', icon: <LogoutOutlined />, label: '退出登录', onClick: logout, danger: true },
  ]

  return (
    <Layout style={{ minHeight: '100vh' }}>
      <Sider
        trigger={null}
        collapsible
        collapsed={collapsed}
        width={SIDEBAR_WIDTH}
        collapsedWidth={SIDEBAR_COLLAPSED}
        style={{
          background: 'linear-gradient(180deg, #1e1b4b 0%, #312e81 30%, #3730a3 60%, #4338ca 100%)',
          position: 'fixed',
          left: 0,
          top: 0,
          bottom: 0,
          zIndex: 100,
          overflow: 'auto',
        }}
      >
        {/* Logo area */}
        <div style={{ height: 64, display: 'flex', alignItems: 'center', justifyContent: 'center', borderBottom: '1px solid rgba(255,255,255,0.08)' }}>
          <Logo collapsed={collapsed} />
        </div>

        {/* Menu */}
        <Menu
          mode="inline"
          selectedKeys={[location.pathname]}
          defaultOpenKeys={[]}
          items={menuItems}
          onClick={({ key }) => navigate(key)}
          style={{
            background: 'transparent',
            borderRight: 0,
            marginTop: 8,
          }}
          theme="dark"
        />

        {/* User section at bottom */}
        {!collapsed && (
          <div style={{
            position: 'absolute',
            bottom: 0,
            left: 0,
            right: 0,
            padding: '12px 16px',
            borderTop: '1px solid rgba(255,255,255,0.08)',
            display: 'flex',
            alignItems: 'center',
            gap: 10,
            background: 'rgba(0,0,0,0.2)',
          }}>
            <Avatar size={32} src={user?.avatarUrl || null} icon={!user?.avatarUrl ? <UserOutlined /> : null}
              style={{ backgroundColor: '#6366f1', flexShrink: 0 }} />
            <div style={{ flex: 1, overflow: 'hidden' }}>
              <div style={{ color: '#fff', fontSize: 13, fontWeight: 500, whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis' }}>
                {user?.displayName || user?.username}
              </div>
              <div style={{ color: 'rgba(255,255,255,0.5)', fontSize: 11 }}>
                {isAdmin ? '管理员' : '业务员'}
              </div>
            </div>
            <Button type="text" size="small" icon={<SettingOutlined style={{ color: 'rgba(255,255,255,0.5)' }} />}
              onClick={() => navigate('/profile')} />
          </div>
        )}
      </Sider>

      <Layout style={{ marginLeft: collapsed ? SIDEBAR_COLLAPSED : SIDEBAR_WIDTH, transition: 'margin-left 0.2s' }}>
        {/* Glassmorphism header */}
        <Header style={{
          padding: '0 24px',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'space-between',
          background: 'rgba(255,255,255,0.85)',
          backdropFilter: 'blur(12px)',
          WebkitBackdropFilter: 'blur(12px)',
          borderBottom: '1px solid rgba(0,0,0,0.06)',
          position: 'sticky',
          top: 0,
          zIndex: 99,
          height: 64,
        }}>
          <Button type="text" icon={collapsed ? <MenuUnfoldOutlined /> : <MenuFoldOutlined />}
            onClick={() => setCollapsed(!collapsed)}
            style={{ fontSize: 18, color: '#4b5563' }} />

          <Space size="middle">
            <Dropdown menu={{ items: notifItems }} trigger={['click']}>
              <Badge count={unreadCount} size="small" offset={[-2, 2]}>
                <Button type="text" icon={<BellOutlined style={{ fontSize: 18 }} />}
                  style={{ color: '#4b5563', position: 'relative' }} />
              </Badge>
            </Dropdown>

            <Dropdown menu={{ items: userMenuItems }} trigger={['click']}>
              <Space style={{ cursor: 'pointer', padding: '4px 8px', borderRadius: 8, transition: 'background 0.2s' }}
                className="user-trigger">
                <Avatar size={32} src={user?.avatarUrl || null} icon={!user?.avatarUrl ? <UserOutlined /> : null}
                  style={{ backgroundColor: '#6366f1' }} />
                <div style={{ lineHeight: 1.3 }}>
                  <div style={{ fontSize: 13, fontWeight: 500, color: '#1f2937' }}>{user?.displayName || user?.username}</div>
                  <div style={{ fontSize: 11, color: '#9ca3af' }}>{isAdmin ? '管理员' : '业务员'}</div>
                </div>
              </Space>
            </Dropdown>
          </Space>
        </Header>

        {/* Content */}
        <Content style={{ margin: 0, padding: 24, minHeight: 280, background: '#f5f5f5' }}>
          {children}
        </Content>
      </Layout>
      <AiChat />
    </Layout>
  )
}