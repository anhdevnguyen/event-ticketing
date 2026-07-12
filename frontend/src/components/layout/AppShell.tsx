import type { ReactNode } from 'react'
import { LogOut, Ticket } from 'lucide-react'
import { navigate } from '../../routes/navigation'

interface AppShellProps {
  children: ReactNode
  role: string
  userName: string
  onLogout: () => Promise<void>
}

export function AppShell({ children, role, userName, onLogout }: AppShellProps) {
  return (
    <main className="app-frame">
      <header className="topbar">
        <button className="brand-link" type="button" onClick={() => navigate('/events')}>
          <Ticket size={22} />
          Event Ticketing
        </button>
        <nav>
          <button type="button" onClick={() => navigate('/events')}>Events</button>
          {role === 'CUSTOMER' && <button type="button" onClick={() => navigate('/tickets')}>My tickets</button>}
          {role === 'CHECKIN_STAFF' && <button type="button" onClick={() => navigate('/checkin')}>Check-in</button>}
          {role === 'ORGANIZER' && <button type="button" onClick={() => navigate('/dashboard')}>Dashboard</button>}
        </nav>
        {role === 'GUEST' ? (
          <button className="outline-action" type="button" onClick={() => navigate('/account')}>Sign in</button>
        ) : (
          <button className="outline-action" type="button" onClick={() => void onLogout()}>
            <LogOut size={16} />
            {userName}
          </button>
        )}
      </header>
      {children}
    </main>
  )
}
