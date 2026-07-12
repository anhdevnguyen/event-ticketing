import { Ticket } from 'lucide-react'
import { LoginForm } from '../components/LoginForm'

export function AuthPage() {
  return (
    <main className="auth-page">
      <section className="auth-copy">
        <Ticket size={34} />
        <h1>Event operations start at sign-in.</h1>
        <p>Customers book tickets, organizers manage events, and gate staff check QR tickets from one account.</p>
      </section>
      <LoginForm />
    </main>
  )
}
