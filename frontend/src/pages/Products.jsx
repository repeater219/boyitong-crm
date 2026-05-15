import { useState, useEffect } from 'react'
import { Table, Button, Modal, Form, Input, InputNumber, Popconfirm, message, Tag, Card, Row, Col, Statistic } from 'antd'
import { PlusOutlined, DeleteOutlined } from '@ant-design/icons'
import api from '../services/api.js'

export default function Products() {
  const [data, setData] = useState([])
  const [loading, setLoading] = useState(true)
  const [open, setOpen] = useState(false)
  const [form] = Form.useForm()

  const load = () => { setLoading(true); api.get('/crm/products').then(r => setData(r.data.data || [])).finally(() => setLoading(false)) }
  useEffect(() => { load() }, [])

  const onCreate = async () => {
    const vals = await form.validateFields()
    await api.post('/crm/products', vals)
    message.success('产品已添加')
    setOpen(false); form.resetFields(); load()
  }

  const onDelete = async (id) => {
    await api.delete(`/crm/products/${id}`)
    message.success('已删除'); load()
  }

  const columns = [
    { title: '名称', dataIndex: 'name', key: 'name' },
    { title: '分类', dataIndex: 'category', key: 'category', render: v => <Tag>{v}</Tag> },
    { title: '单位', dataIndex: 'unit', key: 'unit' },
    { title: '单价', dataIndex: 'price', key: 'price', render: v => `¥${v?.toFixed(2)}` },
    { title: '描述', dataIndex: 'description', key: 'description', ellipsis: true },
    { title: '操作', key: 'action', render: (_, r) => <Popconfirm title="确定删除?" onConfirm={() => onDelete(r.id)}><Button type="link" danger icon={<DeleteOutlined />}>删除</Button></Popconfirm> },
  ]

  return (
    <div>
      <Row gutter={16} style={{ marginBottom: 16 }}>
        <Col span={6}><Card><Statistic title="产品总数" value={data.length} /></Card></Col>
      </Row>
      <div style={{ marginBottom: 16 }}><Button type="primary" icon={<PlusOutlined />} onClick={() => setOpen(true)}>添加产品</Button></div>
      <Table rowKey="id" columns={columns} dataSource={data} loading={loading} />
      <Modal title="添加产品" open={open} onOk={onCreate} onCancel={() => { setOpen(false); form.resetFields() }}>
        <Form form={form} layout="vertical">
          <Form.Item name="name" label="名称" rules={[{ required: true }]}><Input /></Form.Item>
          <Form.Item name="category" label="分类"><Input /></Form.Item>
          <Form.Item name="unit" label="单位"><Input /></Form.Item>
          <Form.Item name="price" label="单价"><InputNumber style={{ width: '100%' }} prefix="¥" /></Form.Item>
          <Form.Item name="description" label="描述"><Input.TextArea /></Form.Item>
        </Form>
      </Modal>
    </div>
  )
}