import { KeyRound } from 'lucide-react'

export function GoogleLoginButton() {
  const apiBaseUrl = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080/api/v1'

  return (
    <a className="secondary-action" href={`${apiBaseUrl}/auth/google`}>
      <KeyRound size={18} />
      Continue with Google
    </a>
  )
}
