import { useState, useEffect } from 'react'
import { Table, Button, Modal, Form, Input, InputNumber, Select, Tag, message, Card, Row, Col, Statistic, Steps } from 'antd'
import { PlusOutlined } from '@ant-design/icons'
import api from '../services/api.js'

const STAGE_CONFIG = {
  INTENT: { label: '意向阶段', color: 'blue' },
  PROPOSAL: { label: '方案阶段', color: 'cyan' },
  QUOTATION: { label: '报价阶段', color: 'orange' },
  NEGOTIATION: { label: '谈判阶段', color: 'purple' },
  WON: { label: '已赢单', color: 'green' },
  LOST: { label: '已输单', color: 'red' },
}

export default function Opportunities() {
  const [data, setData] = useState([])
  const [loading, setLoading] = useState(true)
  const [open, setOpen] = useState(false)
  const [form] = Form.useForm()
  const [customers, setCustomers] = useState([])

  const load = async () => {
    setLoading(true)
    const [opps, custs] = await Promise.all([
      api.get('/crm/opportunities'),
      api.get('/customers', { params: { size: 999 } })
    ])
    setData(opps.data.data || [])
    setCustomers(custs.data.data?.content || [])
    setLoading(false)
  }
  useEffect(() => { load() }, [])

  const onCreate = async () => {
    const vals = await form.validateFields()
    await api.post('/crm/opportunities', vals)
    message.success('商机已创建')
    setOpen(false); form.resetFields(); load()
  }

  const onChangeStage = async (id, stage) => {
    await api.put(`/crm/opportunities/${id}`, { stage })
    message.success('阶段已更新')
    load()
  }

  const totalAmount = data.filter(d => d.stage !== 'LOST').reduce((s, d) => s + (d.amount || 0), 0)
  const wonAmount = data.filter(d => d.stage === 'WON').reduce((s, d) => s + (d.amount || 0), 0)

  const columns = [
    { title: '商机名称', dataIndex: 'name', key: 'name' },
    { title: '金额', dataIndex: 'amount', key: 'amount', render: v => `¥${(v || 0).toFixed(2)}`, sorter: (a, b) => (a.amount || 0) - (b.amount || 0) },
    { title: '阶段', dataIndex: 'stage', key: 'stage',
      render: (v, r) => (
        <Select value={v} onChange={s => onChangeStage(r.id, s)} size="small" style={{ width: 120 }}
          options={Object.entries(STAGE_CONFIG).map(([k, v]) => ({ value: k, label: v.label }))} />
      )
    },
    { title: '赢单率', dataIndex: 'winRate', key: 'winRate', render: v => `${v || 0}%` },
    { title: '客户ID', dataIndex: 'customerId', key: 'customerId' },
  ]

  return (
    <div>
      <Row gutter={16} style={{ marginBottom: 16 }}>
        <Col span={6}><Card><Statistic title="商机总数" value={data.length} /></Card></Col>
        <Col span={6}><Card><Statistic title="商机总额" value={totalAmount} prefix="¥" precision={2} /></Card></Col>
        <Col span={6}><Card><Statistic title="已赢单金额" value={wonAmount} prefix="¥" precision={2} /></Card></Col>
      </Row>
      <div style={{ marginBottom: 16 }}><Button type="primary" icon={<PlusOutlined />} onClick={() => setOpen(true)}>新建商机</Button></div>
      <Table rowKey="id" columns={columns} dataSource={data} loading={loading} />
      <Modal title="新建商机" open={open} onOk={onCreate} onCancel={() => { setOpen(false); form.resetFields() }}>
        <Form form={form} layout="vertical">
          <Form.Item name="name" label="商机名称" rules={[{ required: true }]}><Input /></Form.Item>
          <Form.Item name="customerId" label="关联客户" rules={[{ required: true }]}>
            <Select showSearch optionFilterProp="label"
              options={customers.map(c => ({ value: c.id, label: `#${c.id} ${c.address?.slice(0, 20)}` }))} />
          </Form.Item>
          <Form.Item name="amount" label="预计金额"><InputNumber style={{ width: '100%' }} prefix="¥" /></Form.Item>
          <Form.Item name="stage" label="阶段" initialValue="INTENT">
            <Select options={Object.entries(STAGE_CONFIG).map(([k, v]) => ({ value: k, label: v.label }))} />
          </Form.Item>
          <Form.Item name="description" label="描述"><Input.TextArea /></Form.Item>
        </Form>
      </Modal>
    </div>
  )
}