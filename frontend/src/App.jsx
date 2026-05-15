import { Routes, Route, Navigate } from 'react-router-dom'
import { useAuth } from './services/AuthContext.jsx'
import Layout from './components/Layout.jsx'
import LoginPage from './pages/LoginPage.jsx'
import Dashboard from './pages/Dashboard.jsx'
import Customers from './pages/Customers.jsx'
import CustomerDetail from './pages/CustomerDetail.jsx'
import Import from './pages/Import.jsx'
import ReviewPage from './pages/ReviewPage.jsx'
import MyUploads from './pages/MyUploads.jsx'
import AuditLogs from './pages/AuditLogs.jsx'
import DataDashboard from './pages/DataDashboard.jsx'
import Tasks from './pages/Tasks.jsx'
import Products from './pages/Products.jsx'
import Opportunities from './pages/Opportunities.jsx'
import Contracts from './pages/Contracts.jsx'
import Announcements from './pages/Announcements.jsx'
import CalendarView from './pages/CalendarView.jsx'
import Profile from './pages/Profile.jsx'

function App() {
  const { user, loading } = useAuth()

  if (loading) return <div className="min-h-screen flex items-center justify-center text-gray-400">加载中...</div>

  if (!user) return <LoginPage />

  return (
    <Routes>
      <Route path="/data-dashboard" element={<DataDashboard />} />
      <Route path="/*" element={
        <Layout>
          <Routes>
            <Route path="/" element={<Navigate to="/dashboard" replace />} />
            <Route path="/dashboard" element={<Dashboard />} />
            <Route path="/customers" element={<Customers />} />
            <Route path="/customers/:id" element={<CustomerDetail />} />
            <Route path="/import" element={<Import />} />
            <Route path="/review" element={<ReviewPage />} />
            <Route path="/my-uploads" element={<MyUploads />} />
            <Route path="/audit-logs" element={<AuditLogs />} />
            <Route path="/tasks" element={<Tasks />} />
            <Route path="/products" element={<Products />} />
            <Route path="/opportunities" element={<Opportunities />} />
            <Route path="/contracts" element={<Contracts />} />
            <Route path="/announcements" element={<Announcements />} />
            <Route path="/calendar" element={<CalendarView />} />
            <Route path="/profile" element={<Profile />} />
          </Routes>
        </Layout>
      } />
    </Routes>
  )
}

export default App