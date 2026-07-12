import { LogOut, ShieldCheck } from 'lucide-react'

interface AccountPageProps {
  user: { fullName: string; email: string; role: string }
  onLogout: () => Promise<void>
}

export function AccountPage({ user, onLogout }: AccountPageProps) {
  return (
    <section className="page narrow">
      <section className="session-panel">
        <ShieldCheck size={28} />
        <div>
          <p>Signed in as</p>
          <h1>{user.fullName}</h1>
          <span>{user.email}</span>
        </div>
        <strong>{user.role}</strong>
        <button type="button" onClick={() => void onLogout()}>
          <LogOut size={18} />
          Sign out
        </button>
      </section>
    </section>
  )
}
