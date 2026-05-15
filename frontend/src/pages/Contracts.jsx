import { useState, useEffect } from 'react'
import { Table, Button, Modal, Form, Input, InputNumber, Select, Tag, message, Card, Row, Col, Statistic, DatePicker } from 'antd'
import { PlusOutlined, DollarOutlined } from '@ant-design/icons'
import dayjs from 'dayjs'
import api from '../services/api.js'

const STATUS_MAP = { DRAFT: { label: '草稿', color: 'default' }, ACTIVE: { label: '执行中', color: 'processing' }, COMPLETED: { label: '已完成', color: 'success' }, TERMINATED: { label: '已终止', color: 'error' } }

export default function Contracts() {
  const [data, setData] = useState([])
  const [loading, setLoading] = useState(true)
  const [open, setOpen] = useState(false)
  const [payOpen, setPayOpen] = useState(false)
  const [currentContract, setCurrentContract] = useState(null)
  const [payments, setPayments] = useState([])
  const [form] = Form.useForm()
  const [payForm] = Form.useForm()

  const load = () => { setLoading(true); api.get('/crm/contracts').then(r => setData(r.data.data || [])).finally(() => setLoading(false)) }
  useEffect(() => { load() }, [])

  const onCreate = async () => {
    const vals = await form.validateFields()
    await api.post('/crm/contracts', { ...vals, startDate: vals.startDate?.format('YYYY-MM-DD'), endDate: vals.endDate?.format('YYYY-MM-DD') })
    message.success('合同已创建')
    setOpen(false); form.resetFields(); load()
  }

  const onStatus = async (id, status) => { await api.put(`/crm/contracts/${id}/status`, { status }); message.success('状态已更新'); load() }

  const showPayments = async (contract) => {
    setCurrentContract(contract)
    const res = await api.get('/crm/payments', { params: { contractId: contract.id } })
    setPayments(res.data.data || [])
    setPayOpen(true)
  }

  const addPayment = async () => {
    const vals = await payForm.validateFields()
    await api.post('/crm/payments', { ...vals, contractId: currentContract.id, planDate: vals.planDate?.format('YYYY-MM-DD') })
    message.success('回款计划已添加')
    payForm.resetFields()
    const res = await api.get('/crm/payments', { params: { contractId: currentContract.id } })
    setPayments(res.data.data || [])
  }

  const columns = [
    { title: '合同编号', dataIndex: 'contractNo', key: 'contractNo' },
    { title: '名称', dataIndex: 'name', key: 'name' },
    { title: '金额', dataIndex: 'amount', key: 'amount', render: v => `¥${(v || 0).toFixed(2)}` },
    { title: '状态', dataIndex: 'status', key: 'status', render: (v, r) => (
      <Select value={v || 'DRAFT'} onChange={s => onStatus(r.id, s)} size="small" style={{ width: 100 }}
        options={Object.entries(STATUS_MAP).map(([k, v]) => ({ value: k, label: v.label }))} />
    )},
    { title: '有效期', key: 'period', render: (_, r) => r.startDate && r.endDate ? `${r.startDate} ~ ${r.endDate}` : '-' },
    { title: '回款', key: 'payment', render: (_, r) => <Button size="small" icon={<DollarOutlined />} onClick={() => showPayments(r)}>回款</Button> },
  ]

  const totalAmount = data.reduce((s, d) => s + (d.amount || 0), 0)

  return (
    <div>
      <Row gutter={16} style={{ marginBottom: 16 }}>
        <Col span={6}><Card><Statistic title="合同总数" value={data.length} /></Card></Col>
        <Col span={6}><Card><Statistic title="合同总额" value={totalAmount} prefix="¥" precision={2} /></Card></Col>
      </Row>
      <div style={{ marginBottom: 16 }}><Button type="primary" icon={<PlusOutlined />} onClick={() => setOpen(true)}>新建合同</Button></div>
      <Table rowKey="id" columns={columns} dataSource={data} loading={loading} />
      <Modal title="新建合同" open={open} onOk={onCreate} onCancel={() => { setOpen(false); form.resetFields() }} width={600}>
        <Form form={form} layout="vertical">
          <Form.Item name="name" label="合同名称" rules={[{ required: true }]}><Input /></Form.Item>
          <Row gutter={16}>
            <Col span={12}><Form.Item name="amount" label="金额"><InputNumber style={{ width: '100%' }} prefix="¥" /></Form.Item></Col>
            <Col span={12}><Form.Item name="status" label="状态" initialValue="DRAFT"><Select options={Object.entries(STATUS_MAP).map(([k, v]) => ({ value: k, label: v.label }))} /></Form.Item></Col>
          </Row>
          <Row gutter={16}>
            <Col span={12}><Form.Item name="startDate" label="开始日期"><DatePicker style={{ width: '100%' }} /></Form.Item></Col>
            <Col span={12}><Form.Item name="endDate" label="结束日期"><DatePicker style={{ width: '100%' }} /></Form.Item></Col>
          </Row>
          <Form.Item name="customerId" label="客户ID"><InputNumber style={{ width: '100%' }} /></Form.Item>
          <Form.Item name="description" label="描述"><Input.TextArea /></Form.Item>
        </Form>
      </Modal>
      <Modal title={`回款计划 - ${currentContract?.contractNo || ''}`} open={payOpen} onCancel={() => setPayOpen(false)} footer={null} width={500}>
        <Table rowKey="id" dataSource={payments} columns={[
          { title: '金额', dataIndex: 'amount', key: 'amount', render: v => `¥${(v || 0).toFixed(2)}` },
          { title: '计划日期', dataIndex: 'planDate', key: 'planDate' },
          { title: '实收日期', dataIndex: 'actualDate', key: 'actualDate', render: v => v || '-' },
          { title: '状态', dataIndex: 'status', key: 'status', render: v => v === 'PAID' ? <Tag color="green">已收款</Tag> : <Tag color="orange">待收款</Tag> },
        ]} pagination={false} style={{ marginBottom: 16 }} />
        <Form form={payForm} layout="inline">
          <Form.Item name="amount" rules={[{ required: true }]}><InputNumber placeholder="金额" prefix="¥" /></Form.Item>
          <Form.Item name="planDate"><DatePicker placeholder="计划日期" /></Form.Item>
          <Button type="primary" onClick={addPayment}>添加回款计划</Button>
        </Form>
      </Modal>
    </div>
  )
}