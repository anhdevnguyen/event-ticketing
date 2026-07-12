import type { FormEvent } from 'react'
import { useState } from 'react'
import { LogIn, UserPlus } from 'lucide-react'
import { GoogleLoginButton } from './GoogleLoginButton'
import { useAuth } from '../hooks/useAuth'

export function LoginForm() {
  const [mode, setMode] = useState<'login' | 'register'>('login')
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [fullName, setFullName] = useState('')
  const [error, setError] = useState('')
  const { login, register, loading } = useAuth()

  async function onSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    setError('')
    try {
      if (mode === 'login') {
        await login({ email, password })
      } else {
        await register({ email, password, fullName })
      }
    } catch {
      setError(mode === 'login' ? 'Email or password is incorrect.' : 'Registration failed.')
    }
  }

  return (
    <form className="auth-panel" onSubmit={onSubmit}>
      <div className="brand-row">
        <div>
          <p>Event Ticketing</p>
          <h1>{mode === 'login' ? 'Sign in' : 'Create account'}</h1>
        </div>
        <div className="mode-switch" aria-label="Authentication mode">
          <button type="button" className={mode === 'login' ? 'active' : ''} onClick={() => setMode('login')}>
            Sign in
          </button>
          <button type="button" className={mode === 'register' ? 'active' : ''} onClick={() => setMode('register')}>
            Register
          </button>
        </div>
      </div>

      {mode === 'register' && (
        <label>
          Full name
          <input value={fullName} onChange={(event) => setFullName(event.target.value)} required minLength={2} />
        </label>
      )}

      <label>
        Email
        <input type="email" value={email} onChange={(event) => setEmail(event.target.value)} required />
      </label>

      <label>
        Password
        <input
          type="password"
          value={password}
          onChange={(event) => setPassword(event.target.value)}
          required
          minLength={8}
        />
      </label>

      {error && <p className="form-error">{error}</p>}

      <button className="primary-action" type="submit" disabled={loading}>
        {mode === 'login' ? <LogIn size={18} /> : <UserPlus size={18} />}
        {loading ? 'Please wait' : mode === 'login' ? 'Sign in' : 'Create account'}
      </button>

      <GoogleLoginButton />
    </form>
  )
}
