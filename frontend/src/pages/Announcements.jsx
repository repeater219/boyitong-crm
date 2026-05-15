import { useState, useEffect } from 'react'
import { Card, List, Avatar, Tag, Typography } from 'antd'
import { BellOutlined, PushpinOutlined } from '@ant-design/icons'
import api from '../services/api.js'

const { Text, Paragraph } = Typography

export default function Announcements() {
  const [data, setData] = useState([])
  const [loading, setLoading] = useState(true)
  useEffect(() => { api.get('/crm/announcements').then(r => setData(r.data.data || [])).finally(() => setLoading(false)) }, [])
  return (
    <Card title={<span><BellOutlined /> 公告中心</span>}>
      <List loading={loading} dataSource={data} renderItem={item => (
        <List.Item extra={item.pinned && <Tag icon={<PushpinOutlined />} color="red">置顶</Tag>}>
          <List.Item.Meta
            avatar={<Avatar style={{ backgroundColor: '#1677ff' }}>{item.title?.[0]}</Avatar>}
            title={<Text strong>{item.title}</Text>}
            description={<><Text type="secondary">{item.author}</Text> · <Text type="secondary">{item.createdAt?.replace('T', ' ')}</Text></>}
          />
          <Paragraph style={{ marginTop: 8, marginBottom: 0 }}>{item.content}</Paragraph>
        </List.Item>
      )} />
    </Card>
  )
}