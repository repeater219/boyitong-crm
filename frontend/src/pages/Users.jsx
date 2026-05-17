import { useState, useEffect, useCallback } from 'react'
import { Table, Button, Modal, Form, Input, Select, Space, Tag, message, Popconfirm } from 'antd'
import { PlusOutlined, EditOutlined, DeleteOutlined, UserOutlined } from '@ant-design/icons'
import api from '../services/api.js'

export default function Users() {
  const [users, setUsers] = useState([])
  const [roles, setRoles] = useState([])
  const [loading, setLoading] = useState(true)
  const [modalOpen, setModalOpen] = useState(false)
  const [editingUser, setEditingUser] = useState(null)
  const [form] = Form.useForm()

  const ROLE_COLORS = { ADMIN: 'red', MANAGER: 'blue', USER: 'green' }
  const ROLE_LABELS = { ADMIN: '管理员', MANAGER: '主管', USER: '业务员' }

  const load = useCallback(async () => {
    setLoading(true)
    try {
      const [usersRes, rolesRes] = await Promise.all([
        api.get('/users'),
        api.get('/users/roles')
      ])
      setUsers(usersRes.data.data || [])
      setRoles(rolesRes.data.data || [])
    } catch (e) {
      message.error('加载失败: ' + (e.response?.data?.message || e.message))
    } finally {
      setLoading(false)
    }
  }, [])

  useEffect(() => { load() }, [load])

  const openCreate = () => {
    setEditingUser(null)
    form.resetFields()
    setModalOpen(true)
  }

  const openEdit = (user) => {
    setEditingUser(user)
    form.setFieldsValue({
      username: user.username,
      displayName: user.displayName,
      role: user.role,
      password: '',
    })
    setModalOpen(true)
  }

  const handleSubmit = async () => {
    const values = form.getFieldsValue()
    try {
      if (editingUser) {
        const body = { displayName: values.displayName, role: values.role }
        if (values.password) body.password = values.password
        await api.put(`/users/${editingUser.id}`, body)
        message.success('用户已更新')
      } else {
        await api.post('/users', values)
        message.success('用户已创建')
      }
      setModalOpen(false)
      load()
    } catch (e) {
      message.error(e.response?.data?.message || e.message)
    }
  }

  const handleDelete = async (user) => {
    try {
      await api.delete(`/users/${user.id}`)
      message.success('用户已删除')
      load()
    } catch (e) {
      message.error(e.response?.data?.message || e.message)
    }
  }

  const columns = [
    { title: 'ID', dataIndex: 'id', width: 60 },
    { title: '用户名', dataIndex: 'username', width: 120 },
    { title: '显示名', dataIndex: 'displayName', width: 120 },
    {
      title: '角色', dataIndex: 'role', width: 100,
      render: (role) => <Tag color={ROLE_COLORS[role] || 'default'}>{ROLE_LABELS[role] || role}</Tag>
    },
    {
      title: '操作', width: 120,
      render: (_, record) => (
        <Space>
          <Button type="link" size="small" icon={<EditOutlined />}
            onClick={() => openEdit(record)}>编辑</Button>
          {record.username !== 'admin' && (
            <Popconfirm title="确定删除此用户？" onConfirm={() => handleDelete(record)}>
              <Button type="link" size="small" danger icon={<DeleteOutlined />}>删除</Button>
            </Popconfirm>
          )}
        </Space>
      )
    },
  ]

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 16 }}>
        <h1 style={{ fontSize: 20, fontWeight: 600, margin: 0 }}>用户管理</h1>
        <Button type="primary" icon={<PlusOutlined />} onClick={openCreate}>新增用户</Button>
      </div>

      <Table
        dataSource={users}
        columns={columns}
        rowKey="id"
        loading={loading}
        pagination={false}
        size="middle"
      />

      <Modal
        title={editingUser ? '编辑用户' : '新增用户'}
        open={modalOpen}
        onOk={handleSubmit}
        onCancel={() => setModalOpen(false)}
        okText="保存"
        cancelText="取消"
      >
        <Form form={form} layout="vertical">
          <Form.Item name="username" label="用户名" rules={editingUser ? [] : [{ required: true, message: '请输入用户名' }]}>
            <Input prefix={<UserOutlined />} placeholder="用户名" disabled={!!editingUser} />
          </Form.Item>
          <Form.Item name="displayName" label="显示名">
            <Input placeholder="显示名称" />
          </Form.Item>
          <Form.Item name="role" label="角色" rules={[{ required: true, message: '请选择角色' }]}>
            <Select>
              {roles.map(r => (
                <Select.Option key={r.name} value={r.name}>
                  {ROLE_LABELS[r.name] || r.name}
                </Select.Option>
              ))}
            </Select>
          </Form.Item>
          <Form.Item
            name="password"
            label={editingUser ? '新密码（留空不修改）' : '密码'}
            rules={editingUser ? [] : [{ required: true, message: '请输入密码' }]}
          >
            <Input.Password placeholder={editingUser ? '留空则不修改密码' : '密码'} />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  )
}