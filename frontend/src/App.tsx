import { useEffect, useState } from 'react'
import { AppShell } from './components/layout/AppShell'
import { AccountPage } from './features/auth/pages/AccountPage'
import { AuthPage } from './features/auth/pages/AuthPage'
import { useAuth } from './features/auth/hooks/useAuth'
import { CheckInPage } from './features/checkin/pages/CheckInPage'
import { OrganizerDashboardPage } from './features/dashboard/pages/OrganizerDashboardPage'
import { EventDetailPage } from './features/events/pages/EventDetailPage'
import { EventsPage } from './features/events/pages/EventsPage'
import { MyTicketsPage } from './features/tickets/pages/MyTicketsPage'
import { navigate, usePath } from './routes/navigation'
import './App.css'

function App() {
  const { accessToken, user, refresh, logout } = useAuth()
  const [ready, setReady] = useState(false)
  const path = usePath()

  useEffect(() => {
    void refresh().finally(() => setReady(true))
  }, [refresh])

  useEffect(() => {
    if (!ready || path !== '/') return
    navigate(user?.role === 'ORGANIZER' ? '/dashboard' : user?.role === 'CHECKIN_STAFF' ? '/checkin' : '/events')
  }, [path, ready, user?.role])

  if (!ready) {
    return <p className="page-message">Loading session...</p>
  }

  if (!user && path !== '/events' && !path.startsWith('/events/')) {
    return <AuthPage />
  }

  return (
    <AppShell role={user?.role ?? 'GUEST'} userName={user?.fullName ?? 'Guest'} onLogout={logout}>
      {path === '/events' && <EventsPage />}
      {path.startsWith('/events/') && <EventDetailPage eventId={Number(path.split('/')[2])} signedIn={Boolean(user)} />}
      {path === '/tickets' && user?.role === 'CUSTOMER' && <MyTicketsPage />}
      {path === '/checkin' && user?.role === 'CHECKIN_STAFF' && <CheckInPage />}
      {path === '/dashboard' && user?.role === 'ORGANIZER' && <OrganizerDashboardPage accessToken={accessToken} />}
      {path === '/account' && user && <AccountPage onLogout={logout} user={user} />}
      {path === '/' && <p className="page-message">Redirecting...</p>}
    </AppShell>
  )
}

export default App
