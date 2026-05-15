import { useState, useEffect } from 'react'
import { Card, Form, Input, Button, message, Spin, Descriptions, Divider, Upload, Avatar, Badge } from 'antd'
import { UserOutlined, LockOutlined, CameraOutlined } from '@ant-design/icons'
import { useAuth } from '../services/AuthContext.jsx'
import api from '../services/api.js'

export default function Profile() {
  const { refreshUser } = useAuth()
  const [profile, setProfile] = useState(null)
  const [loading, setLoading] = useState(true)
  const [saving, setSaving] = useState(false)
  const [changingPwd, setChangingPwd] = useState(false)
  const [form] = Form.useForm()
  const [pwdForm] = Form.useForm()

  useEffect(() => {
    api.get('/profile').then(r => {
      setProfile(r.data.data)
      form.setFieldsValue({ displayName: r.data.data.displayName })
    }).catch(() => message.error('加载失败'))
    .finally(() => setLoading(false))
  }, [])

  const saveProfile = async (values) => {
    setSaving(true)
    try {
      const res = await api.put('/profile', values)
      setProfile(res.data.data)
      message.success('已保存')
    } catch (e) {
      message.error('保存失败')
    } finally {
      setSaving(false)
    }
  }

  const changePassword = async (values) => {
    setChangingPwd(true)
    try {
      await api.put('/profile/password', values)
      message.success('密码已修改')
      pwdForm.resetFields()
    } catch (e) {
      message.error(e.response?.data?.message || '修改失败')
    } finally {
      setChangingPwd(false)
    }
  }

  const handleAvatarUpload = async (file) => {
    try {
      const formData = new FormData()
      formData.append('file', file)
      const res = await api.post('/profile/avatar', formData)
      setProfile(res.data.data)
      refreshUser()
      message.success('头像已更新')
    } catch (e) {
      message.error('上传失败: ' + (e.response?.data?.message || e.message))
    }
    return false
  }

  if (loading) return <Spin style={{ display: 'block', margin: '100px auto' }} />

  return (
    <div style={{ maxWidth: 600, margin: '0 auto' }}>
      <Card title={<span><UserOutlined /> 个人资料</span>} style={{ marginBottom: 24 }}>
        <div style={{ textAlign: 'center', marginBottom: 24 }}>
          <Upload showUploadList={false} beforeUpload={handleAvatarUpload} accept="image/*">
            <Badge offset={[-8, 80]} count={<Avatar size="small" icon={<CameraOutlined />} style={{ backgroundColor: '#1677ff' }} />}>
              <Avatar size={96} src={profile?.avatarUrl || null}
                icon={!profile?.avatarUrl ? <UserOutlined /> : null} />
            </Badge>
          </Upload>
          <div style={{ marginTop: 8, fontSize: 12, color: '#999' }}>点击头像上传</div>
        </div>

        <Descriptions column={1}>
          <Descriptions.Item label="用户名">{profile?.username}</Descriptions.Item>
          <Descriptions.Item label="角色">{profile?.role === 'ADMIN' ? '管理员' : '业务员'}</Descriptions.Item>
          <Descriptions.Item label="显示名">{profile?.displayName}</Descriptions.Item>
        </Descriptions>
        <Divider />
        <Form form={form} layout="inline" onFinish={saveProfile}>
          <Form.Item name="displayName" label="修改显示名">
            <Input />
          </Form.Item>
          <Form.Item>
            <Button type="primary" htmlType="submit" loading={saving}>保存</Button>
          </Form.Item>
        </Form>
      </Card>

      <Card title={<span><LockOutlined /> 修改密码</span>}>
        <Form form={pwdForm} layout="vertical" onFinish={changePassword}>
          <Form.Item name="oldPassword" label="原密码" rules={[{ required: true }]}>
            <Input.Password />
          </Form.Item>
          <Form.Item name="newPassword" label="新密码" rules={[{ required: true, min: 6, message: '密码至少6位' }]}>
            <Input.Password />
          </Form.Item>
          <Form.Item>
            <Button type="primary" htmlType="submit" loading={changingPwd}>修改密码</Button>
          </Form.Item>
        </Form>
      </Card>
    </div>
  )
}